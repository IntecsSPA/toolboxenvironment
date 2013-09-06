package it.intecs.pisa.toolbox.plugins.wpsPlugin.manager;

import it.intecs.pisa.util.DOMUtil;
import java.net.URLEncoder;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSCommandResponse {


    private static String CREATE_SERVICE_TYPE="createService";
    private static String ROOT_CREATE_SERVICE="createWPSResult";
    private static String ELEMENT_WPS_SERVICE_NAME="wpsServiceName";
    private static String ELEMENT_SCHEMA_FOLDER="schemaProcessingFolderCreated";
    private static String ELEMENT_EXTERNAL_INFO="ExternalInformationFileCreated";

    private static String GENERATE_OPERATION_TYPE="generateOperation";
    private static String ROOT_GENERATE_OPERATION="createWPSOperationResult";
    
    private static String PARSE_DESCRIBE_TYPE="parseDescribe";
    private static String ROOT_PARSE_DESCRIBE_OPERATION="parseWPSDescribrProcessingResult";
    private static String ELEMENT_ERROR_VALIDATION="ErrorValidation";
    private static String ELEMENT_SERVICE_NAME="ServiceName";
    private static String ELEMENT_PROCESSING_NAME="ProcessingName";
    private static String ELEMENT_PROCESSING_VERSION="ProcessingVersion";
    private static String ELEMENT_PROCESSING_TITLE="ProcessingTitle";
    private static String ELEMENT_ASYNC_INFO="Asynchronous";
    public static String DELETE_PROCESS_TYPE="deleteOperation";
    private static String ROOT_DELETE_PROCESS_OPERATION="deleteWPSProcessingResult";

    private Document documentResponse=null;
    private String operation=null;
    private DOMUtil domUtil=null;


    public WPSCommandResponse(String responseType){
        domUtil=new DOMUtil();
        operation=responseType;
        setResponseDocument();
    }
    
    
    private void setResponseDocument(){
        if(operation.equalsIgnoreCase(GENERATE_OPERATION_TYPE)){
           documentResponse=domUtil.newDocument();
           Element root=getDocumentResponse().createElement(ROOT_GENERATE_OPERATION);
            documentResponse.appendChild(root);
        }else
          if(operation.equalsIgnoreCase(PARSE_DESCRIBE_TYPE)){
           documentResponse=domUtil.newDocument();
           Element root=getDocumentResponse().createElement(ROOT_PARSE_DESCRIBE_OPERATION);
            documentResponse.appendChild(root);
          }else
             if(operation.equalsIgnoreCase(CREATE_SERVICE_TYPE)){
                documentResponse=domUtil.newDocument();
                Element root=getDocumentResponse().createElement(ROOT_CREATE_SERVICE);
                documentResponse.appendChild(root);
             }else
                if(operation.equalsIgnoreCase(DELETE_PROCESS_TYPE)){
                documentResponse=domUtil.newDocument();
                Element root=getDocumentResponse().createElement(ROOT_DELETE_PROCESS_OPERATION);
                documentResponse.appendChild(root);
             }
    }

    public void insertErrorValidation (String error){
        Element errorValidation=documentResponse.createElement(ELEMENT_ERROR_VALIDATION);
        errorValidation.setTextContent(error);
        documentResponse.getDocumentElement().appendChild(errorValidation);
    }

    public void insertOperationResult (boolean result){
      documentResponse.getDocumentElement().setTextContent(""+result);
    }

    public void insertOperationInformation (String serviceName, 
            String operationName, 
            String operationTitle, 
            String operationVersion, 
            boolean async){
        Element infoElement=documentResponse.createElement(ELEMENT_SERVICE_NAME);
        infoElement.setTextContent(serviceName);
        documentResponse.getDocumentElement().appendChild(infoElement);
        
        infoElement=documentResponse.createElement(ELEMENT_PROCESSING_NAME);
        infoElement.setTextContent(operationName);
        documentResponse.getDocumentElement().appendChild(infoElement);
        
        infoElement=documentResponse.createElement(ELEMENT_PROCESSING_TITLE);
        infoElement.setTextContent(operationTitle);
        documentResponse.getDocumentElement().appendChild(infoElement);
        
        infoElement=documentResponse.createElement(ELEMENT_PROCESSING_VERSION);
        infoElement.setTextContent(operationVersion);
        documentResponse.getDocumentElement().appendChild(infoElement);
        
        infoElement=documentResponse.createElement(ELEMENT_ASYNC_INFO);
        infoElement.setTextContent(""+async);
        documentResponse.getDocumentElement().appendChild(infoElement);
    }

    public void insertServiceInformation (String serviceName, String schemaFolder){
        Element infoElement=documentResponse.createElement(ELEMENT_WPS_SERVICE_NAME);
        infoElement.setTextContent(serviceName);
        documentResponse.getDocumentElement().appendChild(infoElement);
        infoElement=documentResponse.createElement(ELEMENT_SCHEMA_FOLDER);
        infoElement.setTextContent(schemaFolder);
        documentResponse.getDocumentElement().appendChild(infoElement);
    }

    public void insertExternalInformationStatus (boolean status){
        Element externalInfoStatus=documentResponse.createElement(ELEMENT_EXTERNAL_INFO);
        externalInfoStatus.setTextContent(""+status);
        documentResponse.getDocumentElement().appendChild(externalInfoStatus);
    }

    public void insertCDATATemplate (String engineName, String template){
        Element templateElement=documentResponse.createElement(engineName+"Template");
        CDATASection cdataTemplate = documentResponse.createCDATASection(template);
        templateElement.appendChild(cdataTemplate);
        documentResponse.getDocumentElement().appendChild(templateElement);
    }


    public void insertTemplateFileURL (String engineName, String url){
        Element templateElement=documentResponse.createElement(engineName+"Template");
        templateElement.setTextContent(url);
        documentResponse.getDocumentElement().appendChild(templateElement);
    }

    /**
     * @return the documentResponse
     */
    public Document getDocumentResponse() {
        return documentResponse;
    }



}
