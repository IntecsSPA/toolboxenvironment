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
public class CanStepIntoMessage extends StructuredMessage {
    protected static final String COMMAND_NAME = "canStepInto";
      
    public CanStepIntoMessage() {
    }

     @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;
           
        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
               
        doc.appendChild(rootEl);
        return doc;
    }

}
