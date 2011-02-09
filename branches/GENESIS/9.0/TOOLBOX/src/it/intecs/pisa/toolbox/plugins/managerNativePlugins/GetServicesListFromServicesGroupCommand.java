
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.toolbox.constants.ServiceConstants;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.constants.OperationConstants;
import it.intecs.pisa.toolbox.service.ServiceManager;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class GetServicesListFromServicesGroupCommand extends NativeCommandsManagerPlugin {

    private static String ID_PARAMETER="id";
    private static String FILES_STORED_FOLDER_PATH="../GUIManagerPlugin/resources/storedData/";

    
    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ZipEntry entry= null;
        DOMUtil util = null;
        Document doc ;
        Element root;
        File tempFile=null;
        Enumeration entries;
        Service currService = null;
        String entryFileName;
        List serviceList = new ArrayList();

        String storedZipFileID=req.getParameter(ID_PARAMETER);
        ZipFile zipFile = new ZipFile(new File(pluginDir, FILES_STORED_FOLDER_PATH+ storedZipFileID));
        entry=zipFile.getEntry(ServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME);

         util= new DOMUtil();
        doc = util.newDocument();
       
        root = doc.createElement("serviceList");
        doc.appendChild(root);
        if(entry !=null){
            tempFile= File.createTempFile(storedZipFileID+
                           ServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME, "xml");
            IOUtil.copy(zipFile.getInputStream(entry), new FileOutputStream(tempFile));
            currService.loadFromFile(tempFile);
            addServiceToServiceList(doc, currService.getServiceName(), root);
        }else{
            entries=zipFile.entries();

            while(entries.hasMoreElements()){
                entry = (ZipEntry)entries.nextElement();
                entryFileName=entry.getName().substring(0,entry.getName().indexOf("."));
                serviceList.add(entryFileName);
            }
            Collections.sort(serviceList, String.CASE_INSENSITIVE_ORDER);
            ListIterator itr = serviceList.listIterator();
            while(itr.hasNext())
               addServiceToServiceList(doc, (String)itr.next(), root);
        }

       resp.setStatus(resp.SC_OK);
       sendXMLAsResponse(resp, doc);
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

       

    }

    protected void addOperationToServiceList(Element serviceTag, Operation operation) throws Exception {
        Element operationTag;
        Document doc;

        doc = serviceTag.getOwnerDocument();

        operationTag = doc.createElement("operation");
        operationTag.setAttribute("name", operation.getName());
        operationTag.setAttribute("soapAction", operation.getSoapAction());

        if (operation.getType().equals(OperationConstants.ASYNCHRONOUS)) {
            operationTag.setAttribute("callbackSoapAction", operation.getCallbackSoapAction());
        }

        serviceTag.appendChild(operationTag);

    }

}
