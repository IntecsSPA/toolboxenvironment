/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class IsOperationNameAvailableCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        String operationName;
        TBXService service;
        boolean isAvailable=false;
        Document doc;
        DOMUtil util;
        Element root;
        ServiceManager servMan;
        try {

            servMan=ServiceManager.getInstance();
            serviceName = req.getParameter("serviceName");
            operationName= req.getParameter("operationName");

            if((serviceName!=null && serviceName.equals("")==false) &&
               (operationName!=null && operationName.equals("")==false))
            {
                try
                {
                    service=servMan.getService(serviceName);
                    isAvailable=!service.getOperations().contains(operationName);
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
