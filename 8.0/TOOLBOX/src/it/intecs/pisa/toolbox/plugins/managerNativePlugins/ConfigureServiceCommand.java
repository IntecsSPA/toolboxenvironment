/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.URLReader;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.InterfacePluginManager;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.XSD;
import java.io.File;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;

/**
 *
 * @author Massimiliano
 */
public class ConfigureServiceCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Hashtable<String, FileItem> mimeparts;
        String serviceName="";
        String requestComingFromServiceCreationWizard;
        String redirectUrl;
        boolean canAutomaticallyAddOperation=false;

        try {
            mimeparts = parseMultiMime(req);
            requestComingFromServiceCreationWizard=this.getStringFromMimeParts(mimeparts, "requestComingFromServiceCreationWizard");

            serviceName=getStringFromMimeParts(mimeparts,"serviceName");
            removeOldFilesAndDirectories(mimeparts);
            setServiceInterfaceInfo(mimeparts);

            canAutomaticallyAddOperation=canAddOperation(mimeparts);

            if(requestComingFromServiceCreationWizard!=null && canAutomaticallyAddOperation==true)
                redirectUrl="createOperationFromPickList.jsp?serviceName=" + serviceName;
            else redirectUrl="viewServiceConfiguration.jsp?serviceName=" + serviceName;

            resp.sendRedirect(redirectUrl);
        } catch (Exception ex) {
            String errorMsg = "Error configuring the service"+serviceName+": " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

   

   

    private void removeOldFilesAndDirectories(Hashtable<String, FileItem> mimeparts) throws Exception {
        File serviceRootDir;
        File schemasDir;
        String[] dirListing;
        File fileToBeRemoved;
        String transformedPath;
        FileItem item;
        
        serviceRootDir = tbxServlet.getServiceRoot(getStringFromMimeParts(mimeparts,"serviceName"));
        schemasDir = new File(serviceRootDir, "Schemas");
        
        dirListing=IOUtil.listDir(schemasDir, schemasDir.getAbsolutePath());
        
        for(String f:dirListing)
        {
            
             if(f.startsWith("\\"))
                    transformedPath=f.substring(1).replace('\\', '/');
                else transformedPath=f.replace('\\', '/');

             if(transformedPath.startsWith("/"))
                 transformedPath=transformedPath.substring(1);

             item=mimeparts.get("schemaFile"+transformedPath);
            if(item!=null && item.getSize()>0)
            {
               
                fileToBeRemoved=new File(schemasDir,transformedPath);
                fileToBeRemoved.delete();
                
                item.write(fileToBeRemoved);
                
                SchemaSetRelocator.updateSchemaLocationToRelative(schemasDir, schemasDir.toURI());
                //SchemaSetRelocator.updateSchemaLocationToAbsolute(schemasDir, schemasDir.toURI());
            }
        }
    }

    private void setServiceInterfaceInfo(Hashtable<String, FileItem> mimeparts) throws Exception {
        TBXService service;
        it.intecs.pisa.common.tbx.Service descriptor;
        it.intecs.pisa.common.tbx.Operation[] implementedOperations=null;
        File schemaDir;
        File serviceSchemaDir;
        String serviceName;
        File tempZipPack;
        String targetNameSpace = "";
        OutputStream outStream;
        File textFile;
        FileItem mainSchemaItem;
        String schemaRootStr;
        Hashtable<String,Hashtable<String,String>> userVariables;
        Enumeration<String> keys;
        String userVarKey,userVarValue;
        String sslFlag;
        Interface implInterface;
        String queuing;
        ServiceManager serviceManager;
        Hashtable<String, String> userVarBundle;

        serviceName = getStringFromMimeParts(mimeparts,"serviceName");

        serviceManager=ServiceManager.getInstance();
        service = serviceManager.getService(serviceName);
        implInterface=service.getImplementedInterface();

        queuing=getStringFromMimeParts(mimeparts,"queuing");
        service.setQueuing(queuing!=null && queuing.equals("on"));
        
        service.setSuspendMode(getStringFromMimeParts(mimeparts,"suspendMode"));
        
        sslFlag=getStringFromMimeParts(mimeparts,"SSLFlag");
        if (sslFlag != null && sslFlag.equals("on")) {
            service.setSSLcertificate(getStringFromMimeParts(mimeparts,"sslCertificateLocation"));
        }
        else
        {
             service.setSSLcertificate("");
        }
        
        //version=new String(s.getVersion());
       
        if (mimeparts.get("serviceAbstract") != null) {
           textFile=new File(service.getServiceRoot(),"Info/abstract.txt");
           IOUtil.dumpString(textFile.getAbsolutePath(), getStringFromMimeParts(mimeparts,"serviceAbstract"));
        }
        if (mimeparts.get("serviceDescription") != null) {
            textFile=new File(service.getServiceRoot(),"Info/description.txt");
           IOUtil.dumpString(textFile.getAbsolutePath(), getStringFromMimeParts(mimeparts,"serviceDescription"));
        }
        //************SCHEMA**************************
        String updateSSESchemaCheck = getStringFromMimeParts(mimeparts, "updateSSESchemaCheckBox");
        String[] dirListing;

        serviceSchemaDir = new File(tbxServlet.getServiceRoot(serviceName), "Schemas");

        if(updateSSESchemaCheck!=null)
        {
            updateSSESchemaSetToLatest(mimeparts);

            dirListing=IOUtil.listDir(serviceSchemaDir, serviceSchemaDir.getAbsolutePath());
            if(dirListing.length>0)
            {
                schemaRootStr=dirListing[0];
                File schemaRoot = new File(serviceSchemaDir, schemaRootStr);
                targetNameSpace = XSD.getTargetNamespace(schemaRoot);
            }
            else
            {
                 targetNameSpace="";
                schemaRootStr="";
            }


        }
        else
        {
            schemaRootStr=getStringFromMimeParts(mimeparts,"mainSchema");

            if(schemaRootStr!=null)
            {
                schemaRootStr=getStringFromMimeParts(mimeparts,"mainSchema");
                File schemaRoot = new File(serviceSchemaDir, schemaRootStr);
                targetNameSpace = XSD.getTargetNamespace(schemaRoot);
            }
            else
            {
                targetNameSpace="";
                schemaRootStr="";
            }
        }

        implInterface.setSchemaRoot(schemaRootStr);
        implInterface.setSchemaDir("Schemas");
        implInterface.setTargetNameSpace(targetNameSpace);

        boolean validationActive;
        String valActive;

        valActive=getStringFromMimeParts(mimeparts,"validationActive");
        validationActive=valActive!=null && valActive.equals("on");
        implInterface.setValidationActive(validationActive);

        userVariables=implInterface.getUserVariable();
        keys=mimeparts.keys();

        while(keys.hasMoreElements())
        {
            userVarKey=keys.nextElement();
            if(userVarKey.startsWith("serviceVariable"))
            {
                userVarValue=getStringFromMimeParts(mimeparts,userVarKey);
                userVarBundle=userVariables.get(userVarKey.substring(15));

                userVarBundle.put(Interface.VAR_TABLE_VALUE, userVarValue);
            }
        }

        implInterface.setUserVariables(userVariables);
        
        //Stefano: WS_Security
        File serviceRoot = Toolbox.getInstance().getServiceRoot(service.getServiceName());
        if (mimeparts.get("WSSecurityFlag") == null && service.isWSSecurity()){
        	//remove JKS and XACML policies
        	ToolboxSecurityConfigurator.removeXACMLfiles(service.getServiceName());
        	ToolboxSecurityConfigurator.removeJKSfile(serviceName);
        	//remove WS-Security settings
        	service.setWSSecurity(false);
        	ToolboxSecurityConfigurator.removeWSSecurityLayerForService(serviceName);
        	//update WSDL
        	service.deployWSDL();
        }else if (mimeparts.get("WSSecurityFlag") != null && !service.isWSSecurity()){
        	//add ws-security settings
        	//assertion: all the necessary data is provided
        	service.setWSSecurity(true);
        	
        	service.setJKSuser(mimeparts.get("jksUserName").getString());
        	service.setJKSpasswd(mimeparts.get("jksPasswd").getString());
        	//TODO key password currently  is the same as the keystore one
        	service.setKeyPasswd(mimeparts.get("jksPasswd").getString());
        	
        	service.setJKSlocation(serviceRoot+File.separator+"Resources"+File.separator+"Security"+File.separator+"service.jks");
            
            //TODO: currently the jks file is loaded and saved in the default location...
        	File jksFile = new File(serviceRoot+File.separator+"Resources"+File.separator+"Security"+File.separator+"service.jks");
        	jksFile.getParentFile().mkdirs();
            jksFile.createNewFile();
            mimeparts.get("jksFile").write(jksFile);

            ToolboxSecurityConfigurator.addWSSecurityLayerForService(service);
            //update WSDL
            service.deployWSDL();
        } else if (mimeparts.get("WSSecurityFlag") != null){
        	//set the data for username and passwd
        	boolean anyChange = false;
        	String user = mimeparts.get("jksUserName").getString();
        	if ((user.compareTo("") != 0) && user.compareTo(ToolboxSecurityConfigurator.getJKSuser(service))!=0){
        		service.setJKSuser(user);
        		anyChange = true;
        	}
        	String passwd = mimeparts.get("jksPasswd").getString();
        	if ((passwd.compareTo("") != 0) && passwd.compareTo(ToolboxSecurityConfigurator.getJKSpassword(service))!=0){
        		service.setJKSpasswd(mimeparts.get("jksPasswd").getString());
        		service.setKeyPasswd(mimeparts.get("jksPasswd").getString());
        		anyChange = true;
        	}
        	if (anyChange){
        		ToolboxSecurityConfigurator.storeJKSinfo(service);
        	}
        }
        
        if (mimeparts.get("XACMLFlag") == null){
        	ToolboxSecurityConfigurator.removeXACMLfiles(service.getServiceName());
        }
        
        //store xacml file, if provided
        FileItem xacml = mimeparts.get("xacmlFile");
        if (xacml != null && xacml.getName().compareTo("") != 0){
        	//first remove the old one
        	ToolboxSecurityConfigurator.removeXACMLfiles(service.getServiceName());
        	File xacmlFile = new File(ToolboxSecurityConfigurator.getXACMLpolicyDir(service.getServiceName())+File.separator+xacml.getName());
        	xacmlFile.getParentFile().mkdirs();
            xacmlFile.createNewFile();
            xacml.write(xacmlFile);
        }
        //store jks file if provided
        //TODO: currently the jks file is loaded and saved in the default location...
    	    	
    	FileItem jksFileItem = mimeparts.get("jksFile");
        if (jksFileItem != null && jksFileItem.getName().compareTo("") != 0){
        	File jksFile;
        	File securityFolder;
        	securityFolder= ToolboxSecurityConfigurator.getSecurityResource(service);
            securityFolder.mkdirs();
            jksFile = new File(securityFolder,ToolboxSecurityConfigurator.SERVICE_KEYSTORE_FILENAME);
            jksFile.getParentFile().mkdirs();
            jksFile.createNewFile();
            jksFileItem.write(jksFile);
        }
        //end ws-security

        service.dumpToDisk(service.getDescriptorFile());
        service.attemptToDeployWSDLAndSchemas();
    }

    private void updateSSESchemaSetToLatest(Hashtable<String, FileItem> mimeparts) throws Exception {
        String serviceName;
        String newVersion;
        String ssePortal;
        String[] fileToDW;
        ServiceManager servMan;
        TBXService service;
        File schemaDir;
        servMan=ServiceManager.getInstance();
        Interface interf;

        serviceName=serviceName=getStringFromMimeParts(mimeparts,"serviceName");

        service=servMan.getService(serviceName);
        schemaDir=new File(service.getServiceRoot(),"Schemas");

        newVersion= URLReader.downloadLastVersion(serviceName);

        IOUtil.rmdir(schemaDir);
        schemaDir.mkdirs();

        URLReader.downloadSSESchemaVersionFromPortal(newVersion, schemaDir);

        interf=service.getImplementedInterface();
        interf.setName("SSE");

        if(newVersion.endsWith("/"))
            newVersion=newVersion.substring(0,newVersion.length()-1);

        interf.setVersion(newVersion);
    }

    private boolean canAddOperation(Hashtable<String, FileItem> mimeparts) {
        String serviceName;
        ServiceManager serviceManager;
        TBXService service;
        Interface implInterface,interf;
        InterfacePluginManager interfman;
        Operation[] operations;
        Script[] scripts;
        boolean canAdd=false;
        boolean opRequireUser=false;
        try
        {
            interfman = InterfacePluginManager.getInstance();
            serviceManager = ServiceManager.getInstance();

            serviceName= getStringFromMimeParts(mimeparts, "serviceName");

            service = serviceManager.getService(serviceName);
            implInterface = service.getImplementedInterface();


            interf = (Interface) interfman.getInterfaceDescription(implInterface);
            operations=interf.getOperations();

            for(Operation op:operations)
            {
                scripts=op.getScripts();

                opRequireUser=false;
                for(Script sc:scripts)
                    opRequireUser|=sc.isOverrideByUser();


                canAdd|=!opRequireUser;
                
            }
                
            return canAdd;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}
