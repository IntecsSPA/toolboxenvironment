/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import java.io.IOException;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

//import javax.xml.soap.*;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.log4j.Logger;

/**
 *
 * @author mariarosaria
 */
public class PersistSAMLToken implements Command {

    public static final String SAML_PROPERTIES_FILE = "saml.properties";
    /*
     * The following fields are read from the properties file
     */
    public static final String SAML_BEAN_URL = "SAML_BEAN_URL";
    public static final String SAML_PERSIST_METHOD = "SAML_PERSIST_METHOD";
    public static final String SAML_BEAN_NAMESPACE = "SAML_BEAN_NAMESPACE";
    public static final String SAML_PERSIST_PARAM = "SAML_PERSIST_PARAM";
    public static final String SAML_PERSIST_RESULT_ELEMENT_NAME = "SAML_PERSIST_RESULT_ELEMENT_NAME";
    public static final String WS_ADDRESSING_NAMESPACE = "WS_ADDRESSING_NAMESPACE";
    public static final String WS_ADDRESSING_RELATES_TO = "WS_ADDRESSING_RELATES_TO";
    // additional internal supporting definitions
    public static final String SAML_BEAN_NAMESPACE_PREFIX = "spb";
    public static final String WS_ADDRESSING_PREFIX = "wsa";
    /*
     * Definitions for supporting the identification of the ws-security information
     */
    public static final String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WS_SECURITY = "Security";
    /*
     * Definitions for supporting the identification of the SAML token
     */
    public static final String SAML_ASSERTION_NAMESPACE = "urn:oasis:names:tc:SAML:1.0:assertion";
    public static final String SAML_ASSERTION = "Assertion";
    /*
     * logger for the class
     */
    static Logger logger = Logger.getLogger(PersistSAMLToken.class);
    /*
     * properties to be read from the SAML properties configuration file
     */
    private Properties properties = null;
    /*
     * serialized token already decrypted, in the SAML Assertion format, 
     * to be sent to the SAML Bean Session endpoint for persistence
     */
    private String dumpedToken;

    @Override
    public Result init(ChainContext cc) {
        properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream(SAML_PROPERTIES_FILE));
            logger.info("The SAML properties are loaded");
            return new Result(Result.SUCCESS);
        } catch (IOException ioEx) {
            return new Result(Result.FAIL, "Error in loading SAML properties: " + ioEx.getMessage());
        }
    }

    @Override
    public Result execute(ChainContext cc) {
        try {

            logger.info("Token persistence process started");

            String samlBeanURL = getPropertyValue(SAML_BEAN_URL);
            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);

            SOAPEnvelope message = msgCtx.getEnvelope();
            SOAPHeader soapHeader = message.getHeader();
            if (soapHeader == null) {
                logger.error("No SOAP Header in the message: no token to be persisted");
                return new Result(Result.FAIL);
            }

            OMElement wsSecurity = soapHeader.getFirstChildWithName(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY));

            if (wsSecurity == null) {
                logger.error("No WS-Security info in the message: no token to be persisted");
                return new Result(Result.FAIL);
            }

            /*
             * A SAML Assertion is expected; in case of old OGC 07-118, v.0.0.4 format, the replacement
             * of the DAIL Assertion with the SAML Assertion is supposed to be already done
             */
            QName tokenTagName = new QName(SAML_ASSERTION_NAMESPACE, SAML_ASSERTION);
            OMElement omResult = wsSecurity.getFirstChildWithName(tokenTagName);

            this.dumpedToken = omResult.toString();

            /**
             * set mandatory parameters need to call the service
             */
            Options options = new Options();
            options.setTo(new EndpointReference(samlBeanURL));
            options.setTransportInProtocol(Constants.TRANSPORT_HTTP);
            options.setAction(getPropertyValue(SAML_PERSIST_METHOD));
            options.setProperty(HTTPConstants.CHUNKED, false);

            ServiceClient sender = new ServiceClient();
            sender.setOptions(options);

            /**
             * call SAML session bean to persist the SAML token
             */
            OMElement samlToken = buildReqMsg4PersistSamlToken();
            OMElement result = sender.sendReceive(samlToken);

            String transactionId = getResultValue(result);
            logger.info("The SAML token has been persisted in database with the identifier: "
                    + transactionId);

            addTransactionId2RelatesTo(message, transactionId);
            logger.info("The transaction identifier has been included in to the SOAP Header");

            return new Result(Result.SUCCESS);


        } catch (AxisFault aFault) {
            logger.error("Error calling SAML EJB: " + aFault.getMessage());
            return new Result(Result.FAIL);

        } catch (Exception e) {
            logger.error("Error when persisting token: " + e.getMessage());
            return new Result(Result.FAIL);
        }
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    /**
     * Build the XML request message for persistSamlToken operation
     * 
     * @return OMElement object contains the built request message
     * @throws IOException
     * @throws XMLStreamException
     */
    private OMElement buildReqMsg4PersistSamlToken() throws IOException,
            XMLStreamException {

        OMElement payloadMessage = null;

        OMFactory factory = OMAbstractFactory.getOMFactory();

        OMNamespace spacebelNs = factory.createOMNamespace(
                getPropertyValue(SAML_BEAN_NAMESPACE), SAML_BEAN_NAMESPACE_PREFIX);

        payloadMessage = factory.createOMElement(
                getPropertyValue(SAML_PERSIST_METHOD), spacebelNs);

        OMElement content = factory.createOMElement(
                getPropertyValue(SAML_PERSIST_PARAM), spacebelNs);

        OMText contentText = factory.createOMText(this.dumpedToken,
                XMLStreamConstants.CDATA);

        content.addChild(contentText);
        payloadMessage.addChild(content);

        return payloadMessage;
    }

    /**
     * Get transaction identifier that be returned from persistSamlToken
     * operation
     * 
     * @param result
     * @return
     */
    private String getResultValue(OMElement result) throws Exception {

        QName idTagName = new QName(getPropertyValue(SAML_BEAN_NAMESPACE), getPropertyValue(SAML_PERSIST_RESULT_ELEMENT_NAME));

        OMElement idElement = result.getFirstChildWithName(idTagName);

        //logger.info("GetResultValue");
        //logger.info("idElement = " + idElement.getText());

        return (idElement.getText());
    }

    /**
     * Insert the value of transaction Id to RelatesTo element in SOAP header
     * 
     * @param soapMessage
     *            SOAP message
     * @param transactionId
     *            transaction identifier
     * @throws Exception
     */
    private void addTransactionId2RelatesTo(SOAPEnvelope soapEnvelope,
            String transactionId) throws Exception {
        // get Header element from the SOAP message
        SOAPHeader soapHeader = soapEnvelope.getHeader();
        OMElement soapHeaderElem = soapHeader.getFirstChildWithName(new QName(getPropertyValue(WS_ADDRESSING_NAMESPACE), getPropertyValue(WS_ADDRESSING_RELATES_TO)));

        // if RelatesTo element exist, remove it
        if (soapHeaderElem != null) {
            soapHeaderElem.detach();

        }
        // create a new RelatesTo element
        OMFactory factory = OMAbstractFactory.getOMFactory();
        OMElement soapHeaderElemNew = factory.createOMElement(new QName(getPropertyValue(WS_ADDRESSING_NAMESPACE), getPropertyValue(WS_ADDRESSING_RELATES_TO), WS_ADDRESSING_PREFIX));
        soapHeaderElemNew.setText(transactionId);
        // add the element to SoAP header
        soapHeader.addChild(soapHeaderElemNew);

    }

    /**
     * Get specified property value
     * 
     * @param key
     *            the specified key to get the value
     * @return either the matched value or null if not match
     */
    private String getPropertyValue(String key) {

        String result = null;

        if (this.properties != null && this.properties.getProperty(key) != null) {
            result = this.properties.getProperty(key).trim();
        } else {
            logger.error("The parameter " + key
                    + " does not exist in properties file: " + SAML_PROPERTIES_FILE);
        }

        logger.debug("Read property " + key + "; value = " + result);

        return result;
    }
}
