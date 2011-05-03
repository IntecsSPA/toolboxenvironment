/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.soap;

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import org.w3c.dom.Node;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class SOAPNamespacePrefixResolver implements  PrefixResolver{

    public String getNamespaceForPrefix(String string) {
        if(string.equals("csw"))
            return "http://www.opengis.net/cat/csw/2.0.2";
        else if(string.equals("dcelem"))
            return "http://purl.org/dc/elements/1.1/";
        else if(string.equals("soap-env"))
            return "http://schemas.xmlsoap.org/soap/envelope/";
        else if(string.equals("sos"))
            return "http://www.opengis.net/sos/1.0";        
        else return "";
    }

    public String getNamespaceForPrefix(String string, Node node) {
        return "";
    }

    public String getBaseIdentifier() {
        return "";
    }

    public boolean handlesNullPrefixes() {
        return false;
    }

}
