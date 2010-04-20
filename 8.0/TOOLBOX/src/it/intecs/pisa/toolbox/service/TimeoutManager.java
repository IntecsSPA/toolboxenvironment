package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.util.Util;
import it.intecs.pisa.soap.toolbox.*;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.SOAPHeaderExtractor;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.addressing.RelatesTo;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

public class TimeoutManager extends Thread {

    private boolean isSleeping = false;
    private boolean stop = false;
    protected String serviceName;
    protected String operationName;
    protected TBXService service;
    protected TBXOperation operation;
    protected Logger logger;

    public TimeoutManager(TBXService service, String op) throws ToolboxException {
        super(service.getServiceName() + '-' + op + "-TimeoutManager");

        serviceName = service.getServiceName();
        operationName = op;

        this.service=service;
        operation = service.getOperation(op);
        logger = service.getLogger();
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        Long[] requestsIds;

        try {
            logger.info(getName() + ": Timeout Manager started");

            for (;;) {
                try {
                    if (stop) {
                        logger.info(getName() + ": Timeout manager stopped, exiting from internal loop");
                        break;
                    }

                    setStop(false);
                    setIsSleeping(false);
                    requestsIds = getRequests();

                    for (Long id : requestsIds) {
                        handleExpiredInstance(id);
                    }


                    try {
                        setIsSleeping(true);
                        long towait=getTimeToWait();
                        sleep(towait);
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
        long now;
        Date nowDate;
        Enumeration<Long> el;
        Statement stm;
        ResultSet rs;
        Vector<Long> v;
        int i = 0;

        v = new Vector<Long>();
        stm = ToolboxInternalDatabase.getInstance().getStatement();

        nowDate = new Date();
        now = nowDate.getTime();

        rs = stm.executeQuery("SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='" + serviceName + "' AND OPERATION_NAME='" + operationName + "' AND STATUS=8 AND EXPIRATION_DATE<" + now);
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

    public boolean isIsSleeping() {
        return isSleeping;
    }

    public void setIsSleeping(boolean isSleeping) {
        this.isSleeping = isSleeping;
    }

    private void handleExpiredInstance(Long id) throws Exception {
        logger.info("Instance "+id+" expored. sending message to client");
        InstanceStatuses.updateInstanceStatus(id, InstanceStatuses.STATUS_EXPIRED);

        sendResponseToClient(id);
    }

    private void sendResponseToClient(Long id) throws Exception {
        Document inputMessage;
        SOAPHeaderExtractor extractor;
        String soapAction;
        String sslCertificateLocation;
        SOAPEnvelope soapEnvResp;
        SOAPEnvelope soapEnv;
        String relatesTo, replyToAddress;
        Document outputMessage;

        try {

            inputMessage = InstanceResources.getXMLResource(id, InstanceResources.TYPE_INPUT_MESSAGE);
            outputMessage = processErrorRequest(id);

            extractor = new SOAPHeaderExtractor(inputMessage);
            relatesTo = extractor.getMessageId();
            replyToAddress = extractor.getReplyTo_address();

            soapAction = operation.getCallbackSoapAction();
            sslCertificateLocation = service.getSSLcertificate();


            RelatesTo rel2 = new RelatesTo();
            rel2.setValue(relatesTo);
            RelatesTo[] arrRelatesTo = new RelatesTo[]{rel2};

            soapEnv = (SOAPEnvelope) Util.getSOAPEnvelope(outputMessage, true);

            if (service.hasWSSecurity()) {
                logger.info("Trying secure communication. URL: " + replyToAddress);
                soapEnvResp = AxisSOAPClient.secureExchange(new URL(replyToAddress), soapEnv, soapAction, sslCertificateLocation, arrRelatesTo);

            } else {

                if (sslCertificateLocation != null && new URL(replyToAddress).getProtocol().equals("https")) {
                    logger.info("Trying secure communication. URL: " + replyToAddress);
                    //STE pushSOAPResponse = AxisSOAPClient.secureExchange(url, soapResponse, soapAction, sslCertificateLocation);
                    soapEnvResp = AxisSOAPClient.secureExchange(new URL(replyToAddress), soapEnv, soapAction, sslCertificateLocation, arrRelatesTo);
                } else {
                    logger.info("Trying unsecure communication. URL: " + replyToAddress);

                    //STE pushSOAPResponse = AxisSOAPClient.exchange(url, soapResponse, soapAction);
                    soapEnvResp = AxisSOAPClient.sendReceive(new URL(replyToAddress), soapEnv, soapAction, arrRelatesTo);
                }
            }

        } catch (Exception e) {
            logger.error("Output message not sent to client..");
        }
    }

    protected Document processErrorRequest(long seviceInstanceId) throws ToolboxException, Exception {
         try {
            InstanceHandler handler;
            Document doc;

            handler = new InstanceHandler(seviceInstanceId);
            doc=(Document) handler.executeScript(TBXScript.SCRIPT_TYPE_GLOBAL_ERROR, false);

            InstanceResources.storeXMLResource(doc, seviceInstanceId, InstanceResources.TYPE_OUTPUT_MESSAGE);
            return doc;
        } catch (Exception e) {
            throw new Exception("Cannot execute error script", e);
        }
    }

    private long getTimeToWait() throws Exception {
        boolean hasNextValue=false;
        long now;
        Date nowDate;
        Enumeration<Long> el;
        Statement stm;
        ResultSet rs;
        Vector<Long> v;
        int i = 0;

        stm = ToolboxInternalDatabase.getInstance().getStatement();

        nowDate = new Date();
        now = nowDate.getTime();

        rs = stm.executeQuery("SELECT EXPIRATION_DATE AS TO_WAIT FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='" + serviceName + "' AND OPERATION_NAME='" + operationName + "' AND STATUS=8 AND EXPIRATION_DATE>" + now+" ORDER BY EXPIRATION_DATE ASC");
        hasNextValue=rs.next();
        if(hasNextValue==true)
            return rs.getLong("TO_WAIT")-now;
        else return operation.getRequestTimeoutInSeconds()*1000;
    }
}
