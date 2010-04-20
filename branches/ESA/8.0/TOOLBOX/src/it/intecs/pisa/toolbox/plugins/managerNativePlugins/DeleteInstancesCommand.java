/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class DeleteInstancesCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        String serviceName = "";
        String opType = "A";
        InstanceHandler handler;
        Enumeration<String> parametersNames;
        String parKey;
        try {
            parametersNames=request.getParameterNames();

            while(parametersNames.hasMoreElements())
            {
                parKey=parametersNames.nextElement();
                if(parKey.startsWith("ID_"))
                {
                    handler=new InstanceHandler(Long.parseLong(request.getParameter(parKey)));
                    handler.deleteInstance();
                }
            }

            opType=request.getParameter("opType");
            serviceName=request.getParameter("serviceName");
            resp.sendRedirect("viewServiceInstances.jsp?instanceType=" + opType + "&serviceName=" + serviceName);
        } catch (Exception ex) {
            String errorMsg = "Error deleting instances ) : " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
