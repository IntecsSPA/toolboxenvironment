

package it.intecs.pisa.toolbox.plugins.wpsPlugin.manager;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.engine.WPSEngine;
import java.io.File;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSOperation {


    public static Operation newWPSSyncOperation(String processingName) throws Exception{
        Operation operationDescr = new Operation();
        operationDescr.setName(WPSEngine.EXECUTE_OPERATION_PREFIX+processingName);
        operationDescr.setAdmittedHosts("");
        operationDescr.setType("synchronous");
        operationDescr.setSoapAction(WPSEngine.EXECUTE_OPERATION_PREFIX+processingName);
        operationDescr.setRequestTimeout("1h");
        operationDescr.setInputType(WPSEngine.EXECUTE_OPERATION_PREFIX+processingName);
        operationDescr.setOutputType(WPSEngine.RESPONSE_MESSAGE_NAME);
        operationDescr.setInputTypeNameSpace(WPSUtil.WPS_TARGET_SCHEMA);
        operationDescr.setOutputTypeNameSpace(WPSEngine.EXECUTE_POST_TARGET_NAMESPACE);

       return operationDescr;

    }

    public static Operation newWPSAsyncOperation(File newServicePath, String processingName) throws Exception{
        // Create WPS Engine Asynchoronus Operation
        Operation operationAsyncDescr=null;
        String execupteOperationAsyncName=WPSEngine.EXECUTE_OPERATION_PREFIX+processingName+WPSEngine.EXECUTE_ASYNC_OPERATION_SUFIX;
        operationAsyncDescr = new Operation();
        operationAsyncDescr.setName(execupteOperationAsyncName);
        operationAsyncDescr.setType("asynchronous");
        operationAsyncDescr.setPollingRate("15s");
        operationAsyncDescr.setRequestTimeout("1d");
        operationAsyncDescr.setSoapAction(execupteOperationAsyncName);
        operationAsyncDescr.setInputType(WPSEngine.EXECUTE_OPERATION_PREFIX+processingName);
        operationAsyncDescr.setOutputType(WPSEngine.RESPONSE_MESSAGE_NAME);
        operationAsyncDescr.setInputTypeNameSpace(WPSUtil.WPS_TARGET_SCHEMA);
        operationAsyncDescr.setOutputTypeNameSpace(WPSEngine.EXECUTE_POST_TARGET_NAMESPACE);
        operationAsyncDescr.setCallbackInputType(WPSEngine.EXECUTE_ASYNC_PUSH_MESSAGE_NAME); //response third
        operationAsyncDescr.setCallbackOutputType(WPSEngine.EXECUTE_ASYNC_PUSH_RESPONSE_NAME); //ack
        operationAsyncDescr.setCallbackSoapAction(execupteOperationAsyncName+"_statusUpdate"); // Soap Action Execute
        operationAsyncDescr.setCallbackInputTypeNameSpace(WPSEngine.EXECUTE_POST_TARGET_NAMESPACE); // namespace third
        operationAsyncDescr.setCallbackOutputTypeNameSpace(WPSEngine.EXECUTE_POST_TARGET_NAMESPACE); // namespace ack

       return  operationAsyncDescr;
    }

}
