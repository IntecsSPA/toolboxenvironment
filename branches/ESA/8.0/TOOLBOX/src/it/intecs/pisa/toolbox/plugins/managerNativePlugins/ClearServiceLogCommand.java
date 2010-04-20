/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.log.ServiceLogHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class ClearServiceLogCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName="UNKNOWN";
        ServiceManager serviceManager;
        TBXService service;
        try {
            serviceName = req.getParameter("serviceName");
            ServiceLogHandler.deleteLog(serviceName);
        } catch (Exception ex) {
            String errorMsg = "Error clearing the service (" + serviceName + ") log: " + CDATA_S + ex.getMessage() + CDATA_E;

            throw new GenericException(errorMsg);
        }
    }
}
