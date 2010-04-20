/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class CanStepOverMessage extends StructuredMessage {
    protected static final String COMMAND_NAME = "canStepOver";
      
    public CanStepOverMessage() {
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
