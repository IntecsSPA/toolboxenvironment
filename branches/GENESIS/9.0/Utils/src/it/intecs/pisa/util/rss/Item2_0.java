/*
 * Item2_0.java
 *
 * Created on 2 ottobre 2007, 9.51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package it.intecs.pisa.util.rss;

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
public class Item2_0 {
    
    private String title=null;
    
    private String link=null;
    
    private String description=null;
    
    /** Creates a new instance of Item2_0 */
    public Item2_0() {
        
    }
    
    public void parse(Element itemEl) {
        NodeList list;
        
        
        
        //Getting mandatory fields
        list=itemEl.getElementsByTagName("title");
        if(list != null && list.getLength()>0) {
            setTitle((String)list.item(0).getTextContent());
           
        }
        
        
        list=itemEl.getElementsByTagName("link");
        if(list != null && list.getLength()>0) {
            setLink((String)list.item(0).getTextContent());
           
        }
        
        list=itemEl.getElementsByTagName("description");
        if(list != null && list.getLength()>0) {
            setDescription((String)list.item(0).getTextContent());
          
        }
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
        
        rootNode=feedDoc.createElement("item");

        newEl=feedDoc.createElement("title");
        text=feedDoc.createTextNode(this.title);
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
        newEl=feedDoc.createElement("link");
        text=feedDoc.createTextNode(this.link);
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
        
        newEl=feedDoc.createElement("description");
        text=feedDoc.createTextNode(this.description);
        newEl.appendChild(text);
        rootNode.appendChild(newEl);
         
        feedDoc.appendChild(rootNode);
        return feedDoc;
    }
    
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
}
