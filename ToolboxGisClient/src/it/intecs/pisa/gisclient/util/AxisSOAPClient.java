/*
 *
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 *  File Name:         $RCSfile: AxisSOAPClient.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */

package it.intecs.pisa.gisclient.util;

import java.net.*;
import java.io.*;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.addressing.RelatesTo;
import org.apache.axis2.client.OperationClient;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;
import org.apache.axis2.wsdl.WSDLConstants;
import org.w3c.dom.*;

// imports needed for the SSL communication
import javax.xml.soap.SOAPMessage;

import java.security.Security;

import java.util.Iterator;
import java.util.LinkedList;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;

public class AxisSOAPClient {
    private static final String USAGE = "USAGE: java [-cp $CLASSPATH] AxisSOAPClient <url> <message_path> <is_message_body> [soap_action]";
    private static final String REQUEST_INFO = "Building request message...";
    private static final String SENDING_INFO = "Sending request ...";
    private static PrintStream out = System.out;
           
    /**
     * @author 
     * @param url
     * @param request
     * @param soapAction
     * @return
     * @throws Exception
     * @deprecated  Replaced by {@link #sendReceive(URL, Element, String )}, {@link #sendReceive(URL, SOAPEnvelope, String )}
     */
    public static SOAPMessage exchange(URL url, SOAPMessage request, String soapAction) throws Exception {
        Options options = new Options();
        ServiceClient client = new ServiceClient();
        options.setTo(new EndpointReference(url.toString()));
        
        options.setAction(soapAction);
        options.setTimeOutInMilliSeconds(6000000);
       options.setProperty(org.apache.axis2.transport.http.HTTPConstants.HEADER_CONTENT_ENCODING, "utf-8");

        
        //Blocking invocation
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);

        client.setOptions(options);
        OMElement OMsoap = XMLUtils.toOM((Element) request.getSOAPBody().getFirstChild(), true);
        OMElement result = client.sendReceive(OMsoap);
        
        Element DOMresult = XMLUtils.toDOM(result);
        SOAPUtils utils = new SOAPUtils();
        return utils.getSOAPMessage(DOMresult.getOwnerDocument()); 
    }

    
       
    /**
     * This method invokes SOAP request using AXIS2 as SOAP engine. 
     * @author Stefano
     * @param url
     * @param message is the Element representing the body of the SOAP message to be sent
     * @param soapAction
     * @return the Element representing the body of the SOAP response
     * @throws Exception
     */
    public static Element sendReceive(URL url, Element message, String soapAction) throws Exception {
        SOAPEnvelope request = SOAPUtils.getSOAPEnvelope(message.getOwnerDocument(),true);
       // request.serialize(System.out);
        SOAPEnvelope response = sendReceive(url, request, soapAction, null, null);
        return XMLUtils.toDOM(response.getBody().getFirstElement());
    }

    /**
     * This method invokes SOAP request using AXIS2 as SOAP engine.
     * @author Messimiliano Fanciulli
     * @param url
     * @param message is the Element representing the body of the SOAP message to be sent
     * @param soapAction
     * @return the Element representing the body of the SOAP response
     * @throws Exception
     */
    public static Element sendReceive(URL url, Element message, Element[] soapHeaders, String soapAction, String messageID, String replyTo) throws Exception {
        Options options = new Options();
        ServiceClient client = new ServiceClient();
        InputStream inStream;
        options.setTo(new EndpointReference(url.toString()));

        options.setAction(soapAction);
        options.setTimeOutInMilliSeconds(60000000);
        //Blocking invocation
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.HEADER_CONTENT_TRANSFER_ENCODING, "utf-8");

        client.setOptions(options);

        if(replyTo!=null && replyTo.equals("")==false)
            options.setReplyTo(new EndpointReference(replyTo));

        if(messageID!=null && messageID.equals("")==false)
            options.setMessageId(messageID);

        if(soapHeaders!=null)
        {
            for(Element header:soapHeaders)
            {
                addHeader(header,client);

            }
        }

        inStream=XmlTools.getDocumentAsInputStream(message.getOwnerDocument());
        OMElement result=null;
        OMElement inputMsg;
        try
        {
            inputMsg=(OMElement)XMLUtils.toOM(inStream);
            result = client.sendReceive(inputMsg);
        }
        catch(Exception ecc) {
            ecc.printStackTrace();
        }
        return XMLUtils.toDOM(result);
    }

    public static Element sendReceive(URL url, Document message, String soapAction, String messageID, String replyTo) throws Exception {
        SOAPEnvelope request = SOAPUtils.getSOAPEnvelope(message,messageID, replyTo, true);
      //  request.serialize(System.out);
        SOAPEnvelope response = sendReceive(url, request, soapAction, null, null);
        return XMLUtils.toDOM(response);
    }

    public static Element sendReceive(URL url, Document message, String soapAction, String messageID) throws Exception {
        SOAPEnvelope request = SOAPUtils.getSOAPEnvelope(message, true);
        SOAPEnvelope response = sendReceive(url, request, soapAction, null);
        return XMLUtils.toDOM(response);
    }


    /**
     *
     * @author Stefano
     * @param url
     * @param inEnvelope
     * @param soapAction
     * @param rels2 WSA relatesTo parameters
     * @return
     * @throws Exception
     */
    public static SOAPEnvelope sendReceive(URL url, SOAPEnvelope inEnvelope, String soapAction, RelatesTo[] rels2) throws Exception{

    	ServiceClient client = new ServiceClient();

        Options options = new Options();
         options.setTimeOutInMilliSeconds(60000000);

        client.setOptions(options);
        options.setTo(new EndpointReference(url.toString()));
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.HEADER_CONTENT_ENCODING, "utf-8");

        options.setAction(soapAction);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        if (rels2 != null)
        	options.setRelationships(rels2);

        MessageContext msgctx = new MessageContext();
        msgctx.setEnvelope(inEnvelope);

        OperationClient opClient = client.createClient(ServiceClient.ANON_OUT_IN_OP);
        opClient.addMessageContext(msgctx);
        opClient.execute(true);
        MessageContext responseMsgCtx = opClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        return responseMsgCtx.getEnvelope();
    }

    /**
     *
     * @author Stefano
     * @param url
     * @param inEnvelope
     * @param soapAction
     * @param rels2 WSA relatesTo parameters
     * @return
     * @throws Exception
     */
    public static SOAPEnvelope sendReceive(URL url, SOAPEnvelope inEnvelope, String soapAction, String messageId, String replyTo) throws Exception{


        ServiceClient client = new ServiceClient();
        Options options = new Options();
         options.setTimeOutInMilliSeconds(60000000);
        options.setTo(new EndpointReference(url.toString()));
        options.setAction(soapAction);
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        if (messageId != null)
        	options.setMessageId(messageId);

        if (replyTo != null)
        	options.setReplyTo(new EndpointReference(replyTo));

        client.setOptions(options);

        MessageContext msgctx = new MessageContext();
        msgctx.setEnvelope(inEnvelope);

        OperationClient opClient = client.createClient(ServiceClient.ANON_OUT_IN_OP);
        opClient.addMessageContext(msgctx);
        opClient.execute(true);
        MessageContext responseMsgCtx = opClient.getMessageContext(WSDLConstants.MESSAGE_LABEL_IN_VALUE);
        return responseMsgCtx.getEnvelope();
    }



    
    /**
     * 
     * @author 
     * @param sendTo
     * @param proxyServer
     * @param proxyPort
     * @param message
     * @param soapAction
     * @return
     * @throws Exception
     * @deprecated Replaced by {@link #exchange(URL, String, String, SOAPEnvelope, String, RelatesTo[])}
     */
    public static SOAPMessage exchange(URL sendTo, String proxyServer, String proxyPort, SOAPMessage message, String soapAction) throws Exception {
        System.setProperty("http.proxyHost", proxyServer);
        System.setProperty("http.proxyPort", proxyPort);
        return exchange(sendTo, message, soapAction);
        
    }
    
    public static SOAPEnvelope exchange(URL sendTo, String proxyServer, String proxyPort, SOAPEnvelope envelope, String soapAction, RelatesTo[] rels2) throws Exception {
        System.setProperty("http.proxyHost", proxyServer);
        System.setProperty("http.proxyPort", proxyPort);
        return sendReceive(sendTo, envelope, soapAction, rels2);
        
    }
    
    /**
     * 
     * @author Stefano
     * @param url
     * @param message
     * @param soapAction
     * @param SSLCertificateLocation
     * @return
     * @throws Exception
     * @deprecated Replaced by {@link #exchange(URL, SOAPEnvelope, String, String, RelatesTo[])}
     */
    public static SOAPMessage secureExchange(URL url, SOAPMessage message, String soapAction, String SSLCertificateLocation) throws Exception {
        // specify the location of where to find key material for the default TrustManager
        System.setProperty("javax.net.ssl.trustStore",SSLCertificateLocation);
        
        // use Sun's reference implementation of a URL handler for the "https" URL protocol type.
        System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
        
        // dynamically register sun's ssl provider
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        
        // note that the url is using https protocol and not http
        return exchange(url, message, soapAction);
    }
    
    public static SOAPEnvelope secureExchange(URL url, SOAPEnvelope envelope, String soapAction, String SSLCertificateLocation, RelatesTo[] rels2) throws Exception {
        // specify the location of where to find key material for the default TrustManager
        System.setProperty("javax.net.ssl.trustStore",SSLCertificateLocation);
        // use Sun's reference implementation of a URL handler for the "https" URL protocol type.
        System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
        // dynamically register sun's ssl provider
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        
        // note that the url is using https protocol and not http
        return sendReceive(url, envelope, soapAction, rels2);
    }

    public static Element secureExchange(URL url, Element documentElement, Element[] soapHeaders, String soapAction, String messageID, String SSLCertificateLocation) throws Exception {
         // specify the location of where to find key material for the default TrustManager
        System.setProperty("javax.net.ssl.trustStore",SSLCertificateLocation);

        // use Sun's reference implementation of a URL handler for the "https" URL protocol type.
        System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");

        // dynamically register sun's ssl provider
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

        return AxisSOAPClient.sendReceive(url,documentElement,soapHeaders,soapAction,messageID,null);
    }

    private static void addHeader(Element header, ServiceClient client) throws Exception {
        OMElement omHeader;
        InputStream inStream;
        SOAPHeaderBlock block;
        OMNamespace omNs,nodeNamespace;

        SOAP11Factory factory = (SOAP11Factory) OMAbstractFactory.getSOAP11Factory();

        inStream= XmlTools.getNodeAsInputStream(header);
        omHeader=(OMElement)XMLUtils.toOM(inStream);


        //OMNamespace omNs = factory.createOMNamespace("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd","wsse");

        nodeNamespace=omHeader.getNamespace();

        block=factory.createSOAPHeaderBlock(omHeader.getLocalName(),
                      factory.createOMNamespace(nodeNamespace.getNamespaceURI(),nodeNamespace.getPrefix()));

        //adding all namespaces
        Iterator namespaces;

        namespaces=omHeader.getAllDeclaredNamespaces();
        while(namespaces.hasNext())
        {
            omNs=(OMNamespace) namespaces.next();
            if(omNs.getPrefix().equals(nodeNamespace.getPrefix())==false)
            {
                block.setNamespace(factory.createOMNamespace(omNs.getNamespaceURI(),omNs.getPrefix()));
            }
        }

        Iterator attributes;
        OMAttribute omAttr,newAttr;
        attributes=omHeader.getAllAttributes();

        while(attributes.hasNext())
        {
            omAttr=(OMAttribute) attributes.next();
            newAttr=factory.createOMAttribute(omAttr.getLocalName(), omAttr.getNamespace(), omAttr.getAttributeType());

            block.addAttribute(newAttr);
        }

        LinkedList children;
        
        children=XmlTools.getChildren(header);
        for(int i=0;i<children.size();i++)
            block.addChild(XMLUtils.toOM((Element)children.get(i)));

        client.addHeader(block);
    }
    
    
}
