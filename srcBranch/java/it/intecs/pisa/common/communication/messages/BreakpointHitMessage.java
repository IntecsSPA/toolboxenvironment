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
public class BreakpointHitMessage extends StructuredMessage {
    protected static final String COMMAND_NAME = "breakpointhit";
    protected static final String TAG_FILE = "file";
    protected static final String TAG_XPATH = "xpath";
    protected static final String ATTR_LINENUMBER = "line";
    
    protected String breakFilePath;
    protected String breakXpath;
    private int lineNumber;
    
    public BreakpointHitMessage() {
    }

    public BreakpointHitMessage(String path, String xPath, int lineNumber) {
        breakFilePath = path;
        breakXpath = xPath;
        this.lineNumber=lineNumber;
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;
        Element filePathEl;
        Element xPathEl;
        Element lineEl;
        
        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
        filePathEl = doc.createElement(TAG_FILE);
        DOMUtil.setTextToElement(doc, filePathEl, breakFilePath);
        rootEl.appendChild(filePathEl);

        xPathEl = doc.createElement(TAG_XPATH);
        DOMUtil.setTextToElement(doc, xPathEl, breakXpath);
        rootEl.appendChild(xPathEl);
        
        lineEl = doc.createElement(ATTR_LINENUMBER);
        DOMUtil.setTextToElement(doc, lineEl, Integer.toString(getLineNumber()));
        rootEl.appendChild(lineEl);
        
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
        filePathEl = DOMUtil.getChildByTagName(rootEl, TAG_FILE);
        xPathEl = DOMUtil.getChildByTagName(rootEl, TAG_XPATH);
        lineEl = DOMUtil.getChildByTagName(rootEl, ATTR_LINENUMBER);
        
        setBreakFilePath(DOMUtil.getTextFromNode(filePathEl));
        setBreakXpath(DOMUtil.getTextFromNode(xPathEl));
        setLineNumber(Integer.parseInt(DOMUtil.getTextFromNode(lineEl)));
    }

    public String getBreakFilePath() {
        return breakFilePath;
    }

    public void setBreakFilePath(String breakFilePath) {
        this.breakFilePath = breakFilePath;
    }

    public String getBreakXpath() {
        return breakXpath;
    }

    public void setBreakXpath(String breakXpath) {
        this.breakXpath = breakXpath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
