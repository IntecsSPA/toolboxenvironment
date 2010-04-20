/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import java.util.HashSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class IsSoapActionAvailableCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        String soapAction;
        String operationName;
        String operationSOAPAction;
        TBXService service;
        boolean isAvailable=false;
        Document doc;
        DOMUtil util;
        Element root;
        HashSet soapActions;
        ServiceManager servMan;
        try {
            servMan=ServiceManager.getInstance();

            serviceName = req.getParameter("serviceName");
            soapAction=req.getParameter("soapAction");
            operationName=req.getParameter("operationName");

            if((serviceName!=null && serviceName.equals("")==false) &&
                (soapAction!=null && soapAction.equals("")==false))
            {
                try
                {
                    service=servMan.getService(serviceName);
                    soapActions=service.getSOAPActionsNames();

                    isAvailable=!soapActions.contains(soapAction);

                    if(isAvailable == false && operationName!= null && operationName.equals("")==false)
                    {
                        operationSOAPAction=service.getOperation(operationName).getSoapAction();
                        isAvailable=operationSOAPAction.equals(soapAction);
                    }
                }
                catch(Exception e)
                {
                    isAvailable=false;
                }
            }

            util = new DOMUtil();
            doc = util.newDocument();
            root = doc.createElement("response");
            root.setAttribute("value", isAvailable?"true":"false");

            doc.appendChild(root);
            sendXMLAsResponse(resp, doc);
        } catch (Exception ex) {
            String errorMsg = "Error while checking service name availability: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }
}
