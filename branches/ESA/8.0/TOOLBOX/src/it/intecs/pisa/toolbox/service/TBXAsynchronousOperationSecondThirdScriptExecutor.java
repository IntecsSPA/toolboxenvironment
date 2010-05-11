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

import it.intecs.pisa.communication.ServerDebugConsole;
import it.intecs.pisa.communication.messages.ExecutionCompletedMessage;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.log.ErrorMailer;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.plugins.exceptions.DebugTerminatedException;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import it.intecs.pisa.util.SOAPUtil;
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

    public TBXAsynchronousOperationSecondThirdScriptExecutor(long id, boolean dbgMode, Logger log) throws Exception {
        super(Long.toString(id) + "_FirstScriptExecutor");

        serviceInstanceId = id;
        debugMode = dbgMode;

        service = ServiceManager.getService(serviceInstanceId);
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
                if(service.isInitialized()==true)
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
                        logger.error("Error while executing second script.");
                        ErrorMailer.send(serviceInstanceId,"Error while executing second script.");
                        throw e;
                    }

                    if (InstanceStatuses.getInstanceStatus(serviceInstanceId) == InstanceStatuses.STATUS_CANCELLED) {
                        return;
                    }

                    if (checkResult != null && checkResult.booleanValue() == true) {
                        executeThirdScript(handler);
                    } else {
                        InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_PENDING);
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
                //TBXService service;

                //service = ServiceManager.getService(serviceInstanceId);

                if (SOAPUtil.isSOAPFault(response) == false && service.getImplementedInterface().isValidationActive()) {
                    response = validateResponse(response);
                }

                InstanceResources.storeXMLResource(response, serviceInstanceId, InstanceResources.TYPE_OUTPUT_MESSAGE);
            } catch (Exception e) {
                Document errorResp;

                logger.error(e.getMessage());

                InstanceResources.storeXMLResource(response, serviceInstanceId, InstanceResources.TYPE_INVALID_OUTPUT_MESSAGE);
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_INVALID_OUTPUT_MESSAGE);

                try{
                    errorResp = TBXOperationOnErrorActions.processErrorRequest(serviceInstanceId, TBXScript.SCRIPT_TYPE_GLOBAL_ERROR, "An error occurred while executing the service logic");
                    InstanceResources.storeXMLResource(errorResp, serviceInstanceId, InstanceResources.TYPE_OUTPUT_MESSAGE);
                    TBXAsynchronousOperationCommonTasks.sendResponseToClient(serviceInstanceId, errorResp);
                }catch(Exception ecc){}
                return;
            }

            try {
                TBXAsynchronousOperationCommonTasks.sendResponseToClient(serviceInstanceId, response);
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_COMPLETED);
            } catch (Exception ecc) {}
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

    private void removeSecondScriptRefFromDb(String id) throws Exception {
        Statement stm = null;

        try {
            stm = ToolboxInternalDatabase.getInstance().getStatement();
            stm.executeUpdate("DELETE FROM T_INSTANCES_RESOURCES WHERE ID=" + id);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }
}
