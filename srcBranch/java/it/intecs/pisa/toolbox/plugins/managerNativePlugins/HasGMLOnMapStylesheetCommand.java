/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class HasGMLOnMapStylesheetCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName="<UNKNOWN SERVICE>";
        TBXService service;
        DOMUtil util;
        Document doc;
        Element rootEl;
        ServiceManager serviceManager;

        try {
            util = new DOMUtil();

            serviceName = req.getParameter("serviceName");
            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);

            doc = util.newDocument();
            rootEl = doc.createElement("hasGMLOnMapStylesheet");
            doc.appendChild(rootEl);

            rootEl.setAttribute("value", Boolean.toString(service.hasGMLOnMapStylesheet()));

            IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), resp.getOutputStream());
        } catch (Exception e) {
            String errorMsg = "Cannot establish if service "+serviceName+" has a GML stylesheet";
            throw new GenericException(errorMsg);
        }
    }
}
