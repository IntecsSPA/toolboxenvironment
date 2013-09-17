/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import it.intecs.pisa.toolbox.security.chain.commands.support.DailPEP;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import org.apache.axiom.soap.SOAPEnvelope;
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

    // These two constant correspond to the name of the subdirectory containing the policies
    private static final String POLICY_TYPE_CATALOGUE = "catalogue";
    private static final String POLICY_TYPE_ORDER = "order";
    // HMA Ordering interface	
    private static final String SUBMIT_TAG_NAME = "Submit";
    private static final String SUBMIT_NAMESPACE = "http://earth.esa.int/hma/ordering";
    private static final String DESCRIBERESULTACCESS_TAG_NAME = "DescribeResultAccess";
    private static final String DESCRIBERESULTACCESS_NAMESPACE = "http://earth.esa.int/hma/ordering";
    private static final String GETSTATUS_TAG_NAME = "GetStatus";
    private static final String GETSTATUS_NAMESPACE = "http://earth.esa.int/hma/ordering";
    private static final String CANCEL_TAG_NAME = "Cancel";
    private static final String CANCEL_NAMESPACE = "http://earth.esa.int/hma/ordering";
    private static final String GETQUOTATION_TAG_NAME = "GetQuotation";
    private static final String GETQUOTATION_NAMESPACE = "http://earth.esa.int/hma/ordering";
    // HMA Catalogue interface	
    private static final String GET_RECORDS_TAG_NAME = "GetRecords";
    private static final String GET_RECORDS_NAMESPACE = "http://www.opengis.net/cat/csw/2.0.2";
    private static final String GET_RECORDBYID_TAG_NAME = "GetRecordById";
    private static final String GET_RECORDBYID_NAMESPACE = "http://www.opengis.net/cat/csw/2.0.2";
    public static final String SAML_ASSERTION = "Assertion";
    static Logger logger = Logger.getLogger(DAILAuthorization.class);
    
    String mRequestType;
    /*
     * command property
     */
    private String dailPolicyDir;

    public void setDailPolicyDir(String inDailPolicyDir) {
        dailPolicyDir = inDailPolicyDir;
    }

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {

        logger.info("DAIL Authorization process started");

        Result result = new Result(Result.FAIL);
        MessageContext msgCtxt = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);

        boolean isRequest;
        if (msgCtxt.getFLOW() == MessageContext.IN_FLOW) {
            isRequest = true;
            logger.info("Processing the request");
        } else {
            isRequest = false;
            logger.info("Processing the response");
        }

        if (!isRequest) {
            ///////////////////////////// RESPONSE /////////////////////////////
            result.setCode(Result.SUCCESS);
            return result;
            ////////////////////////////////////////////////////////////////////
        }

        // get SOAP message to access various parts of it
        SOAPEnvelope soapRequestMessage = msgCtxt.getEnvelope();
        Document soapRequestDoc = null;

        try {
            soapRequestDoc = XMLUtils.toDOM(soapRequestMessage).getOwnerDocument();
            getRequestInfo(soapRequestDoc);
        } catch (Exception e) {
            logger.error("Request message retrieving error: " + e.getMessage());
            result.setCode(Result.FAIL);
            result.setExtraInfo("Response message retrieving error: " + e.getMessage());
            return result;
        }

        logger.info("Authorizing...");
        if (mRequestType == null) {
            logger.info("No policy to be applied, authorizing.");
            result.setCode(Result.SUCCESS);
            return result;
        }

        try {
            authorize(msgCtxt, soapRequestDoc, soapRequestDoc);
        } catch (Exception ex) {
            String errorMsg = "Authorization failed: " + ex.getMessage();
            logger.error(errorMsg);
            result.setCode(Result.FAIL);
            result.setExtraInfo(errorMsg);
            return result;
        }
        result.setCode(Result.SUCCESS);
        return result;
    }

    private void authorize(MessageContext msgCtxt, org.w3c.dom.Document soapDoc, org.w3c.dom.Document docWithAssertion) throws Exception {

        boolean anonymous = false;
        try {
            logger.info("Authorize started");

            Element samlAssertion = null;
            try {
                samlAssertion = getSOAPElement(docWithAssertion, XML.SAML_NS, "Assertion");
            } catch (Exception missingAssertion) {
                // Use a default assertion for anonymous requests
                logger.info("Managing anonymous request");
                anonymous = true;
            }

            String applicablePoliciesDir = dailPolicyDir;
             if ((mRequestType.compareTo(SUBMIT_TAG_NAME) == 0)
                    || (mRequestType.compareTo(GETQUOTATION_TAG_NAME) == 0)) {

                applicablePoliciesDir += "/" + POLICY_TYPE_ORDER + "/";

            } else if ((mRequestType.compareTo(GET_RECORDS_TAG_NAME) == 0)
                    || (mRequestType.compareTo(GET_RECORDBYID_TAG_NAME) == 0)) {
                applicablePoliciesDir += "/" + POLICY_TYPE_CATALOGUE + "/";
            } else {
                //System.out.println("No policy has to be applied for this request type ("+ mRequestType +")");
                logger.info("No policy has to be applied for this request type (" + mRequestType + ")");
                return;
            }

            //System.out.println("Loading policies from: " + applicablePoliciesDir);
            logger.info("Loading policies from: " + applicablePoliciesDir);
            DailPEP reqBuild = new DailPEP(applicablePoliciesDir, anonymous);

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
            int checkResult = reqBuild.enforceChecks(infoAction, soapDoc, docWithAssertion);
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
                logger.info(msg);
                throw new Exception(msg);
            }

        } catch (Exception ex) {
            logger.info("Exception in authorize: " + ex.getMessage());
            throw new Exception(ex.getMessage());
        } catch (java.lang.Throwable o) {
            String msg = "Unknown exception in authorize: ";
            logger.info(msg);
            if (o.getMessage() != null) {
                msg += o.getMessage();
                System.out.println(o.getMessage());
                logger.info(o.getMessage());
            }
            throw new Exception(msg);
        }
    }

    /**
     * Set mRequestType according to the request content
     */
    private void getRequestInfo(org.w3c.dom.Document soapDoc) {
        Element el;

        mRequestType = null;

        try {
            // check if the request is a GetRecordById
            el = getSOAPElement(soapDoc, GET_RECORDBYID_NAMESPACE, GET_RECORDBYID_TAG_NAME);
            mRequestType = GET_RECORDBYID_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }

        try {
            // check if the request is a GetRecords
            el = getSOAPElement(soapDoc, GET_RECORDS_NAMESPACE, GET_RECORDS_TAG_NAME);
            mRequestType = GET_RECORDS_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }

        try {
            // check if the request is a Submit
            el = getSOAPElement(soapDoc, SUBMIT_NAMESPACE, SUBMIT_TAG_NAME);
            mRequestType = SUBMIT_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }

        try {
            // check if the request is a DescribeResultAccess
            el = getSOAPElement(soapDoc, DESCRIBERESULTACCESS_NAMESPACE, DESCRIBERESULTACCESS_TAG_NAME);
            mRequestType = DESCRIBERESULTACCESS_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }

        try {
            // check if the request is a GetStatus
            el = getSOAPElement(soapDoc, GETSTATUS_NAMESPACE, GETSTATUS_TAG_NAME);
            mRequestType = GETSTATUS_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }

        try {
            // check if the request is a Cancel
            el = getSOAPElement(soapDoc, CANCEL_NAMESPACE, CANCEL_TAG_NAME);
            mRequestType = CANCEL_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }

        try {
            // check if the request is a Cancel
            el = getSOAPElement(soapDoc, GETQUOTATION_NAMESPACE, GETQUOTATION_TAG_NAME);
            mRequestType = GETQUOTATION_TAG_NAME;
        } catch (Exception e1) {
        }
        if (mRequestType != null) {
            return;
        }
    }

    private Element getSOAPElement(org.w3c.dom.Document doc, String namespace, String localName) throws Exception {

        NodeList list = doc.getElementsByTagNameNS(namespace, localName);
        if (list == null || list.getLength() == 0) {
            throw new Exception("Cannot get " + localName + " element");
        }

        return (Element) (list.item(0));
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
