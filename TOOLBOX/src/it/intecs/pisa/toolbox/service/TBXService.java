/*
 *
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 *  File Name:         $RCSfile: TBXService.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.util.TimeUtil;
import it.intecs.pisa.toolbox.TimerManager;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.toolbox.db.ServiceStatuses;
import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.common.tbx.lifecycle.LifeCycle;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.log.ErrorMailer;
import it.intecs.pisa.toolbox.resources.LogResourcesPersistence;
import java.io.*;
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import org.apache.log4j.*;
import javax.xml.parsers.*;
import it.intecs.pisa.util.*;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.toolbox.service.tasks.ServiceLifeCycle;
import it.intecs.pisa.util.wsdl.WSDL;
import it.intecs.pisa.common.tbx.WSDLBuilder;
import it.intecs.pisa.toolbox.configuration.ToolboxNetwork;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Semaphore;

/**
 *  An instance of this class represents a single service deployed on the Toolbox. The main entry point is the {@link #processRequest} method that is invoked by the {@link Toolbox#onMessage} method each time a SOAP request addressed to this TBXService is received.
 */
public class TBXService extends Service {

    public static final String CDATA_S = "<![CDATA[";
    public static final String CDATA_E = "]]>";
    public static final String WRONG_HOST = "Unknown or not allowed host: ";
    public static final String BACKEND_NOT_RESPONDING = "Back-end not responding";
    public static final String UNKNOWN_SOAP_PORT = "Unknown SOAP port: ";
    public static final String INVALID_REQUEST = "Invalid request: ";
    public static final String INVALID_RESPONSE = "Invalid response: ";
    public static final String UNKNOWN_OPERATION = "Unknown operation: ";
    public static final String ORDER_ID_IN_USE = "OrderId in use: ";
    public static final String MESSAGE_ID_IN_USE = "MessageId in use: ";
    public static final String UNKNOWN_ORDER_ID = "Unknown OrderId: ";
    public static final String REQUEST_STATUS = "Request status: ";
    public static final String ORIGINAL_EXCEPTION = "originalException";
    public static final String ERROR_CODE = "errorCode";
    public static final String SERVICE_DEFINITION_SCHEMA = "serviceDefinition.xsd";
    public static final String SERVICE_DEFINITION = "serviceDefinition";
    public static final String SERVICE_STATUS_SCHEMA = "serviceStatus.xsd";
    public static final String SERVICE_STATUS = "serviceStatus.xml";
    public static final String XML_SCRIPT_SCHEMA = "xmlScript.xsd";
    public static final String GET_ORDER_ID_XML = "getOrderId.xml";
    public static final String GET_MASS_SOAP_SERVER_URL_XML = "getMassSOAPServerURL.xml";
    public static final String PUSH = "push";
    public static final String POLLING_RATE = "pollingRate";
    public static final String SERVICE_SCHEMA = "serviceSchema";
    public static final String REQUEST_TIMEOUT = "requestTimeout";
    public static final String QUEUING = "queuing";
    
    public static final String SUSPEND_MODE = "suspendMode";
    public static final String REQUEST = "request";
    public static final String NAME = "name";
    
    public static final String PUSH_URL = "pushURL";
    public static final String HOST_NAME = "hostName";
    public static final String HOST_IP = "hostIP";
    public static final String MASS_NAMESPACE = "http://www.esa.int/mass";
    public static final String XMLNS = "xmlns";
    public static final String MASS = "mass";
    
    public static final String MASS_HOST = "massHost";
    public static final String MASS_HOST_ADDRESS = "massHostAddress";
    public static final String IS_AVAILABLE = "isAvailable";
    public static final String ARRIVAL_DATETIME = "arrivalDateTime";
    public static final String EXPIRATION_DATETIME = "expirationDateTime";
    public static final String SERVER_EXPIRATION_DATETIME = "serverExpirationDateTime";
    public static final String CLIENT_EXPIRATION_DATETIME = "clientExpirationDateTime";
    public static final String FTP_USER = "ftpUser";
    public static final String STATUS_PATH = "statusPath";
    public static final String DOT = ".";
    public static final String STATUS = "status";
    public static final String WAITING = "waiting";
    public static final String EXECUTING = "executing";
    public static final String PENDING = "pending";
    public static final String READY = "ready";
    public static final String ABORTED = "aborted";
    public static final String CANCELLED = "cancelled";
    public static final String REJECTED = "rejected";
    public static final String EXPIRED = "expired";
    public static final String STOPPED = "stopped";
    public static final String LEAVING_RESPONSE = "leavingResponse";
    public static final String RESPONSE_LEAVING = "responseLeaving";
    public static final String UNPUSHED = "unpushed";
    public static final String COMPLETED = "completed";
    public static final String ERROR = "error";
    public static final String REMAINING_ATTEMPTS = "remainingAttempts";
    public static final String RETRY_ATTEMPTS = "retryAttempts";
    public static final String RETRY_RATE = "retryRate";
    public static final String REASON_FOR_STATUS = "reasonForStatus";
    public static final String XML_REQUEST = "xmlRequest";
    public static final String SOAP_REQUEST = "soapRequest";
    public static final String SUSPENDED = "suspended";
    public static final String PUSH_RETRY = "pushRetry";
    public static final String XML = "xml";
    public static final String TYPE = "type";
    public static final String VALUE = "value";
    public static final String TIMER = "timer";
    public static final String FTP_ACCOUNT = "ftpAccount";
    public static final String DELAY = "delay";
    public static final String VARIABLE = "variable";
    public static final String SCRIPT = "script";
    public static final String STRING = "string";
    public static final String SEND = "send";
    public static final String CHECK = "check";
    public static final String GET = "get";
    public static final String RESULT = "Result";
    public static final int MILLISECONDS = 1000;
    public static final int SECONDS = 60;
    public static final int MINUTES = 60;
    public static final int HOURS = 24;
    public static final String REQUESTS = "requests";
    public static final String ADMITTED_HOSTS = "admittedHosts";
    public static final String ADMITTED_HOST = "admittedHost";
    public static final String SSL_CERTIFICATE = "sslCertificate";
    protected static final String TIMER_STATUS_XML = "timerStatus.xml";
    private static InetAddress localHost = null;
    public static final String IMPORT = "import";
    public static final String INCLUDE = "include";
    public static final String REDEFINE = "redefine";
    public static final String SCHEMA_LOCATION = "schemaLocation";
    public static final String SERVICE_MODE = "serviceMode";
    public static final String SYNCHRONOUS = "synchronous";
    public static final String VARIABLES = "variables";
    public static final String INT = "int";
    public static final String BYTE = "byte";
    public static final String LONG = "long";
    public static final String SHORT = "short";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";
    public static final String CLEANUP_PROCEDURE = "cleanupProcedure";
    public static final String CLEANUP_MARKERS = "cleanupMarkers";
    public static final String MARKER = "marker";
    public static final String WSDL_XSL = "WSDL.xsl";
    public static final String APACHE_HOST = "apacheHost";
    public static final String TOMCAT_HOST = "tomcatHost";
    public static final String MASS_SCHEMA_URL = "massSchemaURL";
    public static final String SERVICE_SCHEMA_URL = "serviceSchemaURL";
    public static final String SAMPLE_SERVICE_WSDL = "sampleServiceWSDL.xml";
    public static final String DOT_WSDL = ".wsdl";
    public static final String DOT_WSIL = ".wsil";
    public static final String WSDL = "WSDL";
    public static final String SERVICE_DEFINITION_NS = "http://pisa.intecs.it/mass/toolbox/serviceDefinition";
    public static final String SERVICE_DESCRIPTOR_NS = "http://pisa.intecs.it/mass/toolbox/serviceDescriptor";
    public static final String SERVICE_DESCRIPTOR_FILE = "serviceDescriptor.xml";
    public static final String SERVICE_DESCRIPTOR_SCHEMA = "serviceDescriptor.xsd";
    public static final String XML_SCRIPT_NS = "http://pisa.intecs.it/mass/toolbox/xmlScript";
    public static final String RFQ = "RFQ";
    public static final String ORDER = "Order";
    public static final String PORT_8080 = "8080";
    public static final String PORT_80 = "80";
    public static final String COLON = ":";
    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String SERVICES = "services";
    public static final String SLASH = "/";
    public static final String TOOLBOX = "TOOLBOX";
    public static final String TARGET_NAMESPACE = "targetNamespace";
    public static final String XMLNS_TNS = "xmlns:tns";
    public static final String MASS_SCHEMA = "mass.xsd";
    public static final String URL = "url";
    public static final String PROCESS = "process";
    public static final String SEARCH = "Search";
    public static final String PRESENT = "Present";
    public static final String TEST_CONFIGURATION_FILE_NAME = "testConfigurationFileName";
    public static final String WEB_INF = "WEB-INF";
    public static final String WSA_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    public static final String MESSAGE_ID = "MessageID";
    public static final String REPLY_TO = "ReplyTo";
    public static final String ADDRESS = "Address";
    public static final String SERVICE_NAME_U = "ServiceName";
    public static final String PORT_TYPE = "PortType";
    public static final String PORT_TYPE_L = "portType";
    public static final String RELATES_TO = "RelatesTo";
    public static final String WSA = "wsa";
    public static final String XSL = "XSL";
    public static final String SCHEMAS = "schemas";
    public static final String SCRIPTS = "scripts";
    public static final String SOAP_ACTION = "soapAction";
    public static final String SCRIPT_FILE = "scriptFile";
    public static final String ASYNCHRONOUS = "asynchronous";
    public static final String EXPORT_DESCRIPTOR_FILE = "exportDescriptor.xml";
    public static final String EXPORT_DESCRIPTOR_SCHEMA = "exportDescriptor.xsd";
    public static final String EXPORT_DESCRIPTOR_NS = "http://pisa.intecs.it/mass/toolbox/exportDescriptor";
    public static final String SERVICE = "service";
    public static final String SCHEMA_DOCUMENTS = "schemaDocuments";
    public static final String WSDL_INFO = "wsdlInfo";
    public static final String REQUEST_MESSAGE = "requestMessage";
    public static final String RESPONSE_MESSAGE = "responseMessage";
    public static final String PUSH_MESSAGE = "pushMessage";
    public static final String PUSH_RESPONSE = "pushResponse";
    public static final String NS = "ns";
    public static final String WSDL_TARGET_NS = "wsdlTargetNS";
    public static final String OPERATION_MODE = "operationMode";
    public static final String TMP = "tmp";
    public static final String ABSTRACT = "abstract";
    public static final String DESCRIPTION = "description";
    public static final String EMPTY_STRING = "";
    public static final String SSE_SCHEMA_VERSION = "SSESchemaVersion";
    public static final String SSE = "SSE";
    public static final String SYNCHRONOUS_INSTANCES = "synchronousInstances";
    public static final String ASYNCHRONOUS_INSTANCES = "asynchronousInstances";
    public static final String INSTANCE = "instance";
    public static final String KEY = "key";
    public static final String DATE = "date";
    public static final String DOT_LOG = ".log";
    public static final String RESPONSE_SCRIPT = "executionResult_responseBuilder.xml";
    public static final String FIRST_SCRIPT = "executionResult_firstScript.xml";
    public static final String SECOND_SCRIPT = "executionResult_secondScript.xml";
    public static final String THIRD_SCRIPT = "executionResult_thirdScript.xml";
    public static final String ERROR_EMAIL = "errorNotificationEmail";
    public static final String EOLI_SEARCH = "EOLI_Search";
    public static final String EOLI_PRESENT = "EOLI_Present";
    public static final String EOLI_NAMESPACE = "http://earth.esa.int/XML/eoli";
    public static final String ORDERID_LOG_START = "[";
    public static final String ORDERID_LOG_END = "]";

    
    private PushManager pushManager;
    private Toolbox toolbox;
    private Logger logger;
    private File root;
    private File serviceRoot;
    private File publicServiceDir;
    private Semaphore serviceQueueMutex;
    private boolean initialized;
    private File descriptorFile;
    private DocumentBuilder validatingParser;
    private File logFile;
    private File requestDir;
    private Hashtable requests = new Hashtable();
    private TimerManager timerManager;
    private FTPServerManager ftpServerManager;
    private Element getOrderIdScript;
    private String orderIdXpath;
    private HashMap soapActions = new HashMap();
    private File logDir;
    private File timerStatusFile;

    /**
     *
     * @param serviceRoot
     * @param publicServiceDir
     * @throws java.lang.Exception
     */
    public TBXService(File serviceRootDir, File publicServiceDirectory) throws Exception {
        super();
        Document descriptorDocument;
        Operation[] operations;
        DOMUtil util;
        TBXOperation tbxo;
        File schemaRootDir;

        util = new DOMUtil();
        toolbox = Toolbox.getInstance();
        serviceQueueMutex=new Semaphore(1);
        serviceName=serviceRootDir.getName();
        serviceRoot = serviceRootDir;
        publicServiceDir = publicServiceDirectory;
        ftpServerManager = FTPServerManager.getInstance();
        root = toolbox.getRootDir();


        requestDir = new File(serviceRootDir, REQUESTS);
        if (!requestDir.exists()) {
            requestDir.mkdir();
        }

        logDir = new File(toolbox.getLogDir(), serviceName);
        if (!logDir.exists()) {
            logDir.mkdir();
        }

        logger=LogResourcesPersistence.getInstance().getRollingLogForService(serviceName);

        logger.setLevel(toolbox.getLogLevel());
        logger.info("Service " + serviceName + " created");

        /* Loads configuration file into a DOM Document. If configuration file doesn't exists (first service creation), it is copied from the rootEl */
        descriptorFile = new File(serviceRootDir, SERVICE_DESCRIPTOR_FILE);

        descriptorDocument = util.fileToDocument(descriptorFile);
        initializeFromXMLDescriptor(descriptorDocument.getDocumentElement());

        operations=(Operation[]) this.implementedInterface.getOperations();

        for(Operation o:operations)
        {
            tbxo= (TBXOperation)o;
            this.soapActions.put(new String(o.getSoapAction()), "");
        }

        //this is now fixed
        orderIdXpath = "//mass:orderId";
        this.getOrderIdScript=util.fileToDocument(new File(new File(new File(getRoot(), WEB_INF), "scripts"), "getOrderId.xml")).getDocumentElement();

        logger.info("service configuration loaded");

        schemaRootDir = new File(serviceRootDir, implementedInterface.getSchemaDir());
        
        timerStatusFile = new File(serviceRootDir, TIMER_STATUS_XML);
        if (timerStatusFile.exists() == false) {
            IOUtil.copyFile(new File(new File(new File(getRoot(), WEB_INF), XML), TIMER_STATUS_XML), timerStatusFile);
        }

        initialized=true;
        
        logger.info("Started timer manager");
    }

    public void dumpService() throws Exception
    {
        this.implementedInterface.dumpInterface();

        DOMUtil.dumpXML(createServiceDescriptor(), this.descriptorFile);
    }

    @Override
    protected void initInterfaceFromXML(Element interfaceEl) {
        implementedInterface = new TBXSOAPInterface();
        implementedInterface.initFromXML(interfaceEl);
    }

    public void attemptToDeployWSDLAndSchemas() throws Exception
    {
        try
        {
            IOUtil.rmdir(publicServiceDir);
            deployWSDL();
            deploySchemaFilesNew();
           
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.error("Unable to deploy WSDL or schema");
        }

    }

    @Override
    public void setQueuing(boolean queuing) {
        super.setQueuing(queuing);

        if(queuing==false)
        {
            while(this.getServiceQueueMutex().hasQueuedThreads())
                this.getServiceQueueMutex().release();
        }
    }



    public void updateSchemasImportedFromWeb(File schemaFile) throws Exception {
        logger.info("Updating schema references");
        File schemaFileDirectory = schemaFile.getAbsoluteFile().getParentFile();
        HashSet updatedSchemas = new HashSet();
        HashSet toBeUpdatedSchemasSet = new HashSet();
        HashSet referredSchemas = new HashSet();
        referredSchemas.add(schemaFile);
        Iterator toBeUpdatedSchemas;
        Document schemaDoc;
        do {
            toBeUpdatedSchemasSet = (HashSet) referredSchemas.clone();
            referredSchemas.clear();
            toBeUpdatedSchemas = toBeUpdatedSchemasSet.iterator();
            do {
                updateSchema((File) toBeUpdatedSchemas.next(), schemaFileDirectory, referredSchemas);
            } while (toBeUpdatedSchemas.hasNext());
            updatedSchemas.addAll(toBeUpdatedSchemasSet);
            referredSchemas.removeAll(updatedSchemas);
        } while (!referredSchemas.isEmpty());
    }

    private void startTimerManager() {
        try {
            (timerManager = new TimerManager(this)).start();
        } catch (Exception e) {
            logger.error("Error creating timer manager " + CDATA_S + e.getMessage() + CDATA_E);
        }
        logger.info("Started timer manager");

    }

    private void updateSchema(File schemaFile, File directory, HashSet referredSchemas) throws Exception {
        String schemaFilePath = schemaFile.getAbsolutePath();
        Document schemaDoc = new DOMUtil().fileToDocument(schemaFilePath);
        String schemaDocURI = schemaDoc.getDocumentElement().getNamespaceURI();
        updateSchemaElements(schemaDoc.getElementsByTagNameNS(schemaDocURI, IMPORT), directory, referredSchemas);
        updateSchemaElements(schemaDoc.getElementsByTagNameNS(schemaDocURI, INCLUDE), directory, referredSchemas);
        updateSchemaElements(schemaDoc.getElementsByTagNameNS(schemaDocURI, REDEFINE), directory, referredSchemas);
        DOMUtil.dumpXML(schemaDoc, schemaFile);
        logger.info("updated schema file: " + schemaFilePath);

    }

    private void updateSchemaElements(NodeList list, File directory, HashSet referredSchemas) {
        Element element;
        String schemaLocation;
        File schemaFile;
        int backslashLastIndex;
        for (int index = 0; index < list.getLength(); index++) {
            element = (Element) list.item(index);
            schemaLocation = (element.getAttribute(SCHEMA_LOCATION));
            referredSchemas.add(schemaFile = new File(directory, schemaLocation.substring((backslashLastIndex = schemaLocation.lastIndexOf('\\')) > 0 ? backslashLastIndex + 1 : schemaLocation.lastIndexOf('/') + 1)));
            element.setAttribute(SCHEMA_LOCATION, Util.getURI(schemaFile.getAbsolutePath()));
        }
    }

   

    public File getServiceRoot() {
        return serviceRoot;
    }

    /**
     *  This method is the entry point of XML request processing, in order to produce XML responses.
     *  It is called by the {@link Toolbox#onMessage} method.
     *  Its task consists in first recognizing the requested operationName,
     *  then extracting from the request essential information to manage its statusEl (e.g. orderId)
     *  and finally invoke the methods that actually execute XML scripts defining the service.
     */
    public Document processRequest(String soapAction, Document soapRequest, boolean debugMode) throws Exception {
        TBXOperation operation;
        String errorStr;

        if(ServiceStatuses.getStatus(serviceName)==ServiceStatuses.STATUS_STOPPED)
        {
            errorStr="Service "+serviceName+" is stopped";
            logger.error(errorStr);
            throw new Exception(errorStr);
        }

        try
        {
            operation = (TBXOperation) this.implementedInterface.getOperationBySOAPAction(soapAction);
            if (operation==null) {
                errorStr=UNKNOWN_SOAP_PORT+ soapAction;
                logger.error("[" + serviceName + "] " +errorStr);
               
                throw new ToolboxException(errorStr);
                }
            }
        catch(Exception ecc)
        {
            logger.error("[" + serviceName + "] Error processing request: " + soapAction);
            throw new ToolboxException("Error processing request for service " + serviceName + ": " + ecc.getMessage());
        }

        return this.processRequest(operation, soapRequest, debugMode);
        
    }
    
    
    /**
     *  This method is the entry point of XML request processing, in order to produce XML responses. 
     *  It is called by the {@link Toolbox#onMessage} method. 
     *  Its task consists in first recognizing the requested operationName, then extracting from the request essential information 
     *  to manage its statusEl (e.g. orderId) and finally invoke the methods that actually execute XML scripts defining the service.
     *  @author Stefano
     */
    public Document processRequest(TBXOperation operation, Document soapRequest, boolean debugMode) throws ToolboxException, Exception {
        
        /* If the service is stopped, no processing is performed and an exception is thrown */
        if (ServiceStatuses.getStatus(serviceName)==ServiceStatuses.STATUS_STOPPED) {
            ErrorMailer.send(serviceName, null, null, null,"Service stopped");
            throw new ToolboxException("Service stopped");
        }

        try {
            Document responseMsg = operation.processRequest(soapRequest, debugMode);
            return responseMsg;
        } catch (Exception e) {
            logger.error("[" + serviceName + "] Error processing request: " + operation.getName());
            throw new ToolboxException("Error processing request for service " + serviceName + ": " + e.getMessage());
        }
    }
    /**
     *  Extracts orderId from a request using an XML script. THis method uses an ad hoc XML script instead of wiring the necessary DOM manipulation.
     */
    public String getOrderId(Document requestContent) throws Exception {
        try {
            ToolboxEngine toolboxEngine;

            toolboxEngine = new ToolboxEngine(logger);

            toolboxEngine.put(XML_REQUEST, requestContent);
            toolboxEngine.put("orderIdXPath", this.orderIdXpath);

            return (String) toolboxEngine.executeScript(getOrderIdScript);
        } catch (Exception e) {
            return "";
        }
    }

    public void destroy() {
        logger.info("destroy " + getServiceName());
        if (getTimerManager() != null) {
            getTimerManager().interrupt();
        }
    }

    @Override
    public InputStream getServiceAbstract() {
        try {
            return new FileInputStream(new File(this.serviceRoot, "Info/abstract.txt"));
        } catch (FileNotFoundException ex) {
           return new ByteArrayInputStream("".getBytes());
        }
    }

    @Override
    public InputStream getServiceDescription() {
        try {
            return new FileInputStream(new File(this.serviceRoot, "Info/description.txt"));
        } catch (FileNotFoundException ex) {
           return new ByteArrayInputStream("".getBytes());
        }
    }

  

    synchronized public void deployWSDL() throws Exception {
        WSDL wsdl;
        File wsdlFile;

        wsdl=WSDLBuilder.buildFromService(this,getServiceURL(),ToolboxNetwork.getEndpointURL());

        publicServiceDir.mkdirs();
        wsdlFile=new File(this.publicServiceDir,this.serviceName+".wsdl");
        DOMUtil.dumpXML(wsdl.createWSDL(),wsdlFile);

    }

    synchronized public String getServiceURL() throws Exception {
       return ToolboxNetwork.getEndpointURL()+"/services/"+serviceName;
    }

    protected void deploySchemaFilesNew() throws Exception {
        File serviceRoot;
        File schemaDir;
        File publicDir;
        Toolbox tbx;
        URI uri;

        try {
            tbx = Toolbox.getInstance();
            serviceRoot = tbx.getServiceRoot(serviceName);
            schemaDir = new File(serviceRoot, "Schemas");

            publicDir = tbx.getPublicServiceDir(serviceName);
            IOUtil.copyDirectory(schemaDir, publicDir);

            uri = new URI(ToolboxNetwork.getEndpointURL() + "/TOOLBOX/WSDL/" + serviceName);
            //SchemaSetRelocator.updateSchemaLocationToRelative(publicDir, schemaDir.toURI());
            SchemaSetRelocator.updateSchemaLocationToAbsolute(publicDir, uri);
        } catch (Exception e) {
            System.out.println("Cannot copy schema files to public directory");
            logger.error("Cannot copy schema files to public directory");
        }
    }


    public void init()
    {
        TBXOperation oper;

        logger.info("Initing service "+serviceName);

        startTimerManager();
        
        for(Operation op:implementedInterface.getOperations())
        {
            logger.info("Starting operation "+op.getName());
            oper=(TBXOperation) op;
            oper.setLogger(logger);
            oper.start();
        }

        logger.info("Service "+serviceName+" initialized");
        initialized = true;
    }

    public void teardown()
    {
         logger.info("Tearing down service "+serviceName);

        if (getTimerManager() != null) {
            getTimerManager().interrupt();
        }

        setValidatingParser((DocumentBuilder) null);

        if (getTimerManager() != null) {
            getTimerManager().clear();
        }

        TBXOperation[] ops;

        ops=(TBXOperation[]) this.implementedInterface.getOperations();
        for(TBXOperation op:ops)
            op.stop();

        logger.info("Service "+serviceName+" teared down");
        initialized = false;
    }
    /**
     *  Stops this TBXService. The service is put in a "blank" statusEl, i.e. as if the {@link #init} method was not executed. A flag is set so that {@link #processingRequest} method can throw an exception on any request arrival.
     */
    public synchronized void stop() throws Exception {
        teardown();
        ServiceStatuses.updateStatus(serviceName, ServiceStatuses.STATUS_STOPPED);
        logger.info("Service "+serviceName+" stopped");
    }

    /**
     *  It allows this service to restart after a stop, or to process configuration parameters without waiting a request to arrive.
     */
    public synchronized void start() throws Exception {
        init();
        ServiceStatuses.updateStatus(serviceName, ServiceStatuses.STATUS_RUNNING);
        logger.info("Service "+serviceName+" started");
    }

    /**
     *  Sets {@link #suspended} flag and suspended attribute in statusEl. The {@link #start} method is called to ensure statusEl to be loaded.
     */
    public synchronized void suspend() throws Exception {
        //stop();
        ServiceStatuses.updateStatus(serviceName, ServiceStatuses.STATUS_SUSPENDED);
        logger.info("Service "+serviceName+" suspended");
    }

    /**
     *  Sets {@link #suspended} flag and suspended attribute in statusEl. Moreover, if the suspend mode is soft, notifies every suspended thread. The {@link #start} method is called to ensure statusEl to be loaded.
     */
    public synchronized void resume() throws Exception {
        ServiceStatuses.updateStatus(serviceName, ServiceStatuses.STATUS_RUNNING);

        restartQueuedInstances();

        //start();
        
        logger.info("Service "+serviceName+" resumed");

    }

    public void deleteSynchronousIstance(String key) throws Exception {
        File requestLogDir = new File(new File(getLogDir(), SYNCHRONOUS_INSTANCES), key);
        IOUtil.rmdir(requestLogDir);
    }

    public void deleteAsynchronousIstance(String key) throws Exception {
        File requestLogDir = new File(new File(getLogDir(), ASYNCHRONOUS_INSTANCES), key);
        IOUtil.rmdir(requestLogDir);

    }

    public void deleteLogDir() throws Exception {
        Enumeration appenders = logger.getAllAppenders();

        while (appenders.hasMoreElements()) {
            ((Appender) appenders.nextElement()).close();
        }

        logger.removeAllAppenders();

        IOUtil.rmdir(getLogDir());
    }

    /**
     *  Returns a stream directly connected with statusEl descriptor file on disk.
     */
    public InputStream viewDescriptorFile() throws Exception {
        return new FileInputStream(getDescriptorFile());
    }

    public synchronized void deleteOperation(String operationName) throws Exception {
        TBXOperation operation;

        //start();

        logger.info("Deleting operation " + operationName + "...");

        this.implementedInterface.removeOperation(operationName);
       
        logger.info(operationName + " deleted");
        DOMUtil.dumpXML(createServiceDescriptor(), getDescriptorFile());

        //removing TBXOperation dir
        logger.info("Removing " + operationName + " operation script dir");

        ServiceLifeCycle.executeLifeCycleStep(LifeCycle.SCRIPT_DELETE,this,operationName);

        File operationDir = new File(serviceRoot, "Operations/" + operationName);
        IOUtil.rmdir(operationDir);

        attemptToDeployWSDLAndSchemas();
   
    }

    public synchronized void addOperation(TBXOperation operationDescriptor) {
        try
        {
            if (this.implementedInterface.isOperationImplemented(operationDescriptor.getName())) {
                throw new ToolboxException("operation existing!");
            }

            if (this.implementedInterface.isSoapActionImplemented(operationDescriptor.getSoapAction())) {
                throw new ToolboxException("soapAction existing!");
            }

            //--------------------now adding the new operation ----------------------
            logger.info("Adding operation " + operationDescriptor.getName() + "...");
            System.out.println("Operation type "+operationDescriptor.getClass().getCanonicalName());
            implementedInterface.addOperations(operationDescriptor);

            logger.info("Operation " + operationDescriptor.getName() + " successfully added");

            adjustReferences();

            operationDescriptor.setLogger(logger);
            operationDescriptor.start();

            ServiceLifeCycle.executeLifeCycleStep(LifeCycle.SCRIPT_BUILD,operationDescriptor.getParentService(),operationDescriptor.getName());

            try
            {
            attemptToDeployWSDLAndSchemas();
            }
            catch(Exception ecc)
            {
                ecc.printStackTrace();
            }

            ServiceLifeCycle.executeLifeCycleStep(LifeCycle.SCRIPT_BUILD,this,operationDescriptor.getName());
                      
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if(implementedInterface.isOperationImplemented(operationDescriptor.getName()))
                implementedInterface.removeOperation(operationDescriptor.getName());
        }
    }


    /**
     *  Make a back up copy of the log files and restart logging from scratch
     */
    public void clearLog() throws Exception {
        String _LOG = ".log";
        File bkDir = new File(logDir, serviceName + "-" + TimeUtil.getDateTime("-", "T", "-"));
        bkDir.mkdir();
        IOUtil.copy(logFile, bkDir);
        FileWriter ff = new FileWriter(logFile);

//Copy and delete the BK files
        int index = 1;
        File fileToCopy;
        while ((fileToCopy = new File(logDir, serviceName + _LOG + "." + index)).exists()) {
            IOUtil.copy(fileToCopy, bkDir);
            fileToCopy.delete();
            index++;
        }
    }

    public InputStream getAsynchronousResource(String instanceId, String resourceKey) throws Exception {
        final String resKey = new String(resourceKey);
        File requestLogDir = new File(new File(getLogDir(), ASYNCHRONOUS_INSTANCES), instanceId);
        File[] filesArray = requestLogDir.listFiles(new FileFilter() {

            public boolean accept(File file) {
                return (file.getName().indexOf(resKey) != -1);
            }
        });
        return new FileInputStream(filesArray[0]);
    }

    public InputStream getSynchronousResource(String instanceId, String resourceKey) throws Exception {
        return new FileInputStream(new File(new File(new File(getLogDir(), SYNCHRONOUS_INSTANCES), instanceId), resourceKey));
    }

    public InputStream getTimerStatus() throws Exception {
        return this.getTimerManager().getTimerStatus();
    }

    public String getWSDLUrl() throws Exception {
       return ToolboxNetwork.getEndpointURL()+"/WSDL/"+serviceName+"/"+serviceName+".wsdl";
    }

    public String getSchemaUrl() throws Exception {
       /* try {
            if (this.getServiceDescriptor().getServiceSchema().length() != 0) {
                return this.getServiceURL() + WSDL + SLASH + this.getServiceDescriptor().getServiceName() + SLASH + this.getServiceDescriptor().getServiceSchema();
            } else {
                return EMPTY_STRING;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }*/
        return "TO BE MODIFIED";
    }

    public InputStream getWSDL() throws Exception {
        try {
            return new FileInputStream(new File(new File(new File(getRoot(), WSDL), this.getServiceName()), this.getServiceName() + DOT_WSDL));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
    }

    public InputStream getSchema() throws Exception {
        try {
            return new FileInputStream(new File(new File(new File(getToolbox().getRootDir(), WSDL), this.getServiceName()), this.getServiceName()));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
    }

    public InputStream getWSInfoAsStream(String infoType) throws Exception {
        if (infoType.equals("WSDL")) {
            return getWSDL();
        } else {
            return getSchema();
        }
    }

    public HashSet getOperations() {
        HashSet operationNames = new HashSet();
        Operation[] op;

        op=this.implementedInterface.getOperations();

        for(Operation o:op)
            operationNames.add(o);
       
        return operationNames;
    }

    public TBXOperation getOperation(String name) {
        HashSet operationNames = new HashSet();
        Operation[] op;

        op=this.implementedInterface.getOperations();

        for(Operation o:op)
            if(o.getName().equals(name))
                return (TBXOperation) o;

        return null;
    }

    public HashSet getSOAPActionsNames() {
        HashSet soapActionsNames = new HashSet();
        Operation[] op;

        op=this.implementedInterface.getOperations();
       for(Operation o:op)
             soapActionsNames.add(o.getSoapAction());

        return soapActionsNames;
    }

    public void configureOperation(TBXOperation operationDescriptor) throws Exception {
        String operationName;
        File operationDir;
       
        operationName = operationDescriptor.getName();

        operationDir=new File(this.serviceRoot,"Operations");

        deleteOperation(operationDescriptor.getName());
        addOperation(operationDescriptor);

        dumpService();
        attemptToDeployWSDLAndSchemas();
    }

    public boolean hasGMLOnMapStylesheet() {
        File stylesheet;

        stylesheet = getGMLOnMapStylesheet();

        return stylesheet.exists();
    }

    public File getGMLOnMapStylesheet() {
        return new File(this.serviceRoot, "AdditionalResources/OutputOnMap/OUTPUTONMAPXSL.xsl");
    }

    public boolean hasSSEStylesheet(String operation) {
        File st;

        st = getSSEStylesheet(operation);
        return st != null && st.exists();
    }

    public File getSSEStylesheet(String operation) {
        File properties;
        ServiceAdditionalParameters servAddParameters;
        String stylesheetFileStr;

        properties = getSSEStylesheetPropertiesFile();

        servAddParameters = new ServiceAdditionalParameters(properties);
        stylesheetFileStr = servAddParameters.getParameter(operation + ".XSLFILE");

        if (stylesheetFileStr != null) {
            return new File(this.serviceRoot, stylesheetFileStr);
        } else {
            return null;
        }
    }

    public String[] getScheamsPaths()
    {
        File schemaDir;
        String path;

        schemaDir=new File(this.serviceRoot,"Schemas");

        path=schemaDir.getAbsolutePath();
        if(path.endsWith("/")==false)
            path+="/";
        return IOUtil.listDir(schemaDir, path);
    }

    protected File getSSEStylesheetPropertiesFile() {
        return new File(this.serviceRoot, "AdditionalResources/SSEPortalXSL/INTECS_TEST_OPERATION.properties");
    }

    public String getModifiedMessageId(String messageId) {
        return messageId.replaceAll("[/\\\\:*?<>|]", "_");
    }

   

    public String getOperationType(String operationName) throws Exception {
        Operation op;

        op = implementedInterface.getOperationByName(operationName);

        return op.getType();
    }

    public File getLogDir() {
        return logDir;
    }

    public void setLogDir(File logDir) {
        this.logDir = logDir;
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

    public void setToolbox(Toolbox toolbox) {
        this.toolbox = toolbox;
    }

    public DocumentBuilder getValidatingParser() {
        return validatingParser;
    }

    public void setValidatingParser(DocumentBuilder validatingParser) {
        this.validatingParser = validatingParser;
    }

    public FTPServerManager getFtpServerManager() {
        return ftpServerManager;
    }

    public void setFtpServerManager(FTPServerManager ftpServerManager) {
        this.ftpServerManager = ftpServerManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public Hashtable getRequests() {
        return requests;
    }

    public void setRequests(Hashtable requests) {
        this.requests = requests;
    }

   /* public Document getStatus() {
        return status;
    }

    public void setStatus(Document status) {
        this.status = status;
    }*/

    public File getRoot() {
        return root;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    public File getRequestDir() {
        return requestDir;
    }

    public void setRequestDir(File requestDir) {
        this.requestDir = requestDir;
    }

   

    public String getImplementdInterface() {
        it.intecs.pisa.common.tbx.Service descr;

        descr = new it.intecs.pisa.common.tbx.Service();
        descr.loadFromFile(this.getDescriptorFile());

        return descr.getImplementedInterface().getName();
    }

   


    static {
        try {
            /* Saves the localhost name in a variable to avoid retrieving by a method call each time. */
            setLocalHost(InetAddress.getLocalHost());
        } catch (Exception e) {
        }
    }

    public static InetAddress getLocalHost() {
        return localHost;
    }

    public static void setLocalHost(InetAddress aLocalHost) {
        localHost = aLocalHost;
    }

    public HashMap getSoapActions() {
        return soapActions;
    }

    public void setSoapActions(HashMap soapActions) {
        this.soapActions = soapActions;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public File getDescriptorFile() {
        return descriptorFile;
    }

    public void setDescriptorFile(File descriptorFile) {
        this.descriptorFile = descriptorFile;
    }
    
    /**
     * Initializes the ws-security elements.
     * I do this here and not in the parent class to avoid circular dependencies...
     */
    @Override
    public void initializeFromXMLDescriptor(Element serviceEl) throws Exception {
    	super.initializeFromXMLDescriptor(serviceEl);
    	if (this.hasWSSecurity()){
                if(ToolboxSecurityConfigurator.getAxis2ServiceConfigurationElement(this)==null)
                    ToolboxSecurityConfigurator.addWSSecurityLayerForService(this);

	    	this.setJKSlocation(ToolboxSecurityConfigurator.getJKSlocation(this));
	    	this.setJKSpasswd(ToolboxSecurityConfigurator.getJKSpassword(this));
	    	this.setJKSuser(ToolboxSecurityConfigurator.getJKSuser(this));
	    	this.setKeyPasswd(ToolboxSecurityConfigurator.getKeyPassword(this));
    	}
    }
    
    /**
     * Returns the WS-Security associated policy, if any.
     * @author Stefano
     * @return The DOM Element corresponding to the associated policy.
     */
    @Override
    public Element getWSSPolicy(){
    	if (this.hasWSSecurity() == false)
    		return null;
    	return ToolboxSecurityConfigurator.getWSSecurityPolicy(this, false);
    }

    /**
     * @return the serviceQueueMutex
     */
    public Semaphore getServiceQueueMutex() {
        return serviceQueueMutex;
    }

    private void restartQueuedInstances() {
        ToolboxInternalDatabase db;
        Statement stm;
        ResultSet rs;
        long instanceId;
        try
        {
            db=ToolboxInternalDatabase.getInstance();
            stm=db.getStatement();

            rs=stm.executeQuery("SELECT ID FROM T_SERVICE_INSTANCES WHERE STATUS=1 AND SERVICE_NAME='"+serviceName+"'");
            while(rs.next())
            {
                instanceId=rs.getLong("ID");
                restartQueuedInstance(instanceId);
            }
        }
        catch(Exception e)
        {

        }
    }

    private void restartQueuedInstance(long instanceId) {

        try
        {
            logger.info("Restarting instance "+instanceId);

            TBXAsynchronousOperationFirstScriptExecutor fsExecutor;

            fsExecutor = new TBXAsynchronousOperationFirstScriptExecutor(instanceId, false, logger);
            fsExecutor.start();

            logger.info("Instance "+instanceId+" restarted successfully");
        }
        catch(Exception ecc)
        {
            logger.info("Error while restarting instance "+instanceId);
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
    
}