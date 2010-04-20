/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class BooleanValueMessage extends StructuredMessage {
    protected static final String COMMAND_NAME = "booleanValue";
    protected static final String ATTR_VALUE = "value";
    
    protected String value;
   
    
    public BooleanValueMessage() {
    }

    public BooleanValueMessage(boolean value) {
        setValue(value);
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;
         
        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
        rootEl.setAttribute(ATTR_VALUE,value);
        
        doc.appendChild(rootEl);
        return doc;
    }

    @Override
    public void initFromDocument(Document doc) {
        Element rootEl;
        Element filePathEl;
        Element xPathEl;
        Element lineEl;

        rootEl = doc.getDocumentElement();
        value = rootEl.getAttribute(ATTR_VALUE);
    }



    public void setValue(boolean val) {
       this.value=Boolean.toString(val);
    }
    
    public boolean getValue()
    {
        return Boolean.parseBoolean(value);
    }
    
}
