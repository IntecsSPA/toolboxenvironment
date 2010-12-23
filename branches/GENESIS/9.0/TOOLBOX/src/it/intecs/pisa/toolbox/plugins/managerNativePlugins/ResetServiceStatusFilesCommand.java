/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class ResetServiceStatusFilesCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
       String serviceName;
        TBXService service;
        File serviceRootDir;
        File statusFile;
        File timerFile;
        ServiceManager serviceManager;
        try {
            serviceName = req.getParameter("serviceName");

            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);
            
            service.stop();
          

            serviceRootDir = service.getServiceRoot();
            statusFile = new File(serviceRootDir, "serviceStatus.xml");
            timerFile = new File(serviceRootDir, "timerStatus.xml");

            statusFile.delete();
            timerFile.delete();

            //service.init();
            service.start();

            resp.sendRedirect("main.jsp");
        } catch (Exception e) {
            String errorMsg = "Cannot reset service status";
            throw new GenericException(errorMsg);
        }
    }

}
