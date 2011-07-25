/**
 * This class is used to parse a WSDL and provide all pieces
 */
package it.intecs.pisa.util.wsdl;

import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class WSDL {

    private static final String ATTRIBUTE_TARGET_NAMESPACE = "targetNamespace";
    
    private static final String TAG_IMPORT = "import";
    private static final String TAG_SCHEMA = "schema";
    private static final String TAG_TYPES = "types";
    private static final String ATTRIBUTE_SCHEMA_LOCATION = "schemaLocation";
    private static final String ATTRIBUTE_NAMESPACE="namespace";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_BINDING = "binding";
    private static final String TAG_PORT_TYPE = "portType";
    private Document wsdlDoc = null;
    private PortType[] ports = null;
    private PortType[] callbackports = null;
    private Binding[] callbackBindings = null;
    private Binding[] bindings = null;
    private Message[] messages = null;
    private Service[] services = null;
    private Service[] callbackServices = null;
    private String targetNameSpace = null;
    protected String serviceURL;
    protected String name = null;
    protected Import[] imports = null;
    protected Hashtable<String, String> namespaces = null;
    private DOMUtil domutil;
    private String [] importLocations;
    private String [] importNamespaces;

    /**
     * Default constructor
     */
    public WSDL() {
        domutil = new DOMUtil();
        namespaces = new Hashtable<String, String>();
        imports = new Import[0];
    }

    /**
     * This constructor fills the class with WSDL parameters
     * @param wsdl
     */
    public WSDL(File wsdl) {
        this();
        try {
            FileReader reader = new FileReader(wsdl);

            wsdlDoc = domutil.readerToDocument(reader);

            parseWSDL(wsdlDoc);
        } catch (Exception e) {
            wsdlDoc = null;
        }

    }
    
    /**
     * This constructor fills the class with WSDL parameters
     * @param wsdl InputStream
     */
    public WSDL(InputStream wsdlInputStream) {
        this();
        try {
            wsdlDoc = domutil.inputStreamToDocument(wsdlInputStream);

            parseWSDL(wsdlDoc);
        } catch (Exception e) {
            wsdlDoc = null;
        }

    }
    
        /**
     * This constructor fills the class with WSDL parameters
     * @param wsdlURL
     */
    public WSDL(URL wsdlURL) {
        this();
        try {
            InputStreamReader reader = new InputStreamReader(wsdlURL.openStream());
            wsdlDoc = domutil.readerToDocument(reader);
            parseWSDL(wsdlDoc);
        } catch (Exception e) {
            wsdlDoc = null;
        }

    }

    public Document createWSDL() {
        Document wsdl;
        Element definitionsEl;
        Enumeration<String> en;
        String key;

        wsdl = domutil.newDocument();

        definitionsEl = wsdl.createElement("wsdl:definitions");
        definitionsEl.setAttribute("xmlns:wsdl", "http://schemas.xmlsoap.org/wsdl/");
        definitionsEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        definitionsEl.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        definitionsEl.setAttribute("xmlns:plnk", "http://schemas.xmlsoap.org/ws/2003/05/partner-link/");
        definitionsEl.setAttribute("xmlns:tns", targetNameSpace);
        definitionsEl.setAttribute("targetNamespace", targetNameSpace);

        en = this.namespaces.keys();
        while (en.hasMoreElements()) {
            key = en.nextElement();
            definitionsEl.setAttribute("xmlns:" + key, namespaces.get(key));
        }


        if (name != null) {
            definitionsEl.setAttribute("name", name);
        }

        wsdl.appendChild(definitionsEl);

        createImports(wsdl);
        createTypes(wsdl);
        createMessages(wsdl);
        createPortTypes(wsdl);
        createCallbackPortTypes(wsdl);
        createBindings(wsdl);
        createCallbackBindings(wsdl);
        createPorts(wsdl);
        createServices(wsdl);
        createCallbackServces(wsdl);
        createPartnerLink(wsdl);
        return wsdl;
    }

    public void setMessages(Message[] msgs) {
        this.messages = msgs;
    }

    private void createBindings(Document wsdl) {
        for (Binding b : this.bindings) {
            b.appendToXML(wsdl);
        }
    }

    private void createCallbackBindings(Document wsdl) {
        if (this.callbackBindings != null) {
            for (Binding b : this.callbackBindings) {
                b.appendToXML(wsdl);
            }
        }
    }

    private void createImports(Document wsdl) {
        Element importEl;
        Element rootEl;
        Element typesEl;
        Element schemasEl;

        rootEl = wsdl.getDocumentElement();

        typesEl = wsdl.createElement("wsdl:types");
        rootEl.appendChild(typesEl);

        schemasEl = wsdl.createElement("xsd:schema");
        typesEl.appendChild(schemasEl);


        for (Import im : imports) {
            im.appendToXML(schemasEl);
        }


    }

    private void createMessages(Document wsdl) {
        for (Message m : this.messages) {
            m.appendToXML(wsdl);
        }

    }

    private void createPortTypes(Document wsdl) {
        for (PortType t : this.ports) {
            t.appendToXML(wsdl);
        }
    }

    private void createCallbackPortTypes(Document wsdl) {
        if (callbackports != null) {
            for (PortType t : this.callbackports) {
                t.appendToXML(wsdl);
            }
        }
    }

    private void createPorts(Document wsdl) {
    }

    private void createServices(Document wsdl) {
        for (Service service : services) {
            service.appendToXML(wsdl);
        }
    }

    private void createCallbackServces(Document wsdl) {
        if (callbackServices != null) {
            for (Service service : callbackServices) {
                service.appendToXML(wsdl);
            }
        }
    }

    private void createTypes(Document wsdl) {
    }

    /**
     * This method parses the WSDL document and extract some informations
     * @param wsdlDoc2
     * @throws WSDLException
     */
    private void parseWSDL(Document wsdlDoc) throws WSDLException {
        int count = 0;
        Element root = null;
        NodeList children;
        Element tag = null;

        //removing previous information
        ports = null;
        bindings = null;

        root = wsdlDoc.getDocumentElement();

        //gettting target name space
        this.targetNameSpace = root.getAttribute(ATTRIBUTE_TARGET_NAMESPACE);
        
         //parsing import schema locations
        children = root.getElementsByTagNameNS("*", TAG_IMPORT);
        count = children.getLength();
        if(count >0){
          importLocations = new String[count];
          importNamespaces = new String[count];
          for (int i = 0; i < count; i++) {
                    tag = (Element) children.item(i);
                    importLocations[i]=tag.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);
                    importNamespaces[i]=tag.getAttribute(ATTRIBUTE_NAMESPACE);
          }
        }
        
       
        
        
        //parsing message type
        
        
            children = root.getElementsByTagNameNS("*", TAG_MESSAGE);

            count = children.getLength();
            messages = new Message[count];

            for (int i = 0; i < count; i++) {
                tag = (Element) children.item(i);
                DOMUtil.copyNamespaces(root, tag);

                messages[i] = new Message();
                messages[i].createFromXMLSnippet(tag);
            }
      
        
        
        //Parsing portTypes
        children = root.getElementsByTagNameNS("*", TAG_PORT_TYPE);

        count = children.getLength();
        ports = new PortType[count];

        for (int i = 0; i < count; i++) {
            tag = (Element) children.item(i);
            DOMUtil.copyNamespaces(root, tag);

            ports[i] = new PortType(this);
            ports[i].createFromXMLSnippet(tag);
        }

        //		Parsing bindings
        children = root.getElementsByTagNameNS("*", TAG_BINDING); 

        count = children.getLength();
        bindings = new Binding[count];

        for (int i = 0; i < count; i++) {
            tag = (Element) children.item(i);
            DOMUtil.copyNamespaces(root, tag);

            bindings[i] = new Binding(this);
            bindings[i].createFromXMLSnippet(tag);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PortType[] getPortTypes() {
        return ports;
    }

    public void setPortTypes(PortType[] ports) {
        this.ports = ports;
    }

    public Binding[] getBindings() {
        return bindings;
    }

    public void setBindings(Binding[] bindings) {
        this.bindings = bindings;
    }

    public Binding getBindingByName(String name) {
        for (Binding bind : this.bindings) {
            if (bind.getName().equals(name)) {
                return bind;
            }
        }

        return null;
    }

    public PortType getPortTypeByID(String id) {
        for (PortType port : ports) {
            if (port.getID().equals(id)) {
                return port;
            }
        }

        return null;
    }

    public String getTargetNameSpace() {
        return targetNameSpace;
    }

    public void setTargetNameSpace(String targetNameSpace) {
        this.targetNameSpace = targetNameSpace;

        namespaces = new Hashtable<String, String>();
        namespaces.put("tns", targetNameSpace);

    }
    

    public String getNameSpaceValue(String name) {
        return namespaces.get("xmlns:" + name);
    }

    public Hashtable<String, String> getNameSpaces() {
        return namespaces;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public void addImport(String namespace, String location) {
        Import newImport;
        Import[] newArray;
        int count = 0;

        newImport = new Import();
        newImport.setNamespace(namespace);
        newImport.setSchemaLocation(location);

        if (this.imports != null) {
            count = imports.length;
        }

        count++;

        newArray = new Import[count];

        for (int i = 0; i < imports.length; i++) {
            newArray[i] = imports[i];
        }

        newArray[count - 1] = newImport;
        imports = newArray;
    }

    public void setCallbackPortTypes(PortType[] ports) {
        callbackports = ports;
    }

    public void setCallbackBindings(Binding[] cbindings) {
        callbackBindings = cbindings;
    }

    public PortType[] getCallbackPortTypes() {
        return this.callbackports;
    }

    public void setService(Service[] wsdlService) {
        this.services=wsdlService;
    }

    public void setCallbackService(Service[] service) {
        this.callbackServices=service;
    }

    private void createPartnerLink(Document wsdl) {
        if(this.callbackports!=null)
        {
            Element plinkRootEl=wsdl.createElement("plnk:partnerLinkType");
            plinkRootEl.setAttribute("name",name);

            wsdl.getDocumentElement().appendChild(plinkRootEl);

            Element providerRoleEl=wsdl.createElement("plnk:role");
            providerRoleEl.setAttribute("name", name+"ServiceProvider");
            plinkRootEl.appendChild(providerRoleEl);

            Element providerPortTypeEl=wsdl.createElement("plnk:portType");
            providerPortTypeEl.setAttribute("name", "tns:"+name);
            providerRoleEl.appendChild(providerPortTypeEl);


            providerRoleEl=wsdl.createElement("plnk:role");
            providerRoleEl.setAttribute("name", name+"ServiceRequester");
            plinkRootEl.appendChild(providerRoleEl);

            providerPortTypeEl=wsdl.createElement("plnk:portType");
            providerPortTypeEl.setAttribute("name", "tns:"+name+"Callback");
            providerRoleEl.appendChild(providerPortTypeEl);
        }
    }

    /**
     * @return the importLocations
     */
    public String [] getImportLocations() {
        return importLocations;
    }

    /**
     * @return the importNamespaces
     */
    public String [] getImportNamespaces() {
        return importNamespaces;
    }
}
