/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.service;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.security.chain.commands.CommandsConstants;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Hashtable;
import java.util.Vector;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;
import javawebparts.misc.chain.Result;
import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axiom.om.OMAbstractFactory;


import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.util.CopyUtils;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.apache.ws.security.handler.WSHandlerResult;
import org.opensaml.SAMLAssertion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class represents the entrypoint service for the Toolbox Policy
 * Enforcement Point (PEP). Can have WS-Security policies specified in the
 * services.xml. This service class is invoked by Axis2 if the WS-Security
 * checks succeed; then this class checks for the SAML token existence and call
 * the Toolbox PEP.
 *
 * @author Stefano
 */
public class ToolboxSecurityWrapper {

    public static final String SERVICE_NAME = "ToolboxSecurityWrapper";
    // the following string shall preced the specific detail 
    // of possible failure in the policy enforcement process
    private static String policyEnforcementError = "Policy enforcement restricts access. ";
    private static String RESPONSE_MESSAGE = "ResponseMessage";
    
    private ChainManager chainManager = null; 
    
    private void init(){
        if (chainManager == null)
            chainManager = new ChainManager();
    }

    /**
     * payload: represents the payload of the incoming SOAP message.
     *
     * @returns OMElement : the payload of the return SOAP message.
     */
    public OMElement execute(OMElement payload) throws AxisFault {
        
        OMElement response = null;
        AxisFault fault = null;
        Logger logger = Toolbox.getInstance().getLogger();
        logger.info("Processing secured operation");
        
        init();

        MessageContext msgCtx = MessageContext.getCurrentMessageContext();

        HttpServletRequest req = (HttpServletRequest) msgCtx.getProperty("transport.http.servletRequest");

        //String soapAction = msgCtx.getSoapAction();
        //System.out.println("ToolboxSecurityWrapper: soapAction = "+soapAction);

        try {
            //retrieve the SAML token, if any
            SAMLAssertion saml = null;

            //Object samlobj = (SAMLAssertion) msgCtx.getConfigurationContext().getProperty("SAMLToken");

            Vector results = null;
            if ((results = (Vector) msgCtx.getProperty(WSHandlerConstants.RECV_RESULTS)) == null) {
                logger.info("ToolboxSecurityWrapper, No security results!!");
            } else {
                for (int i = 0; i < results.size(); i++) {
                    //Get hold of the WSHandlerResult instance
                    WSHandlerResult rResult = (WSHandlerResult) results.get(i);
                    Vector wsSecEngineResults = rResult.getResults();

                    for (int j = 0; j < wsSecEngineResults.size(); j++) {
                        //Get hold of the WSSecurityEngineResult instance
                        WSSecurityEngineResult wser = (WSSecurityEngineResult) wsSecEngineResults.get(j);
                        if (wser.get(WSSecurityEngineResult.TAG_SAML_ASSERTION) != null) {

                            saml = (SAMLAssertion) wser.get(WSSecurityEngineResult.TAG_SAML_ASSERTION);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("ToolboxSecurityWrapper, an exception occurs while trying to retrieve the SAML token!!!", ex);
        }

        try {
            String operationName = Toolbox.getOperationName(msgCtx);

            String[] requestSplit = req.getRequestURI().split("/");
            String serviceName = requestSplit[requestSplit.length - 1];

            Result result = callServiceChain(serviceName, msgCtx);
            if (result.getCode() == Result.FAIL) {
                String denyMsg = result.getExtraInfo();
                fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);

                OMElement soapElemOM = msgCtx.getEnvelope();//.getBody().getFirstElement();

                Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
                Document soapDoc = soapElemDOM.getOwnerDocument();
                storeAccessDeniedInstanceIntoDB(soapDoc, serviceName, operationName, fault);

                throw fault;
            }
            if (isIncomingTokenToBeRestored(serviceName)) {
                ChainContext ct = chainManager.createContext();
                ct.setAttribute(CommandsConstants.MESSAGE_CONTEXT, msgCtx);
                chainManager.executeChain("default/restoreChain", ct);
                result = ct.getResult();
                if (result.getCode() == Result.FAIL) {
                    String denyMsg = result.getExtraInfo();
                    fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);

                    OMElement soapElemOM = msgCtx.getEnvelope();//.getBody().getFirstElement();

                    Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
                    Document soapDoc = soapElemDOM.getOwnerDocument();
                    storeAccessDeniedInstanceIntoDB(soapDoc, serviceName, operationName, fault);

                    throw fault;
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
            if (fault != null) {
                throw fault;
            } else {
                fault = generateExceptionReport(msgCtx, policyEnforcementError + ex.getMessage());
                throw fault;
            }
        }
        //policies checked successfully
        //now re-route the request
        //Toolbox takes the entire envelope...
        try {
            //Element soapRequestDocument = XMLUtils.toDOM(msgCtx.getEnvelope());
            //now pass the request to the Toolbox
            Toolbox toolbox = Toolbox.getInstance();
            String uri = req.getRequestURI();

            /*The following code can be used during development to force the test service invocation
             if (uri.endsWith("testSeqService") || uri.endsWith("prova")){
             uri = "TOOLBOX/services/testService";
             }*/

            Document respDoc = null;
            try {
                respDoc = toolbox.executeServiceRequest(msgCtx, uri, false);
            } catch (Exception e) {
                //******** an exception has been thrown, sending a SOAP Fault ********************
                try {
                    if (e instanceof ToolboxException) {
                        respDoc = Util.getSOAPFault((ToolboxException) e);
                    } else {
                        respDoc = Util.getSOAPFault(e.getMessage());
                    }
                    fault = new AxisFault("");
                    fault.setDetail(org.apache.axis2.util.XMLUtils.toOM(respDoc.getDocumentElement()));

                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }

            response = XMLUtils.toOM(respDoc.getDocumentElement());

        } catch (AxisFault ex) {
            fault = ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (fault != null) {
            throw fault;
        }

        return response;
    }

    public OMElement check(OMElement payload) throws AxisFault {    
        String ENCRYPTED_DATA_NAMESPACE = "http://www.w3.org/2001/04/xmlenc#";
        String ENCRYPTED_DATA = "EncryptedData";
        String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
        String WS_SECURITY = "Security";

        OMElement response = null;
        AxisFault fault = null;
        Logger logger = Toolbox.getInstance().getLogger();
        logger.info("Processing optional secured operation");
        
        init();

        MessageContext msgCtx = MessageContext.getCurrentMessageContext();

        HttpServletRequest req = (HttpServletRequest) msgCtx.getProperty("transport.http.servletRequest");

        String[] requestSplit = req.getRequestURI().split("/");
        String serviceName = requestSplit[requestSplit.length - 1];

        boolean isTokenIncludedInSOAPHeader = false;
        MessageContext tempMsgCtx = null;
        try {
            String operationName = Toolbox.getOperationName(msgCtx);

            SOAPEnvelope envelope = msgCtx.getEnvelope();
            SOAPHeader soapHeader = envelope.getHeader();

            OMElement wsSecurity = soapHeader.getFirstChildWithName(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY));
            if (wsSecurity != null) {
                OMElement encryptedData = wsSecurity.getFirstChildWithName(new QName(ENCRYPTED_DATA_NAMESPACE, ENCRYPTED_DATA));
                if (encryptedData != null) {
                    isTokenIncludedInSOAPHeader = true;                 
                    ChainContext ct = chainManager.createContext();
                    ct.setAttribute(CommandsConstants.MESSAGE_CONTEXT, msgCtx);
                    chainManager.executeChain("default/decryptAndCheckSignatureChain", ct);
                    Result result = ct.getResult();
                    if (result.getCode() == Result.FAIL) {
                        String denyMsg = result.getExtraInfo();
                        fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);

                        OMElement soapElemOM = msgCtx.getEnvelope();//.getBody().getFirstElement();

                        Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
                        Document soapDoc = soapElemDOM.getOwnerDocument();
                        storeAccessDeniedInstanceIntoDB(soapDoc, serviceName, operationName, fault);

                        throw fault;
                    }

                    result = callServiceAuthenticationChain(serviceName, msgCtx);
                    if (result.getCode() == Result.FAIL) {
                        String denyMsg = result.getExtraInfo();
                        fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);

                        OMElement soapElemOM = msgCtx.getEnvelope();//.getBody().getFirstElement();

                        Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
                        Document soapDoc = soapElemDOM.getOwnerDocument();
                        storeAccessDeniedInstanceIntoDB(soapDoc, serviceName, operationName, fault);

                        throw fault;
                    }
                }

            }

            Result result = callServiceAuthorizationChain(serviceName, msgCtx);
            if (result.getCode() == Result.FAIL) {
                String denyMsg = result.getExtraInfo();
                fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);

                OMElement soapElemOM = msgCtx.getEnvelope();//.getBody().getFirstElement();

                Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
                Document soapDoc = soapElemDOM.getOwnerDocument();
                storeAccessDeniedInstanceIntoDB(soapDoc, serviceName, operationName, fault);

                throw fault;
            }

            tempMsgCtx = new MessageContext();
            SOAPEnvelope tempEnvelope = CopyUtils.copy(msgCtx.getEnvelope());
            tempMsgCtx.setEnvelope(tempEnvelope);

            if (isTokenIncludedInSOAPHeader && isIncomingTokenToBeRestored(serviceName)) {              
                ChainContext ct = chainManager.createContext();
                ct.setAttribute(CommandsConstants.MESSAGE_CONTEXT, msgCtx);
                chainManager.executeChain("default/restoreChain", ct);
                result = ct.getResult();
                if (result.getCode() == Result.FAIL) {
                    String denyMsg = result.getExtraInfo();
                    fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);

                    OMElement soapElemOM = msgCtx.getEnvelope();//.getBody().getFirstElement();

                    Element soapElemDOM = XMLUtils.toDOM(soapElemOM);
                    Document soapDoc = soapElemDOM.getOwnerDocument();
                    storeAccessDeniedInstanceIntoDB(soapDoc, serviceName, operationName, fault);
                }
            }
        } catch (Exception ex) {
            ex.getMessage();
            if (fault != null) {
                throw fault;
            } else {
                fault = generateExceptionReport(msgCtx, policyEnforcementError + ex.getMessage());
                throw fault;
            }
        }
        try {
            //now pass the request to the Toolbox
            Toolbox toolbox = Toolbox.getInstance();
            String uri = req.getRequestURI();

            Document respDoc = null;
            try {
                respDoc = toolbox.executeServiceRequest(msgCtx, uri, false);
            } catch (Exception e) {
                //******** an exception has been thrown, sending a SOAP Fault ********************
                try {
                    if (e instanceof ToolboxException) {
                        respDoc = Util.getSOAPFault((ToolboxException) e);
                    } else {
                        respDoc = Util.getSOAPFault(e.getMessage());
                    }
                    fault = new AxisFault("");
                    fault.setDetail(org.apache.axis2.util.XMLUtils.toOM(respDoc.getDocumentElement()));

                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }

            response = XMLUtils.toOM(respDoc.getDocumentElement());

            if (isAuthorizationCheckOnResponseRequired(serviceName)) {
                Util.addSOAPEnvelope(respDoc);

                tempMsgCtx.setProperty(RESPONSE_MESSAGE, respDoc);
                tempMsgCtx.setFLOW(MessageContext.OUT_FLOW);
                
                Result result = callServiceAuthorizationChain(serviceName, tempMsgCtx);
                if (result.getCode() == Result.FAIL) {
                    String denyMsg = result.getExtraInfo();
                    fault = generateExceptionReport(msgCtx, policyEnforcementError + denyMsg);
                    throw fault;
                }

            }

        } catch (AxisFault ex) {
            fault = ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (fault != null) {
            throw fault;
        }

        return response;
    }

    public OMElement pass(OMElement payload) throws AxisFault {
        OMElement response = null;
        AxisFault fault = null;
        Logger logger = Toolbox.getInstance().getLogger();
        logger.info("Processing not secured operation");
        try {

            MessageContext msgCtx = MessageContext.getCurrentMessageContext();

            HttpServletRequest req = (HttpServletRequest) msgCtx.getProperty("transport.http.servletRequest");

            //now pass the request to the Toolbox
            Toolbox toolbox = Toolbox.getInstance();
            String uri = req.getRequestURI();

            /*The following code can be used during development to force the test service invocation
             if (uri.endsWith("testSeqService") || uri.endsWith("prova")){
             uri = "TOOLBOX/services/testService";
             }*/

            //callChain("Catalogue/passChain", msgCtx);


            Document respDoc = null;
            try {
                respDoc = toolbox.executeServiceRequest(msgCtx, uri, false);
            } catch (Exception e) {
                //******** an exception has been thrown, sending a SOAP Fault ********************
                try {
                    if (e instanceof ToolboxException) {
                        respDoc = Util.getSOAPFault((ToolboxException) e);
                    } else {
                        respDoc = Util.getSOAPFault(e.getMessage());
                    }
                    fault = new AxisFault("");
                    fault.setDetail(org.apache.axis2.util.XMLUtils.toOM(respDoc.getDocumentElement()));

                } catch (Exception ex) {
                    logger.error("", ex);
                }
            }

            response = XMLUtils.toOM(respDoc.getDocumentElement());

        } catch (AxisFault ex) {
            fault = ex;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (fault != null) {
            throw fault;
        }

        return response;
    }

    private boolean isIncomingTokenToBeRestored(String serviceName) {
        ServiceManager serviceManager = ServiceManager.getInstance();
        Hashtable<String, Hashtable<String, String>> serviceVariables;
        boolean res = false;

        try {
            TBXService service = serviceManager.getService(serviceName);
            serviceVariables = service.getImplementedInterface().getUserVariable();

            Hashtable<String, String> serviceVariable = serviceVariables.get("forwardMessageWithIncomingToken");
            String resString = serviceVariable.get("value");
            res = Boolean.parseBoolean(resString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private boolean isAuthorizationCheckOnResponseRequired(String serviceName) {

        ServiceManager serviceManager = ServiceManager.getInstance();
        Hashtable<String, Hashtable<String, String>> serviceVariables;
        boolean res = false;

        try {
            TBXService service = serviceManager.getService(serviceName);
            serviceVariables = service.getImplementedInterface().getUserVariable();

            Hashtable<String, String> serviceVariable = serviceVariables.get("checkAuthorizationOnResponse");
            String resString = serviceVariable.get("value");
            res = Boolean.parseBoolean(resString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private Result callServiceChain(String serviceName, MessageContext msgCtx) {

        Logger logger = Toolbox.getInstance().getLogger();

        ServiceManager serviceManager = ServiceManager.getInstance();
        try {

            TBXService service = serviceManager.getService(serviceName);
            if (service.getCommandChainConfigured() == false) {
                // the ChainManager expects a configuration file in the path WEB-INF/classes
                String serviceCommandsPath = "../services/" + serviceName + "/serviceChain.xml";
                ChainManager cm = new ChainManager(serviceCommandsPath);

                service.setCommandChainConfigured(true);
            }
            ChainContext ct = chainManager.createContext();
            ct.setAttribute(CommandsConstants.MESSAGE_CONTEXT, msgCtx);
            chainManager.executeChain(serviceName + "/securityCommands", ct);
            return ct.getResult();
        } catch (Exception ex) {
            return new Result(Result.FAIL, ex.getMessage());
        }
    }

    private Result callServiceAuthenticationChain(String serviceName, MessageContext msgCtx) {

        Logger logger = Toolbox.getInstance().getLogger();

        ServiceManager serviceManager = ServiceManager.getInstance();
        try {

            TBXService service = serviceManager.getService(serviceName);
            if (service.getCommandChainConfigured() == false) {

                // the ChainManager expects a configuration file in the path WEB-INF/classes
                String serviceCommandsPath = "../services/" + serviceName + "/serviceChain.xml";
                ChainManager cm = new ChainManager(serviceCommandsPath);

                service.setCommandChainConfigured(true);
            }
            ChainContext ct = chainManager.createContext();
            ct.setAttribute(CommandsConstants.MESSAGE_CONTEXT, msgCtx);
            chainManager.executeChain(serviceName + "/authenticationCommands", ct);
            return ct.getResult();
        } catch (Exception ex) {
            return new Result(Result.FAIL, ex.getMessage());
        }
    }

    private Result callServiceAuthorizationChain(String serviceName, MessageContext msgCtx) {

        Logger logger = Toolbox.getInstance().getLogger();
        ServiceManager serviceManager = ServiceManager.getInstance();
        try {

            TBXService service = serviceManager.getService(serviceName);
            if (service.getCommandChainConfigured() == false) {

                // the ChainManager expects a configuration file in the path WEB-INF/classes
                String serviceCommandsPath = "../services/" + serviceName + "/serviceChain.xml";
                ChainManager cm = new ChainManager(serviceCommandsPath);

                service.setCommandChainConfigured(true);
            }
            ChainContext ct = chainManager.createContext();
            ct.setAttribute(CommandsConstants.MESSAGE_CONTEXT, msgCtx);
            chainManager.executeChain(serviceName + "/authorizationCommands", ct);
            return ct.getResult();
        } catch (Exception ex) {
            return new Result(Result.FAIL, ex.getMessage());
        }
    }

    protected Document stringToDocument(String xml) throws IOException, SAXException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return docBuilder.parse(new InputSource(new StringReader(xml)));
    }

    private void storeAccessDeniedInstanceIntoDB(Document soapReq, String serviceName, String soapAction, AxisFault axisFualt) throws Exception {
        TBXService tbxService;
        ServiceManager serviceManager = ServiceManager.getInstance();
        tbxService = serviceManager.getService(serviceName);

        TBXOperation tbxOp = tbxService.getOperationBySoapAction(soapAction);
        tbxOp.processAccessDeniedRequest(soapReq, true, axisFualt);
    }

    private AxisFault generateExceptionReport(MessageContext msgCtxt, String detailTextMsg) {

        OMNamespace requestNamespace = msgCtxt.getEnvelope().getSOAPBodyFirstElementNS();

        QName faultCode = new QName(requestNamespace.getNamespaceURI(), "ServiceExceptionReport", requestNamespace.getPrefix());
        String faultReason = "Policy exception report";

        try {

            String CAT_NAMESPACE = "http://www.opengis.net/cat/csw/2.0.2";
            String ORD_NAMESPACE = "http://earth.esa.int/hma/ordering";

            String exceptionReportVersion = null;
            String exceptionCode = null;


            if (requestNamespace.getNamespaceURI().compareTo(CAT_NAMESPACE) == 0) {
                exceptionReportVersion = "1.2.0";
                exceptionCode = "wrs:InvalidRequest";
            } else {
                exceptionReportVersion = "1.0.0";
                exceptionCode = "NoApplicableCode";
            }

            OMFactory factory = OMAbstractFactory.getOMFactory();

            OMNamespace owsNamespace = factory.createOMNamespace("http://www.opengis.net/ows", "ows");

            OMElement exceptionReportElement = factory.createOMElement("ExceptionReport", owsNamespace);
            exceptionReportElement.addAttribute("version", exceptionReportVersion, null);

            OMElement exceptionElement = factory.createOMElement("Exception", owsNamespace);
            exceptionElement.addAttribute("exceptionCode", exceptionCode, null);
            exceptionReportElement.addChild(exceptionElement);

            OMElement exceptionTextElement = factory.createOMElement("ExceptionText", owsNamespace);
            exceptionTextElement.setText(detailTextMsg);
            exceptionElement.addChild(exceptionTextElement);

            return new AxisFault(faultCode, faultReason, null, null, exceptionReportElement);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new AxisFault(faultCode, faultReason, null, null, null);
        }

    }
}
