/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.log.ErrorMailer;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.toolbox.db.ServiceStatuses;
import it.intecs.pisa.soap.toolbox.exceptions.OperationExecutionException;
import it.intecs.pisa.toolbox.constants.MiscConstants;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.SOAPHeaderExtractor;
import it.intecs.pisa.toolbox.timers.TimeoutManager;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.w3c.dom.Document;

public class TBXAsynchronousOperation extends TBXOperation {
    protected String parentServiceName;

    public TBXAsynchronousOperation() {
        super();
    }

    public TBXAsynchronousOperation(Operation op) {
        super(op);
    }

    @Override
    public void start() {
        TBXService parentService;

        super.init();

         parentService = getParentService();
         parentServiceName = parentService.getServiceName();
    }

    public void suspend() {}

    public void resume() {}

    @Override
    public Document executeRequestScripts(long instanceId, Document soapRequest, boolean debugMode) throws Exception {

        String errorMsg;
        Document result;
        String orderIdLogString = "";

        InstanceHandler handler;

        try {
            long expireDate;

            expireDate=DateUtil.getFutureDate(TimeInterval.getIntervalAsLong(requestTimeout)).getTime();

            TimeoutManager timeoutMan=TimeoutManager.getInstance();
            timeoutMan.scheduleTimeout(instanceId, expireDate);

            logger.info("New request received: " + soapAction);
            logger.info("Executing response builder script");

            handler = new InstanceHandler(instanceId);
            result = (Document) handler.executeScript(TBXScript.SCRIPT_TYPE_RESPONSE_BUILDER, debugMode);
            ((TBXSOAPInterface)this.parentInterf).validateDocument(result);

            logger.info("Response builder successfully executed");

            try {
                InstanceStatuses.updateInstanceStatus(instanceId, InstanceStatuses.STATUS_QUEUED);
                if(ServiceStatuses.getStatus(parentServiceName)==ServiceStatuses.STATUS_SUSPENDED)
                {
                    logger.info("The service has been suspended");
                }
                else
                {
                    TBXAsynchronousOperationFirstScriptExecutor fsExecutor;

                    fsExecutor = new TBXAsynchronousOperationFirstScriptExecutor(instanceId);
                    fsExecutor.start();

                    logger.info(orderIdLogString + " First Script executor started, sending response ...");
                }
            } catch (Exception e) {
                errorMsg = "Impossible to start executing first script: " + MiscConstants.CDATA_S + e.getMessage() + MiscConstants.CDATA_E;
                logger.error(errorMsg);
                ErrorMailer.send(instanceId,errorMsg);

                throw new OperationExecutionException(errorMsg, Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER);
            }

        } finally {
        }


    return result;
    }

    private boolean checkIfInstanceKeyUnique(String instanceKey) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        ResultSet rs = null;
        String sql;
        try {
            sql = "SELECT ID FROM  T_SERVICE_INSTANCES WHERE INSTANCE_ID='" + instanceKey + "' AND SERVICE_NAME='" + parentServiceName + "' AND OPERATION_NAME='" + name + "'";

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            return !rs.next();
        } catch (Exception e) {
            throw new Exception("Cannot check instanceKey unicity", e);
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
    }

    @Override
    protected void storeInstanceKeys(long seviceInstanceId, Document soapRequest, boolean debugEnabled) throws Exception {
        Document request;
        TBXService parentService;
        String instanceKey;
        String orderId;
        SOAPHeaderExtractor extr;
        String pushHost;

        extr = new SOAPHeaderExtractor(soapRequest);

        instanceKey = extr.getMessageId();
        if (instanceKey == null || instanceKey.equals("")) {
            instanceKey = Long.toString(seviceInstanceId);
        }

        /*if (checkIfInstanceKeyUnique(instanceKey) == false) {
            throw new Exception("A message with this messageId has already been submitted");
        }*/
        messageId=instanceKey;

        parentService=(TBXService) parentInterf.getParent();

        request = Util.removeSOAPElements(soapRequest);
        orderId = parentService.getOrderId(request);
        if(orderId==null || orderId.equals(""))
            orderId=instanceKey;
        
        pushHost = extr.getReplyTo_address();
        if(pushHost==null || pushHost.equals(""))
            throw new Exception("Push host is mandatory for asynchronous operations");

        updateInstanceInformation(seviceInstanceId, orderId, instanceKey, pushHost);
    }

    @Override
    protected Document checkMessage(Document soapRequest) throws Exception {
        Document validatedDoc;
        SOAPHeaderExtractor extr;
        String pushHost;

        extr = new SOAPHeaderExtractor(soapRequest);
        pushHost=extr.getReplyTo_address();

        if(pushHost==null || pushHost.equals(""))
            throw new Exception("Push host is mandatory for asynchronous operations");
        
        StringTokenizer tokenizer;
        if(admittedHosts!=null && admittedHosts.equals("")==false)
        {
            tokenizer=new StringTokenizer(admittedHosts,",");

            boolean matches=false;
            while(tokenizer.hasMoreTokens() && matches==false)
            {
                if(pushHost.equals(tokenizer.nextToken()))
                    matches=true;
            }

            if(matches==false)
                throw new Exception("Unknown or not allowed host: "+pushHost);
        }
        validatedDoc=super.checkMessage(soapRequest);
        return validatedDoc;
    }
}
