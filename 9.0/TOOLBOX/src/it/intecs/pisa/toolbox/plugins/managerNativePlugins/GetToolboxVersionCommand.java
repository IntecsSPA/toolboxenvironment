/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class GetToolboxVersionCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
       Document versionDoc;
        DOMUtil domUtil;
        Element rootEl;
        File pluginMainDir;
        File[] pluginDirs;
        try {
            domUtil = new DOMUtil(true);

            versionDoc = domUtil.newDocument();
            rootEl = versionDoc.createElement("versionDescription");
            rootEl.setAttribute("ToolboxVersion", tbxServlet.getToolboxVersion());

            versionDoc.appendChild(rootEl);

            pluginMainDir = new File(tbxServlet.getServletContext().getRealPath("WEB-INF/plugins"));
            pluginDirs = pluginMainDir.listFiles();

            for (File plugin : pluginDirs) {
                addPlguinNameAndVersion(rootEl, plugin);
            }

            sendXMLAsResponse(resp, versionDoc);
        } catch (Exception ex) {
            String errorMsg = "Error while creating the version document";
            logger.error(errorMsg);
            resp.sendError(500);
        }
    }
    
      private void addPlguinNameAndVersion(Element parentEl, File plugin) {
        try {
            Element el;
            Element rootEl;
            File pluginXml;
            DOMUtil util;
            Document pluginDoc;

            util = new DOMUtil();
            pluginXml = new File(plugin, "plugin.xml");
            pluginDoc = util.fileToDocument(pluginXml);

            rootEl = pluginDoc.getDocumentElement();

            el = parentEl.getOwnerDocument().createElement("plugin");
            el.setAttribute("name", rootEl.getAttribute("name"));
            el.setAttribute("version", rootEl.getAttribute("version"));

            parentEl.appendChild(el);
        } catch (Exception ex) {
        }
    }

}
