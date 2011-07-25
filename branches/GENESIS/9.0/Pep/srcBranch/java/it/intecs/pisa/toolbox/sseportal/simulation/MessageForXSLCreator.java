/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.sseportal.simulation;

import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Massimiliano
 */
public class MessageForXSLCreator {

    public static Document getPortalTemplateXML(String tagNameSpace, String tagName, Element toAdd) {
        Document doc;
        DOMUtil util;
        Element root;
        Element el;
        NodeList children;
        Node node;
        try {
            util = new DOMUtil(true);
            doc = util.newDocument();

            root = doc.createElementNS("http://www.intecs.it/portalSimulation", "root");
            doc.appendChild(root);

            el = doc.createElementNS(tagNameSpace, tagName);
            root.appendChild(el);

            if (toAdd != null) {
                children = toAdd.getChildNodes();

                for (int i = 0; i < children.getLength(); i++) {
                    node = children.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        addElement(el, (Element) node);
                    }
                }
            }

        } catch (Exception e) {
            doc = null;
        }
        return doc;
    }

    private static void addElement(Element parentEl, Element elToAppend) {
        Document doc;

        doc = parentEl.getOwnerDocument();

        parentEl.appendChild(doc.importNode(elToAppend, true));

    }
}
