/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.Util;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class HarvestFromFileGUICommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Hashtable<String, FileItem> mimeparts;
        FileItem harvestFile;
        mimeparts = parseMultiMime(req);
        harvestFile = mimeparts.get("filepath");
        DOMUtil domutil = new DOMUtil();
        Toolbox toolboxServlet = Toolbox.getInstance();
        logger = toolboxServlet.getLogger();

// Dump message on a public directory
        Document harvestDoc = domutil.inputStreamToDocument(harvestFile.getInputStream());
        String filename = java.util.UUID.randomUUID().toString() + ".xml";
        File harvestDir = new File(toolboxServlet.getRootDir(), Toolbox.HARVEST);
        File harvest = new File(harvestDir, filename);
        DOMUtil.dumpXML(harvestDoc, harvest);


// Calculate the URL
        String source = toolboxServlet.getPublicAddress()+Toolbox.SLASH+Toolbox.HARVEST+Toolbox.SLASH+filename;

        String servicename = "";
        TBXService service;
        Document message;
        Document soapMessage;
        DOMUtil util;
        Element rootEl;
        Element sourceEl;
        Element resourceTypeEl;
        ServiceManager serviceManager;

        try {
            util = new DOMUtil();
            servicename = getStringFromMimeParts(mimeparts, "serviceName");
            serviceManager = ServiceManager.getInstance();
            service = serviceManager.getService(servicename);
            message = util.newDocument();

            rootEl = message.createElement("Harvest");
            rootEl.setAttribute("service", "CSW");
            rootEl.setAttribute("version", "2.0.2");
            rootEl.setAttribute("xmlns:csw", "http://www.opengis.net/cat/csw/2.0.2");
            rootEl.setAttribute("xmlns", "http://www.opengis.net/cat/csw/2.0.2");
            rootEl.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            rootEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

            message.appendChild(rootEl);

            sourceEl = message.createElement("Source");
            sourceEl.setTextContent(source);
            rootEl.appendChild(sourceEl);

            resourceTypeEl = message.createElement("ResourceType");
            resourceTypeEl.setTextContent(req.getParameter("resourceType"));
            rootEl.appendChild(resourceTypeEl);

            soapMessage = Util.addSOAPElements(message);

            File tmpFile;
            tmpFile = IOUtil.getTemporaryFile();
            DOMUtil.dumpXML(soapMessage, tmpFile);

            service.processRequest("http://www.opengis.net/cat/csw/2.0.2/requests#Harvest", util.fileToDocument(tmpFile), false);

            // clean the HARVEST directory
            if(harvest.exists())
                harvest.delete();

            resp.sendRedirect("ebrrHarvestFromDisk_showresult.jsp");
        } catch (Exception ex) {
            
            if(harvest.exists())
                harvest.delete();

            resp.sendRedirect("ebrrHarvestFromDisk_showresult.jsp?error="+resp.encodeRedirectURL("An error occurred while harvesting data from disk"));
        }
    }
}
