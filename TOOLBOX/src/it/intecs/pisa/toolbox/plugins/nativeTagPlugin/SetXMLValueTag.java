package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SetXMLValueTag extends NativeTagExecutor {
    protected String tagName="setXMLValue";
    
    @Override
    public Object executeTag(org.w3c.dom.Element setXMLValue) throws Exception {
        Iterator children = DOMUtil.getChildren(setXMLValue).iterator();
        Document document = (Document) this.executeChildTag((Element) children.next());
        
        String test=DOMUtil.getDocumentAsString(document);
        Element selectedNode;


        if (setXMLValue.getAttributes().getLength() > 0) {
            selectedNode=(Element) XPathAPI.selectSingleNode(document,
                    (String) executeChildTag((Element) children.next()),
                    setXMLValue);
        } else {
            String xpath=(String) executeChildTag((Element) children.next());
            selectedNode=(Element) XPathAPI.selectSingleNode(document,
                    xpath/*,
                    document.getDocumentElement()*/);
        }

        /*
        Element xPathDocElement=setXMLValue.getOwnerDocument().getDocumentElement();
        Element namespaceElements []= {xPathDocElement,setXMLValue};
        String attrSplit[];
        SaxonDocument saxonDoc= new SaxonDocument(document);
        NamedNodeMap attributes;

        for(int z=0; z<namespaceElements.length;z++){
           attributes=namespaceElements[z].getAttributes();
           if (attributes.getLength() > 0){
            for(int i=0; i<attributes.getLength(); i++){
                attrSplit=attributes.item(i).getNodeName().split(":");
                if(attrSplit[0].equalsIgnoreCase("xmlns") && attrSplit.length == 2)
                        saxonDoc.declareXPathNamespace(attrSplit[1],
                                            attributes.item(i).getNodeValue());
            }
          }
        }

        selectedNode=(Node) saxonDoc.evaluatePath((String) executeChildTag((Element) children.next()), XPathConstants.NODE);
        selectedNode=(Element)matchedNodes.get(0);
*/
        Object objToAdd;

        objToAdd=executeChildTag((Element) children.next());
        if(objToAdd instanceof String)
        {
            selectedNode.setNodeValue((String)objToAdd);
        }
        else if(objToAdd instanceof Document)
        {
            Element elToAdd=((Document)objToAdd).getDocumentElement();
            Element importedNode=(Element) document.importNode(elToAdd, true);
            selectedNode.appendChild(importedNode);
        }

        dumpResourceAndAddToDebugTree(document);
        return null;
    }
}
