/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.Logger;

public class PushManager extends Thread {

    /**
     *  The sleep time, it is given by the <code>pushPollingRate</code> configuration parameter.
     */
    private boolean isSleeping = false;
    private boolean stop = false;
    protected String serviceName;
    protected String operationName;
    protected TBXService service;
    protected TBXOperation operation;
    protected Logger logger;
    /**
     *  Default constructor.
     */
    public PushManager(TBXService service,String op) throws ToolboxException {
        super(service.getServiceName() + '-' + op + "-pushManager");

        serviceName = service.getServiceName();
        operationName = op;

        this.service=service;
        operation=service.getOperation(op);
        logger=service.getLogger();
    }

    

    /**
     *  The body of the push manager, it is actually an infinite loop ending with a sleep. The sleep time is equal to the {@link #sleepTime} (in milliseconds). The body of the loop consists in checking each request handler, to see if it is possible to execute a check script or to send directly a response. Check scripts are executable only if a request is in pending status. If a check script is successful, the get script is executed and the result is pushed to the SSE system. If the status of a request is one of aborted/expired/cancelled/rejected, it is possible to send a response without executing any script. For each pushed response, the corresponding request handler is removed.
     */
    @Override
    public void run() {
        Long[] requestsIds;

        try {
            logger.info(getName() + ": Push manager started");

            for (;;) {
                try {
                    if (stop) {
                        logger.info(getName() + ": Push manager stopped, exiting from internal loop");
                        break;
                    }

                    setStop(false);
                    setIsSleeping(false);
                    requestsIds = getRequests();

                    for(Long id:requestsIds)
                    {
                        logger.info("Executing instance "+id);
                        handlePendingInstance(id);
                    }
                 

                    try {
                        setIsSleeping(true);
                        long sleepAmount=0;

                        sleepAmount=service.getOperation(operationName).getPollingRateInSeconds()*1000;
                        sleep(sleepAmount);
                    } catch (Exception e) {
                        if (e instanceof InterruptedException) {
                            if (!isStop()) {
                                logger.info(getName() + " woken-up...");
                                setIsSleeping(false);
                                continue;
                            } else {
                                logger.info(getName() + " stopping...");
                                break;
                            }
                        }
                    }


                } catch (Exception extEcc) {
                    extEcc.printStackTrace();
                    logger.info(this.getName() + ": An expected exception has been catched. Exiting from thread. Details: " + extEcc.getMessage() + " Cause: " + extEcc.getCause());
                }
            }
        } catch (Exception e) {
        }

    }
   
    protected void handlePendingInstance(Long key) {
        boolean isInstanceUnderDebug;
        Toolbox toolbox = null;
        long debugInstanceKey = -1;
        TBXAsynchronousOperationSecondThirdScriptExecutor secondThirdExec;
        try {
            toolbox = Toolbox.getInstance();

            debugInstanceKey = toolbox.getInstanceKeyUnderDebug();
            isInstanceUnderDebug = debugInstanceKey==key;

           secondThirdExec=new TBXAsynchronousOperationSecondThirdScriptExecutor(key,isInstanceUnderDebug,logger);
           secondThirdExec.start();
        } catch (Exception e) {
            logger.error(getName()+ ": Error while executing Push manager");
        } 
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isIsSleeping() {
        return isSleeping;
    }

    public void setIsSleeping(boolean isSleeping) {
        this.isSleeping = isSleeping;
    }

  private Long[] getRequests() throws Exception {
        Long[] ids=null;
        Enumeration<Long> el;
        Statement stm;
        ResultSet rs;
        Vector<Long> v;
        int i=0;
        long now;
        Date nowDate;

        try
        {
            v=new Vector<Long>();
            stm= ToolboxInternalDatabase.getInstance().getStatement();

            nowDate=new Date();
            now=nowDate.getTime();

            rs=stm.executeQuery("SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='"+serviceName+"' AND OPERATION_NAME='"+operationName+"' AND STATUS=8 AND EXPIRATION_DATE>"+now);
            while(rs.next())
                v.add(rs.getLong("ID"));

            ids=new Long[v.size()];
            el= v.elements();

            while(el.hasMoreElements())
            {
                ids[i]=el.nextElement();
                i++;
            }
        }
        catch(Exception e)
        {
            ids=new Long[0];
        }
        
        return ids;

    }

    private String getInstanceKey(Long requestId) throws Exception {
        Statement stm;
        ResultSet rs;

        stm= ToolboxInternalDatabase.getInstance().getStatement();

        rs=stm.executeQuery("SELECT INSTANCE_ID FROM T_SERVICE_INSTANCES WHERE ID="+requestId);
        rs.next();

        return rs.getString("INSTANCE_ID");

    }
}
