/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.log.ToolboxLogHandler;
import it.intecs.pisa.util.IOUtil;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class GetToolboxLogCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        InputStream logStream;
        try {
            int cursor = (request.getParameter("start") == null ? 0 : Integer.parseInt(request.getParameter("start")));
            ToolboxConfiguration conf=ToolboxConfiguration.getInstance();
            String logType=conf.getConfigurationValue(ToolboxConfiguration.LOG_TYPE);
            if(logType.equals("json"))
                response.setContentType("application/json");
            else
                response.setContentType("text/xml");
            logStream = ToolboxLogHandler.extractLog(cursor);

            IOUtil.copy(logStream, response.getOutputStream());
        } catch (Exception ex) {
            String errorMsg = "Error getting toolbox log: " + CDATA_S + ex.getMessage() + CDATA_E;

            throw new GenericException(errorMsg);
        }
    }
}
