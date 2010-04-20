/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.lifecycle.LifeCycle;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.tasks.ServiceLifeCycle;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class DeleteOperationCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        String serviceName="<UNKNOWN SERVICE>" ;
        String operationName;
        TBXService service;
        Operation[] ops;
        Operation[] newOpArray;
        TBXOperation delOp;
        File operationDir;
        int i=0;
        ServiceManager serviceManager;
        try {
            serviceName = request.getParameter("serviceName");
            operationName= request.getParameter("operationName");

            serviceManager=ServiceManager.getInstance();
            service=serviceManager.getService(serviceName);

            service.deleteOperation(operationName);
       
            resp.sendRedirect("manageOperations.jsp?serviceName="+serviceName);

        } catch (Exception ex) {
            String errorMsg = "Error deleting service (" + serviceName + ") : " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
