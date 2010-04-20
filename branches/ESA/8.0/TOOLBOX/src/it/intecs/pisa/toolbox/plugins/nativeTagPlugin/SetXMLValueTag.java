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
        if (setXMLValue.getAttributes().getLength() > 0) {
            XPathAPI.selectSingleNode(document,
                    (String) executeChildTag((Element) children.next()),
                    setXMLValue).setNodeValue(String.valueOf(executeChildTag((Element) children.next())));
        } else {
            XPathAPI.selectSingleNode(document,
                    (String) executeChildTag((Element) children.next()),
                    document.getDocumentElement()).setNodeValue(String.valueOf(executeChildTag((Element) children.next())));
        }
        
        dumpResourceAndAddToDebugTree(document);
        return null;
    }
}
