/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.soap.toolbox.AxisSOAPClient;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.instances.SOAPHeaderExtractor;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import it.intecs.pisa.util.Util;
import java.util.Vector;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.addressing.RelatesTo;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class PushRetryManager extends Thread {

    private boolean isSleeping = false;
    protected boolean stop = false;
    protected String serviceName;
    protected String operationName;
    protected TBXService service;
    protected TBXOperation operation;
    protected Logger logger;

    public PushRetryManager(TBXService service, String opName) throws ToolboxException {
        super(service.getServiceName() + '-' + opName + '-' + TBXService.PUSH_RETRY + "Manager");

        serviceName = service.getServiceName();
        operationName = opName;

        operation = service.getOperation(opName);
        logger = service.getLogger();

        this.service=service;
    }

    @Override
    public void run() {
        Long[] requestsIds;

        try {
            logger.info(getName() + ": Push Retry manager started");
            for (;;) {
                try {
                    if (stop) {
                        return;
                    }


                    requestsIds = getRequests();
                    for (Long id : requestsIds) {
                        sendResponseToClient(id);
                    }


                    try {
                        setIsSleeping(true);
                        sleep(operation.getRetryRateInSeconds() * 1000);
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

    private Long[] getRequests() throws Exception {
        Long[] ids = null;
        Enumeration<Long> el;
        Statement stm;
        ResultSet rs;
        Vector<Long> v;
        int i = 0;

        v = new Vector<Long>();
        stm = ToolboxInternalDatabase.getInstance().getStatement();

        rs = stm.executeQuery("SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='" + serviceName + "' AND OPERATION_NAME='" + operationName + "' AND STATUS=12 AND AVAILABLE_PUSH_RETRIES>0");
        while (rs.next()) {
            v.add(rs.getLong("ID"));
        }

        ids = new Long[v.size()];
        el = v.elements();

        while (el.hasMoreElements()) {
            ids[i] = el.nextElement();
            i++;
        }

        return ids;

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

    private void decreaseAndCheckRetryAttempts(Long id) throws Exception {
        int retries = 0;
        Enumeration<Long> el;
        Statement stm=null;
        ResultSet rs = null;
        String updateSql;

        try {
            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT AVAILABLE_PUSH_RETRIES FROM T_SERVICE_INSTANCES WHERE ID=" + id);
            rs.next();
            retries = rs.getInt("AVAILABLE_PUSH_RETRIES");
            rs.close();
            rs = null;

            if (retries <= 1) {
                updateSql="UPDATE T_SERVICE_INSTANCES SET STATUS=10 WHERE ID=" + id;
            } else {
                 updateSql="UPDATE T_SERVICE_INSTANCES SET AVAILABLE_PUSH_RETRIES="+(retries-1)+" WHERE ID=" + id;
            }

            System.out.println(updateSql);
            stm.executeUpdate(updateSql);
        } finally {

            if (rs != null) {
                rs.close();
            }

            if(stm!=null)
                stm.close();
        }
    }

     

    private void sendResponseToClient(Long id) throws Exception {
        Document outputMessage;

        try {
            outputMessage=InstanceResources.getXMLResource(id, InstanceResources.TYPE_OUTPUT_MESSAGE);

            TBXAsynchronousOperationCommonTasks.sendResponseToClient(id, outputMessage);

            InstanceStatuses.updateInstanceStatus(id,InstanceStatuses.STATUS_COMPLETED);
        } catch (Exception e) {
           decreaseAndCheckRetryAttempts(id);
        }
    }
}

