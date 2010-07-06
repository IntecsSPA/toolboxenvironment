/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.wsdl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class Import {
    protected static final String ATTRIBUTE_NAMESPACE = "namespace";
    protected static final String ATTRIBUTE_SCHEMALOCATION = "schemaLocation";

    private String namespace;
    private String schemaLocation;


    public void createFromXMLSnippet(Element importTag) throws WSDLException
	{
		namespace=importTag.getAttribute(ATTRIBUTE_NAMESPACE);
        schemaLocation=importTag.getAttribute(ATTRIBUTE_SCHEMALOCATION);
	}

    void appendToXML(Element rootEl) {
        Element messageEl;
        Document wsdl;

        wsdl=rootEl.getOwnerDocument();
        messageEl=wsdl.createElement("xsd:import");
        messageEl.setAttribute(ATTRIBUTE_NAMESPACE, namespace);
        messageEl.setAttribute(ATTRIBUTE_SCHEMALOCATION, schemaLocation);

        rootEl.appendChild(messageEl);
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * @return the schemaLocation
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * @param schemaLocation the schemaLocation to set
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }


}
