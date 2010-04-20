package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.util.DOMUtil;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XmlDocumentTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element xmlDocument) throws Exception {
        DOMUtil domUtil = new DOMUtil();
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        Document document = documentBuilderFactory.newDocumentBuilder().newDocument();
        LinkedList children = DOMUtil.getChildren(xmlDocument);
        Element rootDescriptor = (Element) children.removeLast();
        Iterator storeSequence = children.iterator();
        while (storeSequence.hasNext()) {
            processStore((Element) storeSequence.next());
        }
        buildRoot(document, rootDescriptor);
        InputStream docStream = DOMUtil.getDocumentAsInputStream(document);
        return domUtil.readerToDocument(new InputStreamReader(docStream));
    }
    
 
}
