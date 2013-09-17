/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import it.intecs.pisa.toolbox.plugins.security.SAMLdecryptor;

import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.XML;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;

import org.apache.log4j.Logger;

import org.w3c.dom.Element;

/**
 *
 * @author mariarosaria
 */
public class CheckSAMLTokenSignature implements Command {

    static public String ENCRYPTED_DATA_NAMESPACE = "http://www.w3.org/2001/04/xmlenc#";
    static public String ENCRYPTED_DATA = "EncryptedData";
    public static final String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WS_SECURITY = "Security";
    public static final String SAML_ASSERTION = "Assertion";
    public static final String DIGITAL_SIGNATURE = "Signature";
    /*
     * logger for the class
     */
    static Logger logger = Logger.getLogger(CheckSAMLTokenSignature.class);

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        try {
            logger.info("Signature checking process started");

            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);
            SOAPEnvelope envelope = msgCtx.getEnvelope();
            SOAPHeader soapHeader = envelope.getHeader();
            Element soapHeaderDOM = XMLUtils.toDOM(soapHeader);

            Element assertionDOM = (Element) soapHeaderDOM.getElementsByTagNameNS(XML.SAML_NS, SAML_ASSERTION).item(0);

            Element n = XML.getFirstChildElement(assertionDOM, XML.XMLSIG_NS, DIGITAL_SIGNATURE);
            XMLSignature sig = new XMLSignature((Element) n, "");
            boolean signatureOk = false;
            try {
                SAMLdecryptor decryptor = new SAMLdecryptor();
                signatureOk = decryptor.checkSignature(sig);
                logger.info("SIGNATURE CHECK: " + signatureOk);
            } catch (Exception e) {
                logger.error("Failed to validate SAML signature value: " + e.getMessage());
                return new Result(Result.FAIL, "Failed to validate SAML signature value.");
            }

            if (!signatureOk) {
                logger.error("Failed to validate SAML signature value: invalid signature");
                return new Result(Result.FAIL, "The SAML token signature is not valid.");
            }

            return new Result(Result.SUCCESS);
        } catch (Exception e) {
            logger.error("Error while checking signature: " + e.getMessage());
            return new Result(Result.FAIL, "Error while checking SAML token signature.");
        }
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
