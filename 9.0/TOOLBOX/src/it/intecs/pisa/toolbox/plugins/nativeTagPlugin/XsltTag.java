package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.saxon.SaxonURIResolver;
import it.intecs.pisa.util.saxon.SaxonXSLT;
import it.intecs.pisa.util.saxon.SaxonXSLTParameter;
import java.io.File;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class XsltTag extends NativeTagExecutor {
   // protected String tagName="xslt";
    @Override
    public Object executeTag(org.w3c.dom.Element xslt) throws Exception {
        DOMUtil domUtil = new DOMUtil();
        String stringResponse;
        SaxonXSLT saxonUtil=null;
        PipedInputStream pipeInput = null;
        SaxonURIResolver uriResolver=null;
        Object xsltRef;
        ArrayList<SaxonXSLTParameter> parameters= new ArrayList();

        Iterator children = DOMUtil.getChildren(xslt).iterator();
        Document docXml=(Document) this.executeChildTag((Element) children.next());
        SAXSource docSource = new SAXSource(new InputSource(
                                    DOMUtil.getDocumentAsInputStream(docXml)));

        
        xsltRef=executeChildTag((Element) children.next());

        SAXSource xsltDoc;
        if(xsltRef instanceof String){
            xsltDoc= new SAXSource(new InputSource((String)xsltRef));
            String xsltPath=((String)xsltRef).substring(0,
                    ((String)xsltRef).lastIndexOf(System.getProperty( "file.separator")));
            uriResolver = new SaxonURIResolver(new File(xsltPath));
            saxonUtil=new SaxonXSLT(uriResolver);
        }
        else if(xsltRef instanceof Document){
            xsltDoc= new SAXSource(new InputSource(
                        DOMUtil.getDocumentAsInputStream((Document)xsltRef)));
            saxonUtil=new SaxonXSLT();
        }
        else throw new Exception("XSLT reference type not supported");

        Element child;
        while (children.hasNext()) {
            child = (Element) children.next();
            parameters.add(new SaxonXSLTParameter(child.getAttribute(NAME),
                        executeChildTag(DOMUtil.getFirstChild(child))));
        }


        if (getBool(xslt.getAttribute(XML_OUTPUT))) {
            pipeInput = saxonUtil.saxonXSLPipeTransform(docSource, xsltDoc, parameters,"xml");
            Document document = domUtil.inputStreamToDocument(pipeInput);
            dumpResourceAndAddToDebugTree(document);
            return document;
        } else {
            pipeInput = saxonUtil.saxonXSLPipeTransform(docSource, xsltDoc, parameters,"text");
            stringResponse=IOUtil.inputToString(pipeInput);
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


 /*public class XsltTag extends NativeTagExecutor {
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
    }*/
}
