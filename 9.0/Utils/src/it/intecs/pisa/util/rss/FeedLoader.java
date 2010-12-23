/*
 * FeedLoader.java
 *
 * Created on 2 ottobre 2007, 10.02
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.util.rss;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class FeedLoader {
    
    private static final String VERSION_2_0="2.0";
    
    /** Creates a new instance of FeedLoader */
    public FeedLoader() {
    }
    
    public static Feed loadFeed(File feedFile) {
        try {
            DocumentBuilder builder=null;
            Document feedDoc=null;
            Feed feed=null;
             
            builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
            feedDoc=builder.parse(feedFile);
            
           String version=feedDoc.getDocumentElement().getAttribute("version").toString();
           
           if(version.equals(VERSION_2_0))
           {
              feed=new Feed2_0();
            
             ((Feed2_0)feed).parse(feedDoc);
           }
           
             return feed;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }
}
