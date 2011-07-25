/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.service.log.ServiceLogHandler;
import it.intecs.pisa.util.IOUtil;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class GetServiceLogCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        InputStream logStream;
        ServiceManager serviceManager;
        try {
            String service = req.getParameter("serviceName");
            int cursor = (req.getParameter("start") == null ? 0 : Integer.parseInt(req.getParameter("start")));

            ToolboxConfiguration conf = ToolboxConfiguration.getInstance();
            String logType = conf.getConfigurationValue(ToolboxConfiguration.LOG_TYPE);
            if (logType.equals("json")) {
                resp.setContentType("application/json");
            } else {
                resp.setContentType("text/xml");
            }
            serviceManager = ServiceManager.getInstance();
            logStream = ServiceLogHandler.extractLog(service, cursor);
            IOUtil.copy(logStream, resp.getOutputStream());
        } catch (Exception ex) {
            String errorMsg = "Error getting service log: " + CDATA_S + ex.getMessage() + CDATA_E;

            throw new GenericException(errorMsg);
        }
    }
}
