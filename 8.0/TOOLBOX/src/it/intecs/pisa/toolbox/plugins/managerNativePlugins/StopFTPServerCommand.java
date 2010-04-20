/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class StopFTPServerCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FTPServerManager ftpServer;

        try {
            ftpServer=this.tbxServlet.getFtpServerManager();
            ftpServer.stopServer();
            
            resp.sendRedirect("FTPManagement.jsp");
        } catch (Exception ex) {
            String errorMsg = "Error applying FTP changes: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
