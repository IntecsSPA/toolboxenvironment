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
 *  File Name:         $RCSfile: Util.java,v $
 *  TOOLBOX Version:   $Name: HEAD $
 *  File Revision:     $Revision: 1.1.1.1 $
 *  Revision Date:     $Date: 2006/06/13 15:02:26 $
 *
 */
package it.intecs.pisa.toolbox.util;

import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import java.io.*;
import java.util.*;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.impl.llom.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.w3c.dom.*;

import it.intecs.pisa.util.*;
import org.apache.axiom.om.OMText;

public class Util {

    public static final int MILLISECONDS = 1000;
    public static final int SECONDS = 60;
    public static final int MINUTES = 60;
    public static final int HOURS = 24;
    public static final int DAYS = 7;
    private static final String SPACE_CODE = "%20";
    public static final String SOAP_ENVELOPE = "Envelope";
    public static final String SOAP_HEADER = "Header";
    public static final String SOAP_BODY = "Body";
    public static final String SOAP_NS_URI = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final String XMLNS = "xmlns";
    private static final String SOAP_ENV = "soap-env";
    private static final String SOAP_FAULT = "Fault";
    private static final String FAULT_CODE = "faultcode";
    private static final String FAULT_STRING = "faultstring";
    private static final String FAULT_ACTOR = "faultactor";
    private static final String FAULT_DETAIL = "detail";
    private static final String TOOLBOX = "TOOLBOX";
    public static final String WSA_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    public static final String SCHEMA_INSTANCE_URI = "www.w3.org/2001/XMLSchema-instance";
    public static final String SCHEMA_URI = "www.w3.org/2001/XMLSchema";
    public static final String XSD = "xsd";
    public static final String XSI = "xsi";
    public static final String MUST_UNDERSTAND = "mustUnderstand";
    public static final String ZERO = "0";
    public static final String TYPE = "type";
    public static final String STRING = "string";

    public static final String WSA_ACTION = "Action";
    public static final String WSA_TO = "To";
    public static final String WSA_MESSAGE_ID = "MessageID";
    public static final String WSA_RELATES_TO = "RelatesTo";
    public static final String WSA = "wsa";


    public static long getMilliseconds(String time) {
        int lastPos = time.length() - 1;
        long t = Long.parseLong(time.substring(0, lastPos));
        char c = time.charAt(lastPos);
        switch (c) {
            case 's':
                return t * MILLISECONDS;
            case 'm':
                return t * SECONDS * MILLISECONDS;
            case 'h':
                return t * MINUTES * SECONDS * MILLISECONDS;
            case 'd':
                return t * HOURS * MINUTES * SECONDS * MILLISECONDS;
            case 'w':
                return t * DAYS * HOURS * MINUTES * SECONDS * MILLISECONDS;
            default:
                throw new IllegalArgumentException("Unknown marker: " + c);
        }
    }

    public static String getDateTime() {
        GregorianCalendar gC = new GregorianCalendar();
        int day = gC.get(Calendar.DAY_OF_MONTH);
        int month = gC.get(Calendar.MONTH) + 1;
        int year = gC.get(Calendar.YEAR);
        int hour = gC.get(Calendar.HOUR_OF_DAY);
        int minute = gC.get(Calendar.MINUTE);
        int second = gC.get(Calendar.SECOND);
        StringBuffer date = new StringBuffer().append(addZero(day)).append('-').append(addZero(month)).append('-').append(year).append(' ').append(addZero(hour)).append(':').append(addZero(minute)).append(':').append(addZero(second));
        return date.toString();
    }

    private static String addZero(int num) {
        String result = String.valueOf(num);
        if (num < 10) {
            result = "0" + result;
        }
        return result;
    }

    public static String spaceEncode(String s) {
        if (s.indexOf(' ') < 0) {
            return s;
        }
        StringBuffer buffer = new StringBuffer();
        char c;
        for (int index = 0; index < s.length(); index++) {
            if ((c = s.charAt(index)) != ' ') {
                buffer.append(c);
            } else {
                buffer.append(SPACE_CODE);
            }
        }
        return buffer.toString();
    }

    public static String getURI(String path) {
        return spaceEncode(path.replace(File.separatorChar, '/'));
    }

    public static InputStream addHeaderAndFooter(final String header, final InputStream body, final String footer) throws Exception {
        return new SequenceInputStream(new Enumeration() {

            private InputStream[] streams = {new ByteArrayInputStream(header.getBytes()), body, new ByteArrayInputStream(footer.getBytes())};
            private int index;

            public boolean hasMoreElements() {
                return index < streams.length;
            }

            public Object nextElement() {
                return streams[index++];
            }
        });
    }

    public static Document addSOAPElements(Document payload) {
        Document soapDocument = new DOMUtil(true).newDocument();
        Element envelope = (Element) soapDocument.appendChild(soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE));
        envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
        envelope.appendChild(soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER));
        (envelope.appendChild(soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY))).appendChild(soapDocument.importNode(payload.getDocumentElement(), true));
        return soapDocument;
    }

    public static void addSOAPEnvelope(Document document) {
        /*    Element envelope;
        Element payload = (Element) document.replaceChild((envelope = document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE)), document.getDocumentElement());
        envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
        envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER));
        envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY)).appendChild(payload);*/

        Element envelope;
        if (document.getDocumentElement() != null) {
            Element payload = (Element) document.replaceChild((envelope = document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE)), document.getDocumentElement());
            envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
            envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER));
            envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY)).appendChild(payload);
        } else {
            envelope = (Element) document.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE));
            envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
            envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER));
            envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY));
        }
    }

    /***
     *
     * @author
     * @param document
     * @param relatesTo
     * @deprecated Replaced by {@link #getSOAPEnvelope(Document, boolean)}
     */
    public static void addSOAPEnvelope(Document document, String relatesTo) {
        Element envelope;
        Element header;
        Element relatesToElement;
        Element payload = (Element) document.replaceChild((envelope = document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE)), document.getDocumentElement());
        envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
        header = (Element) envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER));
        relatesToElement = (Element) header.appendChild(document.createElementNS(WSA_URI, WSA + ":" + WSA_RELATES_TO));
        relatesToElement.setAttribute(XMLNS + ":" + XSD, SCHEMA_URI);
        relatesToElement.setAttribute(XMLNS + ":" + WSA, WSA_URI);
        relatesToElement.setAttribute(XMLNS + ":" + XSI, SCHEMA_INSTANCE_URI);
        relatesToElement.setAttributeNS(SOAP_NS_URI, SOAP_ENV + ":" + MUST_UNDERSTAND, ZERO);
        relatesToElement.setAttributeNS(SCHEMA_INSTANCE_URI, XSI + ":" + TYPE, XSD + ":" + STRING);
        relatesToElement.appendChild(document.createTextNode(relatesTo));
        envelope.appendChild(document.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY)).appendChild(payload);
    }

    /***
     * Returns an AXIOM SOAPEnvelope which body embeds the given content
     * @author Stefano
     * @param bodyDoc
     * @param soap11 The SOAP version to be used
     * @return
     * @throws Exception
     */
    public static SOAPEnvelope getSOAPEnvelope(Document bodycontent, boolean soap11) throws Exception {

        SOAPFactory soapFactory = null;

        if (soap11) {
            soapFactory = OMAbstractFactory.getSOAP11Factory();
        } else {
            soapFactory = OMAbstractFactory.getSOAP12Factory();
        }

        SOAPEnvelope newEnvelope = soapFactory.getDefaultEnvelope();
/*        OMFactory fact = OMAbstractFactory.getOMFactory();
        OMNamespace namespace = fact.createOMNamespace(SOAP_NS_URI, SOAP_ENV);
        SOAPHeaderBlock newSOAPHeaderBlock = soapFactory.createSOAPHeaderBlock(SOAP_ENV, namespace);

        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);*/
        String body;
        body = DOMUtil.getDocumentAsString(bodycontent);
        OMElement bodyOMElem = AXIOMUtil.stringToOM(body);

        newEnvelope.getBody().addChild(bodyOMElem);

        return newEnvelope;
    }

    /***
     * Returns an AXIOM SOAPEnvelope which body embeds the given content
     * @author Stefano
     * @param bodyDoc
     * @param soap11 The SOAP version to be used
     * @return
     * @throws Exception
     */
    public static SOAPEnvelope getSOAPEnvelope(Document bodycontent, String to, String action, String relatesTo, String WSANamespace, boolean soap11) throws Exception {

        SOAPFactory soapFactory = null;

//          AGGIUNGERE LA PARTE DEL REPLY TO QUA
/*
        <wsa:To>http://192.168.24.62:8080/orabpel/mrbarone/Pisa_Toolbox_WPS_computeRiskIndex_ogc_05_007_wps_v100_ExecuteProcess/1.0/asynProvider/WPS_ComputeRiskIndexServiceRequester</wsa:To>
        <wsa:MessageID>urn:uuid:E2830DC14A8414F5991291309816700</wsa:MessageID>
        <wsa:Action>ExecuteProcess_computeRiskIndex_ASYNC_statusUpdate</wsa:Action>
        <wsa:RelatesTo>bpel://localhost/mrbarone/Pisa_Toolbox_WPS_computeRiskIndex_ogc_05_007_wps_v100_ExecuteProcess~1.0/3290005-BpInv1-BpSeq1.3-4</wsa:RelatesTo>
         */
        if (soap11) {
            soapFactory = OMAbstractFactory.getSOAP11Factory();
        } else {
            soapFactory = OMAbstractFactory.getSOAP12Factory();
        }

        //creating the envelope
        SOAPEnvelope newEnvelope = soapFactory.getDefaultEnvelope();

        //adding <wsa:To>
        OMFactory fact = OMAbstractFactory.getOMFactory();
        OMNamespace namespace = fact.createOMNamespace(WSANamespace, WSA);
        SOAPHeaderBlock newSOAPHeaderBlock = soapFactory.createSOAPHeaderBlock(WSA_TO, namespace);
        OMText content = soapFactory.createOMText(to);
        newSOAPHeaderBlock.addChild(content);
        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);

        //adding <wsa:MessageID>
        String messageID=java.util.UUID.randomUUID().toString();
        newSOAPHeaderBlock = soapFactory.createSOAPHeaderBlock(WSA_MESSAGE_ID, namespace);
        content = soapFactory.createOMText(messageID);
        newSOAPHeaderBlock.addChild(content);
        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);

        //adding <wsa:Action>
        newSOAPHeaderBlock = soapFactory.createSOAPHeaderBlock(WSA_ACTION, namespace);
        content = soapFactory.createOMText(action);
        newSOAPHeaderBlock.addChild(content);
        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);

        //adding <wsa:RelatesTo>
        newSOAPHeaderBlock = soapFactory.createSOAPHeaderBlock(WSA_RELATES_TO, namespace);
        content = soapFactory.createOMText(relatesTo);
        newSOAPHeaderBlock.addChild(content);
        newEnvelope.getHeader().addChild(newSOAPHeaderBlock);

        String body;
        body = DOMUtil.getDocumentAsString(bodycontent);
        OMElement bodyOMElem = AXIOMUtil.stringToOM(body);

        newEnvelope.getBody().addChild(bodyOMElem);

        return newEnvelope;
    }

    public static Document buildSOAPResponse(Document soapDocument, Document document) {
        DOMUtil domUtil = new DOMUtil(true);
        Element soapDocumentRoot;
        Element header;
        if (domUtil.hasElementNS(soapDocumentRoot = soapDocument.getDocumentElement(), Util.SOAP_HEADER, Util.SOAP_NS_URI)) {
            while ((header = domUtil.getFirstChild(soapDocumentRoot)).hasChildNodes()) {
                header.removeChild(header.getFirstChild());
            }
        }
        soapDocument.replaceChild(soapDocument.importNode(document.getDocumentElement(), true), (Element) domUtil.getChildren((Element) soapDocumentRoot.getElementsByTagNameNS(Util.SOAP_NS_URI, SOAP_BODY).item(0)).getFirst());
        return document = soapDocument;
    }

    public static Document addSOAPElements(Document payload, Element relatesTo) {
        Document soapDocument = new DOMUtil(true).newDocument();
        Element envelope = (Element) soapDocument.appendChild(soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE));
        envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
        Element header = (Element) envelope.appendChild(soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER));
        header.appendChild(soapDocument.importNode(relatesTo, true));
        //header.appendChild(relatesTo);
        (envelope.appendChild(soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY))).appendChild(soapDocument.importNode(payload.getDocumentElement(), true));
        return soapDocument;
    }

    public static Document removeSOAPElements(Document soapDocument) throws Exception {
        DOMUtil domUtil = new DOMUtil(true);
        Document requestContent = domUtil.newDocument();
        Element body = (Element) soapDocument.getElementsByTagNameNS(SOAP_NS_URI, SOAP_BODY).item(0);
        Element bodyChild = domUtil.getFirstChild(body);
        requestContent.appendChild(requestContent.importNode(bodyChild, true));
        Element requestRoot = requestContent.getDocumentElement();
        Element envelope = soapDocument.getDocumentElement();
        Element header = (Element) soapDocument.getElementsByTagNameNS(SOAP_NS_URI, SOAP_HEADER).item(0);
        addNSDeclarations(envelope, requestRoot);
        NodeList nodeList;
        addNSDeclarations(body, requestRoot);
        return requestContent;
    }

    private static void addNSDeclarations(Element source, Element target) throws Exception {
        NamedNodeMap sourceAttributes = source.getAttributes();
        Attr attribute;
        String attributeName;
        String attributeValue;
        for (int i = 0; i <= sourceAttributes.getLength() - 1; i++) {
            attribute = (Attr) sourceAttributes.item(i);
            attributeName = attribute.getName();
            attributeValue = attribute.getValue();
            if (attributeName.startsWith(XMLNS) && !attributeValue.equals(SOAP_NS_URI)) {
                target.setAttribute(attributeName, attributeValue);
            }
        }
    }

    public static Document getSOAPFault(String errorMessage) {
        DOMUtil domUtil = new DOMUtil(true);
        Document soapFault = domUtil.newDocument();
        Element soapFaultElement = soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + SOAP_FAULT);
        soapFault.appendChild(soapFaultElement);
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, FAULT_CODE)).appendChild(soapFault.createTextNode("code"));
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, FAULT_STRING)).appendChild(soapFault.createTextNode(errorMessage));
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, FAULT_ACTOR)).appendChild(soapFault.createTextNode(TOOLBOX));
        return addSOAPElements(soapFault);
    }

    public static Document getSOAPFault(String errorMessage, String serviceName) {
        DOMUtil domUtil = new DOMUtil(true);
        Document soapFault = domUtil.newDocument();
        Element soapFaultElement = soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + SOAP_FAULT);
        soapFault.appendChild(soapFaultElement);
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, FAULT_CODE)).appendChild(soapFault.createTextNode("code"));
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, FAULT_STRING)).appendChild(soapFault.createTextNode(errorMessage));
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, FAULT_ACTOR)).appendChild(soapFault.createTextNode(serviceName));
        return soapFault;
    }

    public static Document getSOAPFault(ToolboxException e) {
        DOMUtil domUtil = new DOMUtil(true);
        String errorMessage = e.getMessage();
        Document soapFault = domUtil.newDocument();
        Element detailNode, excDetailsNode;

        Element soapFaultElement = soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + SOAP_FAULT);
        soapFault.appendChild(soapFaultElement);
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + FAULT_CODE)).appendChild(soapFault.createTextNode("code"));
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + FAULT_STRING)).appendChild(soapFault.createTextNode(errorMessage));
        soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + FAULT_ACTOR)).appendChild(soapFault.createTextNode(TOOLBOX));

        excDetailsNode = e.getDetailsXML();
        if (excDetailsNode != null) {
            detailNode = (Element) soapFaultElement.appendChild(soapFault.createElementNS(SOAP_NS_URI, SOAP_ENV + ':' + FAULT_DETAIL));

            NodeList childNodes = excDetailsNode.getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                detailNode.appendChild(soapFault.importNode((Node) childNodes.item(i), true));
            }
            //.appendChild(soapFault.importNode(,true));
        }

        /*    try
        {
        domUtil.dumpXML(soapFault, new File("c:\\soapFault.xml"));
        }
        catch(Exception ecc)
        {

        }*/
        return soapFault;
    }
}
