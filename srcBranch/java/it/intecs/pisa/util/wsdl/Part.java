/**
 * 
 */
package it.intecs.pisa.util.wsdl;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class Part {
	protected static final String ATTRIBUTE_ELEMENT = "element";
    protected static final String ATTRIBUTE_NAME = "name";

	private String name=null;
	private String elementType=null;
	private String elementNameSpace=null;

	private Document myXMLsnippet=null;
	private DOMUtil domutil=null;
	
	
	
	
	
	public Part()
	{
		domutil=new DOMUtil();
	}
	
	public void createFromXMLSnippet(Element partTag) throws WSDLException {
		Element rootImportedNode=null;
		Element rootEl=null;
		String elementString=null;
		int index=0;
		
		//copying the xml snippet
		myXMLsnippet=domutil.newDocument();
		rootImportedNode=(Element)myXMLsnippet.importNode(partTag, true);
		
		myXMLsnippet.appendChild(rootImportedNode);
		rootEl=myXMLsnippet.getDocumentElement();
		
		//setting port name
		name=rootEl.getAttribute(ATTRIBUTE_NAME);
		if(name==null || name.equals(""))
			throw new WSDLException("No part name");
		
		elementString=rootEl.getAttribute(ATTRIBUTE_ELEMENT);	
		
		index=elementString.indexOf(":");
		if(index>-1)
		{
			elementType=elementString.substring(index+1);
			elementNameSpace=rootImportedNode.getAttribute("xmlns:"+elementString.substring(0,index));
		}
		else
		{
			elementNameSpace="";
			elementType=elementString;
		}
		
	}

    void appendToXml(Element messageEl) {
        Element partEl;
        Document doc;
        String elementStr;

        doc=messageEl.getOwnerDocument();
        partEl=doc.createElement("wsdl:part");
        partEl.setAttribute("name", name);

        if(elementType!=null && elementNameSpace!=null)
        {
            elementStr=elementNameSpace.equals("")?"":elementNameSpace+":";
            elementStr+=elementType;
            partEl.setAttribute("element", elementStr);
        }

        messageEl.appendChild(partEl);
    }

	public String getElementNameSpace() {
		return elementNameSpace;
	}

	public void setElementNameSpace(String elementNameSpace) {
		this.elementNameSpace = elementNameSpace;
	}

	public String getElementType() {
		return elementType;
	}

	public void setElementType(String elementType) {
		this.elementType = elementType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    


}
