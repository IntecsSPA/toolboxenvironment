/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.security.service;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.security.validator.ToolboxPEP;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.om.OMElement;

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
 * This class represents the entrypoint service for the Toolbox Policy Enforcement Point (PEP).
 * Can have WS-Security policies specified in the services.xml.
 * This service class is invoked by Axis2 if the WS-Security checks succeed; then this class
 * checks for the SAML token existence and call the Toolbox PEP.
 * 
 * @author Stefano
 */
public class ToolboxSecurityWrapper {
    
	public static final String SERVICE_NAME = "ToolboxSecurityWrapper";
	
    /**
     * payload: represents the payload of the incoming SOAP message.
     * @returns OMElement : the payload of the return SOAP message.
     */
    public OMElement execute(OMElement payload) throws AxisFault {
        OMElement response= null;
        AxisFault fault = null;
        Logger logger = Toolbox.getInstance().getLogger();
        try{
        
            MessageContext msgCtx = MessageContext.getCurrentMessageContext();
            
            //String soapAction = msgCtx.getSoapAction();
            String operationName = Toolbox.getOperationName(msgCtx);
            //System.out.println("ToolboxSecurityWrapper: soapAction = "+soapAction);

            HttpServletRequest req =(HttpServletRequest)msgCtx.getProperty("transport.http.servletRequest");
            
            //retrieve the SAML token, if any
            SAMLAssertion saml = null;
            try{
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
                                        WSSecurityEngineResult wser = (WSSecurityEngineResult)wsSecEngineResults.get(j);
                                        if (wser.get(WSSecurityEngineResult.TAG_SAML_ASSERTION) != null){
                                            
                                            saml = (SAMLAssertion) wser.get(WSSecurityEngineResult.TAG_SAML_ASSERTION);
                                        }
                                }
                        }
                }  
            }catch(Exception ex){
                logger.error("ToolboxSecurityWrapper, an exception occurs while trying to retrieve the SAML token!!!", ex);
            }

            // TODO : PEP should be called even if SAML is null???
            if (saml != null){
                //call PEP
                //information about the SOAP needs to be passed to the PEP as a Element
                OMElement soapOMElem = msgCtx.getEnvelope();//.getBody().getFirstElement();
                Document soapDoc = stringToDocument(soapOMElem.toString());
            
                ToolboxPEP pep = new ToolboxPEP();
                int resp = pep.enforceChecks(new URI(req.getRequestURI()), operationName, saml, soapDoc.getDocumentElement());
                             
                if (resp == 1){
                    //extract information about the deny
                    String denyMsg ="";
                    NodeList nl =((Element) soapDoc.getFirstChild()).getElementsByTagName("XACMLDeniedRule");
                    Element elem=null;
                    if (nl != null){
	                    for (int i =0; i<nl.getLength(); i++){
	                    	elem = (Element) nl.item(i);
	                    	if (denyMsg.compareTo("")==0)
	                    		denyMsg += elem.getTextContent();
	                    	else
	                    		denyMsg += "\n" + elem.getNodeValue();
	                    }
                    }else{
                    	System.out.println("PEP: impossible to retrieve information about the deny decision.");
                    	denyMsg = "Access denied";
                    }
                    //throw AxisFault exception
                    fault = new AxisFault(new QName("http://www.intecs.it/PEP", "AccessDenied", "pep"), denyMsg,
                            new Exception("PEP: Deny!"));
                    
                    throw fault;                                   
                }
            }
            //policies checked successfully
            //now re-route the request
            /*
            ServiceClient client = new ServiceClient(msgCtx.getConfigurationContext(), null);

            Options options = new Options();
            options.setAction(soapAction);

            //retrieve the endpoint address from the services.xml
            String targetendpoint = (String) msgCtx.getAxisService().getParameter("TargetServiceEndPoint").getValue();
            options.setTo(new EndpointReference(targetendpoint));
            
            //set chunked to false, otherwise the request fails...
            options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
            //options.setSoapVersionURI(SOAP12Constants.SOAP_ENVELOPE_NAMESPACE_URI);
            client.setOptions(options);

            //System.out.println("ToolboxSecurityWrapper invoking request  : " + msgCtx.getEnvelope().getBody().getFirstElement().toStringWithConsume());
            System.out.println("ToolboxSecurityWrapper invoking request....");
            response = client.sendReceive(msgCtx.getEnvelope().getBody().getFirstElement());*/
           
            //Toolbox takes the entire envelope...
            Element soapRequestDocument = XMLUtils.toDOM(msgCtx.getEnvelope());
          
            //now pass the request to the Toolbox
            Toolbox toolbox = Toolbox.getInstance();
            String uri = req.getRequestURI();
            
            /*The following code can be used during development to force the test service invocation
            if (uri.endsWith("testSeqService") || uri.endsWith("prova")){
            	uri = "TOOLBOX/services/testService";
            }*/
            
            Document respDoc = null;
            try{
            	respDoc = toolbox.executeServiceRequest(msgCtx, uri , false);
            }catch (Exception e) {
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
            
        }
        catch (AxisFault ex){
        	fault = ex;
        }
        catch (Exception ex){
        	ex.printStackTrace();
        }
        
        if (fault != null)
        	throw fault;
        
        return response; 
    }
    
    
    protected Document stringToDocument(String xml) throws IOException, SAXException {
    	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder=null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return docBuilder.parse(new InputSource(new StringReader(xml)));
    }

    
}
