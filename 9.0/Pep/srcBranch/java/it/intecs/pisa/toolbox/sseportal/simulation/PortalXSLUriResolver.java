/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.sseportal.simulation;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;

/**
 *
 * @author Massimiliano
 */
public class PortalXSLUriResolver implements URIResolver {

    protected String name;

    public PortalXSLUriResolver(String serviceName) {
        name = serviceName;
    }

    public Source resolve(String arg0, String arg1) throws TransformerException {
        File serviceRoot = Toolbox.getInstance().getServiceRoot(name);
        File resource = null;
        DOMUtil util;

        try {
            util = new DOMUtil();

            if (arg0.contains("sse_common.xsl")) {
                resource = new File(serviceRoot, "AdditionalResources/SSEPortalXSL/sse_common.xsl");
            } else if (arg0.contains("massCatalogue.xsl")) {
                resource = new File(serviceRoot, "AdditionalResources/SSEPortalXSL/massCatalogue.xsl");
            }

            if (resource != null) {
                return new DOMSource(util.fileToDocument(resource));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
