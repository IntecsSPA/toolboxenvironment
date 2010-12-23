package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TerminateMessage extends StructuredMessage{
	private static final String COMMAND_NAME = "terminate";
	
	public TerminateMessage()
	{
		
	}
		
	@Override
    public Document getDoc()
    {
         DOMUtil util;
         Element rootEl;
       
         util=new DOMUtil();
         doc=util.newDocument();
       
          rootEl=doc.createElement(COMMAND_NAME);
          doc.appendChild(rootEl);    
          return doc;
    }
}
