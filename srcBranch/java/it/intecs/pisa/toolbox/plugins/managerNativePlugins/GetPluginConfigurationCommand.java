/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetPluginConfigurationCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document doc;
        DOMUtil util;
        File rootDir;
        File configFile = null;
        String pluginName;

        try {
            pluginName = req.getParameter("pluginName");

            util = new DOMUtil();

            rootDir = tbxServlet.getRootDir();
            configFile = new File(rootDir, "WEB-INF/plugins/" + pluginName + "/resources/config.xml");

            doc = util.fileToDocument(configFile);

            sendXMLAsResponse(resp, doc);
        } catch (Exception ex) {
             throw new GenericException("Cannot get Plugin configuration");
        }
    }
}
