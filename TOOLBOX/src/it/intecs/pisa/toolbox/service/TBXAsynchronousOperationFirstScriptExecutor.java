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
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.log.ErrorMailer;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.log4j.Logger;
import java.util.concurrent.Semaphore;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TBXAsynchronousOperationFirstScriptExecutor extends Thread {

    protected long serviceInstanceId;
    protected boolean debugMode;
    protected Logger logger;

    public TBXAsynchronousOperationFirstScriptExecutor(long id, boolean dbgMode, Logger log) {
        super(Long.toString(id) + "_FirstScriptExecutor");
        serviceInstanceId = id;
        debugMode = dbgMode;
       logger=log;
    }

    @Override
    public void run() {
        InstanceHandler handler=null;
        try {
            try {
                handleQueuing();

                if(InstanceStatuses.getInstanceStatus(serviceInstanceId)==InstanceStatuses.STATUS_CANCELLED)
                    return;

                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_EXECUTING);
                
                handler = new InstanceHandler(serviceInstanceId);
                handler.executeScript(TBXScript.SCRIPT_TYPE_FIRST_SCRIPT, debugMode);

                if(InstanceStatuses.getInstanceStatus(serviceInstanceId)==InstanceStatuses.STATUS_CANCELLED)
                    return;

                //InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_PENDING);

                TBXAsynchronousOperationSecondThirdScriptExecutor secondThirdExec;
                secondThirdExec=new TBXAsynchronousOperationSecondThirdScriptExecutor(serviceInstanceId,debugMode,logger);
                secondThirdExec.start();
            }catch (DebugTerminatedException terExc) {
                closeDebugConsole();
                releaseMutex();
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_CANCELLED);
                handler.deleteAllVariablesDumped();
            }
            catch (Exception e) {
                if (debugMode) {
                    sendTDETerminateMsg();
                    closeDebugConsole();
                }

                if(handler!=null)
                    handler.deleteAllVariablesDumped();
               InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_ABORTED);
               String errorStr;
               errorStr="Error while executing first script. Cause:"+e.getMessage();
               logger.error(errorStr);
               ErrorMailer.send(InstanceInfo.getServiceNameFromInstanceId(serviceInstanceId),
                                InstanceInfo.getSOAPActionFromInstanceId(serviceInstanceId),
                                null, null,errorStr);

                releaseMutex();
            }
        } catch (Exception ecc) {
            releaseMutex();
        }
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
            console.close();
        }

        toolbox.signalDebugConsoleClosed();
    }

     private TBXService getService() throws Exception {
        Statement stm;
        ResultSet rs;

        stm = ToolboxInternalDatabase.getInstance().getStatement();

        rs = stm.executeQuery("SELECT SERVICE_NAME FROM T_SERVICE_INSTANCES WHERE ID=" + serviceInstanceId);
        rs.next();

        return ServiceManager.getInstance().getService(rs.getString("SERVICE_NAME"));

    }

     private void handleQueuing() {
        Semaphore mutex;

        try
        {
            mutex=getMutex();

            if(mutex!=null)
                mutex.acquire();

            logger.info("Mutex acquired by instance "+this.serviceInstanceId);
        }
        catch(Exception e)
        {
            logger.error("An error occurred while acquiring mutex (if required). The execution will continue without mututal exclusion");
        }
    }

    private void releaseMutex() {
        Semaphore mutex;

        try
        {
            mutex=getMutex();

            if(mutex!=null)
                mutex.release();

            logger.info("Mutex released by instance "+this.serviceInstanceId);
        }
        catch(Exception e)
        {
            logger.error("An error occurred while trying to release the mutex (if required)");
        }
    }

    protected Semaphore getMutex() throws Exception
    {
        ServiceManager servMan;
        TBXService service;
        Semaphore mutex;

        service=getService();

        servMan=ServiceManager.getInstance();
        if(servMan.isGlobalQueuingEnabled())
          mutex=servMan.getGlobalQueueMutex();
        else if(service.isQueuing()==true)
          mutex=service.getServiceQueueMutex();
        else mutex=null;

        return mutex;
    }
}
