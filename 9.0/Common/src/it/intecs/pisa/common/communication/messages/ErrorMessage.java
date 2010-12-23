package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ErrorMessage extends StructuredMessage {
    protected static final String ATTR_DETAILS = "details";
    protected static final String CONSTANT_NAME = "error";
    protected String errorMessage="";
 
    public ErrorMessage()
        {

        }
    
      public ErrorMessage(String txt)
    {
        errorMessage=txt;
    }
    
    public void setErrorMessage(String txt)
    {
        errorMessage=txt;
    }
    
     
        
         @Override
         public Document getDoc()
         {
              DOMUtil util;
              Element rootEl;
            
              util=new DOMUtil();
              doc=util.newDocument();
            
               rootEl=doc.createElement(CONSTANT_NAME);
               doc.appendChild(rootEl);      
               rootEl.setAttribute( ATTR_DETAILS,errorMessage);
               
               return doc;
         }
}
