package it.intecs.pisa.toolbox.security;

import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.util.DOMUtil;

import java.net.URL;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.RelatesTo;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.apache.neethi.Policy;
import org.apache.neethi.PolicyEngine;
import org.apache.rampart.RampartMessageData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class allows to send SOAP message with WS-Security
 * @author Stefano
 *
 */
public class Axis2SecureSOAPClient {

	/**
	 * 
	 * @author Stefano
	 * @param service	The secure service, currently it has associated WS-Security policy for asynchronous response by default
	 * @param url
	 * @param message
	 * @param soapAction
	 * @param SSLCertificateLocation
	 * @param wssPolicyAbsolutePath : A string with the absolute path where the ws-security policy is located
	 * @return
	 * @throws Exception
	 */
	public static Document secureExchange(URL url, Document message, String soapAction, String SSLCertificateLocation, String wssPolicyAbsolutePath) throws Exception {
		
		System.setProperty("javax.net.ssl.trustStore",SSLCertificateLocation);
		
		ServiceClient client = new ServiceClient();
		
        Options options = new Options();
        options.setAction(soapAction);
        options.setTo(new EndpointReference(url.toString()));
        
        //set the WS-Security policy to be used to add WS-Security information in the SOAP header
        options.setProperty(RampartMessageData.KEY_RAMPART_POLICY,  loadPolicy(wssPolicyAbsolutePath));
		
        client.setOptions(options);
        
        client.engageModule("addressing"); //do we need this?
        client.engageModule("rampart");

        AxisFault fault = null;
        OMElement response = null;
        try{
        	response = client.sendReceive(XMLUtils.toOM((Element)message.getElementsByTagName("Body").item(0)));
        }catch(AxisFault ex){
        	fault = ex;
        }
        
        if (fault != null){
        	//msgCtx.setProperty(RampartMessageData.KEY_RAMPART_POLICY, null);
        	//if I throw the original fault, the SOAP fault is returned to the client with a security header!!!
        	//probably Rampart intercepts it and applies the loaded policy
        	AxisFault faultbis = new AxisFault(fault.getFaultCode(), fault.getReason(),null);
        	//TODO AXIS FAULT HERE?!?!?!?!
        	throw faultbis;
        }
  
      
        DOMUtil util = new DOMUtil();                                
        Document responseDoc = util.stringToDocument(response.toString());
       
		return responseDoc;
	}
	
	
public static SOAPEnvelope secureExchange(URL url, SOAPEnvelope message, String soapAction, String SSLCertificateLocation, String wssPolicyAbsolutePath, RelatesTo[] rels2) throws Exception {
		
		System.setProperty("javax.net.ssl.trustStore",SSLCertificateLocation);

		ServiceClient client = new ServiceClient();
		
        Options options = new Options();
        options.setAction(soapAction);
        options.setTo(new EndpointReference(url.toString()));
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        
        //set the WS-Security policy to be used to add WS-Security information in the SOAP header
        options.setProperty(RampartMessageData.KEY_RAMPART_POLICY,  loadPolicy(wssPolicyAbsolutePath));
        
        if (rels2 != null)
        	options.setRelationships(rels2);
		
        client.setOptions(options);
        
        client.engageModule("addressing"); //do we need this?
        client.engageModule("rampart");

        MessageContext msgctx = new MessageContext();
        msgctx.setEnvelope(message);
        
        OperationClient opClient = client.createClient(ServiceClient.ANON_OUT_IN_OP);
        opClient.addMessageContext(msgctx);
        opClient.execute(true);
        MessageContext responseMsgCtx = opClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        return responseMsgCtx.getEnvelope();
 
	}
	
	/**
    * Loads the given policy
    * @param xmlPath
    * @return
    * @throws Exception
    */
     private static Policy loadPolicy(String xmlPath) throws Exception {
               
        StAXOMBuilder builder = null;
        try{
             builder = new StAXOMBuilder(xmlPath);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
               
		return PolicyEngine.getPolicy(builder.getDocumentElement());
    }
	
}
