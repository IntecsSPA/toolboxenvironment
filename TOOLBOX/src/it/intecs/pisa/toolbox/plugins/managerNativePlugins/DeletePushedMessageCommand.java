/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import java.io.File;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class DeletePushedMessageCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        String filename="";
        try {
            filename=request.getParameter("name");
            File pushedMsgFile;

            pushedMsgFile=new File(Toolbox.getInstance().getRootDir(),"Push/"+filename);
            pushedMsgFile.delete();
            resp.sendRedirect("viewPushedMsg.jsp");
        } catch (Exception ex) {
            String errorMsg = "Error deleting pushed message "+filename+" : " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
