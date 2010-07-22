
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSCommands;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSUtil;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.Zip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class ImportServicesGroupCommand extends NativeCommandsManagerPlugin {

  /*  private static String IMPORT_SERVICE_WPS_TARGET_SCHEMA ="http://toolbox.pisa.intecs.it/soap/WPS/WPSsoap";
    private static String SERVICE_DESCRIPTOR_TARGET_NAMESPACE_XPATH ="service/interface/targetNameSpace";*/


    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        FileItem item = null;
        ZipEntry entry= null;
        String entryFileName;
        int i;
        DiskFileUpload upload = new DiskFileUpload();
        List items = upload.parseRequest(req);
        Boolean allServicesDeployed=true;
        ArrayList notDeployedService= new ArrayList();
        ArrayList notDeployedServiceError= new ArrayList();
     

        item = (FileItem) items.get(0);
        File packageFile = new File(System.getProperty("java.io.tmpdir"), item.getName());
        item.write(packageFile);
        ZipFile zipFile = new ZipFile(packageFile);

        File tempFile= null;
        Enumeration entries = zipFile.entries();

        while(entries.hasMoreElements()) {
              entry = (ZipEntry)entries.nextElement();

              entryFileName=entry.getName().substring(0,entry.getName().indexOf("."));
              tempFile= File.createTempFile(entryFileName, "zip");
              IOUtil.copy(zipFile.getInputStream(entry), new FileOutputStream(tempFile));
              try {
                  if(this.isNewService(entryFileName))
                    deployServiceWithZipPackage(tempFile, entryFileName, resp);
                  else{
                    allServicesDeployed=false;
                    notDeployedService.add(entryFileName);
                    notDeployedServiceError.add("Service already defined");
                  }
              } catch (Exception e) {
                allServicesDeployed=false;
                notDeployedService.add(entryFileName);
                notDeployedServiceError.add(e.getMessage());
            }

        }
      zipFile.close();

 
      String result;
      if(allServicesDeployed)
          result="All Services are deployed";
      else{
        result="The following services are not deployed: \n";
        for(i=0; i<notDeployedService.size(); i++){
            result+=" - Service \""+notDeployedService.get(i)+"\"  --> " + notDeployedServiceError.get(i)+"\n";
        }

      }
      PrintWriter out = resp.getWriter();
      out.println(result);
     resp.setStatus(resp.SC_OK);

    }

    private void deployServiceWithZipPackage(File packageFile, String serviceName, HttpServletResponse resp) throws Exception {
        ServiceManager serviceManager;
        serviceManager=ServiceManager.getInstance();

        try {      
            File webinfDir;
            File servicesDir;
            File packageDeployDir = null;
            File descriptorFile;
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

            File statusFile= new File(packageDeployDir,"serviceStatus.xml");
            if(statusFile.exists())
               statusFile.delete();
            
            //updating schemas
            schemaDir = new File(packageDeployDir, "Schemas");
            schemaDir.mkdir();
            SchemaSetRelocator.updateSchemaLocationToAbsolute(schemaDir, schemaDir.toURI());

            descriptorFile = new File(packageDeployDir, "serviceDescriptor.xml");
     
            descriptor = util.fileToDocument(descriptorFile);

            root = descriptor.getDocumentElement();
            name = root.getAttribute("serviceName");
            if (name.equals(serviceName) == false) {
                root.setAttribute("serviceName", serviceName);
            }

            if(WPSUtil.isWPS(descriptor)){
                WPSCommands commands= new WPSCommands();
                int i=0;

                commands.importWPSService(serviceName);
                File describe=null;
                File describeFolder=new File(packageDeployDir,WPSCommands.DESCRIBE_PROCESS_PATH);
                String[] describes = describeFolder.list();
                if (describes != null) {
                    for (i=0; i<describes.length; i++) {
                        describe = new File(describeFolder, describes[i]);
                        commands.importWPSProcess(util.fileToDocument(describe), packageDeployDir ,serviceName);
                    }
                }
            }else{
                serviceManager.startService(serviceName);  
           }
        } catch (Exception ex) {
            TBXService service = serviceManager.getService(serviceName);

            if (service != null) {
                serviceManager.deleteService(serviceName);
            }
            logger.info("Cannot deploy service");
            throw new Exception();

        }

    }

   private boolean isNewService(String newServiceName){
       ServiceManager serviceManager;
       String serviceName;
       boolean result=true;
       serviceManager=ServiceManager.getInstance();
        TBXService [] services= serviceManager.getServicesAsArray();
        for(int i=0; i<services.length; i++){
            serviceName=services[i].getServiceName();
            if(serviceName.equals(newServiceName)){
               result=false;
               break;
            }
        }
       return result;
   }


  
}