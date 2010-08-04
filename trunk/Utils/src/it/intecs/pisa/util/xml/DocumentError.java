/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.xml;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to easily create an error XML document by simply providing an error string.
 *
 * @author Massimiliano Fanciulli
 */
public class DocumentError {
    public static Document get(String errorMsg)
    {
        Document doc;
        DOMUtil util;
        util=new DOMUtil();

        doc=util.newDocument();
        Element rootEl;

        rootEl=doc.createElement("root");
        doc.appendChild(rootEl);

        Element el;
        el=doc.createElement("success");
        el.setTextContent("false");
        rootEl.appendChild(el);

        el=doc.createElement("reason");
        el.setTextContent(errorMsg);
        rootEl.appendChild(el);

        return doc;
    }
}
