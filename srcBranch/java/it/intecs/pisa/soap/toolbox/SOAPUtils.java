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
 *  File Name:         $RCSfile: SOAPUtils.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.soap.toolbox;

import java.io.*;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11Factory;
import org.apache.axis2.saaj.SOAPMessageImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;



import it.intecs.pisa.util.*;

public class SOAPUtils {

    private MessageFactory messageFactory;
    private DOMUtil domUtil = new DOMUtil();
    private Transformer copier;

    public SOAPUtils() throws SOAPException, TransformerConfigurationException {
        messageFactory = MessageFactory.newInstance();
        copier = TransformerFactory.newInstance().newTransformer();
    }

    public SOAPMessage getSOAPMessage(InputStream in) throws Exception {
        MimeHeaders mimeHeaders = new MimeHeaders();
        mimeHeaders.addHeader("Content-Type", "text/xml");
        return messageFactory.createMessage(mimeHeaders, in);
    }

    public SOAPMessage getSOAPFault(Throwable t) {
        SOAPMessage message = null;
        try {
            message = messageFactory.createMessage();
            SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
            SOAPFault fault = envelope.getBody().addFault();
            fault.setFaultActor("TOOLBOX");
            fault.setFaultCode("CODE");
            fault.setFaultString(t.getMessage());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            t.printStackTrace(new PrintStream(out, true));
            fault.addDetail().addDetailEntry(envelope.createName("stackTrace")).addTextNode(new String(out.toByteArray()));
        } catch (Exception e) {
        }
        return message;
    }

    public Document getDocument(SOAPMessage message) throws Exception {
        Document document = domUtil.newDocument();
        copier.transform(message.getSOAPPart().getContent(), new DOMResult(document));
        return document;
    }

    //public Document getContent(SOAPMessage message) throws Exception {
    // Document document = getDocument(message);
    // Element envelope = (Element) document.removeChild(document.getDocumentElement());
    // document.appendChild(DOMUtil.getFirstChild(DOMUtil.getChildByTagName(envelope, envelope.getPrefix() + ":Body")));
    /* TODO Add in the new root every namespace declaration appearing in original root (SOAP-ENV:envelope) and maybe also SOAP-ENV:body*/
    /*
    get all attributes from envelope
    for each attribute
    if attribute name starts with "xmlns"
    document.getDocumentElement().setAttribute(attributeName, attribute value)
    do the same with body: DOMUtil.getChildByTagName(envelope, envelope.getPrefix() + ":Body")
     */
    //return document;
    //}
    /** Returns the document containing the payload of the given SOAP message. Its root element
     * contains the namespace declarations possibly present in
     * <CODE>&lt;soap-env:Env&gt;</CODE> and in <CODE>&lt;soap-env:Body&gt;</CODE>.
     * @param message The SOAP message
     *
     * @return The DOM document representing the XML payload of the given SOAP message
     */
    public Document getContent(SOAPMessage message) throws Exception {
        Document document = getDocument(message);
        Element envelope = (Element) document.removeChild(document.getDocumentElement());
        Element body = DOMUtil.getChildByTagName(envelope, envelope.getPrefix() + ":Body");
        Element documentRoot = (Element) document.appendChild(DOMUtil.getFirstChild(body));
        addNSdeclarations(envelope, documentRoot);
        addNSdeclarations(body, documentRoot);
        return document;
    }

    private static void addNSdeclarations(Element source, Element target) throws Exception {
        NamedNodeMap sourceAttributes = source.getAttributes();
        Attr attribute;
        String attributeName;
        for (int i = 0; i <= sourceAttributes.getLength() - 1; i++) {
            attribute = (Attr) sourceAttributes.item(i);
            attributeName = attribute.getName();
            if (attributeName.startsWith("xmlns") && !attributeName.startsWith("xmlns:soap-env")) {
                target.setAttributeNode((Attr) attribute.cloneNode(false));
            }
        }
    }

    public SOAPMessage getSOAPMessage(Document document) throws Exception {
       SOAPMessage message = messageFactory.createMessage();
        message.getSOAPPart().setContent(new DOMSource(document));
        return message;
    }

    public SOAPMessageImpl getMessage(Document document) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copier.transform(new DOMSource(document), new StreamResult(out));
        return new SOAPMessageImpl(new ByteArrayInputStream(out.toByteArray()), null);
    }

    public SOAPMessage getMessage(Document document, boolean isBody) throws Exception {
        /*
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copier.transform(new DOMSource(document), new StreamResult(out));
        return new Message(new ByteArrayInputStream(out.toByteArray()), isBody);*/
        if (isBody) {
            return getMessageFromBody(document);
        } else {
            return getMessage(document);
        }


    }

    public SOAPMessage getMessageFromBody(Document document) throws Exception {

        MessageFactory msgFactory = MessageFactory.newInstance();
        SOAPMessage msg = msgFactory.createMessage();
        msg.getSOAPBody().addDocument(document);

        return msg;
    }

    public SOAPMessage getSOAPMessageContaining(Document content) throws Exception {
        if (content == null) {
            return messageFactory.createMessage();
        }
        DocumentFragment documentFragment = content.createDocumentFragment();
        while (content.hasChildNodes()) {
            documentFragment.appendChild(content.getFirstChild());
        }
        copier.transform(messageFactory.createMessage().getSOAPPart().getContent(), new DOMResult(content));
        Element soapEnvelope = content.getDocumentElement();
        DOMUtil.getChildByTagName(soapEnvelope, soapEnvelope.getPrefix() + ':' + "Body").appendChild(documentFragment);
        return getSOAPMessage(content);
    }

    /*
    public SOAPMessage getSOAPMessage(DOMSource source) throws Exception {
    SOAPMessage message = messageFactory.createMessage();
    message.getSOAPPart().setContent(source);
    return message;
    }*/
    /**
     * Returns the AXIOM SOAPEnvelope representing the given Document SOAPEnvelope
     */
    public static org.apache.axiom.soap.SOAPEnvelope getSOAPEnvelope(Document envelopeDocument) throws Exception {

        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        //copier.transform(new DOMSource(document), new StreamResult(out));

        /*SOAPFactory soap11Factory = OMAbstractFactory.getSOAP11Factory();
        org.apache.axiom.soap.SOAPEnvelope newEnvelope = soap11Factory.getDefaultEnvelope();
        SOAPHeaderBlock newSOAPHeaderBlock = soap11Factory.createSOAPHeaderBlock( localName, namespace);

        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);
        newEnvelope.getBody().addChild(omNode);

        org.apache.axis2.saaj.SOAPMessage soapMessage = messageFactory.createMessage (mimeHeaders, inputStream); */

        String resp = DOMUtil.getDocumentAsString(envelopeDocument);
        OMElement documentElement = AXIOMUtil.stringToOM(resp);

        //Create an XMLStreamReader to get the StAX events

        //TODO retrieve SOAP version to be used
        SOAP11Factory f = new SOAP11Factory();
        StAXSOAPModelBuilder builder = OMXMLBuilderFactory.createStAXSOAPModelBuilder(f, documentElement.getXMLStreamReader());
        org.apache.axiom.soap.SOAPEnvelope returnEnv = builder.getSOAPEnvelope();
        return returnEnv;


    }

    public static SOAPMessage newSOAPMEssage() {
        SOAP11Factory f = new SOAP11Factory();
        StAXSOAPModelBuilder builder;

        builder=new StAXSOAPModelBuilder(null,"1.1");
        return (SOAPMessage) builder.getSoapMessage();
    }
}
