/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class SOAPUtil {
    public static final String FAULT_NAMESPACE = "http://schemas.xmlsoap.org/soap/envelope/";
    public static final String FAULT_ROOT_NODE = "Fault";
    public static boolean isSOAPFault(Document doc)
    {
        Element rootEl;
        String namespace;
        String name;

        try
        {
            rootEl=doc.getDocumentElement();
            namespace=rootEl.getNamespaceURI();
            name=rootEl.getNodeName();

            if(namespace.equals(FAULT_NAMESPACE) && name.endsWith(FAULT_ROOT_NODE))
                return true;
            else return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }
}
