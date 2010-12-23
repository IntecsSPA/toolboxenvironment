
package it.intecs.pisa.gisclient.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.security.Security;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.axis.Message;
import org.apache.axis.client.Call;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;


/**
 *
 * @author Andrea Marongiu
 */
public class SoapTools {
    
     private Transformer copier;
    //private DOMUtil domUtil = new DOMUtil();
     
    /** Creates a new instance of SoapTools */
    public SoapTools() {
        try {
            
            
            copier = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerFactoryConfigurationError ex) {
            ex.printStackTrace();
        }
        
        
    }

 public Document getSoapCall(String UrlService, String operation, Document messageBody ) throws Exception 
    {
   
    //    LinkedList soapParams = DOMUtil.getChildren(soapCall);
      URL url = new URL(UrlService);
     
      org.apache.axis.Message payload = null;
      //System.out.println("Document da trasformare in Message: ");
      //DomUtil.writeXml(messageBody, System.out);
      payload = getMessageFromBody(messageBody);
     
     // String res=SoapTools.exchange(url, payload,operation).getSOAPBody().toString();
     // System.out.println("RISPOSTA: " +res);
      return this.getContent(SoapTools.exchange(url, payload,operation));
     }

  public Document getSoapCall(String UrlService, String operation, Document messageBody, String user, String password ) throws Exception
    {
      //LinkedList soapParams = DOMUtil.getChildren(soapCall);
      URL url = new URL(UrlService);
      System.out.println("URL: " + UrlService);
      org.apache.axis.Message payload = null;
      //System.out.println("Document da trasformare in Message: ");
      //DomUtil.writeXml(messageBody, System.out);
      payload = getMessageFromBody(messageBody);
      System.out.println("SOAP: " + payload.getSOAPBody().toString());
     // String res=SoapTools.exchange(url, payload,operation).getSOAPBody().toString();
     // System.out.println("RISPOSTA: " +res);
      return this.getContent(SoapTools.exchange(url, payload,operation,user,password));
   }
     
 
     public static Message exchange(URL url, Message message, String soapAction) throws Exception 
     {       
        Call call = new Call(url);
        call.setUseSOAPAction(true);
        call.setSOAPActionURI(soapAction);        
        call.setRequestMessage(message);        
        call.setTimeout(new Integer(600000));
        call.invoke();         
        return call.getResponseMessage();
    }

    public static Message exchange(URL url, Message message, String soapAction, String soapUser, String soapPassword) throws Exception
     {
        Call call = new Call(url);
        call.setUseSOAPAction(true);
        call.setSOAPActionURI(soapAction);
        call.setProperty(org.apache.axis.client.Call.USERNAME_PROPERTY, soapUser);
        call.setProperty(org.apache.axis.client.Call.PASSWORD_PROPERTY, soapPassword);
        call.setRequestMessage(message);
        call.setTimeout(new Integer(600000));
        call.invoke();
        return call.getResponseMessage();
    }


    public static Message secureExchange(URL url, Message message, String soapAction, String SSLCertificateLocation) throws Exception {
        // specify the location of where to find key material for the default TrustManager
        System.setProperty("javax.net.ssl.trustStore",SSLCertificateLocation);
        
        // use Sun's reference implementation of a URL handler for the "https" URL protocol type.
        System.setProperty("java.protocol.handler.pkgs","com.sun.net.ssl.internal.www.protocol");
        
        // dynamically register sun's ssl provider
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        
        // note that the url is using https protocol and not http
        return exchange(url, message, soapAction);
    }
    
    public Message getMessage(Document document) throws Exception 
    {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    copier.transform(new DOMSource(document), new StreamResult(out));
    return new Message(new ByteArrayInputStream(out.toByteArray()));
  } 
    
    public Message getMessageFromBody(Document document) throws Exception {
      org.apache.axis.message.SOAPEnvelope envelope = new org.apache.axis.message.SOAPEnvelope();
      envelope.addBodyElement(new org.apache.axis.message.SOAPBodyElement(document.getDocumentElement()));
      return new Message(envelope);
  }


 /*  public Message getMessageFromBodyWithHeader(Document document, Element elementHeader) throws Exception {
      org.apache.axis.message.SOAPEnvelope envelope = new org.apache.axis.message.SOAPEnvelope();
      SOAPHeader header = envelope.addHeader();
      SOAPElement el = header.addHeaderElement(envelope.createName("TicketHeader",
									"", "http://ws.service.com/"));
				el = el.addChildElement(envelope.createName("Ticket", "", "http://ws.service.com/"));
				el.setValue(token);
				msg.saveChanges();
      //SOAPHeaderElement sp=header.addHeaderElement(envelope.createName("test"));
                                SOAPElement el; 
      NodeList nd=elementHeader.getChildNodes();
      for(int i=0; i< nd.getLength(); i++){
        el = header.addHeaderElement(envelope.createName(nd.item(i).getNodeName(),
									"", nd.item(i).getNodeName()));
      }
      envelope.addBodyElement(new org.apache.axis.message.SOAPBodyElement(document.getDocumentElement()));
      return new Message(envelope);
  }*/
   
 /* public SOAPElement parseHeaderElement (Element e, org.apache.axis.message.SOAPEnvelope envelope){
    SOAPHeader header = envelope.addHeader();  
    SOAPElement el = header.addHeaderElement(envelope.createName(e.getNodeName(),"", e.getNamespaceURI()));  
    if(e.hasChildNodes()){
        el.addChildElement(envelope.createName(e.getNodeName(), "", e.getNamespaceURI()));
    
    }else{
      el.setValue(e.getNodeValue());   
      return(el);
    }
     
 }  */
 
 public Document getDocument(SOAPMessage message) throws Exception {
    Document document = XmlTools.newDocument();
   
    copier.transform(message.getSOAPPart().getContent(), new DOMResult(document));
    return document;
  }
 
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
}


