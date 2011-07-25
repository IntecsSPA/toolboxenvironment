package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExecutionTreeMessage extends StructuredMessage {

    protected static final String TYPE_DOCUMENT = "org.w3c.dom.Document";
    private static final String COMMAND_NAME = "executionTree";
    private static final String ATTR_FILENAME = "filename";
   private static final String TAG_VALUE = "value";
    
    protected Document xmlValue;
    protected String filename="";
    protected boolean needsRootElementFiltering=true;

    public ExecutionTreeMessage() {
    }

    public ExecutionTreeMessage(Document doc) {
        this.xmlValue = doc;
    }
    
    public ExecutionTreeMessage(String filename,Document doc) {
        this.xmlValue = doc;
        this.filename=filename;
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;
        Element valueEl;
        Element elToCopyFrom=null;
           
        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
        rootEl.setAttribute(ATTR_FILENAME, filename);
        doc.appendChild(rootEl);

        valueEl = doc.createElement(TAG_VALUE);
        
        if(needsRootElementFiltering==true)
                elToCopyFrom=DOMUtil.getFirstChild(xmlValue.getDocumentElement());       
        else  elToCopyFrom=xmlValue.getDocumentElement();
        
       DOMUtil.removeSchemaLocation(elToCopyFrom);
        
        valueEl.appendChild(doc.importNode(elToCopyFrom, true));
        rootEl.appendChild(valueEl);
        return doc;
    }

    @Override
    public void initFromDocument(Document doc) {
        Element rootEl;
        Element valueEl;
        DOMUtil util;

        util = new DOMUtil();
        rootEl = doc.getDocumentElement();

        valueEl = DOMUtil.getFirstChild(rootEl);
        xmlValue = util.newDocument();

        xmlValue.appendChild(xmlValue.importNode(DOMUtil.getFirstChild(valueEl), true));
        DOMUtil.indent(xmlValue);

        filename=rootEl.getAttribute(ATTR_FILENAME);
    }

   public Document getExecutionTree()
   {
       return this.xmlValue;
   }
   
   public String getFileName()
   {
	   return this.filename;
   }
   
   public void setFileName(String filename)
   {
	   this.filename=filename;
   }
}
