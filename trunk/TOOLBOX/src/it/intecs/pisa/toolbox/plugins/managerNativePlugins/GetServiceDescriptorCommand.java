/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.Zip;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class GetServiceDescriptorCommand extends NativeCommandsManagerPlugin{
    protected static final String ZIP_DEPLOY_PACKAGE_MIME_TYPE = "application/x-zip-compressed";
       
    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
         String serviceName;
        File tempDir;
        File tempServiceDir;
        File serviceDir;
        File tempSchemaDir;
        File sourceSchemaDIr;
        File zipPackage;
        OutputStream out;

        try {
            serviceName = req.getParameter("serviceName");

            tempDir = new File(System.getProperty("java.io.tmpdir"), "exportPackages");
            if (tempDir.exists() == false) {
                tempDir.mkdir();
            }
            tempServiceDir = new File(tempDir, serviceName);
            if (tempServiceDir.exists()) {
                IOUtil.rmdir(tempServiceDir);
            }
            tempServiceDir.mkdir();

            serviceDir = tbxServlet.getServiceRoot(serviceName);

            IOUtil.copyDirectory(serviceDir, tempServiceDir);


            tempSchemaDir = new File(tempServiceDir, "Schemas");
            sourceSchemaDIr = new File(serviceDir, "Schemas");

            SchemaSetRelocator.updateSchemaLocationToRelative(tempSchemaDir, sourceSchemaDIr.toURI());

            zipPackage = new File(tempDir, serviceName + ".zip");
            Zip.zipDirectory(zipPackage.getAbsolutePath(), tempServiceDir.getAbsolutePath(), false);

            resp.setContentType(ZIP_DEPLOY_PACKAGE_MIME_TYPE);
            resp.setHeader("Content-disposition", "attachment; filename=" + zipPackage.getName());
           
            out=resp.getOutputStream();
            IOUtil.copy(new FileInputStream(zipPackage), out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            String errorMsg = "Error getting service descriptor: " + CDATA_S + ex.getMessage() + CDATA_E;
             throw new GenericException(errorMsg);
        }
    }

}
