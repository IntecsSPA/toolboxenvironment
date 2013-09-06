package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.toolbox.constants.MiscConstants;
import static it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor.FILE_TYPE;
import static it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor.METHOD;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class HttpTag extends NativeTagExecutor {
    // protected String tagName="Http";

     {
         tagName="Http";
     }

    @Override
    public Object executeTag(org.w3c.dom.Element http) throws Exception {
       HttpMethod method;
       int statusCode = 0;
        String urlStr;
        String methodAttribute;

        TransformerFactory tfactory=null;
        Transformer transformer=null;
        Iterator children = DOMUtil.getChildren(http).iterator();
        Element urlTag = DOMUtil.getChildByTagName(http, "url");
        urlStr = (String) this.executeChildTag(DOMUtil.getFirstChild(urlTag));


        urlStr=urlStr.replaceAll("&amp;", "&");
        
        String params[]= urlStr.split("&");
        String paramValue;
        String valueSplit[];
        if(params.length>1)
            for(int i=0; i<params.length; i++){
               valueSplit= params[i].split("=");
               paramValue= valueSplit[valueSplit.length-1];
               urlStr= urlStr.replaceAll(paramValue, URLEncoder.encode(paramValue));
            }
       // Create an instance of HttpClient.
       HttpClient client = new HttpClient();
       HostConfiguration hc=client.getHostConfiguration();
       if(System.getProperty(MiscConstants.PROXY_HOST_KEY)!=null &&
               System.getProperty(MiscConstants.PROXY_PORT_KEY)!= null)
            hc.setProxy(System.getProperty(MiscConstants.PROXY_HOST_KEY), 
               new Integer(System.getProperty(MiscConstants.PROXY_PORT_KEY)));
       client.setHostConfiguration(hc);
       
       methodAttribute=http.getAttribute(METHOD); 
       methodAttribute=this.engine.evaluateString(methodAttribute, IEngine.EngineStringType.ATTRIBUTE);
       
       if (methodAttribute.equals(POST)) {

          method = new PostMethod(urlStr);
          Element bodyTag;
          Element headerTag;

          bodyTag = DOMUtil.getChildByTagName(http, "body");
          headerTag = DOMUtil.getChildByTagName(http, "headers");
          String content=null;
          if (bodyTag != null) {
                Object obj= executeChildTag(DOMUtil.getFirstChild(bodyTag));
                if(obj instanceof String)
                   content = (String) executeChildTag(DOMUtil.getFirstChild(bodyTag));
                else{
                     if(obj instanceof Document){
                         tfactory = TransformerFactory.newInstance();
                         transformer = tfactory.newTransformer();
                         StringWriter writer = new StringWriter();
                         transformer.transform(new DOMSource((Document)obj),new StreamResult(writer));
                         content=writer.toString();
                     }

                }
                if (headerTag != null) {
                    NodeList headers=headerTag.getElementsByTagName("header");
                    NamedNodeMap attributes;
                    for(int i=0;i<headers.getLength(); i++){
                        attributes=headers.item(i).getAttributes();
                        method.setRequestHeader(attributes.getNamedItem("key").getNodeValue(), attributes.getNamedItem("value").getNodeValue());
                    }
                }
                ((PostMethod)method).setRequestBody(content);

        }
       
       }else{
           // Create a method instance.
           method = new GetMethod(urlStr);

           // Provide custom retry handler is necessary
           method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));

       }

          // Execute the method.
          statusCode = client.executeMethod(method);

          if (statusCode != HttpStatus.SC_OK) {
            System.err.println("Method failed: " + method.getStatusLine());
          }


       // Read the response body.

        if (http.getAttribute(FILE_TYPE).equals(XML)) {
            return new DOMUtil().inputStreamToDocument(method.getResponseBodyAsStream());
        }else
           if (http.getAttribute(FILE_TYPE).equals(TEXT)) {
            return method.getResponseBodyAsString();
        }else 
           return method.getResponseBody(); 
       
    }
}
            
    
