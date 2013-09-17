/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceVariable;
import it.intecs.pisa.toolbox.db.OperationInfo;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import java.io.File;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TBXOperationOnErrorActions {
    public static Document processErrorRequest(long seviceInstanceId,String scriptToExecute, String errorMsg) throws ToolboxException, Exception {
        Logger serviceLogger;
        File errorResultFile;
        long id;
        String type;
        Document doc;
        TBXService service;
        TBXOperation op;

        try
        {
            service=InstanceInfo.getService(seviceInstanceId);

            serviceLogger = service.getLogger();
            serviceLogger.error(errorMsg);

            id=XMLResourcesPersistence.getInstance().getNewResourceFile();
            errorResultFile=XMLResourcesPersistence.getInstance().getXMLFile(id);

            op=service.getOperation(OperationInfo.getOperationName(seviceInstanceId));

            type = scriptToExecute.equals(TBXScript.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER) ? InstanceResources.TYPE_RESPONSE_ERROR_BUILDER_EXECUTION : InstanceResources.TYPE_GLOBAL_ERROR_SCRIPT_EXECUTION;
            InstanceResources.storeResourceEntry(id, seviceInstanceId,type);

            InstanceVariable.storeVarIntoDB(seviceInstanceId, "errorMessage", InstanceVariable.STRING, errorMsg);

            if(service.getImplementedInterface()!=null && service.getImplementedInterface().getTargetNameSpace()!=null)
                InstanceVariable.storeVarIntoDB(seviceInstanceId, "targetNamespace", InstanceVariable.STRING, service.getImplementedInterface().getTargetNameSpace());

            InstanceHandler handler;

            handler=new InstanceHandler(seviceInstanceId);
            doc= (Document) handler.executeScript(scriptToExecute, false);

            return doc;
        }
        catch(Exception e)
        {
            throw new Exception("Cannot execute error script",e);
        }
    }

}
