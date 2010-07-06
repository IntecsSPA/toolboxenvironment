package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.StringWriter;
import java.util.Iterator;
import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class XsltTag extends NativeTagExecutor {
    protected String tagName="xslt";
    @Override
    public Object executeTag(org.w3c.dom.Element xslt) throws Exception {
        DOMUtil domUtil = new DOMUtil();
        String stringResponse;
        Object xsltRef;
        Transformer transformer;

        Iterator children = DOMUtil.getChildren(xslt).iterator();
        DOMSource domSource = new DOMSource((Document) this.executeChildTag((Element) children.next()));

        xsltRef=executeChildTag((Element) children.next());


        if(xsltRef instanceof String)
            transformer= net.sf.saxon.TransformerFactoryImpl.newInstance().newTemplates(
                new StreamSource(new File((String) xsltRef))).newTransformer();
        else if(xsltRef instanceof Document)
            transformer= net.sf.saxon.TransformerFactoryImpl.newInstance().newTemplates(
                new DOMSource((Document) xsltRef)).newTransformer();
        else throw new Exception("XSLT reference type not supported");
        Element child;
        while (children.hasNext()) {
            transformer.setParameter((child = (Element) children.next()).getAttribute(NAME),
                    executeChildTag(
                    DOMUtil.getFirstChild(child)));
        }
        if (getBool(xslt.getAttribute(XML_OUTPUT))) {
            Document document = domUtil.newDocument();
            transformer.transform(domSource, new DOMResult(document));
            
            dumpResourceAndAddToDebugTree(document);
            return document;
        } else {
            StringWriter out = new StringWriter();
            transformer.transform(domSource, new StreamResult(out));
            
            stringResponse=out.toString();
            dumpResourceAndAddToDebugTree(stringResponse);
            return stringResponse;
        }
    }

/*
 
    public Object executeTag(org.w3c.dom.Element xslt) throws Exception {
        DOMUtil domUtil = new DOMUtil();
        String stringResponse;
        Object xsltRef;
        Transformer transformer;

        Iterator children = DOMUtil.getChildren(xslt).iterator();
        DOMSource domSource = new DOMSource((Document) this.executeChildTag((Element) children.next()));

        xsltRef=executeChildTag((Element) children.next());

        if(xsltRef instanceof String)
            transformer= TransformerFactory.newInstance().newTemplates(
                new StreamSource(new File((String) xsltRef))).newTransformer();
        else if(xsltRef instanceof Document)
            transformer= TransformerFactory.newInstance().newTemplates(
                new DOMSource((Document) xsltRef)).newTransformer();
        else throw new Exception("XSLT reference type not supported");
        Element child;
        while (children.hasNext()) {
            transformer.setParameter((child = (Element) children.next()).getAttribute(NAME),
                    executeChildTag(
                    DOMUtil.getFirstChild(child)));
        }
        if (getBool(xslt.getAttribute(XML_OUTPUT))) {
            Document document = domUtil.newDocument();
            transformer.transform(domSource, new DOMResult(document));
            
            dumpResourceAndAddToDebugTree(document);
            return document;
        } else {
            StringWriter out = new StringWriter();
            transformer.transform(domSource, new StreamResult(out));
            
            stringResponse=out.toString();
            dumpResourceAndAddToDebugTree(stringResponse);
            return stringResponse;
        }
    }
 */


 
}
