/**
 * 
 */
package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to parse the STARTDEBUG command
 * @author Massimiliano
 *
 */
public class ExecutionCompletedMessage extends StructuredMessage {

    private static final String COMMAND_NAME = "executioncompleted";

    public ExecutionCompletedMessage() {
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
