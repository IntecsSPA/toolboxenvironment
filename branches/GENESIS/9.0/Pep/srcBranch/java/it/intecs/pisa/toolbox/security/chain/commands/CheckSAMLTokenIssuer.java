/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import org.opensaml.SAMLAssertion;
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
public class CheckSAMLTokenIssuer implements Command {

    static public String SAML_ASSERTION = "Assertion";
    /*
     * logger for the class
     */
    static Logger logger = Logger.getLogger(CheckSAMLTokenIssuer.class);
    /*
     * command property
     */
    private String issuer;

    public void setIssuer(String inIssuer) {
        issuer = inIssuer;
    }

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {

        try {
            logger.info("Token issuer check started");
            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);
            SOAPEnvelope envelope = msgCtx.getEnvelope();
            SOAPHeader soapHeader = envelope.getHeader();

            Element soapHeaderDOM = XMLUtils.toDOM(soapHeader);

            Element assertion = (Element) soapHeaderDOM.getElementsByTagNameNS(XML.SAML_NS, SAML_ASSERTION).item(0);

            SAMLAssertion saml = new SAMLAssertion(assertion);

            String tokenIssuer = saml.getIssuer();
            if (issuer.compareTo(tokenIssuer) == 0) {
                logger.info("The SAML token issuer is valid");
                return new Result(Result.SUCCESS);
            } else {
                logger.error("The SAML token issuer is not valid");
                return new Result(Result.FAIL, "The SAML token issuer is not valid.");
            }

        } catch (Exception e) {
            logger.error("Error when checking SAML token issuer: " + e.getMessage());
            return new Result(Result.FAIL, "Error while checking SAML token issuer.");
        }
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
