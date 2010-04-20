/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class IsServiceNameAvailableCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        boolean isAvailable=false;
        Document doc;
        DOMUtil util;
        Element root;
        ServiceManager servMan;
        try {
            serviceName = req.getParameter("serviceName");
            servMan=ServiceManager.getInstance();


            if(serviceName!=null && serviceName.equals("")==false)
                isAvailable=!servMan.isServiceDeployed(serviceName);
            
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
