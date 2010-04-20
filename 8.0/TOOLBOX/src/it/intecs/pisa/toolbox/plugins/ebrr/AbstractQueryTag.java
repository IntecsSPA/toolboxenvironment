/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.model.csw.AbstractQueryType;
import be.kzen.ergorr.model.util.JAXBUtil;
import it.intecs.pisa.toolbox.plugins.TagExecutor;
import it.intecs.pisa.util.DOMUtil;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class AbstractQueryTag extends TagExecutor{
    @Override
    public Object executeTag(org.w3c.dom.Element queryTag) throws Exception {
        Element xmlTag;
        Document doc;
        JAXBElement<? extends AbstractQueryType> query;
        String queryString;
        
        xmlTag=DOMUtil.getFirstChild(queryTag);
        doc= (Document) this.executeChildTag(xmlTag);
        doc.getDocumentElement().setAttribute("xmlns:rim", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
        doc.getDocumentElement().setAttribute("xmlns:wrs", "http://www.opengis.net/cat/wrs/1.0");
    
        queryString=DOMUtil.getDocumentAsString(doc);
        
        return JAXBUtil.getInstance().unmarshall(queryString);
    }
}
