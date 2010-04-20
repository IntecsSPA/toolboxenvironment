package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlTag extends NativeTagExecutor{
        /**
     * The Constant ATTRIBUTE_PREFIX.
     */
    private static final String ATTRIBUTE_PREFIX = "attributePrefix";
    
    /**
     * The Constant TEXT_TAG.
     */
    private static final String TEXT_TAG = "textTag";
    
      
    /**
     * The Constant NAME.
     */
    private static final String NAME = "name";
    
    
    public XmlTag()
    {
        tagName="xml";
    }
    
    /**
	 * 
	 * @param debugTag
	 * @param tagEl
	 */
    @Override
	public Object executeTag(org.w3c.dom.Element tagEl) throws Exception{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.
                newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document document = documentBuilderFactory.newDocumentBuilder().
                newDocument();
        document.appendChild(processXML(document, DOMUtil.getFirstChild(tagEl),
                tagEl.getAttribute(ATTRIBUTE_PREFIX),
                tagEl.getAttribute(TEXT_TAG)));
 
        dumpResourceAndAddToDebugTree(document);
         return document;
}
        
            /**
     * Process XML.
     *
     * @param element the element
     * @param attributePrefix the attribute prefix
     * @param document the document
     * @param textTag the text tag
     *
     * @return the element
     */
    private Element processXML(Document document, Element element,
            String attributePrefix, String textTag) {
//        Element newElement = document.createElement(element.getTagName());
        /*the previous line has been replaced by the following one in order to make the getXmlGetResponse work (namespace of the root element)*/
        Element newElement = document.createElementNS(element.getNamespaceURI(),
                element.getTagName());
        NamedNodeMap attributes = element.getAttributes();
        Attr attribute;
        String value;
        for (int index = 0; index < attributes.getLength(); index++) {
            attribute = (Attr) attributes.item(index);
            value = attribute.getValue();
            newElement.setAttribute(
                    attribute.getName(),
                    value.startsWith(attributePrefix) ?
                        engine.getVariablesStore().getVariable(value.substring(attributePrefix.length())).toString() :
                        value
                    );
        }
        NodeList children = element.getChildNodes();
        Node child;
        short childNodeType;
        Element childElement;
        
        for (int index = 0; index < children.getLength(); index++) {
            child = children.item(index);
            childNodeType = child.getNodeType();
            if (childNodeType == Node.ELEMENT_NODE) {
                childElement = (Element) child;
                if (childElement.getTagName().equals(textTag)) {
                    newElement.appendChild(document.createTextNode(engine.getVariablesStore().getVariable(
                            childElement.getAttribute(NAME)).toString()));
                } else {
                    newElement.appendChild(processXML(document, childElement,
                            attributePrefix, textTag));
                }
            } else if (childNodeType == Node.TEXT_NODE &&
                    Pattern.matches("^\n\\s*", child.getNodeValue())) {
                continue;
            } else {
                newElement.appendChild(document.importNode(child, true));
            }
        }
        return newElement;
    }
}
