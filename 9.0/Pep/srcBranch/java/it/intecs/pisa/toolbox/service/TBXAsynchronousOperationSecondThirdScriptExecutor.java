/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.common.communication.ServerDebugConsole;
import it.intecs.pisa.common.communication.messages.ExecutionCompletedMessage;
import it.intecs.pisa.pluginscore.exceptions.DebugTerminatedException;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.log.ErrorMailer;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import it.intecs.pisa.toolbox.timers.AsynchInstancesSecondScriptWakeUpManager;
import it.intecs.pisa.util.SOAPUtil;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TBXAsynchronousOperationSecondThirdScriptExecutor extends Thread {

    protected long serviceInstanceId;
    protected boolean debugMode;
    protected Logger logger;
    protected Semaphore mutex = null;
    protected TBXService service;

    public TBXAsynchronousOperationSecondThirdScriptExecutor(long id) throws Exception {
        super(Long.toString(id) + "_FirstScriptExecutor");

        serviceInstanceId = id;

        Toolbox tbxInstance;
        tbxInstance=Toolbox.getInstance();
        debugMode=tbxInstance.getInstanceKeyUnderDebug()==id;

        service = InstanceInfo.getService(serviceInstanceId);
        logger = service.getLogger();
    }

    @Override
    public void run() {
        Boolean checkResult=null;
        InstanceHandler handler = null;
        String ssExRes;
        Document errorResp;
        try {
            try {
                if(service.isInitialized()==true && shallStopExecution()==false)
                {
                    handler = new InstanceHandler(serviceInstanceId);

                    try
                    {
                        ssExRes = getSecondScriptExecutionResultId();
                        InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_CHECKING);

                        checkResult = (Boolean) handler.executeScript(TBXScript.SCRIPT_TYPE_SECOND_SCRIPT, debugMode);
                    }
                    catch(Exception e)
                    {
                        logger.error("Error while executing second script. Details: "+e.getMessage());
                        ErrorMailer.send(serviceInstanceId,"Error while executing second script.");
                        throw e;
                    }

                    if (shallStopExecution())  return;

                    if (checkResult != null && checkResult.booleanValue() == true) {
                        executeThirdScript(handler);
                    } else {
                        InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_PENDING);

                        long delay=0;
                        String pollingRate=InstanceInfo.getPollingRateFromInstanceId(serviceInstanceId);
                        delay=TimeInterval.getIntervalAsLong(pollingRate);

                        AsynchInstancesSecondScriptWakeUpManager wakeUpMan=AsynchInstancesSecondScriptWakeUpManager.getInstance();
                        wakeUpMan.scheduleInstance(serviceInstanceId,delay);
                    }
                }

            } catch (DebugTerminatedException terExc) {
                closeDebugConsole();
                releaseMutex();
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_CANCELLED);
                handler.deleteAllVariablesDumped();
            } catch (Exception e) {
               
                if (debugMode) {
                    sendTDETerminateMsg();
                    closeDebugConsole();
                }
                

                releaseMutex();
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_ERROR);
                
                errorResp = TBXOperationOnErrorActions.processErrorRequest(serviceInstanceId, TBXScript.SCRIPT_TYPE_GLOBAL_ERROR, "An error occurred while executing the service logic");
                TBXAsynchronousOperationCommonTasks.sendResponseToClient(serviceInstanceId, errorResp);

                handler = new InstanceHandler(serviceInstanceId);
                handler.deleteAllVariablesDumped();
            }
        } catch (Exception ex) {
            releaseMutex();
        }
    }

    protected boolean shallStopExecution() throws Exception
    {
         byte status=InstanceStatuses.getInstanceStatus(serviceInstanceId);
         if (status == InstanceStatuses.STATUS_CANCELLED ||
             status == InstanceStatuses.STATUS_EXPIRED||
             status == InstanceStatuses.STATUS_PUSH_RETRY)
             return true;
         else return false;
    }

    protected void executeThirdScript(InstanceHandler handler) throws Exception {
        Document response;

        try {
            try {
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_READY);
                response = (Document) handler.executeScript(TBXScript.SCRIPT_TYPE_THIRD_SCRIPT, debugMode);
            }
            catch(Exception e)
            {
                 logger.error("Error while executing third script.");
                ErrorMailer.send(serviceInstanceId,"Error while executing third script.");
                throw e;
            }
            finally {
                releaseMutex();

                if (debugMode) {
                    sendTDETerminateMsg();
                    closeDebugConsole();
                }
            }

            if (InstanceStatuses.getInstanceStatus(serviceInstanceId) == InstanceStatuses.STATUS_CANCELLED) {
                return;
            }

            try {
                if (SOAPUtil.isSOAPFault(response) == false && service.getImplementedInterface().isValidationActive()) {
                    response = validateResponse(response);
                }

                InstanceResources.storeXMLResource(response, serviceInstanceId, InstanceResources.TYPE_OUTPUT_MESSAGE);
            } catch (Exception e) {
                Document errorResp;
                logger.error("An error occurred while validating the output message. See catalina.out for further information.");

                InstanceResources.storeXMLResource(response, serviceInstanceId, InstanceResources.TYPE_INVALID_OUTPUT_MESSAGE);
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_INVALID_OUTPUT_MESSAGE);

                try{
                    errorResp = TBXOperationOnErrorActions.processErrorRequest(serviceInstanceId, TBXScript.SCRIPT_TYPE_GLOBAL_ERROR, "An error occurred while executing the service logic");
                    InstanceResources.storeXMLResource(errorResp, serviceInstanceId, InstanceResources.TYPE_OUTPUT_MESSAGE);
                    TBXAsynchronousOperationCommonTasks.sendResponseToClient(serviceInstanceId, errorResp);
                }catch(Exception ecc){}
                return;
            }

            InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_COMPLETED);
            TBXAsynchronousOperationCommonTasks.sendResponseToClient(serviceInstanceId, response);
        } finally {
            handler.deleteAllVariablesDumped();
        }
    }

    private void releaseMutex() {
        Semaphore mutex;

        try {
            mutex = getMutex();

            if (mutex != null) {
                mutex.release();
            }

            logger.info("Mutex released by instance " + this.serviceInstanceId);
        } catch (Exception e) {
            logger.error("An error occurred while trying to release the mutex (if required)");
        }
    }

    protected Semaphore getMutex() throws Exception {
        ServiceManager servMan;
        //TBXService service;
        Semaphore mutex;

        //service = ServiceManager.getService(serviceInstanceId);

        servMan = ServiceManager.getInstance();
        if (servMan.isGlobalQueuingEnabled()) {
            mutex = servMan.getGlobalQueueMutex();
        } else if (service.isQueuing() == true) {
            mutex = service.getServiceQueueMutex();
        } else {
            mutex = null;
        }

        return mutex;
    }

    protected Document validateResponse(Document response) throws Exception {
        if (SOAPUtil.isSOAPFault(response) == false) {
            //TBXService service;

            //service = ServiceManager.getService(serviceInstanceId);
            response = ((TBXSOAPInterface) service.getImplementedInterface()).validateDocument(response);
        }
        return response;
    }

    private void sendTDETerminateMsg() {
        ServerDebugConsole dbgConsole;
        dbgConsole = Toolbox.getInstance().getDbgConsole();
        if (dbgConsole != null) {
            dbgConsole.sendCommand(new ExecutionCompletedMessage());
        }
    }

    protected void closeDebugConsole() {
        ServerDebugConsole console;
        Toolbox toolbox;

        toolbox = Toolbox.getInstance();
        console = toolbox.getDbgConsole();

        if (console != null) {
            //console.sendCommand(new ErrorMessage("Instace in abort status"));
            console.close();
        }

        toolbox.signalDebugConsoleClosed();
    }

    private String getSecondScriptExecutionResultId() throws Exception {
        Statement stm;
        ResultSet rs;
        String sql;

        stm = ToolboxInternalDatabase.getInstance().getStatement();

        sql = "SELECT ID FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID=" + serviceInstanceId + " AND TYPE='" + InstanceResources.TYPE_SECOND_SCRIPT_EXECUTION + "'";
        rs = stm.executeQuery(sql);
        rs.next();

        try {
            return rs.getString("ID");
        } catch (Exception e) {
            return null;
        }
    }
}
