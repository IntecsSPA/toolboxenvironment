/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;



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
public class CheckSAMLTokenValidityTime implements Command {

    static public String SAML_ASSERTION = "Assertion";
    /*
     * logger for the class
     */
    static Logger logger = Logger.getLogger(CheckSAMLTokenValidityTime.class);

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);

    }

    @Override
    public Result execute(ChainContext cc) {

        try {
            logger.info("Token time validity check started");
            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);
            SOAPEnvelope envelope = msgCtx.getEnvelope();
            SOAPHeader soapHeader = envelope.getHeader();

            Element soapHeaderDOM = XMLUtils.toDOM(soapHeader);

            Element assertion = (Element) soapHeaderDOM.getElementsByTagNameNS(XML.SAML_NS, SAML_ASSERTION).item(0);

            SAMLAssertion saml = new SAMLAssertion(assertion);
            Date beforeDate = saml.getNotBefore();
            Date onOrAfterDate = saml.getNotOnOrAfter();

            Calendar calBeforeDate = Calendar.getInstance();
            Calendar calOnOrAfterDate = Calendar.getInstance();
            calBeforeDate.setTime(beforeDate);
            calOnOrAfterDate.setTime(onOrAfterDate);

            Calendar calCurrentDate = Calendar.getInstance();


            if (calCurrentDate.after(calBeforeDate) && calCurrentDate.before(calOnOrAfterDate)) {
                logger.info("The SAML token time condition is met");
                return new Result(Result.SUCCESS);

            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                logger.error("The token is expired; the valid time interval was: "
                        + sdf.format(beforeDate) + " through " + sdf.format(onOrAfterDate));
                return new Result(Result.FAIL, "The SAML token has expired.");
            }

        } catch (Exception e) {
            logger.error("Error when checking SAML token validity time: " + e.getMessage());
            return new Result(Result.FAIL, "Error while checking SAML token validity time.");
        }
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
