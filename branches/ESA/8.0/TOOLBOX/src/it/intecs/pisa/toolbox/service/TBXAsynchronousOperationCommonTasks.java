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
import it.intecs.pisa.util.Util;
import java.net.URL;
import it.intecs.pisa.toolbox.service.instances.SOAPHeaderExtractor;
import it.intecs.pisa.soap.toolbox.AxisSOAPClient;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.OperationInfo;
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
        Toolbox toolbox;
        TBXSOAPInterface implInterf;
        TBXAsynchronousOperation asynchOp;
        String soapAction;
        String sslCertificateLocation;
        SOAPEnvelope soapEnvResp;
        String relatesTo, replyToAddress="";
        SOAPEnvelope soapEnv = null;
        TBXService service;
        Logger logger=null;

        try {
            service = ServiceManager.getService(serviceInstanceId);
            logger = service.getLogger();

            inputMessageId = InstanceResources.getXMLResourceId(serviceInstanceId, InstanceResources.TYPE_INPUT_MESSAGE);
            xmlPers = XMLResourcesPersistence.getInstance();
            inputMessage = xmlPers.retrieveXML(inputMessageId);

            extractor = new SOAPHeaderExtractor(inputMessage);
            relatesTo = extractor.getMessageId();
            replyToAddress = extractor.getReplyTo_address();

            toolbox = Toolbox.getInstance();
            implInterf = (TBXSOAPInterface) service.getImplementedInterface();
            asynchOp = (TBXAsynchronousOperation) implInterf.getOperationByName(OperationInfo.getOperationName(serviceInstanceId));
            soapAction = asynchOp.getCallbackSoapAction();
            sslCertificateLocation = service.getSSLcertificate();


            RelatesTo rel2 = new RelatesTo();
            rel2.setValue(relatesTo);
            RelatesTo[] arrRelatesTo = new RelatesTo[]{rel2};

            soapEnv = Util.getSOAPEnvelope(response, true);

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
            logger.error(e.getMessage());
            activatePushAttempts(serviceInstanceId);
            throw new Exception("Cannot send message to host "+replyToAddress);
        }
    }

    public static void activatePushAttempts(long serviceInstanceId) throws Exception {
        TBXService service;
        TBXSOAPInterface implInterf;
        TBXAsynchronousOperation asynchOp;
        Toolbox toolbox;
        String attempts;

        service = ServiceManager.getService(serviceInstanceId);
        toolbox = Toolbox.getInstance();
        implInterf = (TBXSOAPInterface) service.getImplementedInterface();
        asynchOp = (TBXAsynchronousOperation) implInterf.getOperationByName(OperationInfo.getOperationName(serviceInstanceId));
        attempts = asynchOp.getRetryAttempts();
        if (attempts != null && attempts.equals("") == false) {
            InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_PUSH_RETRY);
        } else {
            InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_UNPUSHED);
        }
    }
}
