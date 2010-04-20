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
public class InvalidateAuthenticationCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
       try
       {
           tbxServlet.setDeployAdministrationToken(null);
       }
       catch(Exception ec)
       {
           throw new GenericException ("Cannot invalidate authentication session");
       }
    }

}
