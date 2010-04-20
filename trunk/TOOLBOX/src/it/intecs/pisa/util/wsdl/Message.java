/**
 * 
 */
package it.intecs.pisa.util.wsdl;

import it.intecs.pisa.util.DOMUtil;

import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class Message {
	
	private static final String TAG_PART = "part";
	private String name=null;
	private Part[] parts=null;


	
	private Document myXMLsnippet=null;
	private DOMUtil domutil=null;
	
	
	private static final String ATTRIBUTE_NAME = "name";
	
	public Message()
	{
		domutil=new DOMUtil();
	}
	
	/**
	 * This method creates a port type class, parsing its information from a xml snippet
	 * @param portTypeTag
	 * @return
	 * @throws Exception 
	 */
	public void createFromXMLSnippet(Element messageTag) throws WSDLException
	{
		LinkedList partList=null;
		int partCount=0;
		Element rootImportedNode=null;
		Element rootEl=null;
		
		//copying the xml snippet
		myXMLsnippet=domutil.newDocument();
		rootImportedNode=(Element)myXMLsnippet.importNode(messageTag, true);
		
		myXMLsnippet.appendChild(rootImportedNode);
		rootEl=myXMLsnippet.getDocumentElement();
		
		//setting port name
		name=rootEl.getAttribute(ATTRIBUTE_NAME);
		if(name==null || name.equals(""))
			throw new WSDLException("No message name");
		
		//parsing operations
		partList=DOMUtil.getChildrenByTagName(rootEl,TAG_PART);
		partCount=partList.size();
		
		parts=new Part[partCount];
		
		for(int i=0; i< partCount;i++)
		{
			Element partTag=(Element)partList.get(i);
			
			DOMUtil.copyNamespaces(messageTag,partTag);
			
			parts[i]=new Part();
			parts[i].createFromXMLSnippet(partTag);
			
			rootEl.removeChild(partTag);
		}
	}

    void appendToXML(Document wsdl) {
        Element rootEl;
        Element messageEl;

        rootEl=wsdl.getDocumentElement();
        messageEl=wsdl.createElement("wsdl:message");
        messageEl.setAttribute("name", name);

        rootEl.appendChild(messageEl);

        for(Part p:this.parts)
        {
            p.appendToXml(messageEl);
        }

        
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParts(Part[] parts) {
        this.parts = parts;
    }
	
	 public String getName() {
        return name;
    }

    public Part[] getParts() {
        return parts;
    }
	
}
