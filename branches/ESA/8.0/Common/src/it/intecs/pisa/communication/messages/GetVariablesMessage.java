package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GetVariablesMessage extends StructuredMessage{
	private static final String COMMAND_NAME = "getvariables";
	
	public GetVariablesMessage()
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
