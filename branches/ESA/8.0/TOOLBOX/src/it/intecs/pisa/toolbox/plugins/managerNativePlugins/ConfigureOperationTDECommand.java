/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXSOAPInterface;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Massimiliano
 */
public class ConfigureOperationTDECommand extends NativeCommandsManagerPlugin {

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

        try {
            util=new DOMUtil();

            serviceName=req.getParameter("serviceName");
            operationName = req.getParameter("operationName");

            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);

            interf = (TBXSOAPInterface) service.getImplementedInterface();
            operationDescr=(TBXOperation) interf.getOperationByName(operationName);

            operationDescr.setPollingRate(req.getParameter("pollingRate"));

            DOMUtil.dumpXML(service.createServiceDescriptor(), service.getDescriptorFile());
            //service.configureOperation(operationDescr);
            
        } catch (Exception ex) {
             String errorMsg = "Error configuring operation: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

     
}
