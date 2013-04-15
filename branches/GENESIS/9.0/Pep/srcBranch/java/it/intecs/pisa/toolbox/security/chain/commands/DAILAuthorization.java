/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import it.intecs.pisa.toolbox.security.chain.commands.support.DailPEP;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import org.apache.axiom.om.OMElement;
import org.opensaml.XML;


import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mariarosaria
 */
public class DAILAuthorization implements Command {

    public static final String SAML_ASSERTION = "Assertion";
    static Logger logger = Logger.getLogger(DAILAuthorization.class);
    /*
     * command property
     */
    private String policyDir;

    public void setPolicyDir(String dailPolicyDir) {
        policyDir = dailPolicyDir;
    }

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        try {
            logger.info("Authorization process started");
            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);

            OMElement soapElemOM = msgCtx.getEnvelope();

            Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
            Document soapDoc = soapElemDOM.getOwnerDocument();

            Element assertionDOM = (Element) soapDoc.getElementsByTagNameNS(XML.SAML_NS, SAML_ASSERTION).item(0);

            boolean anonymous = false;
           
            if (assertionDOM == null) {
                anonymous = true;
                logger.info("Managing anonymous request");
            } 
            
            logger.info("Loading policies from: " + policyDir);

            DailPEP reqBuild = new DailPEP(policyDir, anonymous);

            Node tempNode = soapDoc.getElementsByTagNameNS("*", "Body").item(0);
            NodeList nodeList = tempNode.getChildNodes();
            int index = 0;
            while (index < nodeList.getLength()) {
                tempNode = nodeList.item(index);
                if (tempNode.getNodeType() == tempNode.ELEMENT_NODE) {
                    break;
                }
                index++;
            }

            String request = new String(tempNode.getLocalName());
            String namespace = new String(tempNode.getNamespaceURI());
            String infoAction = new String(request);
            infoAction = infoAction + ' ' + namespace;

            logger.info("EnforceChecks");

            int checkResult = reqBuild.enforceChecks(infoAction, soapDoc, soapDoc);
            if (checkResult != 0) {
                String msg;
                switch (checkResult) {
                    case 1:
                        msg = "Authorization denied";
                        break;
                    case 2:
                        msg = "A decision cannot be made";
                        break;
                    case 3:
                        msg = "No applicable policies were found for the request";
                        break;
                    default:
                        msg = "Authorization failed";
                        break;
                }
                logger.error(msg);
                return new Result(Result.FAIL, msg);
            }

        } catch (Exception ex) {
            logger.error("Exception in authorize: " + ex.getMessage());
            return new Result(Result.FAIL, ex.getMessage());
        } catch (java.lang.Throwable o) {
            String msg = "Unknown exception in authorize: ";
            logger.error(msg);
            if (o.getMessage() != null) {
                msg += o.getMessage();
                logger.error(o.getMessage());

            }
            return new Result(Result.FAIL, msg);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
