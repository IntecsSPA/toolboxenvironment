/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 *
 * @author Massimiliano
 */
public class GetOperationScriptCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
       String operationName = "";
        String serviceName = "";
        String scriptNumber = "";
        int requestScriptIndex = 0;
        TBXService service;
        Operation operationDescr;
        Script[] scriptDescriptors;
        Script scriptDescr;
        Node descScript=null;
        Document script;
        ServiceManager serviceManager;
        DOMUtil domUtil = new DOMUtil(true);
        try {
            serviceName = req.getParameter("serviceName");
            operationName = req.getParameter("operationName");
            scriptNumber = req.getParameter("scriptNumber") == null ? "1" : req.getParameter("scriptNumber");

            requestScriptIndex = Integer.parseInt(scriptNumber) - 1;

            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);
            operationDescr = service.getImplementedInterface().getOperationByName(operationName);

            
            //scriptDescr

            if (descScript instanceof Document) {
                script = (Document) descScript;
            } else {
                script = domUtil.newDocument();
                script.appendChild(script.importNode(descScript, true));
            }

            sendXMLAsResponse(resp, script);
        } catch (Exception ex) {
            String errorMsg = "Error getting " + operationName + " operation script (" + requestScriptIndex + ") for " +
                    serviceName + " service: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

}
