



package it.intecs.pisa.saxon;

import javax.xml.namespace.QName;
import javax.xml.xpath.*;

/**
 *
 * @author Andrea Marongiu
 */
public class SaxonXPathFunctionResolver {



    /**
     * This class serves as a function resolver. The only function used is f:toCentimetres.
     * @param qName the name of the variable required
     * @return the current value of the variable
     */

    public XPathFunction resolveFunction(QName qName, int arity) {
        if (qName.getLocalPart().equals("toCentimetres")) {
            return null;
        } else {
            return null;
        }
    }

}
