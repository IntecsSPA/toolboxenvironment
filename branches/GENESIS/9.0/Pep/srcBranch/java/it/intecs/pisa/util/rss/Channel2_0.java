/*
 * Channel2_0.java
 *
 * Created on 2 ottobre 2007, 9.51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.util.rss;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author Massimiliano
 */
public class Channel2_0 {
    
    private Vector items;
    
    private String title;
    
    private String pubDate;
    
    private SimpleDateFormat formatter=null;
    /** Creates a new instance of Channel2_0 */
    public Channel2_0() {
        formatter=new SimpleDateFormat("dd MM yyyy HH:mm:ss Z");
        setItems(new Vector());
        
        this.pubDate=formatter.format(new Date());
    }
    
    public void parse(Element channelElement) throws Exception{
        NodeList childrenEl;
        
        //Getting mandatory fields
        childrenEl=channelElement.getElementsByTagName("title");
        setTitle((String)childrenEl.item(0).getTextContent());
        
        childrenEl=channelElement.getElementsByTagName("link");
        setLink((String)childrenEl.item(0).getTextContent());
        
        childrenEl=channelElement.getElementsByTagName("description");
        setDescription((String)childrenEl.item(0).getTextContent());
        
        childrenEl=channelElement.getElementsByTagName("pubDate");
         if(childrenEl != null && childrenEl.getLength()>0) {
        setPubDate((String)childrenEl.item(0).getTextContent());
         }
        
        childrenEl=channelElement.getElementsByTagName("lastBuildDate");
         if(childrenEl != null && childrenEl.getLength()>0) {
        setLastBuildDate((String)childrenEl.item(0).getTextContent());
         }
        //Getting optional field
        
        //Parsing items
        childrenEl=channelElement.getElementsByTagName("item");
        for(int i=0;i<childrenEl.getLength();i++) {
            Element it=(Element)childrenEl.item(i);
            Item2_0 item=new Item2_0();
            
            item.parse(it);
            
            this.getItems().add(item);
        }
        
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    private String link;
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    private String description;
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void addItem(Item2_0 newitem) {
        this.getItems().add(newitem);
    }
    
    public Document createDocument() throws ParserConfigurationException {
        DocumentBuilder builder=null;
        Document feedDoc=null;
        Element newEl=null;
        Element rootNode=null;
        Attr attr=null;
        Text text=null;
        
        builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
        feedDoc=builder.newDocument();
        
        rootNode=feedDoc.createElement("channel");

        newEl=feedDoc.createElement("title");
        text=feedDoc.createTextNode(this.getTitle());
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
        newEl=feedDoc.createElement("link");
         text=feedDoc.createTextNode(this.getLink());
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
        newEl=feedDoc.createElement("description");
        text=feedDoc.createTextNode(this.getDescription());
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
        newEl=feedDoc.createElement("pubDate");
        text=feedDoc.createTextNode(this.getPubDate());
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
        newEl=feedDoc.createElement("lastBuildDate");
        text=feedDoc.createTextNode(formatter.format(new Date()));
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
       Enumeration en=items.elements();     
       for(int i=0;i<items.size();i++)
       // for(Object item : items)
        {
            Item2_0 item2_0=(Item2_0)en.nextElement();;
            
            Document itDoc=item2_0.createDocument();
            newEl=(Element)feedDoc.importNode(itDoc.getDocumentElement(),true);
            rootNode.appendChild(newEl);
        }
        
        
        feedDoc.appendChild(rootNode);
        return feedDoc;
    }

    public Vector getItems() {
        return items;
    }

    public void setItems(Vector items) {
        this.items = items;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    private String lastBuildDate;

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }
    
}
