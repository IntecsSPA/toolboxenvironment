/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class CancelInstanceCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        String name = "";
        String opType = "A";
        String id;
        InstanceHandler handler;
        try {
            id=request.getParameter("id");
            opType=request.getParameter("opType");
            name=request.getParameter("serviceName");

            handler=new InstanceHandler(Long.parseLong(id));
            handler.cancelInstance();

            resp.sendRedirect("viewServiceInstances.jsp?instanceType=" + opType + "&serviceName=" + name);
        } catch (Exception ex) {
            String errorMsg = "Error cancelling service (" + name + ") : " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

  
}
