/*
 * Feed2_0.java
 *
 * Created on 2 ottobre 2007, 9.54
 *
 *Created following the specification found at http://rss.specifiche.it/2.0/
 *
 */

package it.intecs.pisa.util.rss;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class Feed2_0 implements Feed{
    private static final String VERSION="2.0";
    private Channel2_0 channel;
    
    /** Creates a new instance of Feed2_0 */
    public Feed2_0() {
        channel=new Channel2_0();
    }
    
    public String GetVersion() {
        return VERSION;
    }
    
    public void parse(Document doc) throws Exception {
        //Parsing rss document
        Element root=null;
        Element channelElement=null;
        
        //Checking if version is 2.0
        root=doc.getDocumentElement();
        
        if(root.getAttribute("version").equals(VERSION)==false) {
            throw new Exception("RSS version is not 2.0");
        }
        
        channelElement=(Element)root.getElementsByTagName("channel").item(0);
        if(channelElement != null) {
            channel.parse(channelElement);
        } else throw new Exception("RSS feed doesn't contain any channel");
        
    }
    

    
    public void dump(File dumpFile) throws Exception {
        DocumentBuilder builder=null;
        Document feedDoc=null;
        Document channelDoc=null;
        Element newEl=null;
        Element rootNode=null;
        Attr attr=null;
        Transformer serializer = null;
        
        builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
        feedDoc=builder.newDocument();
        
        rootNode=feedDoc.createElement("rss");
        rootNode.setAttribute("version",VERSION);
        
        feedDoc.appendChild(rootNode);
        
        channelDoc=channel.createDocument();
        newEl=(Element)feedDoc.importNode(channelDoc.getDocumentElement(),true);
        
        rootNode.appendChild(newEl);
        
        //Dumping to disk
        OutputStream out = new FileOutputStream(dumpFile);
     
        serializer = TransformerFactory.newInstance().newTransformer();
        serializer.transform(new DOMSource(feedDoc),new StreamResult(out));
        out.close();
    }

    public Channel2_0 GetChannel() {
            return this.channel;
    }

}
