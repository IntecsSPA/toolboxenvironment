/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.common.tbx.lifecycle.LifeCycle;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperation;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXSOAPInterface;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import it.intecs.pisa.toolbox.service.tasks.ServiceLifeCycle;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class BulkOperationAddCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        String operationName;
        ServiceManager serviceManager;
        TBXService service;
        TBXSOAPInterface interf;
        TBXOperation operationDescr;
        Hashtable<String, FileItem> mimeparts;
        String[] scriptsNeededTypes;
        TBXScript script;
        DOMUtil util;
        FileItem item;
        File tmpFile;
        Set<String> keyset;
        Iterator<String> iterator;
        String itemKey;
        Map paramMap;

        try {
            util=new DOMUtil();

            serviceManager=ServiceManager.getInstance();

            serviceName=req.getParameter("serviceName");

            service = serviceManager.getService(serviceName);
            interf = (TBXSOAPInterface) service.getImplementedInterface();
            paramMap = req.getParameterMap();
            iterator=paramMap.keySet().iterator();

            while(iterator.hasNext())
            {
                itemKey=iterator.next();

                if(itemKey.startsWith("operationToAdd"))
                {
                    operationName =itemKey.substring(14);
                    addOperationToService(service,operationName);
                }

            }

            resp.sendRedirect("viewServiceConfiguration.jsp?serviceName="+serviceName);
        } catch (Exception ex) {
             String errorMsg = "Error configuring operation: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

    private void addOperationToService(TBXService service, String operationName) {
       TBXOperation operToAdd;
       
       try
       {
           operToAdd=getOperationToAdd(service,operationName);
           service.addOperation(operToAdd);
           service.dumpService();

           ServiceLifeCycle.executeLifeCycleStep(LifeCycle.SCRIPT_BUILD,service,operationName);
       }
       catch(Exception e)
       {
            e.printStackTrace();
       }

    }

    protected TBXOperation getOperationToAdd(TBXService service, String operationName) throws Exception
    {
       Operation repoOperation;
       Operation operationDescr;
       Interface repoInterface;
       Interface implementedInterface;
       String interfaceName;
       String interfaceVersion;
       String interfaceType;
       String interfaceMode;
       InterfacePluginManager interfman;
       boolean isAsynchronous;
       File repoRoot;
       File serviceRoot;

       implementedInterface = service.getImplementedInterface();
       interfaceName=implementedInterface.getName();
       interfaceVersion=implementedInterface.getVersion();
       interfaceType=implementedInterface.getType();
       interfaceMode=implementedInterface.getMode();

       interfman = InterfacePluginManager.getInstance();
       repoInterface=interfman.getInterfaceDescription(interfaceName, interfaceVersion,interfaceType,interfaceMode);
       repoOperation=(Operation) repoInterface.getOperationByName(operationName).clone();

       isAsynchronous=repoOperation.isAsynchronous();
       if(isAsynchronous)
            operationDescr = new TBXAsynchronousOperation(repoOperation);
       else operationDescr = new TBXSynchronousOperation(repoOperation);

       repoRoot=interfman.getInterfaceDescriptionPluginDirectory(interfaceName, interfaceVersion, interfaceType, interfaceMode);
       loadScripts(operationDescr,repoRoot);

       serviceRoot=service.getServiceRoot();
       dumpOperationScripts(operationDescr,serviceRoot);

       return (TBXOperation) operationDescr;
    }

    private void loadScripts(Operation operationDescr,File startDir) throws Exception {
       Script[] scripts;
       File scriptFile;
       File path;
       Document doc;
       DOMUtil util;

       util=new DOMUtil();

       scripts= operationDescr.getScripts();
       for(Script s:scripts)
       {
           if(s.isOverrideByUser()==false)
           {
               path=new File(s.getPath());
               scriptFile=new File(startDir,s.getPath());
               doc=util.fileToDocument(scriptFile);
               s.setScriptDoc(doc);
               s.setPath("Operations/"+operationDescr.getName()+"/"+path.getName());

           }
       }
    }

     private void dumpOperationScripts(Operation operationDescr,File serviceRootDir) throws Exception {
       Script[] scripts;
       File scriptFile;
       File opDir;

       opDir=new File(serviceRootDir,"Operations/"+operationDescr.getName());
       opDir.mkdir();

       scripts= operationDescr.getScripts();
       for(Script s:scripts)
       {
           scriptFile=new File(serviceRootDir,s.getPath());
           DOMUtil.dumpXML(s.getScriptDoc(), scriptFile);
       }
    }
     
}
