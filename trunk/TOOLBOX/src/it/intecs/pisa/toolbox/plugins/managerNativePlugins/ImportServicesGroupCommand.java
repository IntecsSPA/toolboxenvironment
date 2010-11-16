
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.constants.ServiceConstants;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSCommands;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSUtil;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SchemaSetRelocator;
import it.intecs.pisa.util.Zip;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
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
public class ImportServicesGroupCommand extends NativeCommandsManagerPlugin {

    private static String ID_PARAMETER="id";
    private static String NEW_SERVICE_NAMES_PARAMETER="newServicesName";
    private static String OLD_SERVICE_NAMES_PARAMETER="oldServicesName";
    private static String FILES_STORED_FOLDER_PATH="../GUIManagerPlugin/resources/storedData/";

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        ZipEntry entry= null;
        String entryFileName;
        File tempFile= null;
        Enumeration entries;
        int i;

        String storedZipFileID=req.getParameter(ID_PARAMETER);
        String newServiceNames=req.getParameter(NEW_SERVICE_NAMES_PARAMETER);
        String oldServiceNames=req.getParameter(OLD_SERVICE_NAMES_PARAMETER);
        
        Boolean allServicesDeployed=true;
        ArrayList notDeployedService= new ArrayList();
        ArrayList notDeployedServiceError= new ArrayList();
 
        ZipFile zipFile = new ZipFile(new File(pluginDir, FILES_STORED_FOLDER_PATH+ storedZipFileID));
        entry=zipFile.getEntry(ServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME);

        if(entry !=null){

            /*Single Service*/
            tempFile= File.createTempFile(newServiceNames, "zip");
            IOUtil.copy(new FileInputStream(
                    new File(pluginDir, FILES_STORED_FOLDER_PATH+ storedZipFileID)),
                    new FileOutputStream(tempFile));
            try {
                 if(this.isNewService(newServiceNames))
                    deployServiceWithZipPackage(tempFile, newServiceNames, resp);
                 else{
                   allServicesDeployed=false;
                   notDeployedService.add(newServiceNames);
                   notDeployedServiceError.add("Service already defined");
                }
             } catch (Exception e) {
                allServicesDeployed=false;
                notDeployedService.add(newServiceNames);
                notDeployedServiceError.add(e.toString());
            }

        }else{

           /*Service Group*/
           i=0;
           entries=zipFile.entries();
           String[] newServices=newServiceNames.split(",");
           String[] oldServices=oldServiceNames.split(",");
            while(entries.hasMoreElements() && i<oldServices.length) {
                  entry = (ZipEntry)entries.nextElement();
                  entryFileName=entry.getName().substring(0,entry.getName().indexOf("."));
                  if(entryFileName.equals(oldServices[i])){
                      tempFile= File.createTempFile(newServices[i], "zip");
                      IOUtil.copy(zipFile.getInputStream(entry), new FileOutputStream(tempFile));
                      try {
                          if(this.isNewService(newServices[i]))
                            deployServiceWithZipPackage(tempFile, newServices[i], resp);
                          else{
                            allServicesDeployed=false;
                            notDeployedService.add(newServices[i]);
                            notDeployedServiceError.add("Service already defined");
                          }
                      } catch (Exception e) {
                        allServicesDeployed=false;
                        notDeployedService.add(newServices[i]);
                        notDeployedServiceError.add(e.toString());
                    }
                    i++;
                }
            }
          zipFile.close();
          
        }


        String result;
        if(allServicesDeployed)
              result="All Services have been deployed";
        else{
            result="The following services are not deployed: \n";
            for(i=0; i<notDeployedService.size(); i++)
                result+=" - Service \""+notDeployedService.get(i)+"\"  --> " + notDeployedServiceError.get(i)+"\n";
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

            descriptorFile = new File(packageDeployDir, ServiceConstants.SERVICE_DESCRIPTOR_FILE_NAME);
     
            descriptor = util.fileToDocument(descriptorFile);

            root = descriptor.getDocumentElement();
            name = root.getAttribute("serviceName");
            if (name.equals(serviceName) == false) {
                root.setAttribute("serviceName", serviceName);
            }

            util.dumpXML(descriptor, descriptorFile);

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
                serviceManager.createService(serviceName);
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
