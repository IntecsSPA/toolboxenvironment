/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.util;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.util.DOMUtil;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ScriptUtil {
    public static long createScriptFromLinkedList(LinkedList elements,IVariableStore variableStore) throws Exception
    {
        Document doc;
        DOMUtil util;

        util=new DOMUtil();
        doc=util.newDocument();

        Element sequenceEl;
        sequenceEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","sequence");
        doc.appendChild(sequenceEl);

        Hashtable variables = variableStore.getVariables();

        Set keySet = variables.keySet();
        String[] keyArray = (String[]) keySet.toArray(new String[0]);

        for(String key:keyArray)
        {
            Element setVariableEl;

            setVariableEl=getSetVariable(doc,key,variables.get(key));
            if(setVariableEl!=null)
                sequenceEl.appendChild(setVariableEl);
        }

        Iterator iter=elements.iterator();
        while(iter.hasNext())
        {
            Element child=(Element) iter.next();
            Element importedEl=(Element) doc.importNode(child, true);
            sequenceEl.appendChild(importedEl);
        }

        XMLResourcesPersistence xmlPers;
        xmlPers=XMLResourcesPersistence.getInstance();

        String idStr=xmlPers.storeXML(doc);
        return Long.valueOf(idStr);
    }

    protected static Element getSetVariable(Document doc, String key, Object value)
    {
        Element setVariableEl = doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","setVariable");
        setVariableEl.setAttribute("name", key);

        Element bodyEl=null;
        if(value instanceof Byte)
            bodyEl=getByteEl(doc,(Byte)value);
        if(value instanceof Integer)
            bodyEl=getIntegerEl(doc,(Integer)value);
        if(value instanceof Short)
            bodyEl=getShortEl(doc,(Short)value);
        if(value instanceof Long)
            bodyEl=getLongEl(doc,(Long)value);
        if(value instanceof Float)
            bodyEl=getFloatEl(doc,(Float)value);
        if(value instanceof Double)
            bodyEl=getDoubleEl(doc,(Double)value);
        if(value instanceof Character)
            bodyEl=getStringEl(doc,((Character)value).toString());
        if(value instanceof Boolean)
            bodyEl=getBooleaneEl(doc,(Boolean)value);
        if(value instanceof String)
            bodyEl=getStringEl(doc,(String)value);
        if(value instanceof Document)
            bodyEl=getDocumentEl(doc,(Document)value);

        if(bodyEl!=null)
            setVariableEl.appendChild(bodyEl);
        return setVariableEl;
    }

    private static Element getStringEl(Document doc, String string) {
        Element stringEl;

        stringEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","string");
        stringEl.setTextContent(string);
        return stringEl;
    }

    private static Element getByteEl(Document doc, Byte value) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "byte");
        valueEl.setAttribute("value",Byte.toString(value));
        return valueEl;
    }

    private static Element getIntegerEl(Document doc, Integer value) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "int");
        valueEl.setAttribute("value",Integer.toString(value));
        return valueEl;
    }

    private static Element getShortEl(Document doc, Short value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "short");
        valueEl.setAttribute("value",Short.toString(value));
        return valueEl;
    }

    private static Element getLongEl(Document doc, Long value) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "long");
        valueEl.setAttribute("value",Long.toString(value));
        return valueEl;
    }

    private static Element getFloatEl(Document doc, Float value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "float");
        valueEl.setAttribute("value",Float.toString(value));
        return valueEl;
    }

    private static Element getDoubleEl(Document doc, Double value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "double");
        valueEl.setAttribute("value",Double.toString(value));
        return valueEl;
    }

    private static Element getBooleaneEl(Document doc, Boolean value) {
         Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","literal");
        valueEl.setAttribute("type", "boolean");
        valueEl.setAttribute("value",Boolean.toString(value));
        return valueEl;
    }

    private static Element getDocumentEl(Document doc, Document document) {
        Element valueEl;

        valueEl=doc.createElementNS("http://pisa.intecs.it/mass/toolbox/xmlScript","xml");
        valueEl.setAttribute("textTag", "evaluate");
        valueEl.setAttribute("attributePrefix","x");

        Element importedEl=(Element) doc.importNode(document.getDocumentElement(), true);
        valueEl.appendChild(importedEl);
        return valueEl;
    }

    
}
