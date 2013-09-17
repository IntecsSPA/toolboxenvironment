/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;


import javax.xml.namespace.QName;

//import javax.xml.soap.*;

import org.apache.axiom.om.OMElement;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.context.MessageContext;

import org.apache.log4j.Logger;

/**
 *
 * @author mariarosaria
 */
public class RestoreEncryptedData implements Command {

    public static final String ENCRYPTED_DATA_NAMESPACE = "http://www.w3.org/2001/04/xmlenc#";
    public static final String ENCRYPTED_DATA = "EncryptedData";
    public static final String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WS_SECURITY = "Security";
    public static final String SAML_ASSERTION_NAMESPACE = "urn:oasis:names:tc:SAML:1.0:assertion";
    public static final String SAML_ASSERTION = "Assertion";
    /*
     * logger for the class
     */
    static Logger logger = Logger.getLogger(RestoreEncryptedData.class);

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);

    }

    @Override
    public Result execute(ChainContext cc) {

        try {
            logger.info("Encrypted token restore process started");

            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);
            SOAPEnvelope envelope = msgCtx.getEnvelope();
            SOAPHeader soapHeader = envelope.getHeader();
            OMElement wsSecurity = soapHeader.getFirstChildWithName(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY));

           
            OMElement tokenDecrypted = wsSecurity.getFirstChildWithName(new QName(SAML_ASSERTION_NAMESPACE, SAML_ASSERTION));
            tokenDecrypted.detach();
            
            OMElement tokenEncrypted = (OMElement)msgCtx.getProperty(ENCRYPTED_DATA);
            wsSecurity.addChild(tokenEncrypted);

            logger.info("Encrypted token restore process finished with success");
            return new Result(Result.SUCCESS);
        } catch (Exception e) {
            logger.error("Error when restoring encrypted token: " + e.getMessage());
            return new Result(Result.FAIL, "Error while restoring SAML encrypted token.");
        }

    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
