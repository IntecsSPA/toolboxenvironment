/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Massimiliano
 */
public class ValidateToolboxScriptCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        try {
            Hashtable<String, FileItem> mimeparts;
            FileItem script;
            DOMUtil domUtil;
            File schema;
            boolean valid=true;

            mimeparts=parseMultiMime(req);
            script=mimeparts.get("scriptFile");

            schema=new File(tbxServlet.getRootDir(),"WEB-INF/schemas/xmlScript.xsd");

            domUtil=new DOMUtil();
            try
            {
            domUtil.validateDocument(schema, script.getInputStream());
            }
            catch(Exception e)
            {
                valid=false;
            }
            resp.sendRedirect("validateScriptResult.jsp?valid="+valid);
        } catch (Exception ex) {
            String errorMsg = "Error while validating script: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
