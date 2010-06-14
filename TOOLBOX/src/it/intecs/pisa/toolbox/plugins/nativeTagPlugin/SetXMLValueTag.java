package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.apache.xpath.XPathAPI;
//import net.sf.saxon.xpath.XPathEvaluator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SetXMLValueTag extends NativeTagExecutor {
    protected String tagName="setXMLValue";
    
    @Override
    public Object executeTag(org.w3c.dom.Element setXMLValue) throws Exception {
        Iterator children = DOMUtil.getChildren(setXMLValue).iterator();
        Document document = (Document) this.executeChildTag((Element) children.next());

        Element selectedNode;
        if (setXMLValue.getAttributes().getLength() > 0) {
            selectedNode=(Element) XPathAPI.selectSingleNode(document,
                    (String) executeChildTag((Element) children.next()),
                    setXMLValue);
        } else {
            selectedNode=(Element) XPathAPI.selectSingleNode(document,
                    (String) executeChildTag((Element) children.next()),
                    document.getDocumentElement());
        }

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
