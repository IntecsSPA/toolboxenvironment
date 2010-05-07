package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XPathTag extends NativeTagExecutor {
    @Override
    public Object executeTag(org.w3c.dom.Element xPath) throws Exception {
        String outputType="";
        Iterator children = DOMUtil.getChildren(xPath).iterator();
        Document document = (Document) this.executeChildTag((Element) children.next());
        String xPathString = new String((String) executeChildTag((Element) children.next()));
        XObject result;
        Element firstEl;
        Document newDoc=null;
        DOMUtil util;
        String[] outputAsStringArray;

        outputType=xPath.getAttribute("outputType");
       
        if (xPath.getAttributes().getLength() > 0) {
            result = XPathAPI.eval(document, xPathString, xPath);
        } else {
            result = XPathAPI.eval(document, xPathString,
                    document.getDocumentElement());
        }
        
        if(outputType.equals("xml"))
        {
            util=new DOMUtil();
            
            firstEl=(Element) result.nodelist().item(0);
            if(firstEl == null)
                return null;
             else{ 
                 newDoc=util.newDocument();
                 newDoc.appendChild(newDoc.importNode(firstEl,true));
                 return newDoc;
               }
        }
        else if(outputType.equals("array"))
        {
            //returning a string array.. shall be checked if strings are selected by xpath
            int resultCount;
            NodeList list;

            list=result.nodelist();
            resultCount=list.getLength();

            outputAsStringArray=new String[resultCount];
            for(int i=0;i<resultCount;i++)
                outputAsStringArray[i]=list.item(i).getNodeValue();

            return outputAsStringArray;
        }
        else
        {
            return result.str();
        }
    }
}
