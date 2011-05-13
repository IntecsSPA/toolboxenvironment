/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.soap;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class SimpleSOAPClient {

    private static final String SOAP_SERVICE_NAME = "ServiceName";
    private static final String SOAP_PORT_TYPE = "PortType";
    private static final String SOAP_ADDRESS = "Address";
    private static final String SOAP_REPLY_TO = "ReplyTo";
    private static final String SOAP_MESSAGE_ID = "MessageID";
    public static final String SOAP_ENVELOPE = "Envelope";
    public static final String SOAP_HEADER = "Header";
    public static final String SOAP_BODY = "Body";
    public static final String SOAP_NS_URI = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String SOAP_ADDRESSING_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    private static final String XMLNS = "xmlns";
    private static final String SOAP_ENV = "soap-env";
    public static final String WSA_URI = "http://schemas.xmlsoap.org/ws/2003/03/addressing";
    public static final String WSA = "wsa";

    protected URL to;
    protected String soapAction;
    private String messageId;
    private String replyTo;

    public SimpleSOAPClient() {
    }

    public Document sendReceive(Document payload) throws Exception {
        HttpURLConnection conn;
        Document doc,soapDoc;
        try {
            conn = (HttpURLConnection) to.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("SOAPAction", soapAction);
            conn.addRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            soapDoc=buildSOAPMessage(payload, getMessageId(), getReplyTo());
            OutputStream outStream = conn.getOutputStream();
            IOUtil.copy(DOMUtil.getDocumentAsInputStream(soapDoc), outStream);

            DOMUtil util;
            
            InputStream inStream=null;
            if(conn.getResponseCode() > 400)
                inStream = conn.getErrorStream();
            else
                inStream = conn.getInputStream();
            
            util = new DOMUtil();
            doc = util.inputStreamToDocument(inStream);
            return doc;
        } catch (Exception e) {
            throw new Exception("Cannot complete SOAP exchange. Details: " + e.getLocalizedMessage());
        }
    }

    public static Document buildSOAPMessage(Document document, String messageId, String replyToStr) {
        Document soapDocument;
        DOMUtil util;
        Element envelope;
        Element header;
        Element relatesToElement;
        Element payload;
        Element messageID;
        Element replyTo;
        Element address;
        Element portType;
        Element serviceName;
        Element body;
        Node importedNode;



        util = new DOMUtil();
        soapDocument = util.newDocument();

        //creating envelope node
        envelope = soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_ENVELOPE);
        envelope.setAttribute(XMLNS + ":" + SOAP_ENV, SOAP_NS_URI);
        envelope.setAttribute(XMLNS + ":" + WSA, WSA_URI);
        soapDocument.appendChild(envelope);

        header = soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_HEADER);
        envelope.appendChild(header);

        if(messageId!=null)
        {
            messageID = soapDocument.createElementNS(WSA_URI, WSA + ":" + SOAP_MESSAGE_ID);
            header.appendChild(messageID);
            DOMUtil.setTextToElement(soapDocument, messageID, messageId);
        }

        if(replyToStr!=null)
        {
            replyTo = soapDocument.createElementNS(WSA_URI, WSA + ":" + SOAP_REPLY_TO);
            header.appendChild(replyTo);

            address = soapDocument.createElementNS(WSA_URI, WSA + ":" + SOAP_ADDRESS);
            replyTo.appendChild(address);
            DOMUtil.setTextToElement(soapDocument, address, replyToStr);

            portType = soapDocument.createElementNS(WSA_URI, WSA + ":" + SOAP_PORT_TYPE);
            replyTo.appendChild(portType);
            DOMUtil.setTextToElement(soapDocument, portType, "ServicePortType");

            serviceName = soapDocument.createElementNS(WSA_URI, WSA + ":" + SOAP_SERVICE_NAME);
            replyTo.appendChild(serviceName);
            DOMUtil.setTextToElement(soapDocument, serviceName, SOAP_SERVICE_NAME);
        }

        body = soapDocument.createElementNS(SOAP_NS_URI, SOAP_ENV + ":" + SOAP_BODY);
        envelope.appendChild(body);

        importedNode = soapDocument.importNode(document.getDocumentElement(), true);
        body.appendChild(importedNode);

        return soapDocument;
    }

    /**
     * @return the to
     */
    public URL getTo() {
        return to;
    }

    /**
     * @param to the to to set
     */
    public void setTo(URL to) {
        this.to = to;
    }

    /**
     * @return the soapAction
     */
    public String getSoapAction() {
        return soapAction;
    }

    /**
     * @param soapAction the soapAction to set
     */
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the replyTo
     */
    public String getReplyTo() {
        return replyTo;
    }

    /**
     * @param replyTo the replyTo to set
     */
    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }
}
