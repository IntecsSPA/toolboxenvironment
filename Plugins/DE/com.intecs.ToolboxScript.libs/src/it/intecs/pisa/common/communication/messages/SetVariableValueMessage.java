package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetVariableValueMessage extends StructuredMessage {
	private static final String COMMAND_NAME = "setVariableValue";
	private static final String ATTR_NAME = "name";
	protected static final String TYPE_DOCUMENT = "org.w3c.dom.Document";
	private static final String TAG_VALUE = "value";
	private static final String ATTR_TYPE = "type";

	private String name;
	private String type;
	private String value;
	private Document xmlValue;

	public SetVariableValueMessage() {

	}
        
    public SetVariableValueMessage(String name,String type,String value) {
           this.name=name;
           this.type=type;
           this.value=value;
	}

	public SetVariableValueMessage(String name,Document value) {
		this.name=name;
		this.type=TYPE_DOCUMENT;
        this.xmlValue=value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Object getValue() {
		if(type.equals(TYPE_DOCUMENT))
			return xmlValue;
		else return value;
	}

	public void setValue(Object value) {
		if(value.getClass().getName().equals(TYPE_DOCUMENT))
			this.xmlValue = (Document) value;
		else this.value=(String) value;
	}

	@Override
	public Document getDoc() {
		DOMUtil util;
		Element rootEl;
		Element valueEl;

		util = new DOMUtil();
		doc = util.newDocument();

		rootEl = doc.createElement(COMMAND_NAME);
		rootEl.setAttribute(ATTR_NAME, name);
		rootEl.setAttribute(ATTR_TYPE, type);
		doc.appendChild(rootEl);
		
		valueEl = doc.createElement(TAG_VALUE);
		rootEl.appendChild(valueEl);
		
		if(type.equals(TYPE_DOCUMENT))
        {
      	  valueEl.appendChild(doc.importNode(xmlValue.getDocumentElement(), true));
        }
        else DOMUtil.setTextToElement(doc, valueEl, value);
		
		return doc;
	}

	@Override
	public void initFromDocument(Document doc) {
		Element rootEl;
		Element valueEl;
		DOMUtil util;
				
        rootEl = doc.getDocumentElement();

        name = rootEl.getAttribute(ATTR_NAME);
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
        	value=DOMUtil.getTextFromNode(valueEl);
        }
	}

}
