/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.wsil.WSILBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class DeleteServiceCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        String service="<UNKNOWN SERVICE>" ;
        ServiceManager serviceManager;
        try {
            service = request.getParameter("serviceName");

            serviceManager=ServiceManager.getInstance();
            serviceManager.deleteService(service);

            WSILBuilder.createWSIL();
        } catch (Exception ex) {
            String errorMsg = "Error deleting service (" + service + ") : " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
