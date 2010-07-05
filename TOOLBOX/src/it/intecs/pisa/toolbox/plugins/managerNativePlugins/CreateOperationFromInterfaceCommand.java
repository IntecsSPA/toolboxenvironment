/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperation;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class CreateOperationFromInterfaceCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Hashtable<String, FileItem> mimeparts;
        TBXOperation operation;
        TBXService service;
        String serviceName;
        String operationName;
        String redirectUrl;
        ServiceManager serviceManager;
       

        try {
            mimeparts = parseMultiMime(req);
            
            serviceName = mimeparts.get("serviceName").getString();
            operationName = mimeparts.get("operationName").getString();
            
            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);

            operation =getOperationToAdd(mimeparts);

            service.addOperation(operation);
            service.dumpService();

            redirectUrl = "configureOperation.jsp?serviceName=" + serviceName + "&operationName=" + operationName;
            resp.sendRedirect(redirectUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
            String errorMsg = "Error configuring operation: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

    protected TBXOperation getOperationToAdd(Hashtable<String, FileItem> mimeparts) throws Exception {
        TBXOperation operationDescr;
        InterfacePluginManager interfman;
        Interface implementedInterface;
        boolean isAsynchronous;
        TBXService service;
        Operation repoOperation;
        Interface repoInterface;
        String operationName;
        String interfaceName;
        String interfaceVersion;
        String interfaceType;
        String interfaceMode;
        File serviceRoot;
        File repoRoot;
        ServiceManager servMan;

        isAsynchronous = mimeparts.get("opType").getString().equals("asynchronous");
        operationName = mimeparts.get("operationName").getString();
        interfman = InterfacePluginManager.getInstance();

        servMan=ServiceManager.getInstance();
        service=servMan.getService(mimeparts.get("serviceName").getString());
        serviceRoot=service.getServiceRoot();
        implementedInterface = service.getImplementedInterface();

        interfaceName=implementedInterface.getName();
        interfaceVersion=implementedInterface.getVersion();
        interfaceType=implementedInterface.getType();
        interfaceMode=implementedInterface.getMode();
        if(interfman.hasInterfaceDescription(interfaceName,interfaceVersion,interfaceType,interfaceMode ))
        {
            repoInterface=interfman.getInterfaceDescription(interfaceName, interfaceVersion,interfaceType,interfaceMode);
            repoOperation=repoInterface.getOperationByName(operationName);
        }
        else
        {
            repoInterface=interfman.getOutOfRepoInterfaceDescription();
            repoOperation=repoInterface.getOperationByName(isAsynchronous?Operation.OPERATION_TYPE_ASYNCHRONOUS:Operation.OPERATION_TYPE_SYNCHRONOUS);
            repoOperation.setName(operationName);
            repoOperation.setSoapAction(this.getStringFromMimeParts(mimeparts, "soapAction"));
        }

        if(isAsynchronous)
            operationDescr = new TBXAsynchronousOperation(repoOperation);
        else  operationDescr = new TBXSynchronousOperation(repoOperation);

        repoRoot=interfman.getInterfaceDescriptionPluginDirectory(interfaceName, interfaceVersion, interfaceType, interfaceMode);
        loadScripts(operationDescr,repoRoot);
        setUserDefinedScripts(mimeparts,operationDescr,serviceRoot);
        operationDescr.dumpOperationScripts();

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

    private void setUserDefinedScripts(Hashtable<String, FileItem> mimeparts,Operation operationDescr,File serviceRootDir) throws Exception {
        Script[] scripts;
       Document doc;
       DOMUtil util;
       File path;

       util=new DOMUtil();

       scripts= operationDescr.getScripts();
       for(Script s:scripts)
       {
           if(s.isOverrideByUser())
           {
               doc=util.stringToDocument(mimeparts.get(s.getType()).getString());
               s.setScriptDoc(doc);
               path=new File(s.getPath());
               s.setPath("Operations/"+operationDescr.getName()+"/"+path.getName());
           }
       }
    }





}
