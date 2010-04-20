/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.resources.LogResourcesPersistence;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class ClearToolboxLogCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        try {
            LogResourcesPersistence.getInstance().deleteToolboxLog();
        } catch (Exception ex) {
            String errorMsg = "Error clearing the Toolbox log: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

   
}
