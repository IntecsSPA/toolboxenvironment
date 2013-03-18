/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.security.validator.ToolboxPEP;
import java.net.URI;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import javax.servlet.http.HttpServletRequest;
import org.apache.axiom.om.OMElement;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.XML;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;

import org.apache.log4j.Logger;

import org.opensaml.SAMLAssertion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author mariarosaria
 */
public class GeoAuthorization implements Command {

    public static final String SAML_ASSERTION = "Assertion";
    static Logger logger = Logger.getLogger(GeoAuthorization.class);

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        try {
            logger.info("Geo Authorization process started");
            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);
            OMElement soapElemOM = msgCtx.getEnvelope();

            Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
            Document soapDoc = soapElemDOM.getOwnerDocument();

            Element assertionDOM = (Element) soapDoc.getElementsByTagNameNS(XML.SAML_NS, SAML_ASSERTION).item(0);
            SAMLAssertion saml = new SAMLAssertion(assertionDOM);

            //String soapAction = msgCtx.getSoapAction();
            String operationName = Toolbox.getOperationName(msgCtx);
            //System.out.println("ToolboxSecurityWrapper: soapAction = "+soapAction);

            HttpServletRequest req = (HttpServletRequest) msgCtx.getProperty("transport.http.servletRequest");


            ToolboxPEP pep = new ToolboxPEP();
            int resp = pep.enforceChecks(new URI(req.getRequestURI()), operationName, saml, soapDoc);

            /* The resp variable can take one of the following values:
             * com.sun.xacml.ctx.Result.DECISION_DENY           (1)
             * com.sun.xacml.ctx.Result.DECISION_INDETERMINATE  (2)
             * com.sun.xacml.ctx.Result.DECISION_NOT_APPLICABLE (3)
             * com.sun.xacml.ctx.Result.DECISION_PERMIT         (0)
             */
            if (resp == 1) {
                //extract information about the deny
                String denyMsg = "";
                NodeList nl = ((Element) soapDoc.getFirstChild()).getElementsByTagName("XACMLDeniedRule");
                Element elem = null;
                if (nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        elem = (Element) nl.item(i);
                        if (denyMsg.compareTo("") == 0) {
                            denyMsg += elem.getTextContent();
                        } else {
                            denyMsg += "\n" + elem.getNodeValue();
                        }
                    }
                } else {
                    denyMsg = "You are not allowed to access the requested resource(s)";
                }
                logger.error("Error while performing authorization check: " + denyMsg);
                return new Result(Result.FAIL, denyMsg);
            } else if (resp == 2) {
                //throw AxisFault exception
                String denyMsg = "The evaluation of the policy is indeterminate";
                logger.error("Error while performing authorization check: " + denyMsg);
                return new Result(Result.FAIL, denyMsg);
            }
            return new Result(Result.SUCCESS);
        } catch (Exception e) {
            logger.error("Error while performing authorization check: " + e.getMessage());
            return new Result(Result.FAIL);
        }
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
