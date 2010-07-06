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
public class SetBreakpointMessage extends StructuredMessage {
	public static final String CURRENT_FILE="__current_file";
    protected static final String COMMAND_NAME = "setBreakpoint";
    protected static final String ATTR_XPATH = "xpath";
    protected static final String ATTR_FILE = "file";
    protected String value;
    protected String file;

    public SetBreakpointMessage() {
    }

    public SetBreakpointMessage(String file, String xpath) {
        setXPath(xpath);
        setFile(file);
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
        rootEl.setAttribute(ATTR_FILE, file);
        rootEl.setAttribute(ATTR_XPATH, value);

        doc.appendChild(rootEl);
        return doc;
    }

    @Override
    public void initFromDocument(Document doc) {
        Element rootEl;

        rootEl = doc.getDocumentElement();
        value = rootEl.getAttribute(ATTR_XPATH);
        file=rootEl.getAttribute(ATTR_FILE);
    }

    public void setXPath(String path) {
        value = path;
    }

    public String getXPath() {
        return value;
    }

    public void setFile(String file) {
        this.file=file;
    }
    
    public String getFile() {
        return file;
    }
}
