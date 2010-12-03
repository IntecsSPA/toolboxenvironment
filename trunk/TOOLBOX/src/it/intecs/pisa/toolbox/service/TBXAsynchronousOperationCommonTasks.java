/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import org.w3c.dom.Document;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.addressing.RelatesTo;
import it.intecs.pisa.toolbox.util.Util;
import java.net.URL;
import it.intecs.pisa.toolbox.service.instances.SOAPHeaderExtractor;
import it.intecs.pisa.soap.toolbox.AxisSOAPClient;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.log.ErrorMailer;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import it.intecs.pisa.toolbox.timers.PushRetryManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TBXAsynchronousOperationCommonTasks {

    public static void sendResponseToClient(long serviceInstanceId, Document response) throws Exception {
        XMLResourcesPersistence xmlPers;
        String inputMessageId;
        Document inputMessage;
        SOAPHeaderExtractor extractor;
        String namespace="";
        TBXSOAPInterface implInterf;
        TBXAsynchronousOperation asynchOp;
        String soapAction="";
        String sslCertificateLocation;
        SOAPEnvelope soapEnvResp;
        String relatesTo, replyToAddress="";
        SOAPEnvelope soapEnv = null;
        Logger logger=null;

        try {
            inputMessageId = InstanceResources.getXMLResourceId(serviceInstanceId, InstanceResources.TYPE_INPUT_MESSAGE);
            xmlPers = XMLResourcesPersistence.getInstance();
            inputMessage = xmlPers.retrieveXML(inputMessageId);

            extractor = new SOAPHeaderExtractor(inputMessage);
            relatesTo = extractor.getMessageId();
            replyToAddress = extractor.getReplyTo_address();
            namespace = extractor.getNamespace();
            TBXAsynchronousOperation operation;
            operation=(TBXAsynchronousOperation) InstanceInfo.getOperation(serviceInstanceId);
            soapAction = operation.getCallbackSoapAction();

            TBXService service;
            service=InstanceInfo.getService(serviceInstanceId);
            sslCertificateLocation = service.getSSLcertificate();

            logger = service.getLogger();

            TBXSOAPInterface interf;
            interf=(TBXSOAPInterface) service.getImplementedInterface();

            boolean admitted=checkAdmittedHosts(interf,replyToAddress);

            if(admitted==true)
            {
                // Create the response getting the WSA version from the client
                soapEnv = Util.getSOAPEnvelope(response,replyToAddress,soapAction,relatesTo,namespace, true);

            if (service.hasWSSecurity()) {
                logger.info("Trying secure communication. URL: " + replyToAddress);
                soapEnvResp = AxisSOAPClient.secureExchange(new URL(replyToAddress), soapEnv, soapAction, sslCertificateLocation);

            } else {

                if (sslCertificateLocation != null && new URL(replyToAddress).getProtocol().equals("https")) {
                    logger.info("Trying secure communication. URL: " + replyToAddress);
                    soapEnvResp = AxisSOAPClient.secureExchange(new URL(replyToAddress), soapEnv, soapAction, sslCertificateLocation);
                } else {
                    logger.info("Trying unsecure communication. URL: " + replyToAddress);
                    soapEnvResp = AxisSOAPClient.sendReceive(new URL(replyToAddress), soapEnv, soapAction);
                }
            }
            }
            else
            {
                ErrorMailer.send(service.getServiceName(), soapAction, null, null,"URL "+replyToAddress+" is not admitted as Push Host");
                throw new Exception("Push Host is not admitted");
            }
        } catch (Exception e) {
            String error;
            error="Cannot send message to push host: "+e.getMessage();
            logger.error(error);
            ErrorMailer.send(serviceInstanceId,error);

            schedulePushAttempts(serviceInstanceId);
            //throw new Exception("Cannot send message to host "+replyToAddress);
        }
    }

    private static boolean decreaseAndCheckRetryAttempts(Long id) {
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

            if (retries==0) {
                updateSql="UPDATE T_SERVICE_INSTANCES SET STATUS=10 WHERE ID=" + id;              
            } else {
                 updateSql="UPDATE T_SERVICE_INSTANCES SET AVAILABLE_PUSH_RETRIES="+(retries-1)+" WHERE ID=" + id;
            }

            stm.executeUpdate(updateSql);

            return retries>0;
        }
        catch(Exception e)
        {
            return false;
        }
        finally
        {
            try {
                if (rs != null)
                    rs.close();

                if(stm!=null)
                     stm.close();
            } catch (SQLException ex) {

            }
        }
    }

    public static void schedulePushAttempts(long serviceInstanceId) throws Exception {
        TBXOperation op;

        op=InstanceInfo.getOperation(serviceInstanceId);
        if(op.getRetryAttempts()!=null && op.getRetryAttempts().equals("")==false)
        {
            if(decreaseAndCheckRetryAttempts(serviceInstanceId))
            {
                InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_PUSH_RETRY);

                long retry=op.getRetryRateInSeconds()*1000;
                PushRetryManager.getInstance().scheduleInstance(serviceInstanceId, retry);
            }
        }
        else InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_UNPUSHED);
    }

    private static boolean checkAdmittedHosts(TBXSOAPInterface implInterf, String replyToAddress) throws Exception {
        String hosts;
        StringTokenizer tokenizer;
        boolean res=true;
        String replyAdd;

        replyAdd=(new URL(replyToAddress)).getHost();

        hosts=implInterf.getAdmittedHosts();
        tokenizer=new StringTokenizer(hosts," ");

        if(tokenizer.hasMoreElements())
            res=false;

        while(tokenizer.hasMoreElements())
        {
            res|=tokenizer.nextToken().equals(replyAdd);
}

        return res;
    }

    public static void executeErrorScriptAndSendToClient(Long id) throws Exception {
        Document inputMessage = InstanceResources.getXMLResource(id, InstanceResources.TYPE_INPUT_MESSAGE);
        Document outputMessage = processErrorRequest(id);

        sendResponseToClient(id,outputMessage);
    }

    protected static Document processErrorRequest(long seviceInstanceId) throws ToolboxException, Exception {
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
}
