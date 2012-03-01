package it.intecs.pisa.toolbox.plugins.gatewayPlugin.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.wsdl.extensions.soap.SOAPAddressImpl;
import com.ibm.wsdl.extensions.soap.SOAPOperationImpl;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XMLSchemaUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

/**
 *
 * @author Andrea Marongiu
 */
public class GatewayCommands {

    // Json request Properties
    private static String WSDL_URL_PROPERTY = "wsdl";
    private static String SSL_CERTIFICATE_PROPERTY = "sslCertificate";
    private static String FORWARD_MESSAGE_CRYPTED_TOKEN_PROPERTY = "forwardMessageWithCryptedToken";
    private static String FORWARD_MESSAGE_CLEAR_TOKEN_PROPERTY = "forwardMessageWithClearToken";
    private static String KEY_ALIAS_PROPERTY = "keyAlias";
    private static String JKS_PASSWORD_PROPERTY = "jksPasswd";
    private static String JKS_USER_PROPERTY = "jksUserName";
    private static String JKS_FILE_LOCATION_PROPERTY = "jksFileLocation";
    private static String XACML_FILE_LOCATION_PROPERTY = "xacmlFileLocation";
    private static String OPERATION_INFO_PROPERTY = "operations";
    private static String SERVICE_OPERATION_PROPERTY = "service";
    private static String PORT_OPERATION_PROPERTY = "port";
    private static String BINDING_OPERATION_PROPERTY = "binding";
    private static String ADDRESS_OPERATION_PROPERTY = "address";
    private static String OPERATION_PROPERTY = "operation";
    private static String SOAP_OPERATION_PROPERTY = "soapOperation";
    private static String OPERATION_OUTPUT_TYPE_PROPERTY = "outputType";
    private static String OPERATION_OUTPUT_TYPE_NAMESPACE_PROPERTY = "outputTypeNamespace";
    private static String OPERATION_INPUT_TYPE_PROPERTY = "inputType";
    private static String OPERATION_INPUT_TYPE_NAMESPACE_PROPERTY = "inputTypeNamespace";
    private static String SERVICE_NAME_PROPERTY = "service";
    private static String SERVICE_TEMPLATE_PATH = "WEB-INF/plugins/GatewayPlugin/templateService/Gateway.zip";
    private static String SERVICE_SCHEMAS_PATH = "Schemas";
    static final String GATEWAY_OPERATIONS_FOLDER = "Operations";
    static final String GATEWAY_SYNC_RESOURCES_FOLDER = "Resources/Common Scripts/Synchronous";
    static final String FIRST_SCRIPT_FILE_NAME = "firstScript.tscript";
    static final String SECOND_SCRIPT_FILE_NAME = "secondScript.tscript";
    static final String THIRD_SCRIPT_FILE_NAME = "thirdScript.tscript";
    static final String RESPONSE_BUILDER_SCRIPT_FILE_NAME = "respBuilder.tscript";
    static final String RESPONSE_BUILDER_ERROR_SCRIPT_FILE_NAME = "errorOnRespBuilder.tscript";
    static final String GLOBAL_ERROR_SCRIPT_FILE_NAME = "globalError.tscript";

    public GatewayCommands() {
    }

    public JsonObject createorUpdateGatewayService(String serviceName, JsonObject serviceInformationJson) {
        String wsdlURL = "", sslCertificate = "";
        String jksPasswd = "", jksUser = "", jksFileLocation = "";
        String errorDetails = "", xacmlFileLocation = "";
        String forwardMessageWithClearToken = "";
        String forwardMessageWithCryptedToken = "", keyAlias = "";
        RestResponse createGatewayResponse = new RestResponse("createGatewayService");

        JsonElement wsdlURLEl = serviceInformationJson.get(WSDL_URL_PROPERTY);
        if (!(wsdlURLEl == null || wsdlURLEl instanceof com.google.gson.JsonNull)) {
            wsdlURL = wsdlURLEl.getAsString();
        } else {
            return updateGatewayService(serviceName, serviceInformationJson);
        }
        //errorDetails+="WSDL URL ("+WSDL_URL_PROPERTY+") mandatory property missing. "; 


        JsonElement sslCertificateEl = serviceInformationJson.get(SSL_CERTIFICATE_PROPERTY);
        if (!(sslCertificateEl == null || sslCertificateEl instanceof com.google.gson.JsonNull)) {
            sslCertificate = sslCertificateEl.getAsString();
        }

        JsonElement xacmlFileLocationEl = serviceInformationJson.get(XACML_FILE_LOCATION_PROPERTY);
        if (!(xacmlFileLocationEl == null || xacmlFileLocationEl instanceof com.google.gson.JsonNull)) {
            xacmlFileLocation = ((JsonObject) xacmlFileLocationEl).get("uploadID").getAsString();
        }

        JsonElement forwardMessageWithClearTokenEl = serviceInformationJson.get(FORWARD_MESSAGE_CLEAR_TOKEN_PROPERTY);
        if (!(forwardMessageWithClearTokenEl == null || forwardMessageWithClearTokenEl instanceof com.google.gson.JsonNull)) {
            forwardMessageWithClearToken = forwardMessageWithClearTokenEl.getAsString();
        }

        JsonElement forwardMessageWithCryptedTokenEl = serviceInformationJson.get(FORWARD_MESSAGE_CRYPTED_TOKEN_PROPERTY);
        if (!(forwardMessageWithCryptedTokenEl == null || forwardMessageWithCryptedTokenEl instanceof com.google.gson.JsonNull)) {
            forwardMessageWithCryptedToken = forwardMessageWithCryptedTokenEl.getAsString();
        }

        JsonElement keyAliasEl = serviceInformationJson.get(KEY_ALIAS_PROPERTY);
        if (!(keyAliasEl == null || keyAliasEl instanceof com.google.gson.JsonNull)) {
            keyAlias = keyAliasEl.getAsString();
        }

        JsonElement jksFileLocationEl = serviceInformationJson.get(JKS_FILE_LOCATION_PROPERTY);
        if (!(jksFileLocationEl == null || jksFileLocationEl instanceof com.google.gson.JsonNull)) {
            jksFileLocation = ((JsonObject) jksFileLocationEl).get("uploadID").getAsString();
        } else {
            errorDetails += "JKS FILE LOCATION (" + JKS_FILE_LOCATION_PROPERTY + ") mandatory property missing. ";
        }


        JsonElement jksPasswdEl = serviceInformationJson.get(JKS_PASSWORD_PROPERTY);
        if (!(jksPasswdEl == null || jksPasswdEl instanceof com.google.gson.JsonNull)) {
            jksPasswd = jksPasswdEl.getAsString();
        } else {
            errorDetails += "JKS PASSWORD (" + JKS_PASSWORD_PROPERTY + ") mandatory property missing. ";
        }


        JsonElement jksUserEl = serviceInformationJson.get(JKS_USER_PROPERTY);
        if (!(jksUserEl == null || jksUserEl instanceof com.google.gson.JsonNull)) {
            jksUser = jksUserEl.getAsString();
        } else {
            errorDetails += "JKS USER (" + JKS_USER_PROPERTY + ") mandatory property missing. ";
        }

        if (!errorDetails.equals("")) {
            createGatewayResponse.setSuccess(false);
            createGatewayResponse.setDetails(errorDetails);
            return createGatewayResponse.getJsonRestResponse();
        }

        File gatewayServiceTemplate = new File(Toolbox.getInstance().getRootDir(), SERVICE_TEMPLATE_PATH);
        ServiceManager serviceManager;
        serviceManager = ServiceManager.getInstance();

        try {
            serviceManager.deployService(gatewayServiceTemplate, serviceName);
        } catch (Exception ex) {
            ex.printStackTrace();
            createGatewayResponse.setSuccess(false);
            createGatewayResponse.setDetails(ex.getMessage());
            return createGatewayResponse.getJsonRestResponse();
        }

        try {
            TBXService service = serviceManager.getService(serviceName);
            File serviceRoot = Toolbox.getInstance().getServiceRoot(service.getServiceName());
            service.setSSLcertificate(sslCertificate);
            service.setWSSecurity(true);
            service.setJKSuser(jksUser);
            service.setJKSpasswd(jksPasswd);
            //TODO key password currently  is the same as the keystore one
            service.setKeyPasswd(jksPasswd);

            service.setJKSlocation(serviceRoot + File.separator + "Resources" + File.separator + "Security" + File.separator + "service.jks");

            File jksFile = new File(serviceRoot + File.separator + "Resources" + File.separator + "Security" + File.separator + "service.jks");
            jksFile.getParentFile().mkdirs();
            jksFile.createNewFile();
            IOUtil.copy(new FileInputStream(jksFileLocation), new FileOutputStream(jksFile));

            Hashtable<String, Hashtable<String, String>> serviceVariables =
                    new Hashtable<String, Hashtable<String, String>>();
            Hashtable<String, String> variable = new Hashtable<String, String>();
            variable.put("value", forwardMessageWithClearToken);
            variable.put("type", "boolean");
            variable.put("displayedText", "Forward message with security token unencrypted");
            serviceVariables.put("forwardMessageWithClearToken", variable);

            variable = new Hashtable<String, String>();
            variable.put("value", forwardMessageWithCryptedToken);
            variable.put("type", "boolean");
            variable.put("displayedText", "Forward message with security token encrypted (this will override all forwarding options)");
            serviceVariables.put("forwardMessageWithCryptedToken", variable);

            variable = new Hashtable<String, String>();
            variable.put("value", keyAlias);
            variable.put("type", "string");
            variable.put("displayedText", "Alias of the key to be used for encryption");
            serviceVariables.put("keyAlias", variable);

            service.getImplementedInterface().setUserVariables(serviceVariables);

            ToolboxSecurityConfigurator.addWSSecurityLayerForService(service);

            //update WSDL
            service.deployWSDL();
            ToolboxSecurityConfigurator.removeXACMLfiles(service.getServiceName());

            if (!xacmlFileLocation.equalsIgnoreCase("")) {
                String xacmlName = new File(xacmlFileLocation).getName();
                if (!xacmlName.endsWith(".xml")) {
                    xacmlName += ".xml";
                }
                File xacmlFile = new File(ToolboxSecurityConfigurator.getXACMLpolicyDir(service.getServiceName()) + File.separator + xacmlName);
                xacmlFile.getParentFile().mkdirs();
                xacmlFile.createNewFile();
                IOUtil.copy(new FileInputStream(xacmlFileLocation), new FileOutputStream(xacmlFile));
                new File(xacmlFileLocation).delete();
            }

            if (!jksFileLocation.equalsIgnoreCase("")) {
                File securityFolder = ToolboxSecurityConfigurator.getSecurityResource(service);
                securityFolder.mkdirs();
                jksFile = new File(securityFolder, ToolboxSecurityConfigurator.SERVICE_KEYSTORE_FILENAME);
                jksFile.getParentFile().mkdirs();
                jksFile.createNewFile();
                IOUtil.copy(new FileInputStream(jksFileLocation), new FileOutputStream(jksFile));
            }
            new File(jksFileLocation).delete();
            service.dumpToDisk(service.getDescriptorFile());
            service.attemptToDeployWSDLAndSchemas();

            XMLSchemaUtil schemaUtil = new XMLSchemaUtil();
            File serviceFolder = new File(serviceManager.getServicesRootDir(), serviceName);
            File localSchemaFolder = new File(serviceFolder, SERVICE_SCHEMAS_PATH);
            localSchemaFolder.mkdirs();
            schemaUtil.saveWSDLSchemas(wsdlURL, localSchemaFolder);
            createGatewayResponse.addJsonElement(OPERATION_INFO_PROPERTY, getSoapOperationsInfoList(wsdlURL));
        } catch (Exception ex) {
            ex.printStackTrace();
            ServiceManager sm = ServiceManager.getInstance();
            createGatewayResponse.setSuccess(false);
            createGatewayResponse.setDetails(ex.getMessage());
            if (sm.isServiceDeployed(serviceName)) {
                try {
                    sm.deleteService(serviceName);
                } catch (Exception ex1) {
                    ex.printStackTrace();
                }
            }
            return createGatewayResponse.getJsonRestResponse();
        }

        createGatewayResponse.setSuccess(true);
        return createGatewayResponse.getJsonRestResponse();
    }

    public JsonObject updateGatewayService(String serviceName, JsonObject serviceInformationJson) {
        RestResponse updateGatewayResponse = new RestResponse("updateGatewayService");
        ServiceManager serviceManager;
        serviceManager = ServiceManager.getInstance();
        JsonElement jsEl = null;
        String fileLocation = null;

        ToolboxSecurityConfigurator.removeXACMLfiles(serviceName);

        try {
            TBXService service = serviceManager.getService(serviceName);
            jsEl = serviceInformationJson.get(SSL_CERTIFICATE_PROPERTY);
            if (!(jsEl == null || jsEl instanceof com.google.gson.JsonNull)) {
                service.setSSLcertificate(jsEl.getAsString());
            }

            jsEl = serviceInformationJson.get(JKS_USER_PROPERTY);
            if (!(jsEl == null || jsEl instanceof com.google.gson.JsonNull)) {
                service.setJKSuser(jsEl.getAsString());
            }

            jsEl = serviceInformationJson.get(JKS_PASSWORD_PROPERTY);
            if (!(jsEl == null || jsEl instanceof com.google.gson.JsonNull)) {
                service.setJKSpasswd(jsEl.getAsString());
                service.setKeyPasswd(jsEl.getAsString());
            }


            jsEl = serviceInformationJson.get(JKS_FILE_LOCATION_PROPERTY);
            if (!(jsEl == null || jsEl instanceof com.google.gson.JsonNull)) {
                fileLocation = ((JsonObject) jsEl).get("uploadID").getAsString();
                File jksFile;
                File securityFolder;

                securityFolder = ToolboxSecurityConfigurator.getSecurityResource(service);
                securityFolder.mkdirs();
                jksFile = new File(securityFolder, ToolboxSecurityConfigurator.SERVICE_KEYSTORE_FILENAME);
                jksFile.getParentFile().mkdirs();
                jksFile.createNewFile();
                IOUtil.copy(new FileInputStream(fileLocation), new FileOutputStream(jksFile));
                new File(fileLocation).delete();
                service.setJKSlocation(jksFile.getCanonicalPath());

                ToolboxSecurityConfigurator.addWSSecurityLayerForService(service);
                //update WSDL
                service.deployWSDL();
                ToolboxSecurityConfigurator.removeXACMLfiles(service.getServiceName());
            }

            jsEl = serviceInformationJson.get(XACML_FILE_LOCATION_PROPERTY);
            if (!(jsEl == null || jsEl instanceof com.google.gson.JsonNull)) {
                ToolboxSecurityConfigurator.removeXACMLfiles(service.getServiceName());
                String xacmlFileLocation = ((JsonObject) jsEl).get("uploadID").getAsString();
                String xacmlName = new File(xacmlFileLocation).getName();
                if (!xacmlName.endsWith(".xml")) {
                    xacmlName += ".xml";
                }
                File xacmlFile = new File(ToolboxSecurityConfigurator.getXACMLpolicyDir(service.getServiceName()) + File.separator + xacmlName);
                xacmlFile.getParentFile().mkdirs();
                xacmlFile.createNewFile();
                IOUtil.copy(new FileInputStream(xacmlFileLocation), new FileOutputStream(xacmlFile));
                new File(xacmlFileLocation).delete();
                /* fileLocation = ((JsonObject)jsEl).get("uploadID").getAsString();
                String xacmlName = new File(fileLocation).getName();
                File xacmlFile = new File(ToolboxSecurityConfigurator.getXACMLpolicyDir(service.getServiceName()) + File.separator + xacmlName);
                xacmlFile.getParentFile().mkdirs();
                xacmlFile.createNewFile();
                IOUtil.copy(new FileInputStream(fileLocation), new FileOutputStream(xacmlFile));
                new File(fileLocation).delete();*/
            }

            service.dumpToDisk(service.getDescriptorFile());
            service.attemptToDeployWSDLAndSchemas();


        } catch (Exception ex) {
            ex.printStackTrace();
            ServiceManager sm = ServiceManager.getInstance();
            updateGatewayResponse.setSuccess(false);
            updateGatewayResponse.setDetails(ex.getMessage());
            return updateGatewayResponse.getJsonRestResponse();
        }

        updateGatewayResponse.setSuccess(true);
        return updateGatewayResponse.getJsonRestResponse();
    }

    public JsonObject getGatewayServiceConfiguration(String serviceName) {
        RestResponse getConfigurationGatewayResponse = new RestResponse("getConfigurationGatewayService");
        ServiceManager serviceManager;
        serviceManager = ServiceManager.getInstance();
        JsonObject serviceConf = new JsonObject();
        Hashtable<String, Hashtable<String, String>> serviceVariables;

        try {
            TBXService service = serviceManager.getService(serviceName);
            serviceConf.addProperty(SSL_CERTIFICATE_PROPERTY, service.getSSLcertificate());

            serviceConf.addProperty(JKS_USER_PROPERTY, service.getJKSuser());
            serviceConf.addProperty(JKS_PASSWORD_PROPERTY, service.getJKSpasswd());
            serviceConf.addProperty(JKS_FILE_LOCATION_PROPERTY, service.getJKSlocation());
            //serviceConf.addProperty(XACML_FILE_LOCATION_PROPERTY,service.get);
            serviceConf.addProperty(SSL_CERTIFICATE_PROPERTY, service.getSSLcertificate());

            serviceVariables = service.getImplementedInterface().getUserVariable();
            Hashtable<String, String> serviceVariable = serviceVariables.get("remoteUrl");
            serviceConf.addProperty("remoteUrl", serviceVariable.get("value"));
            serviceVariable = serviceVariables.get("forwardMessageWithClearToken");
            serviceConf.addProperty("forwardMessageWithClearToken", serviceVariable.get("value"));
            serviceVariable = serviceVariables.get("forwardMessageWithCryptedToken");
            serviceConf.addProperty("forwardMessageWithCryptedToken", serviceVariable.get("value"));
            serviceVariable = serviceVariables.get("keyAlias");
            serviceConf.addProperty("keyAlias", serviceVariable.get("value"));

        } catch (Exception ex) {
            ex.printStackTrace();
            ServiceManager sm = ServiceManager.getInstance();
            getConfigurationGatewayResponse.setSuccess(false);
            getConfigurationGatewayResponse.setDetails(ex.getMessage());
            return getConfigurationGatewayResponse.getJsonRestResponse();
        }
        getConfigurationGatewayResponse.addJsonElement("serviceConfiguration", serviceConf);
        getConfigurationGatewayResponse.setSuccess(true);
        return getConfigurationGatewayResponse.getJsonRestResponse();
    }

    public JsonObject createGatewayOperation(String operationName, JsonObject operationInformationJson) {
        RestResponse createGatewayOperationResponse = new RestResponse("createGatewayOperation");
        String operationType = "SYNC"/*operationInformationJson.get(OPERATION_TYPE_PROPERTY).getAsString()*/;
        // TODO: Asynchronus operation not yet suppoeted
        if (operationType.equalsIgnoreCase("SYNC")) {
            return createGatewaySyncOperation(operationName, operationInformationJson);
        } else if (operationType.equalsIgnoreCase("ASYNC")) {
            return createGatewayAsyncOperation(operationName, operationInformationJson);
        } else {
            createGatewayOperationResponse.setSuccess(false);
            createGatewayOperationResponse.setDetails("Operation types supported: SYNC or ASYNC");
            return createGatewayOperationResponse.getJsonRestResponse();
        }
    }

    public JsonObject createGatewaySyncOperation(String operationName, JsonObject operationInformationJson) {
        RestResponse createGatewayOperationResponse = new RestResponse("createGatewayOperation");
        String operationInputType, operationOutputType,
                operationInputTypeNameSpace, operationOutputTypeNameSpace,
                soapAction, operationEndPoint;
        String serviceName;
        ServiceManager serviceManager = ServiceManager.getInstance();
        operationInputType = operationInformationJson.get(OPERATION_INPUT_TYPE_PROPERTY).getAsString();
        operationEndPoint = operationInformationJson.get(ADDRESS_OPERATION_PROPERTY).getAsString();
        operationOutputType = operationInformationJson.get(OPERATION_OUTPUT_TYPE_PROPERTY).getAsString();
        operationInputTypeNameSpace = operationInformationJson.get(OPERATION_INPUT_TYPE_NAMESPACE_PROPERTY).getAsString();
        operationOutputTypeNameSpace = operationInformationJson.get(OPERATION_OUTPUT_TYPE_NAMESPACE_PROPERTY).getAsString();
        operationOutputTypeNameSpace = operationInformationJson.get(OPERATION_OUTPUT_TYPE_NAMESPACE_PROPERTY).getAsString();

        operationOutputTypeNameSpace = operationInformationJson.get(OPERATION_OUTPUT_TYPE_NAMESPACE_PROPERTY).getAsString();
        operationOutputTypeNameSpace = operationInformationJson.get(OPERATION_OUTPUT_TYPE_NAMESPACE_PROPERTY).getAsString();

        serviceName = operationInformationJson.get(SERVICE_NAME_PROPERTY).getAsString();
        soapAction = operationInformationJson.get(SOAP_OPERATION_PROPERTY).getAsString();
        TBXSynchronousOperation operationDescr = new TBXSynchronousOperation();
        operationDescr.setName(operationName);
        operationDescr.setAdmittedHosts("");
        operationDescr.setType("synchronous");
        operationDescr.setSoapAction(soapAction);
        operationDescr.setRequestTimeout("1h");
        operationDescr.setInputType(operationInputType);
        operationDescr.setOutputType(operationOutputType);
        operationDescr.setInputTypeNameSpace(operationInputTypeNameSpace);
        operationDescr.setOutputTypeNameSpace(operationOutputTypeNameSpace);
        TBXService service = null;
        try {
            service = serviceManager.getService(serviceName);
            Hashtable<String, Hashtable<String, String>> serviceVariables =
                    service.getImplementedInterface().getUserVariable();
            Hashtable<String, String> variable = new Hashtable<String, String>();
            variable.put("value", operationEndPoint);
            variable.put("type", "string");
            variable.put("displayedText", "URL of the SOAP endpoint");
            serviceVariables.put("remoteUrl", variable);

            service.getImplementedInterface().setUserVariables(serviceVariables);
            operationDescr.setScripts(getGatewayScriptDescriptorSync(service.getServiceRoot(), operationName));
            service.addOperation(operationDescr);
            service.dumpService();
        } catch (Exception ex) {
            createGatewayOperationResponse.setSuccess(false);
            createGatewayOperationResponse.setDetails(ex.getMessage());
            return createGatewayOperationResponse.getJsonRestResponse();
        }

        createGatewayOperationResponse.setSuccess(true);
        return createGatewayOperationResponse.getJsonRestResponse();
    }

    public JsonObject createGatewayAsyncOperation(String operationName, JsonObject operationInformationJson) {

        RestResponse createGatewayResponse = new RestResponse("createGatewayService");
        //operationName=operationInformationJson.get(SERVICE_NAME_PROPERTY).getAsString();
        // not yet implemented
        return createGatewayResponse.getJsonRestResponse();
    }

    public JsonObject deleteGatewayOperation(String serviceName, String operationName) {
        RestResponse deleteGatewayOperationResponse = new RestResponse("deleteGatewayOperation");

        return deleteGatewayOperationResponse.getJsonRestResponse();
    }

    public JsonArray getSoapOperationsInfoList(String wsdlURL) throws Exception {
        JsonArray operationsJArray = new JsonArray();
        WSDLFactory factory = null;
        factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", true);
        reader.setFeature("javax.wsdl.importDocuments", true);
        Definition defElement = reader.readWSDL(wsdlURL);
        operationsJArray = getSoapOperationsFromWSDL11(defElement);
        return operationsJArray;
    }

    private static JsonArray getSoapOperationsFromWSDL11(Definition definitionElement) {
        JsonArray operationsInfoJArray = new JsonArray();
        JsonObject operationObj = null;
        Map services = definitionElement.getAllServices();
        Iterator servicesIterator = services.values().iterator();
        SOAPAddressImpl soapAdd = null;
        SOAPOperationImpl soapOp = null;
        QName inputPartElement = null, outputPartElement = null;

        while (servicesIterator.hasNext()) {
            javax.wsdl.Service service = (javax.wsdl.Service) servicesIterator.next();
            Map ports = service.getPorts();
            Iterator portsIterator = ports.values().iterator();
            while (portsIterator.hasNext()) {
                Port port = (Port) portsIterator.next();
                Iterator portExtElIterator = port.getExtensibilityElements().iterator();
                while (portExtElIterator.hasNext()) {
                    Object nativeAttribute = portExtElIterator.next();
                    if (nativeAttribute instanceof SOAPAddressImpl) {
                        soapAdd = (SOAPAddressImpl) nativeAttribute;
                        break;
                    }
                }
                if (soapAdd != null) {
                    Binding currentBinding = port.getBinding();
                    Iterator bindingOperationsIterator = currentBinding.getBindingOperations().iterator();
                    while (bindingOperationsIterator.hasNext()) {
                        BindingOperation bindingOperation = (BindingOperation) bindingOperationsIterator.next();
                        javax.wsdl.Operation operation = bindingOperation.getOperation();

                        if (!operation.isUndefined()) {
                            operationObj = new JsonObject();
                            Iterator opExtElIterator = bindingOperation.getExtensibilityElements().iterator();
                            while (opExtElIterator.hasNext()) {
                                Object nativeAttribute = opExtElIterator.next();
                                if (nativeAttribute instanceof SOAPOperationImpl) {
                                    soapOp = (SOAPOperationImpl) nativeAttribute;
                                    break;
                                }
                            }
                            // QName inputMess = operation.getInput().getMessage().getQName();
                            // QName outputMess = operation.getOutput().getMessage().getQName();

                            Iterator inputExtElIterator = operation.getInput().getMessage().getParts().values().iterator();
                            while (inputExtElIterator.hasNext()) {
                                Object nativeAttribute = inputExtElIterator.next();
                                if (nativeAttribute instanceof Part) {
                                    inputPartElement = ((Part) nativeAttribute).getElementName();
                                    break;
                                }
                            }

                            Iterator outputExtElIterator = operation.getOutput().getMessage().getParts().values().iterator();
                            while (outputExtElIterator.hasNext()) {
                                Object nativeAttribute = outputExtElIterator.next();
                                if (nativeAttribute instanceof Part) {
                                    outputPartElement = ((Part) nativeAttribute).getElementName();
                                    break;
                                }
                            }

                            operationObj.addProperty(SERVICE_OPERATION_PROPERTY,
                                    service.getQName().getLocalPart());
                            operationObj.addProperty(PORT_OPERATION_PROPERTY,
                                    port.getName());
                            operationObj.addProperty(BINDING_OPERATION_PROPERTY,
                                    currentBinding.getQName().getLocalPart());
                            operationObj.addProperty(ADDRESS_OPERATION_PROPERTY,
                                    soapAdd.getLocationURI());
                            operationObj.addProperty(OPERATION_PROPERTY,
                                    operation.getName());
                            operationObj.addProperty(SOAP_OPERATION_PROPERTY,
                                    soapOp.getSoapActionURI());
                            operationObj.addProperty(OPERATION_INPUT_TYPE_PROPERTY,
                                    inputPartElement.getLocalPart());
                            operationObj.addProperty(OPERATION_INPUT_TYPE_NAMESPACE_PROPERTY,
                                    inputPartElement.getNamespaceURI());
                            operationObj.addProperty(OPERATION_OUTPUT_TYPE_PROPERTY,
                                    outputPartElement.getLocalPart());
                            operationObj.addProperty(OPERATION_OUTPUT_TYPE_NAMESPACE_PROPERTY,
                                    outputPartElement.getNamespaceURI());

                            operationsInfoJArray.add(operationObj);
                        }
                    }
                }
            }
        }


        return operationsInfoJArray;
    }

    private TBXScript[] getGatewayScriptDescriptorSync(File servicePath, String operationName) throws Exception {
        TBXScript[] scripts = new TBXScript[2];
        DOMUtil domUtil = new DOMUtil();
        scripts[0] = new TBXScript();
        File operationsPath = new File(servicePath, GATEWAY_OPERATIONS_FOLDER);
        operationsPath.mkdirs();
        File newOpPath = new File(operationsPath, operationName);
        newOpPath.mkdir();
        File resourceOpPath = new File(servicePath, GATEWAY_SYNC_RESOURCES_FOLDER);
        IOUtil.copyFile(new File(resourceOpPath, FIRST_SCRIPT_FILE_NAME), new File(newOpPath, FIRST_SCRIPT_FILE_NAME));
        IOUtil.copyFile(new File(resourceOpPath, GLOBAL_ERROR_SCRIPT_FILE_NAME), new File(newOpPath, GLOBAL_ERROR_SCRIPT_FILE_NAME));


        scripts[0].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(newOpPath, FIRST_SCRIPT_FILE_NAME))));
        scripts[0].setPath(GATEWAY_OPERATIONS_FOLDER + "/" + operationName + "/" + FIRST_SCRIPT_FILE_NAME);
        scripts[0].setType(Script.SCRIPT_TYPE_FIRST_SCRIPT);
        scripts[1] = new TBXScript();
        scripts[1].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(newOpPath, GLOBAL_ERROR_SCRIPT_FILE_NAME))));
        scripts[1].setPath(GATEWAY_OPERATIONS_FOLDER + "/" + operationName + "/" + GLOBAL_ERROR_SCRIPT_FILE_NAME);
        scripts[1].setType(Script.SCRIPT_TYPE_GLOBAL_ERROR);
        return scripts;
    }
}
