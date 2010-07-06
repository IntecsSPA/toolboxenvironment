package it.intecs.pisa.communication.messages;

import java.io.IOException;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class VariableDescriptionMessage extends StructuredMessage{
	protected static final String TYPE_DOCUMENT = "org.w3c.dom.Document";
	private static final String COMMAND_NAME = "variableDescription";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_TYPE = "type";
	private static final String TAG_VALUE = "value";
	
	protected String varname;
	protected String type;
	protected String strValue;
	protected Document xmlValue;
	
	public VariableDescriptionMessage()
	{
		
	}
	
	public VariableDescriptionMessage(String name,String type,String value)
	{
		this.varname=name;
		this.strValue=value!=null?value:"";
		this.type=type;
	}
	
	public VariableDescriptionMessage(String name,Document value)
	{
		this.varname=name;
		this.xmlValue=value;
		this.type=TYPE_DOCUMENT;
	}
	
		
	@Override
    public Document getDoc()
    {
         DOMUtil util;
         Element rootEl;
         Element valueEl;
       
         util=new DOMUtil();
         doc=util.newDocument();
       
          rootEl=doc.createElement(COMMAND_NAME);
          rootEl.setAttribute(ATTR_NAME, varname);
          rootEl.setAttribute(ATTR_TYPE, type);
          doc.appendChild(rootEl);    
          
          valueEl=doc.createElement(TAG_VALUE);
          
          if(type.equals(TYPE_DOCUMENT))
          {
        	  valueEl.appendChild(doc.importNode(xmlValue.getDocumentElement(), true));
          }
          else DOMUtil.setTextToElement(doc, valueEl, strValue);
          
          rootEl.appendChild(valueEl);
          return doc;
    }

	@Override
	public void initFromDocument(Document doc) {
		Element rootEl;
		Element valueEl;
		DOMUtil util;
				
        rootEl = doc.getDocumentElement();

        varname = rootEl.getAttribute(ATTR_NAME);
        type=rootEl.getAttribute(ATTR_TYPE);
        
        valueEl=DOMUtil.getFirstChild(rootEl);
        
        if(type.equals(TYPE_DOCUMENT))
        {
        	util=new DOMUtil();
        	xmlValue=util.newDocument();
        	
        	xmlValue.appendChild(xmlValue.importNode(DOMUtil.getFirstChild(valueEl), true));
        }
        else
        {
        	strValue=DOMUtil.getTextFromNode(valueEl);
        }
	}

	public String getType() {
		return type;
	}

	public String getVarname() {
		return varname;
	}
	
	public String getValue()
	{
		if(type.equals(TYPE_DOCUMENT))
		{
			try {
				return DOMUtil.getDocumentAsString(xmlValue);
			} catch (Exception e) {
				e.printStackTrace();
                                return "";
			}
		}
		else return strValue;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setVarname(String varname) {
		this.varname = varname;
	}
	
	public void setValue(String value)
	{
		DOMUtil util;
		
		if(type.equals(TYPE_DOCUMENT))
		{
			util=new DOMUtil();
			try {
				xmlValue=util.stringToDocument(value);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else strValue=value;
	}
}
