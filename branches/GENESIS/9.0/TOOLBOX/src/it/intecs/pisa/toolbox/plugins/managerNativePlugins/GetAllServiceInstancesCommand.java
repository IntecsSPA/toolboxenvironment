/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetAllServiceInstancesCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String orderId,pushHost;
        Document respDoc;

        try {
            orderId = req.getParameter("orderId");
            pushHost = req.getParameter("pushHost");

            if(pushHost==null || pushHost.equals("")==true)
                respDoc = InstanceLister.getInstancesByOrderId(orderId);
            else respDoc = InstanceLister.getInstancesByOrderIdAndPushHost(orderId,pushHost);


            sendXMLAsResponse(resp, respDoc);
        } catch (Exception ex) {
            String errorMsg = "Error getting synchronous instances: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
