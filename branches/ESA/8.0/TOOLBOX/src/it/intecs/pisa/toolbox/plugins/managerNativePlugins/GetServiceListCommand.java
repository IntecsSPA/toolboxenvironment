/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
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
public class GetServiceListCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document doc;
        DOMUtil util;
        Element root;
        TBXService[] services;
        String serviceName;
        ServiceManager serviceManager;

        try {
            util = new DOMUtil();
            doc = util.newDocument();
            root = doc.createElement("serviceList");

            doc.appendChild(root);

            serviceManager=ServiceManager.getInstance();
            services = serviceManager.getServicesAsArray();
            for(TBXService service:services)
            {
                serviceName =service.getServiceName();
                addServiceToServiceList(doc, serviceName, root);
            }
            sendXMLAsResponse(resp, doc);
        } catch (Exception ex) {
           String errorMsg = "Error getting the service list: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
    
     protected void addServiceToServiceList(Document doc, String serviceName, Element root) throws Exception {
        Element serviceTag;
        TBXService service;
        Operation[] operations;
        ServiceManager serviceManager;

        serviceManager=ServiceManager.getInstance();
        service = serviceManager.getService(serviceName);

        serviceTag = doc.createElement("service");
        serviceTag.setAttribute("name", serviceName);

        root.appendChild(serviceTag);

        operations=service.getImplementedInterface().getOperations();
        for(Operation op: operations)
            addOperationToServiceList(serviceTag, op);
           
    }
     
     protected void addOperationToServiceList(Element serviceTag, Operation operation) throws Exception {
        Element operationTag;
        Document doc;

        doc = serviceTag.getOwnerDocument();

        operationTag = doc.createElement("operation");
        operationTag.setAttribute("name", operation.getName());
        operationTag.setAttribute("soapAction", operation.getSoapAction());

        if (operation.getType().equals(TBXService.ASYNCHRONOUS)) {
            operationTag.setAttribute("callbackSoapAction", operation.getCallbackSoapAction());
        }

        serviceTag.appendChild(operationTag);

    }
}
