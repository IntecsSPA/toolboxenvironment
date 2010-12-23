/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.pluginscore.TagExecutor;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class DescribeRecordTag extends TagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element connEl) throws Exception {
        Document xmlDoc;
        DOMUtil util;
        Element rootEl;
        Element schemaComponentEl;
        File rimFile;
        Toolbox tbx;

        try
        {
            util = new DOMUtil();
            xmlDoc=util.newDocument();

            rootEl=xmlDoc.createElement("DescribeRecordResponse");
            rootEl.setAttribute("xmlns", "http://www.opengis.net/cat/csw/2.0.2");

            xmlDoc.appendChild(rootEl);

            schemaComponentEl=xmlDoc.createElement("SchemaComponent");
            schemaComponentEl.setAttribute("schemaLanguage", "http://www.w3c.org/2001/XMLSchema");
            schemaComponentEl.setAttribute("targetNamespace", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0");
            rootEl.appendChild(schemaComponentEl);

            tbx=Toolbox.getInstance();

            Document rimDoc;
            rimFile=new File(tbx.getRootDir(),"WEB-INF/plugins/ebRRPlugin/resources/rim.xsd");
            rimDoc=util.fileToDocument(rimFile);

            Element rimEl=(Element) xmlDoc.importNode(rimDoc.getDocumentElement(),true);
            schemaComponentEl.appendChild(rimEl);

            return xmlDoc;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
       
    }
}
