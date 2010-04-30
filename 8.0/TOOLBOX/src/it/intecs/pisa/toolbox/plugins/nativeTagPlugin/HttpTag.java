package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class HttpTag extends NativeTagExecutor {
    // protected String tagName="Http";
     
     public HttpTag()
     {
         tagName="Http";
     }
     
    @Override
    public Object executeTag(org.w3c.dom.Element http) throws Exception {
       HttpMethod method;
        Element parameter;
        Document xmlResponse;
        String stringResponse;
        URL url;
        String urlStr;
        String urlPath;
        String host;
        String queryStr="";
        int port;
        TransformerFactory tfactory=null; 
        Transformer transformer=null;
        Iterator children = DOMUtil.getChildren(http).iterator();
        Element urlTag = DOMUtil.getChildByTagName(http, "url");
        urlStr = (String) this.executeChildTag(DOMUtil.getFirstChild(urlTag));
        
        if(urlStr.startsWith("http://"))
        {
            url=new URL(urlStr);
            host=url.getHost();
            port=url.getPort();
            urlPath=url.getPath();
            queryStr=url.getQuery();
        }
        else
        {
            host=engine.evaluateString(http.getAttribute(HOST),IEngine.EngineStringType.ATTRIBUTE);
            port=Integer.parseInt(this.engine.evaluateString(http.getAttribute(PORT),IEngine.EngineStringType.ATTRIBUTE));
            urlPath=urlStr;
        }

        if(queryStr==null)
                queryStr="";
        

        if (http.getAttribute(METHOD).equals(POST)) {
            PostMethod pMethod = (PostMethod) (method = new PostMethod(urlPath));

            Element bodyTag;

            bodyTag = DOMUtil.getChildByTagName(http, "body");
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
               
                pMethod.setRequestBody(content);
                   
               
            } else {
                while (children.hasNext()) {
                    parameter = (Element) children.next();
                    if(parameter.getNodeName().contains("parameter"))
                    {
                    pMethod.addParameter(parameter.getAttribute(NAME),
                            (String) executeChildTag(DOMUtil.getFirstChild(parameter)));
                    }
                }
            }


        } else {
            GetMethod gMethod = (GetMethod) (method = new GetMethod(urlPath));

            while (children.hasNext()) {
                parameter = (Element) children.next();
                  if(parameter.getNodeName().contains("parameter")){
                      if(queryStr.equals("")==false && queryStr.endsWith("&")==false)
                          queryStr+="&";
                      
                    queryStr+=engine.evaluateString(parameter.getAttribute(NAME),IEngine.EngineStringType.ATTRIBUTE);
                    queryStr+="=";
                    queryStr+= (String) executeChildTag(DOMUtil.getFirstChild(parameter));
                  }
            }
            if(queryStr!=null && queryStr.equals("")==false)
                gMethod.setQueryString(queryStr);
        }

        HttpConnection conn = new HttpConnection(host, port);
        conn.open();
        
        String proxyHost;
        String proxyPort;
        if ((proxyHost = System.getProperty("http.proxyHost")) != null &&
                (proxyPort = System.getProperty("http.proxyPort")) != null) {
            conn.setProxyHost(proxyHost);
            conn.setProxyPort(Integer.parseInt(proxyPort));
        }

        method.execute(new HttpState(), conn);


        if (http.getAttribute(FILE_TYPE).equals(BINARY)) {
            DataInputStream in = new DataInputStream(method.getResponseBodyAsStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            for (count = in.read(buffer); count >= 0; count = in.read(buffer)) {
                out.write(buffer, 0, count);
                out.flush();
            }
            return out.toByteArray();
        }

        Reader in = new InputStreamReader(method.getResponseBodyAsStream());
        StringBuffer out = new StringBuffer();
        char[] buffer = new char[1024];
        int count;
        for (count = in.read(buffer); count >= 0; count = in.read(buffer)) {
            out.append(buffer, 0, count);
        }

        if (http.getAttribute(FILE_TYPE).equals(XML)) {
            xmlResponse=new DOMUtil().stringToDocument(out.toString());
            dumpResourceAndAddToDebugTree(xmlResponse);
            return xmlResponse;
        } else {
            stringResponse=out.toString();
            dumpResourceAndAddToDebugTree(stringResponse);
            return stringResponse;
        }
    }
    
 
}
