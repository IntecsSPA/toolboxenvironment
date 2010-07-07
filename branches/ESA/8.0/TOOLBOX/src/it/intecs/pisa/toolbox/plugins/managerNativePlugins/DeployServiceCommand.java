/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.exceptions.ServiceValidationException;
import it.intecs.pisa.soap.toolbox.exceptions.ValidationException;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeployServiceCommand extends NativeCommandsManagerPlugin {
    public static final String ZIP_DEPLOY_PACKAGE_MIME_TYPE = "application/x-zip-compressed";

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        executeDeploy(req,resp);
    }

     private void executeDeploy(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contentType = null;
        File packageFile = null;
        FileItem item = null;
        String serviceName = null;
        String fieldName = null;
        String token = "";
        boolean forward = false;

        try {
            //checking if uploaded file is a file descriptor
            DiskFileUpload upload = new DiskFileUpload();
            List items = upload.parseRequest(req);

            Iterator iter = items.iterator();

            while (iter.hasNext()) {
                //geting item
                item = (FileItem) iter.next();

                contentType = item.getContentType();

                fieldName = item.getFieldName();
                if ((fieldName != null && fieldName.equals("importFile")) || (contentType != null && contentType.contains(ZIP_DEPLOY_PACKAGE_MIME_TYPE))) {

                    packageFile = new File(System.getProperty("java.io.tmpdir"), item.getName());
                    item.write(packageFile);

                } else if (fieldName.equals("newName")) {
                    serviceName = item.getString();
                } else if (fieldName.equals("authToken")) {
                    token = item.getString();
                } else if (fieldName.equals("forwardToPage")) {
                    forward = true;
                }
            }

            String deployAdminToken = Toolbox.getInstance().getDeployAdminToken();
            if (deployAdminToken != null && token.equals(deployAdminToken) == false) {
                resp.sendError(500);
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return;
        }

        if (serviceName == null || serviceName.equals("")) {
            serviceName = packageFile.getName();
            serviceName = serviceName.substring(0, serviceName.lastIndexOf('.'));
        }

        try {
            ServiceManager serviceManager;

            serviceManager=ServiceManager.getInstance();
            serviceManager.deployService(packageFile, serviceName);
        }
        catch(ServiceValidationException val)
        {
            resp.sendRedirect("selectImportOrCreate.jsp?serviceName=" + serviceName+"&error=validationError");
            return;
        }
        catch (Exception e) {
            resp.sendRedirect("selectImportOrCreate.jsp?serviceName=" + serviceName+"&error=serviceexist");
            return;
        }
        if (forward == true) {
            resp.sendRedirect("serviceConfiguration.jsp?serviceName=" + serviceName);
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
