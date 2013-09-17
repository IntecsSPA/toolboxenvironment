package it.intecs.pisa.toolbox.plugins.wpsPlugin.manager;


import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.engine.WPSEngine;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperation;
import it.intecs.pisa.toolbox.service.TBXSOAPInterface;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSCommands extends WPSUtil{
    

    /*CREATE WPS SERVICE VARIABLES*/
    private static String CREATE_SERVICE_OP="createService";
    private static String SERVICE_NAME_TAG_NAME ="serviceName";
    private static String SERVICE_TEMPLATE_PATH ="WEB-INF/WPS/templateService/WPSSoap.zip";
    private static String SERVICE_SCHEMAS_PATH ="Schemas";
    
    private static String SERVICE_CREATE_EXTERNAL_INFORMATION_XSLT_PATH ="AdditionalResources/WPS/XSL/GenerateWPSExternalInformation.xsl";
    private static String SERVICE_EXTERNAL_INFORMATION_PATH ="AdditionalResources/ExternalServiceInformation.xml";
    private static String SERVICE_RESOURCES_PATH ="Resources";
    private static String CLIENT_INTERFACE_INFORMATION_PATH ="AdditionalResources/ClientInterfaceInformation.xml";
   // private static String ENGINE_PREFIX="WPS";

    

    /* PARSE IMPORTED DESCRIBE */
    private static String PROCESS_DESCRIPTION_ELEMENT="ProcessDescription";
    private static String IDENTIFIER_ELEMENT="Identifier";
    private static String OWS_NAMESPACE="http://www.opengis.net/ows/1.1";

    

    
    public WPSCommands (){
     
    }

    public Document createWPSService(Document serviceInformationDocument) throws Exception {
      String serviceName="";
      Document xslDocument,doc=null;
      WPSCommandResponse createResponse=new WPSCommandResponse(CREATE_SERVICE_OP);
      NodeList nl=serviceInformationDocument.getElementsByTagName(SERVICE_NAME_TAG_NAME);
      serviceName=DOMUtil.getTextFromNode((Element) nl.item(0));
      File wpsServiceTemplate=new File(Toolbox.getInstance().getRootDir(), SERVICE_TEMPLATE_PATH);
      ServiceManager serviceManager;
      serviceManager=ServiceManager.getInstance();
      try {
           // deployWPSTempateServiceWithZipPackage(wpsServiceTemplate,serviceName);
          serviceManager.deployService(wpsServiceTemplate, serviceName);
      } catch (Exception ex) {
         /*  ServiceManager serviceManager;
           serviceManager=ServiceManager.getInstance();
           Service service = serviceManager.getService(serviceName);
          if (service != null) {
              serviceManager.deleteService(serviceName);
          }*/
      }

      File newServicePath=tbxServlet.getServiceRoot(serviceName);
      boolean newFolderSchemaProcessingCreated=new File (newServicePath, SERVICE_SCHEMAS_PATH+"/"+serviceName).mkdir();
      createResponse.insertServiceInformation(serviceName, ""+newFolderSchemaProcessingCreated);

      IOUtil.copy(DOMUtil.getDocumentAsInputStream(serviceInformationDocument),
                new FileOutputStream(new File(newServicePath,CLIENT_INTERFACE_INFORMATION_PATH)));

      File stylesheet=new File(newServicePath,SERVICE_CREATE_EXTERNAL_INFORMATION_XSLT_PATH);
      if(stylesheet.exists()){
           xslDocument=domUtil.fileToDocument(stylesheet);
           transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
           doc=domUtil.newDocument();
           transformer.transform(new StreamSource(new FileInputStream(new File(newServicePath,CLIENT_INTERFACE_INFORMATION_PATH))),
                                new DOMResult(doc));
           IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), new FileOutputStream(new File(newServicePath,SERVICE_EXTERNAL_INFORMATION_PATH)));
           createResponse.insertExternalInformationStatus(true);
        }
      return createResponse.getDocumentResponse();
    }

    public void importWPSService(String serviceName) throws Exception {

      Document xslDocument,doc=null;
      File wpsServiceTemplate=new File(Toolbox.getInstance().getRootDir(), SERVICE_TEMPLATE_PATH);
      ServiceManager serviceManager;
      serviceManager=ServiceManager.getInstance();
      try {
            //deployWPSTempateServiceWithZipPackage(wpsServiceTemplate,serviceName);
            serviceManager.deployService(wpsServiceTemplate, serviceName);
      } catch (Exception ex) {
            
           /* Service service = serviceManager.getService(serviceName);
          if (service != null) {
               serviceManager.deleteService(serviceName);
          }
          ex.printStackTrace(System.out);
          throw new Exception(ex.getMessage());*/
      }

      File newServicePath=tbxServlet.getServiceRoot(serviceName);
     // boolean newFolderSchemaProcessingCreated=new File (newServicePath, SERVICE_SCHEMAS_PATH+"/"+serviceName).mkdir();
      File stylesheet=new File(newServicePath,SERVICE_CREATE_EXTERNAL_INFORMATION_XSLT_PATH);
      if(stylesheet.exists()){
           xslDocument=domUtil.fileToDocument(stylesheet);
           transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
           doc=domUtil.newDocument();
           transformer.transform(new StreamSource(new FileInputStream(new File(newServicePath,CLIENT_INTERFACE_INFORMATION_PATH))),
                                new DOMResult(doc));
           IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), new FileOutputStream(new File(newServicePath,SERVICE_EXTERNAL_INFORMATION_PATH)));
        }
    }



    public void importWPSProcess(Document importDescribeDocument, File importServiceFolder ,String serviceName) throws Exception {
        WPSCommandResponse createResponse=new WPSCommandResponse(PARSE_DESCRIBE_OP);
        int i;
        String engineType="";
        File script=null;
        File newServicePath=tbxServlet.getServiceRoot(serviceName);
        String asynchronous=null, processingName=null;
        Element processDescription=(Element)(importDescribeDocument.getElementsByTagName(PROCESS_DESCRIPTION_ELEMENT).item(0));
        Element identifier=(Element) processDescription.getElementsByTagNameNS(OWS_NAMESPACE,IDENTIFIER_ELEMENT).item(0);
        asynchronous=processDescription.getAttribute(DESCRIBE_PROCESS_STORE_SUPPORTED_ATRRIBUTE);
        processingName=DOMUtil.getTextFromNode(identifier);
        createResponse.insertOperationInformation(serviceName, processingName, Boolean.valueOf(asynchronous));
        File describeFolder=new File(newServicePath, DESCRIBE_PROCESS_PATH);
        describeFolder.mkdirs();
        DOMUtil.dumpXML(importDescribeDocument, new File(describeFolder, DESCRIBE_FILE_PREFIX+processingName+".xml"));

        // Generate Engine outputManager Scripts
        List<WPSEngine> listEngins=getWPSEngines();
        for(i=0; i<listEngins.size(); i++){
           listEngins.get(i).generateEngineOutputManager(newServicePath, importDescribeDocument, processingName);
           if(new File(importServiceFolder, listEngins.get(i).getScriptPathforProcessingName(processingName)).exists()){
             engineType=listEngins.get(i).getEngineName()+"Engine";
             script=new File(importServiceFolder,listEngins.get(i).getOriginalScriptPathforProcessingName(processingName));
             if(!script.exists())
                 script=new File(importServiceFolder,listEngins.get(i).getScriptPathforProcessingName(processingName));
           }
         }

        // Generate Portal Interface Resources
        generatePortalInterfaceResources(newServicePath,processingName,importDescribeDocument);

        //Update Service WPS Soap Schema
        updateWPSSoapSchema(newServicePath,importDescribeDocument,serviceName,processingName);

        //Operation operationDescr=null;

        createWPSProcess(serviceName,processingName,Boolean.valueOf(asynchronous),engineType, new FileInputStream(script));
      
       
}

    public Document parseWPSDescribeProcess(Document describeDocument, String serviceName, File pluginDir) throws Exception {
        WPSCommandResponse createResponse=new WPSCommandResponse(PARSE_DESCRIBE_OP);
        boolean validate=true;
        int i;
        File newServicePath=tbxServlet.getServiceRoot(serviceName);
        String asynchronous=null, processingName=null;
        ServiceManager serviceManager=ServiceManager.getInstance();
        TBXService tbxService=serviceManager.getService(serviceName);
        try{
            ((TBXSOAPInterface)tbxService.getImplementedInterface()).validateDocument(describeDocument);
            //domUtil.validateDocument(new File(newServicePath,DESCRIBE_RESPONSE_SCHEMA_LOCATION), describeDocument);
        }catch(SAXException saxE){
                createResponse.insertErrorValidation(saxE.getMessage());
                validate=false;
        }
         if(validate){
            Element processDescription=DOMUtil.getElementByXPath(DESCRIBE_PROCESS_DESCRIPTION_XPATH, describeDocument);
            Element identifier=DOMUtil.getElementByXPath(DESCRIBE_PROCESS_DESCRIPTION_IDENTIFIER_XPATH, describeDocument);
            asynchronous=processDescription.getAttribute(DESCRIBE_PROCESS_STORE_SUPPORTED_ATRRIBUTE);
            processingName=DOMUtil.getTextFromNode(identifier);

            createResponse.insertOperationInformation(serviceName, processingName, Boolean.valueOf(asynchronous));
 
            Document describeDoc=domUtil.newDocument();
            Node describeElement=processDescription.cloneNode(true);
            describeDoc.adoptNode(describeElement);
            describeDoc.appendChild(describeElement);
            File describeFolder=new File(newServicePath, DESCRIBE_PROCESS_PATH);
            describeFolder.mkdirs();
            File infoFolder=new File(newServicePath, INFO_PROCESS_PATH);
            infoFolder.mkdirs();
            DOMUtil.dumpXML(describeDoc, new File(infoFolder, DESCRIBE_FILE_PREFIX+processingName+"temp.xml"));

            // Generate Engine Scripts Template and outputManager
            List<WPSEngine> listEngins=getWPSEngines();
            String template=null;
            File templateFile=null;
            String templateRequest="rest/wps/storedData/";
            String id;
            for(i=0; i<listEngins.size(); i++){
               template=listEngins.get(i).generateEngineTemplate(newServicePath, describeDocument, processingName);
               id = DateUtil.getCurrentDateAsUniqueId();
               templateFile = new File(pluginDir, "resources/storedData/" + id);
               IOUtil.copy(new ByteArrayInputStream(template.getBytes()), new FileOutputStream(templateFile));
               //createResponse.insertCDATATemplate(listEngins.get(i).getEngineName(), template);
               createResponse.insertTemplateFileURL(listEngins.get(i).getEngineName(), templateRequest+id);
               listEngins.get(i).generateEngineOutputManager(newServicePath, describeDocument, processingName);
            }

            // Generate Portal Interface Resources
            generatePortalInterfaceResources(newServicePath,processingName,describeDocument);

            
         }

     return createResponse.getDocumentResponse();
    

}
    public Document updateWPSDescribeProcess(Document describeDocument, String serviceName, String processingName, String engineType, boolean currentAsync, File pluginDir) throws Exception {
        Document parseResponse=parseWPSDescribeProcess(describeDocument,serviceName, pluginDir);
        String asynchronous="", newProcessingName="";
        NodeList errors=parseResponse.getElementsByTagName("ErrorValidation");

        if(errors.getLength()==0){
            File newServicePath=tbxServlet.getServiceRoot(serviceName);
            String processingNameAsync="ExecuteProcess_"+processingName+"_ASYNC";
            ServiceManager serviceManager;
            serviceManager=ServiceManager.getInstance();
            TBXService service = serviceManager.getService(serviceName);

            Class wpsEngineClass = Class.forName(ENGINE_CLASS_PACKAGE+ENGINE_PREFIX+engineType);
            WPSEngine wpsEngine = (WPSEngine) wpsEngineClass.newInstance();

            File originalScriptTemp=File.createTempFile(serviceName+"_"+processingName, "xml");

            File originalScript=new File(newServicePath, wpsEngine.getOriginalScriptPathforProcessingName(processingName));

            IOUtil.copy(new FileInputStream(originalScript),
                                        new FileOutputStream(originalScriptTemp));
               
            service.deleteOperation("ExecuteProcess_"+processingName);
            if(service.getOperation(processingNameAsync)!=null)
               service.deleteOperation(processingNameAsync);

            Element processDescription=DOMUtil.getElementByXPath(DESCRIBE_PROCESS_DESCRIPTION_XPATH, describeDocument);
            Element identifier=DOMUtil.getElementByXPath(DESCRIBE_PROCESS_DESCRIPTION_IDENTIFIER_XPATH, describeDocument);
            asynchronous=processDescription.getAttribute(DESCRIBE_PROCESS_STORE_SUPPORTED_ATRRIBUTE);
            newProcessingName=DOMUtil.getTextFromNode(identifier);

            createWPSProcess(serviceName, newProcessingName,
                    Boolean.valueOf(asynchronous), engineType, new FileInputStream(originalScriptTemp));
            
           /* 
               WPSCommandResponse updateCommandResponse=new WPSCommandResponse(PARSE_DESCRIBE_OP);
               updateCommandResponse.insertErrorValidation("It is not allowed modify the process identifier");
               updateResponse=updateCommandResponse.getDocumentResponse();*/
        }
        
        return parseResponse;
    }

    public Document createWPSProcess(String serviceName, String processingName, boolean async, String engineType, InputStream script) throws Exception{
       File newServicePath=tbxServlet.getServiceRoot(serviceName);
       
       File originalScript=new File(newServicePath,SERVICE_RESOURCES_PATH+"/execute_"+processingName+"_script");
       IOUtil.copy(script, new FileOutputStream(originalScript));
       File describeFolder=new File(newServicePath, DESCRIBE_PROCESS_PATH);
       File infoFolder=new File(newServicePath, INFO_PROCESS_PATH);
       infoFolder.mkdirs();
       describeFolder.mkdirs();
       File tempDescribe=new File(infoFolder, DESCRIBE_FILE_PREFIX+processingName+"temp.xml");
       if(tempDescribe.exists()){
         File describe=new File(describeFolder, DESCRIBE_FILE_PREFIX+processingName+".xml");
         IOUtil.copy(new FileInputStream(tempDescribe), new FileOutputStream(describe));
         //Update Service WPS Soap Schema
         Document describeDocument=domUtil.fileToDocument(describe);
         updateWPSSoapSchema(newServicePath,describeDocument,serviceName,processingName);
         tempDescribe.delete();
       }


       createWPSProcessingInformationDocument(infoFolder, serviceName, processingName, engineType, async);
       WPSCommandResponse createResponse=new WPSCommandResponse(CREATE_PROCESS_OP);
       Class wpsEngineClass = Class.forName(ENGINE_CLASS_PACKAGE+ENGINE_PREFIX+engineType);
       WPSEngine wpsEngine = (WPSEngine) wpsEngineClass.newInstance();
       wpsEngine.setScriptEngine(new FileInputStream(originalScript));

       TBXSynchronousOperation operationDescr=wpsEngine.createWPSSyncOperation(newServicePath, processingName);
       ServiceManager serviceManager=ServiceManager.getInstance();
       TBXService service = serviceManager.getService(serviceName);
       service.addOperation(operationDescr);


       operationDescr.dumpOperationScripts();

       if(async){
           // Create Engine Asynchoronus Operation
           TBXAsynchronousOperation operationAsyncDescr=wpsEngine.createWPSAsyncOperation(newServicePath,processingName);
           service.addOperation(operationAsyncDescr);
           operationAsyncDescr.dumpOperationScripts();
           
        }
       
       service.dumpService();
       createResponse.insertOperationResult(true);

       return createResponse.getDocumentResponse();
    }

    public Document removeWPSProcess(String serviceName, String processingName, boolean async, String engineType) throws Exception{
       WPSCommandResponse createResponse=new WPSCommandResponse(DELETE_PROCESS_OP);
       ServiceManager serviceManager;
       serviceManager=ServiceManager.getInstance();
       TBXService service = serviceManager.getService(serviceName);
       service.deleteOperation("ExecuteProcess_"+processingName);
       if(async)
           service.deleteOperation("ExecuteProcess_"+processingName+"_ASYNC");
          
       File newServicePath=tbxServlet.getServiceRoot(serviceName);
       File describeFolder=new File(newServicePath, DESCRIBE_PROCESS_PATH);

       deleteWPSProcessingEngineResources(newServicePath, processingName, engineType);
       deleteWPSProcessingDescribeResources(describeFolder,serviceName,processingName);
       createResponse.insertOperationResult(true);

       return createResponse.getDocumentResponse();
    }

    public void deleteWPSProcessingResources(String serviceName, String processingName){
        File newServicePath=tbxServlet.getServiceRoot(serviceName);
        File describeFolder=new File(newServicePath, DESCRIBE_PROCESS_PATH);
        deleteWPSProcessingInformationDocument(describeFolder, serviceName, processingName);
        

   }


    public Document updateWPSProcessEngineScript(String serviceName, String processingName, boolean async, String engineType, InputStream script) throws Exception{
       File newServicePath=tbxServlet.getServiceRoot(serviceName);
       File originalScript=new File(newServicePath,SERVICE_RESOURCES_PATH+"/execute_"+processingName+"_script");
       IOUtil.copy(script, new FileOutputStream(originalScript));
       /*File describeFolder=new File(newServicePath, DESCRIBE_PROCESS_PATH);
       describeFolder.mkdirs();*/
       File infoFolder=new File(newServicePath, INFO_PROCESS_PATH);
       infoFolder.mkdirs();
       createWPSProcessingInformationDocument(infoFolder, serviceName, processingName, engineType, async);
       WPSCommandResponse createResponse=new WPSCommandResponse(CREATE_PROCESS_OP);
       Class wpsEngineClass = Class.forName(ENGINE_CLASS_PACKAGE+ENGINE_PREFIX+engineType);
       WPSEngine wpsEngine = (WPSEngine) wpsEngineClass.newInstance();
       //wpsEngine.setScriptEngine(new FileInputStream(originalScript));
       wpsEngine.updateScriptEngine(newServicePath, processingName,new FileInputStream(originalScript));
       createResponse.insertOperationResult(true);
       return createResponse.getDocumentResponse();
    }



    public static void updateWPSFileAfterClone(File serviceClonedFolder, String oldNameService, String newNameService) throws SAXException, Exception{
       DOMUtil du=new DOMUtil();
       String clientString, serviceString;
       File clientFile=new File(serviceClonedFolder, CLIENT_INTERFACE_INFORMATION_PATH);
       File serviceFile=new File(serviceClonedFolder, SERVICE_EXTERNAL_INFORMATION_PATH);

       clientString=DOMUtil.getDocumentAsString(du.fileToDocument(clientFile));
       serviceString=DOMUtil.getDocumentAsString(du.fileToDocument(serviceFile));

       clientString=clientString.replaceAll(oldNameService, newNameService);
       serviceString=serviceString.replaceAll(oldNameService, newNameService);

       IOUtil.copy(new ByteArrayInputStream(clientString.getBytes()), new FileOutputStream(clientFile));
       IOUtil.copy(new ByteArrayInputStream(serviceString.getBytes()), new FileOutputStream(serviceFile));

    }
}
