/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class HarvestFromGUICommand extends NativeCommandsManagerPlugin {

    private static final String SERVICE_EXCEPTION_ELEMENT_NAME = "ServiceExceptionReport";

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String servicename="";
        TBXService service;
        Document message;
        Document soapMessage;
        DOMUtil util;
        Element rootEl;
        Element sourceEl;
        String responseMessage="";
        Element resourceTypeEl;
        ServiceManager serviceManager;
        OutputStream out=resp.getOutputStream();

        try {
            util=new DOMUtil();
            
            servicename=req.getParameter("serviceName");

            serviceManager=ServiceManager.getInstance();
            service=serviceManager.getService(servicename);

            message=util.newDocument();
            
            rootEl=message.createElement("Harvest");
            rootEl.setAttribute("service", "CSW");
            rootEl.setAttribute("version", "2.0.2");
            rootEl.setAttribute("xmlns:csw", "http://www.opengis.net/cat/csw/2.0.2");
            rootEl.setAttribute("xmlns","http://www.opengis.net/cat/csw/2.0.2");
            rootEl.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
            rootEl.setAttribute("xmlns:xsd","http://www.w3.org/2001/XMLSchema");
          

            message.appendChild(rootEl);

            sourceEl=message.createElement("Source");
            sourceEl.setAttribute("xmlns","http://www.opengis.net/cat/csw/2.0.2");
            sourceEl.setTextContent(req.getParameter("source"));
            rootEl.appendChild(sourceEl);

            resourceTypeEl=message.createElement("ResourceType");
            resourceTypeEl.setAttribute("xmlns","http://www.opengis.net/cat/csw/2.0.2");
            resourceTypeEl.setTextContent(req.getParameter("resourceType"));
            rootEl.appendChild(resourceTypeEl);

            soapMessage=Util.addSOAPElements(message);

            File tmpFile;
            tmpFile=IOUtil.getTemporaryFile();
            DOMUtil.dumpXML(soapMessage, tmpFile);
            Document harvestResponse=service.processRequest("http://www.opengis.net/cat/csw/2.0.2/requests#Harvest", util.fileToDocument(tmpFile), false);

            if(harvestResponse.getDocumentElement().getLocalName().equalsIgnoreCase(SERVICE_EXCEPTION_ELEMENT_NAME))
               responseMessage="{'error': '"+resp.encodeRedirectURL("An error occurred while harvesting data from disk.")+"', 'serviceName' :'"+servicename+"'}";
            else
               responseMessage="{'info' : '"+resp.encodeRedirectURL("Metadata Harvested.")+"', 'serviceName' :'"+servicename+"'}";

        } catch (Exception ex) {
            responseMessage="{'error': '"+resp.encodeRedirectURL("An error occurred while harvesting data from disk.")+"', 'serviceName' :'"+servicename+"'}";
           
        }

        out.write(responseMessage.getBytes());
        out.close();
    }
}
