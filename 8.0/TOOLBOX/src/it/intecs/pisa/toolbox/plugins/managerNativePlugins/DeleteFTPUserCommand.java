/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class DeleteFTPUserCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
       String username;

        try {
            username = req.getParameter("user");

            tbxServlet.getFtpServerManager().removeUser(username);
            resp.sendRedirect("listFTPAccounts.jsp");
        } catch (Exception ex) {
            String errorMsg = "Error deleting FTP user: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
