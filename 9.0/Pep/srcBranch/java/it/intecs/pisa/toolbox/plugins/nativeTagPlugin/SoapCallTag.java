package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.net.URL;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import it.intecs.pisa.soap.toolbox.AxisSOAPClient;

public class SoapCallTag extends NativeTagExecutor {
    protected String tagName="soapCall";
    
    @Override
    public Object executeTag(org.w3c.dom.Element soapCall) throws Exception {
        String messageID=null;
        String relateTo=null;
        Document request;
        Document headerDoc;
        Element el;
        URL url;
        LinkedList soapParams;
        String soapAction;
        Element soapResponse;
        Element[] headers=null;
        int headersCount=0;
        DOMUtil util;

        util=new DOMUtil();

        try
        {
        soapParams= DOMUtil.getChildren(soapCall);
        url = new URL((String) this.executeChildTag((Element) soapParams.get(0)));
        messageID=evaluateAttribute(soapCall, "messageId");
        relateTo=evaluateAttribute(soapCall, "relatesTo");

        soapAction = (soapCall.hasAttribute(OPERATION) ? evaluateAttribute(soapCall,OPERATION) : "");
        
        el=(Element) soapParams.get(1);
        if(el.getLocalName().contains("soapHeaders"))
        {
            LinkedList list = DOMUtil.getChildren(el);
            headersCount=list.size();

            headers=new Element[headersCount];
            for(int i=0;i<headersCount;i++)
            {
                el=(Element) list.get(i);
                Object result= executeChildTag(el);
                if(result != null){
                   headerDoc=(Document) executeChildTag(el);
                   headers[i]= (Element) headerDoc.getDocumentElement();
                }else
                  headers[i]=null;  
            }

            el=(Element) soapParams.get(2);
        }
            
        request = (Document) executeChildTag(el);
        }
        catch(Exception e)
        {
            throw new Exception("Invalid input");
        }


        try
        {
        if (soapCall.hasAttribute(SSL_CERTIFICATE_LOCATION)) {
            if(relateTo== null)
                soapResponse=AxisSOAPClient.secureExchange(url,request.getDocumentElement(), headers, soapAction,messageID,evaluateAttribute(soapCall,SSL_CERTIFICATE_LOCATION));
            else
                soapResponse=AxisSOAPClient.secureExchange(url,request.getDocumentElement(), headers, soapAction,messageID,evaluateAttribute(soapCall,SSL_CERTIFICATE_LOCATION));
        } else {
            if(relateTo== null)
              soapResponse=AxisSOAPClient.sendReceive(url, request.getDocumentElement() , headers,soapAction,messageID);
            else{
              soapResponse=AxisSOAPClient.sendReceive(url, request.getDocumentElement() , headers,soapAction,messageID,relateTo);
            }
        }
        }
        catch(Exception e)
        {
            throw new Exception("An error occurred while performing the SOAP exchange");
        }

       
        dumpResourceAndAddToDebugTree(soapResponse.getOwnerDocument());

        return soapResponse.getOwnerDocument();
    }

   
}
