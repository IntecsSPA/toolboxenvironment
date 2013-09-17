/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxNetwork;
import it.intecs.pisa.toolbox.constants.MiscConstants;
import it.intecs.pisa.toolbox.constants.ToolboxFoldersFileConstants;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 * @author Andrea Manrongiu
 */
public class HarvestFromFileGUICommand extends NativeCommandsManagerPlugin {

    private static String ID_PARAMETER="id";
    private static String SERVICE_NAME="serviceName";

    private static final String RESOURCE_TYPE_NAME = "EarthObservation";
    private static final String HARVEST_SOAP_ACTION = "http://www.opengis.net/cat/csw/2.0.2/requests#Harvest";
    private static final String CSW_NAMESPACE = "http://www.opengis.net/cat/csw/2.0.2";
    private static final String XML_SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private static final String XML_SCHEMA_INSTANCE_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";
    private static final String CSW_VERSION = "2.0.2";
    private static final String CSW_SERVICE_NAME = "CSW";

    private static final String HARVEST_ELEMENT_NAME = "Harvest";
    private static final String SOURCE_ELEMENT_NAME = "Source";
    private static final String RESOURCE_TYPE_ELEMENT_NAME = "ResourceType";
    private static final String SERVICE_EXCEPTION_ELEMENT_NAME = "ServiceExceptionReport";
    private static String FILES_STORED_FOLDER_PATH="../GUIManagerPlugin/resources/storedData/";

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        

        DOMUtil domutil = new DOMUtil();
        ServiceManager serviceManager=ServiceManager.getInstance();
        String responseMessage="";
        String javascriptResponse="";
        Document harvestDoc=null;
        String servicename = req.getParameter(SERVICE_NAME)/*getStringFromMimeParts(mimeparts, "serviceName")*/;
        String metadataFileID=req.getParameter(ID_PARAMETER);
        logger = serviceManager.getService(servicename).getLogger();
        ZipFile zipFile=null;
        boolean harvestResult=false;
        boolean list=true;
        int countMetadata=0;
        int listSize=0;

        File harvestData= new File(pluginDir, FILES_STORED_FOLDER_PATH+ metadataFileID);/*File.createTempFile(java.util.UUID.randomUUID().toString(), "dat");
        IOUtil.copy(harvestFile.getInputStream(), new FileOutputStream(harvestData));*/
        try {
             zipFile = new ZipFile(harvestData);
        } catch (ZipException exZip) {
            /*Single Metadata*/
                list=false;
                listSize=1;
                harvestDoc = domutil.inputStreamToDocument(new FileInputStream(harvestData));
                try {
                    harvestResult=this.harvestDocument(servicename, harvestDoc);

                } catch (Exception ex) {
                    harvestResult=false;
                }
                 if(harvestResult)
                   countMetadata++;
        }
        
        /*Metadata List*/
        
        if(list){
            ZipEntry entry= null;
            Enumeration entries=zipFile.entries();
            while(entries.hasMoreElements()) {
                  listSize++;
                  entry = (ZipEntry)entries.nextElement();
                  harvestDoc = domutil.inputStreamToDocument(zipFile.getInputStream(entry));
                  try {
                        harvestResult=this.harvestDocument(servicename, harvestDoc);
                  } catch (Exception ex) {
                    harvestResult=false;
                  }
                  if(harvestResult){
                     countMetadata++;
                     logger.debug(entry.getName()+" harvested");
                  } else {
                    logger.error("Unable to harvest the following file: " + entry.getName());
                  }
            }
            zipFile.close();
        }
        OutputStream out=resp.getOutputStream();
        if(countMetadata==0){
            javascriptResponse=resp.encodeRedirectURL("An error occurred while harvesting data from disk.");
            responseMessage="{error : ";
        } else{
            javascriptResponse=resp.encodeRedirectURL("<br> Number of Metadata to harvest: "+listSize+
                              " <br> Number of Metadata harvested: " + countMetadata);
            responseMessage="{info : ";
        } 
        if(countMetadata<listSize)
            javascriptResponse+=" <br> See the service Log.";

        responseMessage+="\""+javascriptResponse+"\", serviceName :\""+servicename+"\"}";
        out.write(responseMessage.getBytes());
        out.close();
    }


    private boolean harvestDocument(String serviceName, Document harvestDocument) throws Exception{
        DOMUtil util=null;
        TBXService service;
        Document message;
        Document soapMessage;
        Element rootEl;
        Element sourceEl;
        Element resourceTypeEl;
        ServiceManager serviceManager;
        Toolbox toolboxServlet = Toolbox.getInstance();


        String filename = java.util.UUID.randomUUID().toString() + ".xml";
        String metadateNamespace=harvestDocument.getDocumentElement().getNamespaceURI();
        String resourceTypePrefix=metadateNamespace.substring(metadateNamespace.lastIndexOf("/")+1);
        File harvestDir = new File(toolboxServlet.getRootDir(), ToolboxFoldersFileConstants.HARVEST);
        File harvest = new File(harvestDir, filename);
        DOMUtil.dumpXML(harvestDocument, harvest);
        util = new DOMUtil();
        serviceManager = ServiceManager.getInstance();
        service = serviceManager.getService(serviceName);
        message = util.newDocument();

        String source = ToolboxNetwork.getEndpointURL()+ MiscConstants.SLASH+ToolboxFoldersFileConstants.HARVEST+MiscConstants.SLASH+filename;

        rootEl = message.createElement(HARVEST_ELEMENT_NAME);
        rootEl.setAttribute("service", CSW_SERVICE_NAME);
        rootEl.setAttribute("version", CSW_VERSION);
        rootEl.setAttribute("xmlns:csw", CSW_NAMESPACE);
        rootEl.setAttribute("xmlns", CSW_NAMESPACE);
        rootEl.setAttribute("xmlns:xsi", XML_SCHEMA_INSTANCE_NAMESPACE);
        rootEl.setAttribute("xmlns:xsd", XML_SCHEMA_NAMESPACE);
        message.appendChild(rootEl);

        sourceEl = message.createElement(SOURCE_ELEMENT_NAME);
        sourceEl.setAttribute("xmlns", CSW_NAMESPACE);
        sourceEl.setTextContent(source);
        rootEl.appendChild(sourceEl);

        resourceTypeEl = message.createElement(RESOURCE_TYPE_ELEMENT_NAME);
        resourceTypeEl.setTextContent(resourceTypePrefix+":"+RESOURCE_TYPE_NAME);
        resourceTypeEl.setAttribute("xmlns", CSW_NAMESPACE);
        rootEl.appendChild(resourceTypeEl);

        soapMessage = Util.addSOAPElements(message);

        File tmpFile;
        tmpFile = IOUtil.getTemporaryFile();
        DOMUtil.dumpXML(soapMessage, tmpFile);
        Document harvestResponse=service.processRequest(HARVEST_SOAP_ACTION, util.fileToDocument(tmpFile), false);


         // clean the HARVEST directory
         if(harvest.exists())
            harvest.delete();

        if(harvestResponse.getDocumentElement().getLocalName().equalsIgnoreCase(SERVICE_EXCEPTION_ELEMENT_NAME))
            return false;
        else
           return true;
    }
}
