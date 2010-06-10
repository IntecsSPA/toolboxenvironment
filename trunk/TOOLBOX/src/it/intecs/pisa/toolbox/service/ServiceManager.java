/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.toolbox.db.ServiceStatuses;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.common.tbx.lifecycle.LifeCycle;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.ServiceInfo;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.tasks.ServiceLifeCycle;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.Zip;
import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 * This class handles all services currently deployed under TOOLBOX.
 *
 * @author Massimiliano Fanciulli
 */
public class ServiceManager {

    public static final String SERVICEPUBLICDIR = "ServicePublicDir";
    private static ServiceManager manager = new ServiceManager();
    protected Hashtable<String, TBXService> services;
    protected File servicesRootDir;
    protected Logger logger;
    protected Semaphore queueMutex;

    public static ServiceManager getInstance() {
        return manager;
    }

    protected ServiceManager() {
        services=new Hashtable<String, TBXService>();
       
    }

    public void deployService(File packageFile, String serviceName) throws Exception {
        TBXService newServ;
        File serviceRoot;
        File schemaDir;

        try {
            if (isServiceDeployed(serviceName)) {
                throw new ToolboxException("Service already exists!");
            }

            serviceRoot = new File(servicesRootDir, serviceName);
            serviceRoot.mkdir();

            Zip.extractZipFile(packageFile.getAbsolutePath(), serviceRoot.getAbsolutePath());

            schemaDir=new File(serviceRoot,"Schemas");
            SchemaSetRelocator.updateSchemaLocationToAbsolute(schemaDir, schemaDir.toURI());

            newServ = new TBXService(serviceRoot, getWSDLDir(serviceName));
            ServiceStatuses.removeServiceStatus(serviceName);
            ServiceStatuses.addServiceStatus(serviceName);
            services.put(serviceName, newServ);
            newServ.start();

            newServ.attemptToDeployWSDLAndSchemas();
        } catch (Exception ex) {
            deleteService(serviceName);
            throw ex;

        }

    }

    

    public void createService(Service descriptor) throws Exception {
        String serviceName;
        File serviceRoot;
        File publicServiceDir;
        File serviceSchemaDir;
        File infoDir;
        File operationsDir;
        File resourcesDir;
        TBXService newService;
        Document descriptorDocument;
        String serviceAbstract, serviceDescription;
        File file;
        FileWriter writer;
        File descriptorFile;

        serviceName = descriptor.getServiceName();

        serviceRoot = new File(servicesRootDir, serviceName);
        serviceRoot.mkdir();

        serviceSchemaDir = new File(serviceRoot, "Schemas");
        serviceSchemaDir.mkdir();

        infoDir = new File(serviceRoot, "Info");
        infoDir.mkdir();

        operationsDir = new File(serviceRoot, "Operations");
        operationsDir.mkdir();

        resourcesDir = new File(serviceRoot, "Resources");
        resourcesDir.mkdir();
        
        //add security folder to Resources
        File securityDir = new File(resourcesDir, "Security");
        securityDir.mkdir();
        File policyDir = new File(securityDir, "Policy");//it will hold the XACML policies for the current service
        policyDir.mkdir();

        publicServiceDir = getWSDLDir(serviceName);
        publicServiceDir.mkdir();

        serviceAbstract = IOUtil.inputToString(descriptor.getServiceAbstract());
        if (serviceAbstract != null) {
            //creating abstract file
            file = new File(infoDir, "abstract.txt");
            writer = new FileWriter(file);

            writer.write(serviceAbstract);
            writer.flush();
            writer.close();
        }

        serviceDescription = IOUtil.inputToString(descriptor.getServiceDescription());
        if (serviceDescription != null) {
            //creating description file
            file = new File(infoDir, "description.txt");
            writer = new FileWriter(file);

            writer.write(serviceDescription);
            writer.flush();
            writer.close();
        }

        descriptorFile = new File(serviceRoot, TBXService.SERVICE_DESCRIPTOR_FILE);

        descriptorDocument = descriptor.createServiceDescriptor();
        DOMUtil.dumpXML(descriptorDocument, descriptorFile);
        
        if (descriptor.hasWSSecurity()){
        	try{
        		ToolboxSecurityConfigurator.addWSSecurityLayerForService(descriptor);
        	}catch(Exception ex){
        		throw new ToolboxException("Impossible to set WS-Security");
        	}
        }

        ServiceStatuses.removeServiceStatus(serviceName);
        ServiceStatuses.addServiceStatus(serviceName);
        
        newService = new TBXService(serviceRoot, publicServiceDir);
        newService.start();

        this.services.put(serviceName, newService);
        
        newService.attemptToDeployWSDLAndSchemas();

        ServiceLifeCycle.executeLifeCycleStep(LifeCycle.SCRIPT_BUILD,newService);

        log("Service " + serviceName + " has been created and started");
    }

    public void deleteService(String serviceName) throws Exception {
        File logDir;
    	TBXService service=null;
        boolean removewssecurity=false;

        try
        {
        service= this.services.get(serviceName);
        if(service!=null)
        {
            service.stop();
            removewssecurity = service.hasWSSecurity();
        }

        }
        catch(Exception e)
        {

        }

        try
        {
        ServiceStatuses.removeServiceStatus(serviceName);

        services.remove(serviceName);
        }
        catch(Exception e)
        {

        }

        try
        {
            logDir = new File(Toolbox.getInstance().getLogDir(), serviceName);
            IOUtil.rmdir(logDir);

        }
        catch(Exception ex)
        {

        }

        try {
            IOUtil.rmdir(getServiceRoot(serviceName));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            IOUtil.rmdir(getWSDLDir(serviceName));
        } catch (Exception ex) {
             ex.printStackTrace();
        }
        
      //check if the service is wrapped with ws-security
       try
       {
        if (removewssecurity)
        	ToolboxSecurityConfigurator.removeWSSecurityLayerForService(serviceName);
       }
       catch(Exception e)
       {
           e.printStackTrace();
       }

        try
        {
       deleteAllInstances(serviceName);
    }
        catch(Exception e)
        {

        }

        try
        {
            ServiceLifeCycle.executeLifeCycleStep(LifeCycle.SCRIPT_BUILD,service);
        }
        catch(Exception e)
        {

        }
    }

    public TBXService[] getServicesAsArray() {
        Iterator<TBXService> iter;
        TBXService[] servArray;
        int i=0;

        servArray=new TBXService[services.size()];
        iter = services.values().iterator();

        while(iter.hasNext())
        {
            servArray[i]=iter.next();
            i++;
        }

        return servArray;
    }

    public void enableGlobalQueuing() {
        this.queueMutex=new Semaphore(1);
    }

    public void disableGlobalQueuing()
    {
        while(queueMutex!=null && queueMutex.hasQueuedThreads() )
            queueMutex.release();
        this.queueMutex=null;
    }

    public boolean isGlobalQueuingEnabled()
    {
        return this.queueMutex!=null;
    }

    public void suspendService(String serviceName) throws Exception {
        getService(serviceName).suspend();
    }

    public void resumeService(String serviceName) throws Exception {
        getService(serviceName).resume();
    }

    public File getServicesRootDir() {
        return servicesRootDir;
    }

    public void setServicesRootDir(File servicesRootDir) {
        this.servicesRootDir = servicesRootDir;
    }

    public void startServices() {
        TBXService[] serviceArray;

       serviceArray=getServicesAsArray();
       for(TBXService service:serviceArray)
           try
              {
              if(ServiceStatuses.getStatus(service.getServiceName())==ServiceStatuses.STATUS_RUNNING)
                service.start();
            } catch (Exception ex) {
                log("Service " + service.getServiceName() + " raised an exception when trying to start it.");
            }
    }

    public void stopServices() {
       TBXService[] serviceArray;

       serviceArray=getServicesAsArray();
       for(TBXService service:serviceArray)
           try
             {
                service.stop();
            } catch (Exception ex) {
                log("Service " + service.getServiceName() + " raised an exception when trying to stop it.");
            }
    }

     public void startService(String serviceName) throws Exception {
        TBXService srv;

        srv=services.get(serviceName);
        srv.start();
    }

    public void stopService(String serviceName) throws Exception {
        TBXService srv;

        srv=services.get(serviceName);
        srv.stop();
    }

    protected void initService(String serviceName) throws Exception {
        TBXService service;
        File serviceRoot=null;

        try
        {
            serviceRoot=getServiceRoot(serviceName);
        }
        catch(Exception e)
        {
            logger.error("Cannot find root directory for service "+serviceName);
        }

        try
        {
            service=new TBXService(serviceRoot, getWSDLDir(serviceName));
            service.init();
            services.put(serviceName, service);
        }
        catch(Exception e)
        {
            logger.error("Cannot startup service "+serviceName+". The service directory has been deleted.");
            IOUtil.rmdir(serviceRoot);
        }
    }

    public void initServices() throws Exception {
        File[] serviceDirArray;
        String serviceName;

        serviceDirArray = servicesRootDir.listFiles();
        for (File ser : serviceDirArray) {
            if (ser.isDirectory()) {
                serviceName = ser.getName();

                //if(ServiceStatuses.getStatus(serviceName)==ServiceStatuses.STATUS_RUNNING)
                initService(serviceName);
            }
        }
    }

    public void tearDownService(String serviceName)
    {
        TBXService srv;

        srv=services.get(serviceName);
        srv.teardown();
    }

    public void tearDownServices() throws Exception {
        TBXService[] servs;

        servs=getServicesAsArray();
        for(TBXService ser:servs)
                ser.teardown();
            
    }

    protected File getServiceRoot(String serviceName) throws ToolboxException {
        File serviceRoot;

        serviceRoot = new File(servicesRootDir, serviceName);
        if (serviceRoot.exists()) {
            return serviceRoot;
        } else {
            throw new ToolboxException("Service doesn't exists");
        }
    }

    protected File getWSDLDir(String serviceName) {
        Toolbox tbxServlet;

        tbxServlet=Toolbox.getInstance();
        return new File(tbxServlet.getRootDir(),"WSDL/"+serviceName);
    }

    public TBXService getService(String name) throws ToolboxException {
        TBXService service;

        service = this.services.get(name);
       /* if (service == null) {
            throw new ToolboxException("No service with name: " + name);
        }*/

        return service;
    }

    protected void log(String logString)
    {
        if(logger!=null)
            logger.info(logString);
    }

    public void setLogger(Logger logger)
    {
        this.logger=logger;
    }

    public boolean isServiceDeployed(String serviceName) {
        return services.containsKey(serviceName);
    }

    Semaphore getGlobalQueueMutex() {
        return queueMutex;
    }

    private void deleteAllInstances(String serviceName) throws Exception {
        Statement stm=null;
        ResultSet rs=null;
        long id;
        InstanceHandler handler;
        try
        {
            stm=ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='"+serviceName+"'");
            while(rs.next())
            {
                id=rs.getLong("ID");
                handler=new InstanceHandler(id);
                handler.deleteInstance();
            }
        }
        finally
        {
            if(rs!=null)
                rs.close();
            
            if(stm!=null)
                stm.close();
        }

    }

    public static TBXService getService(long serviceInstanceId) throws Exception
    {
      return ServiceManager.getInstance().getService(ServiceInfo.getServiceName(serviceInstanceId));
    }
}
