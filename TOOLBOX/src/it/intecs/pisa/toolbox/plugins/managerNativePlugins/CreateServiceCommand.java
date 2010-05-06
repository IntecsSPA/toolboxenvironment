/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.XSD;
import it.intecs.pisa.util.Zip;
import it.intecs.pisa.toolbox.util.wsil.WSILBuilder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class CreateServiceCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        
        Hashtable<String, FileItem> mimeparts;
        String requestComingFromServiceCreationWizard;
        String redirectUrl;

        try {
            mimeparts = parseMultiMime(req);
            requestComingFromServiceCreationWizard=this.getStringFromMimeParts(mimeparts, "requestComingFromServiceCreationWizard");
            createStandardService(mimeparts);

            WSILBuilder.createWSIL();

            redirectUrl="configureService.jsp?serviceName=" + mimeparts.get("serviceName").getString();
            redirectUrl+=requestComingFromServiceCreationWizard!=null?"&requestComingFromServiceCreationWizard="+requestComingFromServiceCreationWizard:"";
            resp.sendRedirect(redirectUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
            String errorMsg = "Deploy from interface error: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

   
    protected void setServiceInformations(Service descr, Hashtable<String, FileItem> mimeparts) throws Exception {

        descr.setServiceName(mimeparts.get("serviceName").getString());
        if (mimeparts.get("serviceAbstract") != null) {
            descr.setServiceAbstract(new ByteArrayInputStream(mimeparts.get("serviceAbstract").getString().getBytes()));
        }
        if (mimeparts.get("serviceDescription") != null) {
            descr.setServiceDescription(new ByteArrayInputStream(mimeparts.get("serviceDescription").getString().getBytes()));
        }
        if (mimeparts.get("SSLFlag") != null) {
            descr.setSSLcertificate(mimeparts.get("SSLFlag").getString());
        }
        descr.setSuspendMode(mimeparts.get("suspendMode").getString());
        if (mimeparts.get("queuing") != null) {
            descr.setQueuing(Boolean.parseBoolean(mimeparts.get("queuing").getString()));
        } else {
            descr.setQueuing(false);
        }
        //Stefano
        if (mimeparts.get("WSSecurityFlag") != null){
        	descr.setWSSecurity(true);
        	
        	descr.setJKSuser(mimeparts.get("jksUserName").getString());
        	descr.setJKSpasswd(mimeparts.get("jksPasswd").getString());
        	descr.setKeyPasswd(mimeparts.get("jksPasswd").getString());
        	//TODO service jks location: set by default, right?
        	File serviceRoot = Toolbox.getInstance().getServiceRoot(descr.getServiceName());
            descr.setJKSlocation(serviceRoot+File.separator+"Resources"+File.separator+"Security"+File.separator+"service.jks");
        }
    }

    private void createStandardService(Hashtable<String, FileItem> mimeparts) throws Exception {
        Service descr;
        TBXService service = null;
        String serviceName=null;
        ServiceManager serviceManager=null;
        try {
            descr = new Service();

            serviceName=getStringFromMimeParts(mimeparts,"serviceName");
            
            descr.setServiceName(serviceName);
            setServiceInformations(descr, mimeparts);
            setServiceInterfaceInfo(descr,mimeparts);
          
            serviceManager=ServiceManager.getInstance();
            serviceManager.createService(descr);
            
            service = serviceManager.getService(serviceName);
            
            
            //Stefano:
            if (descr.isWSSecurity()){
            	//WS-Security: service directories have been created, now I can store the JKS and xacml file
            	//TODO: currently the jks file is loaded and saved in the default location...
	        	File jksFile = new File(descr.getJKSlocation());
	        	jksFile.createNewFile();
	            mimeparts.get("jksFile").write(jksFile);
	            
	            //store XACML file, if provided
	            FileItem xacml = mimeparts.get("xacmlFile");
	            if (xacml.getName().compareTo("") != 0){
	            	File xacmlFile = new File(ToolboxSecurityConfigurator.getXACMLpolicyDir(service.getServiceName())+File.separator+xacml.getName());
	            	xacmlFile.createNewFile();
		            xacml.write(xacmlFile);
	            }
            }

            
        } catch (Exception e) {
            File serviceDir;

            serviceManager=ServiceManager.getInstance();
            if (service != null) {
                serviceManager.deleteService(serviceName);
            }

            serviceDir=tbxServlet.getServiceRoot(serviceName);
            if(serviceDir.exists())
               IOUtil.rmdir(serviceDir);

            throw e;
        }

    }

    

    private void setServiceInterfaceInfo(Service descr, Hashtable<String, FileItem> mimeparts) throws Exception {
        Interface opInterface;
        String interfname = null, interfVersion = null;
        String interfType,interfMode;
        String interfnameFullString;
        File serviceSchemaDir = null;
        String serviceName;
        FileItem fullSchemaSet;
        String schemaRoot = "";
        String targetNameSpace = "";

        interfnameFullString = mimeparts.get("interfaceNames").getString();

        serviceName = descr.getServiceName();
        
        serviceSchemaDir = new File(tbxServlet.getServiceRoot(serviceName), "Schemas");

        if (interfnameFullString.equals("UserDefined") == false) {
            addRepoInterface(descr, mimeparts);
        } else {
            fullSchemaSet = mimeparts.get("fullSchemaSet");

            if (fullSchemaSet != null && fullSchemaSet.getString().equals("") == false) {
                schemaRoot = addPortalSchemaSet(mimeparts);

                interfname = "SSE";
                interfVersion = schemaRoot.substring(0, schemaRoot.indexOf("/"));
                interfType="Ordering";
                interfMode="Standard";
            } else {
                schemaRoot = addZipSchemaPackage(descr, mimeparts);
                interfname = "";
                interfVersion = "";
                interfType="Standard";
                interfMode="Standard";
            }

            if (schemaRoot != null && schemaRoot.equals("") == false) {
                targetNameSpace = XSD.getTargetNamespace(new File(serviceSchemaDir, schemaRoot));
            } else {
                targetNameSpace = "";
            }

            opInterface = new Interface();
            opInterface.setName(interfname);
            opInterface.setVersion(interfVersion);
            opInterface.setType(interfType);
            opInterface.setMode(interfMode);
            opInterface.setSchemaRoot(schemaRoot);
            opInterface.setTargetNameSpace(targetNameSpace);
     
            descr.setImplementedInterface(opInterface);
        }

        SchemaSetRelocator.updateSchemaLocationToAbsolute(serviceSchemaDir, serviceSchemaDir.toURI());
    }

    protected void addRepoInterface(Service descr, Hashtable<String, FileItem> mimeparts) throws Exception {
        String interfname = null, interfVersion = null;
        InterfacePluginManager interfman;
        Interface interf;
        Interface opInterface;
        File schemaDir;
        File serviceSchemaDir;
        String interfnameFullString;
        String interfaceType;
        String interfaceMode;

        interfman = InterfacePluginManager.getInstance();

        interfnameFullString = mimeparts.get("interfaceNames").getString();
        interfaceMode= mimeparts.get("serviceImplementationMode")!=null?mimeparts.get("serviceImplementationMode").getString():null;
        interfaceType= mimeparts.get("serviceType")!=null?mimeparts.get("serviceType").getString():"UserDefined";
        interfname = interfnameFullString.substring(0, interfnameFullString.lastIndexOf(" "));
        interfVersion = interfnameFullString.substring(interfnameFullString.lastIndexOf(" ") + 1);

        interf = (Interface) interfman.getInterfaceDescription(interfname, interfVersion,interfaceType,interfaceMode).clone();

        opInterface = new Interface();
        opInterface.setName(interfname);
        opInterface.setVersion(interfVersion);
        opInterface.setMode(interfaceMode);
        opInterface.setType(interfaceType);
        opInterface.setSchemaRoot(interf.getSchemaRoot());
        opInterface.setSchemaDir("Schemas");
        opInterface.setTargetNameSpace(interf.getTargetNameSpace());
        opInterface.setUserVariables(interf.getUserVariable());

        descr.setImplementedInterface(opInterface);

        schemaDir = interfman.getSchemaDirForInterface(interfname, interfVersion,interfaceType,interfaceMode);
        serviceSchemaDir = new File(tbxServlet.getServiceRoot(descr.getServiceName()), "Schemas");
        serviceSchemaDir.mkdirs();
        IOUtil.copyDirectory(schemaDir, serviceSchemaDir);
    }

    protected String addZipSchemaPackage(Service descr, Hashtable<String, FileItem> mimeparts) throws Exception {
        File tempZipPack;
        File serviceSchemaDir;
        String serviceName;
        String schemaRoot="";

        serviceName = descr.getServiceName();
        tempZipPack = new File(System.getProperty("java.io.tmpdir"), "tmpSchemaPack.zip");
        mimeparts.get("schemaZip").write(tempZipPack);

        serviceSchemaDir = new File(tbxServlet.getServiceRoot(serviceName), "Schemas");

        if(tempZipPack.exists() && tempZipPack.length()>0)
        {
        Zip.extractZipFile(tempZipPack.getAbsolutePath(), serviceSchemaDir.getAbsolutePath());
        tempZipPack.delete();

        String[] dirListing;
        dirListing = IOUtil.listDir(serviceSchemaDir, serviceSchemaDir.getAbsolutePath());

        if (dirListing.length > 0) {
            schemaRoot = dirListing[0];

            if (schemaRoot.startsWith("\\")) {
                schemaRoot = schemaRoot.substring(1).replace('\\', '/');
            } else {
                schemaRoot = schemaRoot.replace('\\', '/');
            }
        } else {
            schemaRoot = "";
        }
        }

        return schemaRoot;
    }

    protected String addPortalSchemaSet(Hashtable<String, FileItem> mimeparts) throws Exception {
        String schemaRoot;
        StringTokenizer tokenizer;
        String fullSchemaSet;
        String token = "";
        DOMUtil util;
        Document xsdDoc;
        String serviceName;
        File serviceRootDir;
        File schemaFile;

        util = new DOMUtil();

        serviceName = mimeparts.get("serviceName").getString();
        serviceRootDir = tbxServlet.getServiceRoot(serviceName);

        fullSchemaSet = mimeparts.get("fullSchemaSet").getString();
        tokenizer = new StringTokenizer(fullSchemaSet, " ");
        String url;
        GetMethod method;

        while (tokenizer.hasMoreTokens()) {
            token = (String) tokenizer.nextToken();

            ToolboxConfiguration toolboxConfiguration;
            toolboxConfiguration=ToolboxConfiguration.getInstance();

            String host = toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SSE_PORTAL);
          
            URL portalSchemaUrl;
            portalSchemaUrl=new URL("http://services.eoportal.org/schemas/"+token);
            URLConnection con=portalSchemaUrl.openConnection();


            xsdDoc = util.inputStreamToDocument(con.getInputStream());

            schemaFile = new File(serviceRootDir, "Schemas/" + token);
            schemaFile.getParentFile().mkdirs();

            DOMUtil.dumpXML(xsdDoc, schemaFile);
        }

        return token;
    }

   
   
}
