/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetAsynchronousInstancesCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        int cursor = 0;
        DOMUtil util;
        Document respDoc;
        ServiceManager serviceManager;
        TBXService service;
        try {
            util = new DOMUtil();
            serviceName = req.getParameter("serviceName");
            cursor = (req.getParameter("start") == null ? 0 : Integer.parseInt(req.getParameter("start")));

            serviceManager=ServiceManager.getInstance();
            service=serviceManager.getService(serviceName);

            respDoc =InstanceLister.getSynchronousInstancesAsDocument(serviceName, cursor, 20);
            sendXMLAsResponse(resp, respDoc);
        } catch (Exception ex) {
            String errorMsg = "Error getting asynchronous instances: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
