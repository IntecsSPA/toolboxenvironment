
package it.intecs.pisa.util.saxon;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 *
 * @author Andrea Marongiu
 */
public class SaxonNamespaceResolver implements NamespaceContext{

    private Map<String, String> prefixToURInamespaces;
    private Map<String, Set> uriToPrefixnamespaces;

    SaxonNamespaceResolver(){

        this.prefixToURInamespaces=new HashMap<String, String>();
        this.uriToPrefixnamespaces=new HashMap<String, Set>();

    }


    public String getNamespaceURI(String namespacePrefix) {
        if (namespacePrefix == null)
            throw new IllegalArgumentException("prefix cannot be null");
        if (this.prefixToURInamespaces.containsKey(namespacePrefix))
            return (String) this.prefixToURInamespaces.get(namespacePrefix);
        else
            return XMLConstants.NULL_NS_URI;
    }

    public String getPrefix(String namespaceURI) {
        return (String) getPrefixes(namespaceURI).next();
    }

    public Iterator getPrefixes(String namespaceURI) {
        if (namespaceURI == null)
            throw new IllegalArgumentException("namespaceURI cannot be null");
        if (this.uriToPrefixnamespaces.containsKey(namespaceURI)) {
            return ((Set) this.uriToPrefixnamespaces.get(namespaceURI)).iterator();
        } else {
            return Collections.EMPTY_SET.iterator();
        }
    }

    public synchronized void addNamespace(String prefix, String namespaceURI){
        this.prefixToURInamespaces.put(prefix, namespaceURI);
        if (this.uriToPrefixnamespaces.containsKey(namespaceURI))
          (this.uriToPrefixnamespaces.get(namespaceURI)).add(prefix);
        else {
          Set<String> newSet = new HashSet<String>();
          newSet.add(prefix);
          uriToPrefixnamespaces.put(namespaceURI, newSet);
        }

    }

}


