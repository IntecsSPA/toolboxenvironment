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
package it.intecs.pisa.gisclient.util;

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
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.factory.OMXMLBuilderFactory;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.impl.builder.StAXSOAPModelBuilder;
import org.apache.axiom.soap.impl.dom.soap11.SOAP11Factory;
import org.apache.axis2.saaj.SOAPMessageImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;




public class SOAPUtils {
  public static final String SOAP_ENVELOPE = "Envelope";
  public static final String SOAP_HEADER = "Header";
  public static final String SOAP_BODY = "Body";
  public static final String SOAP_NS_URI = "http://schemas.xmlsoap.org/soap/envelope/";
  public static final String WS_ADDRESSING = "wsa";
  public static final String WS_ADDRESSING_NS_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
  public static final String WS_ADDRESSING_MESSAGE_ID = "MessageID";
  public static final String WS_ADDRESSING_REPLY_TO = "ReplyTo";
  public static final String WS_ADDRESSING_ADDRESS = "Address";
  public static final String WS_ADDRESSING_SERVICE_NAME = "ServiceName";
  public static final String WS_ADDRESSING_PORT_TYPE = "PortType";

  private static final String XMLNS = "xmlns";
  private static final String SOAP_ENV = "soap-env";
  private static final String SOAP_FAULT = "Fault";
  private static final String FAULT_CODE = "faultcode";
  private static final String FAULT_STRING = "faultstring";
  private static final String FAULT_ACTOR = "faultactor";
  private static final String FAULT_DETAIL = "detail";

    private MessageFactory messageFactory;
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
        Document document = XmlTools.newDocument();
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
        Element body = XmlTools.getChildByTagName(envelope, envelope.getPrefix() + ":Body");
        Element documentRoot = (Element) document.appendChild(XmlTools.getFirstChild(body));
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
        XmlTools.getChildByTagName(soapEnvelope, soapEnvelope.getPrefix() + ':' + "Body").appendChild(documentFragment);
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
        //copier.transform(new DOMSource(document), new StreamResult(out));u

        /*SOAPFactory soap11Factory = OMAbstractFactory.getSOAP11Factory();
        org.apache.axiom.soap.SOAPEnvelope newEnvelope = soap11Factory.getDefaultEnvelope();
        SOAPHeaderBlock newSOAPHeaderBlock = soap11Factory.createSOAPHeaderBlock( localName, namespace);

        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);
        newEnvelope.getBody().addChild(omNode);

        org.apache.axis2.saaj.SOAPMessage soapMessage = messageFactory.createMessage (mimeHeaders, inputStream); */

        String resp = XmlTools.getDocumentAsString(envelopeDocument);
        OMElement documentElement = AXIOMUtil.stringToOM(resp);

        //Create an XMLStreamReader to get the StAX events

        //TODO retrieve SOAP version to be used
        SOAP11Factory f = new SOAP11Factory();
        StAXSOAPModelBuilder builder = OMXMLBuilderFactory.createStAXSOAPModelBuilder(f, documentElement.getXMLStreamReader());
        org.apache.axiom.soap.SOAPEnvelope returnEnv = builder.getSOAPEnvelope();
        return returnEnv;
    }

  public static org.apache.axiom.soap.SOAPEnvelope getSOAPEnvelope(Document bodycontent, boolean soap11) throws Exception{
	  SOAPFactory soapFactory = null;
	  if (soap11)
		  soapFactory= OMAbstractFactory.getSOAP11Factory();
	  else
		  soapFactory= OMAbstractFactory.getSOAP12Factory();

	  org.apache.axiom.soap.SOAPEnvelope newEnvelope = soapFactory.getDefaultEnvelope();
	  OMFactory fact = OMAbstractFactory.getOMFactory();

      OMNamespace namespace = fact.createOMNamespace(SOAP_NS_URI, SOAP_ENV);
	  SOAPHeaderBlock newSOAPHeaderBlock = soapFactory.createSOAPHeaderBlock( SOAP_ENV, namespace);

	  newEnvelope.getHeader().addChild(newSOAPHeaderBlock);
	  String body;
	  body = XmlTools.getDocumentAsString(bodycontent);
	  OMElement bodyOMElem = AXIOMUtil.stringToOM(body);

	  newEnvelope.getBody().addChild(bodyOMElem);

	  return newEnvelope;
  }

  /*
   * (001) <S:Envelope xmlns:S="http://www.w3.org/2003/05/soap-envelope"
                xmlns:wsa="http://schemas.xmlsoap.org/ws/2004/08/addressing">
(002)   <S:Header>
(003)    <wsa:MessageID>
(004)      uuid:6B29FC40-CA47-1067-B31D-00DD010662DA
(005)    </wsa:MessageID>
(006)    <wsa:ReplyTo>
(007)      <wsa:Address>http://business456.example/client1</wsa:Address>
(008)    </wsa:ReplyTo>
(009)    <wsa:To>http://fabrikam123.example/Purchasing</wsa:To>
(010)    <wsa:Action>http://fabrikam123.example/SubmitPO</wsa:Action>
(011)   </S:Header>
(012)   <S:Body>

   *
   * */

    public static org.apache.axiom.soap.SOAPEnvelope getSOAPEnvelope(Document bodycontent,String messageID, String replyTo, boolean soap11) throws Exception{

	  SOAPFactory soapFactory = null;
	  if (soap11)
		  soapFactory= OMAbstractFactory.getSOAP11Factory();
	  else
		  soapFactory= OMAbstractFactory.getSOAP12Factory();

	  org.apache.axiom.soap.SOAPEnvelope newEnvelope = soapFactory.getDefaultEnvelope();
	  OMFactory fact = OMAbstractFactory.getOMFactory();

      OMNamespace addessingNamespace = fact.createOMNamespace(WS_ADDRESSING_NS_URI, WS_ADDRESSING);
	  SOAPHeaderBlock messageIdHeaderBlock = soapFactory.createSOAPHeaderBlock( WS_ADDRESSING_MESSAGE_ID, addessingNamespace);
      messageIdHeaderBlock.setText(messageID);
	  newEnvelope.getHeader().addChild(messageIdHeaderBlock);

      //<wsa:ReplyTo>
      SOAPHeaderBlock replyToHeaderBlock = soapFactory.createSOAPHeaderBlock( WS_ADDRESSING_REPLY_TO, addessingNamespace);
      //<wsa:Address>http://business456.example/client1</wsa:Address>

      SOAPHeaderBlock addressBlock = soapFactory.createSOAPHeaderBlock( WS_ADDRESSING_ADDRESS, addessingNamespace);
      replyToHeaderBlock.addChild(addressBlock);
      addressBlock.setText(replyTo);


      SOAPHeaderBlock serviceNameBlock = soapFactory.createSOAPHeaderBlock( WS_ADDRESSING_SERVICE_NAME, addessingNamespace);
      replyToHeaderBlock.addChild(serviceNameBlock);
      serviceNameBlock.setText("serviceName");

       SOAPHeaderBlock portTypeBlock = soapFactory.createSOAPHeaderBlock( WS_ADDRESSING_PORT_TYPE, addessingNamespace);
      replyToHeaderBlock.addChild(portTypeBlock);
      portTypeBlock.setText("portType");
      

      newEnvelope.getHeader().addChild(replyToHeaderBlock);



      String body;
	  body = XmlTools.getDocumentAsString(bodycontent);
	  OMElement bodyOMElem = AXIOMUtil.stringToOM(body);

	  newEnvelope.getBody().addChild(bodyOMElem);

	  return newEnvelope;
  }


    public static SOAPMessage newSOAPMEssage() {
        SOAP11Factory f = new SOAP11Factory();
        StAXSOAPModelBuilder builder;

        builder=new StAXSOAPModelBuilder(null,"1.1");
        return (SOAPMessage) builder.getSoapMessage();
    }
}
