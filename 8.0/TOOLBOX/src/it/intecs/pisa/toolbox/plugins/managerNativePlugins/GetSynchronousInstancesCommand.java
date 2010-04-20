/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetSynchronousInstancesCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String service;
        int cursor = 0;
        DOMUtil util;
        Document respDoc;
        ServiceManager serviceManager;
        try {
            util = new DOMUtil();
            service = req.getParameter("serviceName");
            cursor = (req.getParameter("start") == null ? 0 : Integer.parseInt(req.getParameter("start")));

            serviceManager=ServiceManager.getInstance();
            respDoc = InstanceLister.getSynchronousInstancesAsDocument(service, cursor, 20);
                    
            sendXMLAsResponse(resp, respDoc);
        } catch (Exception ex) {
            String errorMsg = "Error getting synchronous instances: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
