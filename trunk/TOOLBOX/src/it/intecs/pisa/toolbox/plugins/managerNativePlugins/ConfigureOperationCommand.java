/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXSOAPInterface;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Massimiliano
 */
public class ConfigureOperationCommand extends NativeCommandsManagerPlugin {

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

            serviceManager=ServiceManager.getInstance();

            mimeparts = parseMultiMime(req);

            serviceName=mimeparts.get("serviceName").getString();
            operationName = mimeparts.get("operationName").getString();
            
            service = serviceManager.getService(serviceName);
            interf = (TBXSOAPInterface) service.getImplementedInterface();

            operationDescr=(TBXOperation) interf.getOperationByName(operationName);

            operationDescr.setSoapAction(mimeparts.get("soapAction").getString());
            
            if(operationDescr.isAsynchronous())
            {
                operationDescr.setAdmittedHosts(mimeparts.get("admittedHosts").getString());
                operationDescr.setRequestTimeout(mimeparts.get("requestTimeoutAmount").getString()+mimeparts.get("requestTimeoutMeasureUnit").getString());
                operationDescr.setPollingRate(mimeparts.get("pushPollingRateAmount").getString()+mimeparts.get("pushPollingRateMeasureUnit").getString());
                if(mimeparts.get("retry")!=null && mimeparts.get("retry").getString().equals("on"))
                {
                    operationDescr.setRetryAttempts(mimeparts.get("retryAttempts").getString());
                    operationDescr.setRetryRate(mimeparts.get("retryRateAmount").getString()+mimeparts.get("retryRateMeasureUnit").getString());
                }

            }

            scriptsNeededTypes=operationDescr.getNeededScriptsType();
            for (String type : scriptsNeededTypes)
            {
              if(operationDescr.scriptMustBeOverridden(type))
              {
                  script=(TBXScript) operationDescr.getScript(type);

                  if(mimeparts.get(type)!=null)
                  {
                      try
                      {
                          tmpFile=IOUtil.getTemporaryFile();

                          item=mimeparts.get(type);
                          item.write(tmpFile);
                          script.setScriptDoc(util.fileToDocument(tmpFile));
                          tmpFile.delete();
                      }
                      catch(Exception ecc)
                      {

                      }
                  }

              }
            }

            service.configureOperation(operationDescr);
            
            resp.sendRedirect("manageOperations.jsp?serviceName="+serviceName);
        } catch (Exception ex) {
             String errorMsg = "Error configuring operation: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

     
}
