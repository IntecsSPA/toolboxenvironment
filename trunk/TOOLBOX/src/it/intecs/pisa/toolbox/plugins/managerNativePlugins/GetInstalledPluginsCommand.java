/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class GetInstalledPluginsCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document doc;
        DOMUtil util;
        Element root;
        Element el;
        File rootDir;
        File pluginDir = null;
        Hashtable services;
        Enumeration servicekeys;
        File[] pluginDirs;

        String serviceName;
        try {

            //setting response type

            util = new DOMUtil();
            doc = util.newDocument();
            root = doc.createElement("pluginList");

            doc.appendChild(root);

            rootDir = tbxServlet.getRootDir();
            pluginDir = new File(rootDir, "WEB-INF/plugins");

            pluginDirs = pluginDir.listFiles();

            for (File dir : pluginDirs) {
                el = doc.createElement("plugin");
                DOMUtil.setTextToElement(doc, el, dir.getName());
                root.appendChild(el);
            }

            sendXMLAsResponse(resp, doc);
        } catch (Exception ex) {
             throw new GenericException("Cannot get installed plugins list");
        }
    }
}
