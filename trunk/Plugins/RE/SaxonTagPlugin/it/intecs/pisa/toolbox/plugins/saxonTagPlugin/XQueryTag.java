package it.intecs.pisa.toolbox.plugins.saxonTagPlugin;

import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.*;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import net.sf.saxon.AugmentedSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.QueryResult;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.trans.XPathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XQueryTag extends NativeTagExecutor {
   

    public XQueryTag() {
        tagName = "xQuery";
    }

    @Override
    public Object executeTag(org.w3c.dom.Element xquery) throws Exception {
        Document xml;
        String queryStringFile;
        String queryString;
        AugmentedSource xmlSource;
        List returnValues;
        String outputType;
        Object result = null;
        DOMUtil util;
        DocumentInfo docInfo;
        Document doc;
        
        util = new DOMUtil();
        doc = util.newDocument();
            
        outputType = xquery.getAttribute("outputType");

        Iterator children = DOMUtil.getChildren(xquery).iterator();
        xml = (Document) this.executeChildTag((Element) children.next());
        queryStringFile = (String) this.executeChildTag((Element) children.next());
        queryString=IOUtil.loadString(queryStringFile);
                
        Configuration config = new Configuration();
        StaticQueryContext staticContext = new StaticQueryContext(config);

        XQueryExpression exp = staticContext.compileQuery(queryString);

        xmlSource = AugmentedSource.makeAugmentedSource(new DOMSource(xml));
       
        DynamicQueryContext dynamicContext = new DynamicQueryContext(config);
        dynamicContext.setContextItem(config.buildDocument(xmlSource));

        if (outputType.equals("WRAPPED")==true ) {
            docInfo = QueryResult.wrap(exp.iterator(dynamicContext), config);

            QueryResult.serialize(docInfo, new DOMResult(doc), System.getProperties());
        
            return doc;
        } else  if (outputType.equals("ARRAY")==true ){
            returnValues = exp.evaluate(dynamicContext);
            result = Array.newInstance(Object.class, returnValues.size());

            for (int i = 0; i < returnValues.size(); i++) {
                  Array.set(result, i, getXalanObject(returnValues.get(i)));
            }

            return result;
        }
        else  return getXalanObject(exp.evaluateSingle(dynamicContext));
    }
    
    protected Object getXalanObject(Object item) throws XPathException
    {
         DOMUtil util;
         Properties props;
         Document doc;
         
        util=new DOMUtil();
        
          if (item instanceof java.lang.Boolean ||
                        item instanceof java.lang.Character ||
                        item instanceof java.lang.Double ||
                        item instanceof java.lang.Float ||
                        item instanceof java.lang.Short ||
                        item instanceof java.lang.String) {
                   return item;
                } else {
                 
                    doc=util.newDocument();
                    QueryResult.serialize((NodeInfo) item, new DOMResult(doc), System.getProperties());

                    return doc;
                }
    }
}
