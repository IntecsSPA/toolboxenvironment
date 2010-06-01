/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.Util;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XSLT;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class HarvestFromGUICommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String servicename="";
        TBXService service;
        Document message;
        Document response;
        Document soapMessage;
        DOMUtil util;
        Element rootEl;
        Element sourceEl;
        Element resourceTypeEl;
        ServiceManager serviceManager;

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
            sourceEl.setTextContent(req.getParameter("source"));
            rootEl.appendChild(sourceEl);

            resourceTypeEl=message.createElement("ResourceType");
            resourceTypeEl.setTextContent(req.getParameter("resourceType"));
            rootEl.appendChild(resourceTypeEl);

            soapMessage=Util.addSOAPElements(message);

            File tmpFile;
            tmpFile=IOUtil.getTemporaryFile();
            DOMUtil.dumpXML(soapMessage, tmpFile);

            response = service.processRequest("http://www.opengis.net/cat/csw/2.0.2/requests#Harvest", util.fileToDocument(tmpFile), false);

            File xsltFile=new File(tbxServlet.getRootDir(),"WEB-INF/XSL/resourceDisplay.xsl");
            resp.setContentType("text/html");

            XSLT.transform(new StreamSource(xsltFile), new StreamSource(DOMUtil.getDocumentAsInputStream(response)), new StreamResult(resp.getOutputStream()));


//            IOUtil.copy(DOMUtil.getDocumentAsInputStream(response), resp.getOutputStream());


        } catch (Exception ex) {
             /*ex.printStackTrace();
             
            String errorMsg = "Error trying to trigger an harvest session: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);*/
            resp.sendError(500, "Harvest raised an error");
           
        }
    }
}
