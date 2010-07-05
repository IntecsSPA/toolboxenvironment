package it.intecs.pisa.toolbox.plugins.wpsPlugin.manager;


import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.engine.WPSEngine;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.Zip;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSUtil {

    private static String WPS_PLUGIN_CONF_FILE="../resources/plugin.xml";
    private static String WPS_PLUGIN_ENGINES_XPATH="engine";
    private static String ENGINE_CLASS_NAME_ATTRIBUTE="className";
    public static String ENGINE_CLASS_PACKAGE="it.intecs.pisa.toolbox.plugins.wpsPlugin.engine.";
    public static String  WPS_TARGET_SCHEMA ="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap";
    private static String SERVICE_DESCRIPTOR_TARGET_NAMESPACE_NODENAME ="targetNameSpace";
    private static String SERVICE_DESCRIPTOR_INTERFACE_NODENAME ="interface";

    
    private static String SERVICE_DESCRIPTOR_FILE_NAME="serviceDescriptor.xml";

    /* INTERFACE VARIABLES*/
    private static String INTERFACE_RESOURCES_PATH="Resources/Interface/";
    private static String INTERFACE_FACELET_XLST_PATH="AdditionalResources/WPS/XSL/Interface/generate_facelet.xsl";
    private static String INTERFACE_XSL_XLST_PATH="AdditionalResources/WPS/XSL/Interface/generate_xslt.xsl";
    private static String INTERFACE_FACELET_FILE_NAME="order.xhtml";
    private static String INTERFACE_XSL_FILE_NAME="transformation.xsl";

    /* PARSE DESCRIBE PROCESS VARIABLES*/
    public static String PARSE_DESCRIBE_OP="parseDescribe";
    public static String DESCRIBE_PROCESS_DESCRIPTION_XPATH="//wps:ProcessDescriptions/ProcessDescription";
    public static String DESCRIBE_PROCESS_DESCRIPTION_IDENTIFIER_XPATH="//wps:ProcessDescriptions/ProcessDescription/ows:Identifier";
    public static String DESCRIBE_PROCESS_STORE_SUPPORTED_ATRRIBUTE="storeSupported";
    public static String DESCRIBE_FILE_PREFIX="DescribeInformation_";
    public static String DESCRIBE_RESPONSE_SCHEMA_LOCATION ="Schemas/wps/1.0.0/wpsDescribeProcess_response.xsd";

    /*WPS PROCESSING INFORMATION DOCUMENT */
    private static String INFORMATION_PROCESSING_FILE_NAME_SUFFIX="_Info.xml";
    private static String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    private static String INFORMATION_PROCESSING_ELEMENT="ProcessingINFO";
    private static String INFORMATION_SERVICE_NAME_ELEMENT="ServiceName";
    private static String INFORMATION_NAME_ELEMENT="Name";
    private static String INFORMATION_TYPE_ELEMENT="Type";
    private static String INFORMATION_ASYNCHRONOUS_ELEMENT="Asynchronous";
    private static String INFORMATION_CREATION_DATE_ELEMENT="CreationDate";
    //private static String INFORMATION_IMPORTED_ELEMENT="Imported";

    /* UPDATE WPS SCHEMA*/
    private static String UPDATE_WPS_ALL_SCHEMA_XLST_PATH="AdditionalResources/WPS/XSL/GenerateWPSSoapAllSchema.xsl";
    private static String GENERATE_WPS_EXECUTE_SCHEMA_XLST_PATH="AdditionalResources/WPS/XSL/GenerateSoapExecuteSchema.xsl";
    private static String SCHEMA_FOLDER ="Schemas/";
    private static String ADDITIONAL_RESOURCES_PATH="AdditionalResources/WPS/";
    private static String DESCRIBE_TREE_FILE_NAME="DescribeTreeDump.xml";
    private static String ALL_SCHEMA_LOCATION ="Schemas/wps/1.0.0/wpsAll.xsd";
    private static String SCHEMA_SERVICE_ALL_FILE_NAME ="WPSSoapAll.xsd";

    protected DOMUtil domUtil=null;
    protected Toolbox tbxServlet;
    protected ToolboxConfiguration toolboxConfigurator;
    protected Transformer transformer;

    /*CREATE PROCESS VARIABLES*/
    public static String CREATE_PROCESS_OP="generateOperation";
    public static String DELETE_PROCESS_OP="deleteOperation";
    public static String ENGINE_PREFIX="WPS";

    public static String DESCRIBE_PROCESS_PATH ="AdditionalResources/WPS/DescribeProcess";
    public static String INFO_PROCESS_PATH ="AdditionalResources/WPS/InfoProcess";

    public WPSUtil(){
        domUtil= new DOMUtil();
        toolboxConfigurator= ToolboxConfiguration.getInstance();
        tbxServlet = Toolbox.getInstance();

    }

    public List<WPSEngine> getWPSEngines() throws Exception {
       List<WPSEngine> listEngins=new ArrayList();
       int i;
       Class wpsEngineClass;
       String engineClassName;
       URL wpsPluginXMLUrl=this.getClass().getResource(WPS_PLUGIN_CONF_FILE);
       Document wpsPluginInfo=domUtil.inputStreamToDocument(wpsPluginXMLUrl.openStream());
       NodeList engines= wpsPluginInfo.getElementsByTagName(WPS_PLUGIN_ENGINES_XPATH);
       for(i=0; i<engines.getLength(); i++ ){
         engineClassName=((Element)engines.item(i)).getAttribute(ENGINE_CLASS_NAME_ATTRIBUTE);
         wpsEngineClass=Class.forName(ENGINE_CLASS_PACKAGE+engineClassName);
         listEngins.add((WPSEngine) wpsEngineClass.newInstance());
       }
      return listEngins;
    }

    public void deployWPSTempateServiceWithZipPackage(File packageFile, String serviceName) throws Exception {
            File webinfDir,servicesDir,descriptorFile=null,packageDeployDir = null;
            File schemaDir;
            Document descriptor;
            DOMUtil util;
            String name;
            Element root;

            util = new DOMUtil();
            webinfDir = new File(tbxServlet.getRootDir(), "WEB-INF");
            servicesDir = new File(webinfDir, "services");
            packageDeployDir = new File(servicesDir, serviceName);

            packageDeployDir.mkdir();
            Zip.extractZipFile(packageFile.getAbsolutePath(), packageDeployDir.getAbsolutePath());

            schemaDir = new File(packageDeployDir, "Schemas");
            schemaDir.mkdir();
            SchemaSetRelocator.updateSchemaLocationToAbsolute(schemaDir, schemaDir.toURI());

            descriptorFile = new File(packageDeployDir, SERVICE_DESCRIPTOR_FILE_NAME);
            descriptor = util.fileToDocument(descriptorFile);

            root = descriptor.getDocumentElement();
            name = root.getAttribute("serviceName");
            if (name.equals(serviceName) == false) {
                root.setAttribute("serviceName", serviceName);
            }

          //  toolboxConfigurator.createService(descriptor);

            ServiceManager serviceManager;
            serviceManager=ServiceManager.getInstance();
            serviceManager.startService(serviceName);

    }

    public void generatePortalInterfaceResources(File newServicePath, String processingName, Document describeDocument) throws Exception{
        File stylesheet;
        Document xslDocument;
        File interfaceResourcesFolder=new File(newServicePath,INTERFACE_RESOURCES_PATH);
        interfaceResourcesFolder.mkdirs();
        File interfaceOperationResourcesFolder=new File(interfaceResourcesFolder,processingName);
        interfaceOperationResourcesFolder.mkdirs();
        /* XSLT INTERFACE -- START*/
            // facelet
            stylesheet=new File(newServicePath, INTERFACE_FACELET_XLST_PATH);
            if(stylesheet.exists()){
               xslDocument=domUtil.fileToDocument(stylesheet);
               transformer = TransformerFactory.newInstance().newTemplates(new DOMSource(xslDocument)).newTransformer();
               transformer.transform(new StreamSource(
                       DOMUtil.getDocumentAsInputStream(describeDocument)),
                       new StreamResult(new FileOutputStream(new File(interfaceOperationResourcesFolder,
                       INTERFACE_FACELET_FILE_NAME))));
            }

            // XSLT
            stylesheet=new File(newServicePath, INTERFACE_XSL_XLST_PATH);
            if(stylesheet.exists()){
               xslDocument=domUtil.fileToDocument(stylesheet);
               transformer = TransformerFactory.newInstance().newTemplates(new DOMSource(xslDocument)).newTransformer();
               transformer.transform(new StreamSource(
                       DOMUtil.getDocumentAsInputStream(describeDocument)),
                       new StreamResult(new FileOutputStream(new File(interfaceOperationResourcesFolder,
                       INTERFACE_XSL_FILE_NAME))));
            }
      /* XSLT INTERFACE -- END*/
    }

   public void updateWPSSoapSchema(File newServicePath, Document describeDocument, String serviceName, String processingName) throws Exception{
       File stylesheet=new File(newServicePath, UPDATE_WPS_ALL_SCHEMA_XLST_PATH);
       Document xslDocument;
       new File(newServicePath, SCHEMA_FOLDER+serviceName).mkdir();
       Document treeDescribeFolder=IOUtil.getDocumentFromDirectory(new File(newServicePath, DESCRIBE_PROCESS_PATH).getAbsolutePath());
       DOMUtil.dumpXML(treeDescribeFolder, new File(newServicePath, ADDITIONAL_RESOURCES_PATH+DESCRIBE_TREE_FILE_NAME));

       if(stylesheet.exists()){
          xslDocument=domUtil.fileToDocument(stylesheet);
          transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
          transformer.setParameter("wpsSchemaLocation", /*new File(newServicePath, */ALL_SCHEMA_LOCATION/*).getAbsolutePath()*/);
          transformer.setParameter("wpsExecuteSchemaFolder", /*new File(newServicePath, */SCHEMA_FOLDER+serviceName/*).getAbsolutePath()*/);
          transformer.transform(new DOMSource(treeDescribeFolder), new StreamResult(new FileOutputStream(new File(newServicePath,SCHEMA_FOLDER+SCHEMA_SERVICE_ALL_FILE_NAME))));
       }

       stylesheet=new File(newServicePath, GENERATE_WPS_EXECUTE_SCHEMA_XLST_PATH);
       if(stylesheet.exists()){
           xslDocument=domUtil.fileToDocument(stylesheet);
           transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
           transformer.setParameter("wpsSchemaLocation", /*new File(newServicePath, */ALL_SCHEMA_LOCATION/*).getAbsolutePath()*/);
           transformer.transform(new DOMSource(describeDocument), new StreamResult(new FileOutputStream(new File(newServicePath,SCHEMA_FOLDER+serviceName+"/"+processingName+".xsd"))));
       }

       ServiceManager serviceManager=ServiceManager.getInstance();
       TBXService tbxService=serviceManager.getService(serviceName);
       tbxService.attemptToDeployWSDLAndSchemas();
    }


   public static boolean isWPS(Document serviceDescriptor){
    Element root = serviceDescriptor.getDocumentElement();
    NodeList interfaceList=root.getElementsByTagNameNS(
                            ((Node)root).getNamespaceURI(),
                            SERVICE_DESCRIPTOR_INTERFACE_NODENAME);

    NodeList tnsList=((Element)interfaceList.item(0)).getElementsByTagNameNS(
                            ((Node)root).getNamespaceURI(),
                            SERVICE_DESCRIPTOR_TARGET_NAMESPACE_NODENAME);
    
    String targetNameSpace=tnsList.item(0).getTextContent();
    return targetNameSpace.equalsIgnoreCase(WPS_TARGET_SCHEMA);
   }


   public void createWPSProcessingInformationDocument(File infoProcessPath, String serviceName, String processingName, String engineType, boolean async) throws Exception{
        Document processingInfoDoc=domUtil.newDocument();
        Element root=processingInfoDoc.createElement(INFORMATION_PROCESSING_ELEMENT);
        Element el=processingInfoDoc.createElement(INFORMATION_NAME_ELEMENT);
        el.setTextContent(processingName);
        root.appendChild(el);
        el=processingInfoDoc.createElement(INFORMATION_SERVICE_NAME_ELEMENT);
        el.setTextContent(serviceName);
        root.appendChild(el);
        el=processingInfoDoc.createElement(INFORMATION_ASYNCHRONOUS_ELEMENT);
        el.setTextContent(""+async);
        root.appendChild(el);
        el=processingInfoDoc.createElement(INFORMATION_TYPE_ELEMENT);
        el.setTextContent(engineType);
        root.appendChild(el);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        el=processingInfoDoc.createElement(INFORMATION_CREATION_DATE_ELEMENT);
        el.setTextContent(sdf.format(cal.getTime()));
        root.appendChild(el);
        processingInfoDoc.appendChild(root);
        File infoFile= new File(infoProcessPath, processingName+INFORMATION_PROCESSING_FILE_NAME_SUFFIX);
        IOUtil.copy(DOMUtil.getDocumentAsInputStream(processingInfoDoc), new FileOutputStream(infoFile));
   }
   
   public void deleteWPSProcessingInformationDocument(File describeProcessPath, String serviceName, String processingName){
    File infoFile= new File(describeProcessPath, processingName+INFORMATION_PROCESSING_FILE_NAME_SUFFIX);
    infoFile.delete();
   
   }

   public void deleteWPSProcessingDescribeResources(File describeProcessPath, String serviceName, String processingName){
    File describeFile= new File(describeProcessPath, DESCRIBE_FILE_PREFIX+processingName+".xml");
    describeFile.delete();
    deleteWPSProcessingInformationDocument(describeProcessPath,serviceName,processingName);

   }

   public void deleteWPSProcessingEngineResources(File newServicePath, String processingName, String processingType) throws Exception{
    Class wpsEngineClass = Class.forName(ENGINE_CLASS_PACKAGE+ENGINE_PREFIX+processingType);
    WPSEngine wpsEngine = (WPSEngine) wpsEngineClass.newInstance();
    wpsEngine.deleteScriptEngine(newServicePath, processingName);
   }


}
