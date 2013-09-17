package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.saxon.SaxonDocument;

import java.util.Iterator;

import javax.xml.xpath.XPathConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class XPathTag extends NativeTagExecutor {
    @Override
    public Object executeTag(org.w3c.dom.Element xPath) throws Exception {
        String outputType="";
        Iterator children = DOMUtil.getChildren(xPath).iterator();
        Document document = (Document) this.executeChildTag((Element) children.next());
        String xPathString = new String((String) executeChildTag((Element) children.next()));
        Element firstEl;
        Document newDoc=null;
        DOMUtil util;
        String[] outputAsStringArray;
      
        outputType=xPath.getAttribute("outputType");

        Element xPathDocElement=xPath.getOwnerDocument().getDocumentElement();
        Element namespaceElements []= {xPathDocElement,xPath};
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
        
       
        
        if(outputType.equals("xml")) {
           NodeList matchedNodes=(NodeList) saxonDoc.evaluatePath(xPathString, XPathConstants.NODESET);
           if (matchedNodes != null){

               firstEl=(Element)matchedNodes.item(0);
               if(firstEl == null)
                return null;
                else{
                 util=new DOMUtil();
                 newDoc=util.newDocument();
                 newDoc.appendChild(newDoc.importNode(firstEl,true));
                 return newDoc;
               }
           }else
             return null;
            
        }
        else if(outputType.equals("array"))
        {
         NodeList matchedNodes=(NodeList) saxonDoc.evaluatePath(xPathString, XPathConstants.NODESET);
         //returning a string array.. shall be checked if strings are selected by xpath
         int resultCount;
         
         resultCount=matchedNodes.getLength();

         outputAsStringArray=new String[resultCount];
         for(int i=0;i<resultCount;i++)
            outputAsStringArray[i]=matchedNodes.item(i).getTextContent();

         return outputAsStringArray;
        }
        else{
            String res=(String)saxonDoc.evaluatePath(xPathString, XPathConstants.STRING);
           
            return res;

        }
        
          
        
    }
}



/*public class XPathTag extends NativeTagExecutor {
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
    }*/
