/**
 * 
 */
package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class InitBreakpointsMessage extends StructuredMessage {

    private static final String COMMAND_NAME = "initbreakpoints";
    private String[][] breakpoints = null;

    public InitBreakpointsMessage() {
    }

    public InitBreakpointsMessage(String[][] breakpoints) {
        this.breakpoints = breakpoints;
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;
        Element breakpointEl;

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
        for (String[] breakpoint : getBreakpoints()) {
            breakpointEl = doc.createElement("breakpoint");
            breakpointEl.setAttribute("file", breakpoint[0]);
            breakpointEl.setAttribute("xPath", breakpoint[1]);
             breakpointEl.setAttribute("lineNumber", breakpoint[2]);

            rootEl.appendChild(breakpointEl);
        }

        doc.appendChild(rootEl);
        return doc;
    }

    public void initFromDocument(Document doc) {
        Element rootEl;
        Element breakpointEl;
        LinkedList children = null;
        int childrenCount=0;
        
        rootEl = doc.getDocumentElement();
        children = DOMUtil.getChildren(rootEl);
        childrenCount=children.size();
        
        breakpoints=new String[childrenCount][3];
        for(int i=0;i<childrenCount;i++)
        {
            breakpointEl=(Element) children.get(i);
            breakpoints[i]=new String[3];
            
            breakpoints[i][0]=breakpointEl.getAttribute("file");
            breakpoints[i][1]=breakpointEl.getAttribute("xPath");
            breakpoints[i][2]=breakpointEl.getAttribute("lineNumber");
        }
        
    }

    public String[][] getBreakpoints() {
        return breakpoints;
    }
  
}
