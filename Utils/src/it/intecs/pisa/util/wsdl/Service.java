/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.wsdl;

import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to contains all information relative to the Service section of the WSDL
 * @author Massimiliano Fanciulli
 */
public class Service {
    private String name;
    private Map<String,String> port;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the ports
     */
    public Map<String,String> getPorts() {
        return port;
    }

    /**
     * @param ports the ports to set
     */
    public void setPort(Map<String,String> port) {
        this.port = port;
    }

    void appendToXML(Document wsdl) {
        Element serviceEl;
        Element rootEl;

        rootEl=wsdl.getDocumentElement();
        serviceEl=wsdl.createElement("wsdl:service");
        serviceEl.setAttribute("name", name);
        rootEl.appendChild(serviceEl);

        Element portEl=wsdl.createElement("wsdl:port");
        portEl.setAttribute("binding", port.get("binding"));
        portEl.setAttribute("name", port.get("name"));
        serviceEl.appendChild(portEl);

        Element soapAddressEl=wsdl.createElement("soap:address");
        soapAddressEl.setAttribute("location", port.get("location"));
        portEl.appendChild(soapAddressEl);
    }
}
