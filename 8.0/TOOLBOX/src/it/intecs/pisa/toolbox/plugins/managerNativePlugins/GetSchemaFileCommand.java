/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class GetSchemaFileCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        String schema;
        File schemaFile;
        try {
            serviceName=req.getParameter("serviceName");
            schema=req.getParameter("schema");
            
            schemaFile=new File(tbxServlet.getServiceRoot(serviceName),"Schemas/"+schema);
                      
            resp.setContentType("text/xml");
            resp.setHeader( "Content-Disposition", "attachment; filename=\"" + schemaFile.getName() + "\"" );
            IOUtil.copy(new FileInputStream(schemaFile), resp.getOutputStream());
            
        } catch (Exception ex) {
            String errorMsg = "Error applying FTP changes: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
