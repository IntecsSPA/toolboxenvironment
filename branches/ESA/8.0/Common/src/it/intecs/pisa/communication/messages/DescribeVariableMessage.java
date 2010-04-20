package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DescribeVariableMessage extends StructuredMessage{
	private static final String COMMAND_NAME = "describeVariable";
	private static final String ATTR_NAME = "name";
	protected String varname;
	
	public DescribeVariableMessage()
	{
		
	}
	
	public DescribeVariableMessage(String name)
	{
		varname=name;
	}
	
	public void setName(String name)
	{
		varname=name;
	}
        
        public String getName()
	{
		return varname;
	}
		
	@Override
    public Document getDoc()
    {
         DOMUtil util;
         Element rootEl;
       
         util=new DOMUtil();
         doc=util.newDocument();
       
          rootEl=doc.createElement(COMMAND_NAME);
          rootEl.setAttribute(ATTR_NAME, varname);
          doc.appendChild(rootEl);    
          return doc;
    }

	@Override
	public void initFromDocument(Document doc) {
		Element rootEl;

        rootEl = doc.getDocumentElement();

        varname = rootEl.getAttribute(ATTR_NAME);
	}
}
