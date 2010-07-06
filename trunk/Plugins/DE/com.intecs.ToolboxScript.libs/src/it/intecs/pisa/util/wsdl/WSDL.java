/**
 * This class is used to parse a WSDL and provide all pieces
 */
package it.intecs.pisa.util.wsdl;

import it.intecs.pisa.util.DOMUtil;

import it.intecs.pisa.util.XSD;
import java.io.File;
import java.io.FileReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class WSDL {
	
	private static final String ATTRIBUTE_TARGET_NAMESPACE = "targetNamespace";

	private static final String TAG_MESSAGE = "message";

	private static final String TAG_BINDING = "binding";

	private static final String TAG_PORT_TYPE = "portType";

	private Document wsdlDoc=null;
	
	private PortTypes[] ports=null;
	private Binding[] bindings=null;
	private Message[] messages=null;
	
	private String targetNameSpace=null;


    protected String serviceURL;
	protected String name=null;
    protected Import[] imports=null;
    protected Hashtable<String,String> namespaces=null;
	private DOMUtil domutil;


	/**
	 * Default constructor
	 */
	public WSDL()
	{
		domutil=new DOMUtil();
        namespaces=new Hashtable<String,String>();
        imports=new Import[0];
	}
	
	/**
	 * This constructor fills the class with WSDL parameters
	 * @param wsdl
	 */
	public WSDL(File wsdl)
	{
		this();
		try {
			FileReader reader=new FileReader(wsdl);
			
			wsdlDoc=domutil.readerToDocument(reader);
			
			parseWSDL(wsdlDoc);
		} catch (Exception e) {
			wsdlDoc=null;
		}
		
	}

    public Document createWSDL()
    {
        Document wsdl;
        Element definitionsEl;
        Enumeration<String> en;
        String key;

        wsdl=domutil.newDocument();

        definitionsEl=wsdl.createElement("wsdl:definitions");
        definitionsEl.setAttribute("xmlns:wsdl", "http://schemas.xmlsoap.org/wsdl/");
        definitionsEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        definitionsEl.setAttribute("targetNamespace", targetNameSpace);

        en=this.namespaces.keys();
        while(en.hasMoreElements())
        {
            key=en.nextElement();
            definitionsEl.setAttribute("xmlns:"+key,namespaces.get(key));
        }


        if(name!=null)
            definitionsEl.setAttribute("name", name);

        wsdl.appendChild(definitionsEl);

        createImports(wsdl);
        createTypes(wsdl);
        createMessages(wsdl);
        createPortTypes(wsdl);
        createBindings(wsdl);
        createPorts(wsdl);
        createServices(wsdl);
        return wsdl;
    }

    void setMessages(Message[] msgs) {
        this.messages=msgs;
    }

    private void createBindings(Document wsdl) {
        for(Binding b:this.bindings)
        {
            b.appendToXML(wsdl);
        }
    }

    private void createImports(Document wsdl) {
        Element importEl;
        Element rootEl;
        Element typesEl;
        Element schemasEl;

        rootEl=wsdl.getDocumentElement();

        typesEl=wsdl.createElement("wsdl:types");
        rootEl.appendChild(typesEl);

        schemasEl=wsdl.createElement("xsd:schema");
        typesEl.appendChild(schemasEl);


        for(Import im:imports)
        {
            im.appendToXML(schemasEl);
        }

        
    }

    private void createMessages(Document wsdl) {
        for(Message m:this.messages)
        {
           m.appendToXML(wsdl);
        }

    }

    private void createPortTypes(Document wsdl) {
        for(PortTypes t:this.ports)
        {
           t.appendToXML(wsdl);
        }
    }

    private void createPorts(Document wsdl) {

    }

    private void createServices(Document wsdl) {
       Element rootEl;
       Element serviceEl;
       Element portEl;
       Element addressEl;

       rootEl=wsdl.getDocumentElement();

       serviceEl=wsdl.createElement("wsdl:service");
       serviceEl.setAttribute("name", "ToolboxService");
       rootEl.appendChild(serviceEl);

       portEl=wsdl.createElement("wsdl:port");
       portEl.setAttribute("name", "ToolboxServicePort");
       portEl.setAttribute("binding", "tns:ToolboxServiceSOAPBinding");
       serviceEl.appendChild(portEl);

       addressEl=wsdl.createElement("soap:address");
       addressEl.setAttribute("location", this.serviceURL);
       portEl.appendChild(addressEl);
    }

    private void createTypes(Document wsdl) {

    }
	
	/**
	 * This method parses the WSDL document and extract some informations
	 * @param wsdlDoc2
	 * @throws WSDLException 
	 */
	private void parseWSDL(Document wsdlDoc) throws WSDLException {
		int count=0;
		Element root=null;
		LinkedList children;
		Element tag=null;
		
		//removing previous information
		ports=null;
		bindings=null;
		
		root=wsdlDoc.getDocumentElement();
		
		//gettting target name space
		this.targetNameSpace=root.getAttribute(ATTRIBUTE_TARGET_NAMESPACE);
		
		//parsing message type
		children=DOMUtil.getChildrenByTagName(root, TAG_MESSAGE);
		
		count=children.size();
		messages=new Message[count];
		
		for(int i=0;i<count;i++)
		{
			tag=(Element)children.get(i);
			DOMUtil.copyNamespaces(root,tag);
			
			messages[i]=new Message();
			messages[i].createFromXMLSnippet(tag);
		}
		
		
		//Parsing portTypes
		children=DOMUtil.getChildrenByTagName(root, TAG_PORT_TYPE);
		
		count=children.size();
		ports=new PortTypes[count];
		
		for(int i=0;i<count;i++)
		{
			tag=(Element)children.get(i);
			DOMUtil.copyNamespaces(root,tag);
			
			ports[i]=new PortTypes(this);
			ports[i].createFromXMLSnippet(tag);
		}
		
		//		Parsing bindings
		children=DOMUtil.getChildrenByTagName(root, TAG_BINDING);
		
		count=children.size();
		bindings=new Binding[count];
		
		for(int i=0;i<count;i++)
		{
			tag=(Element)children.get(i);
			DOMUtil.copyNamespaces(root,tag);
			
			bindings[i]=new Binding(this);
			bindings[i].createFromXMLSnippet(tag);
		}

	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
	

	public PortTypes[] getPortTypes() {
		return ports;
	}

	public void setPortTypes(PortTypes[] ports) {
		this.ports = ports;
	}

	public Binding[] getBindings() {
		return bindings;
	}

	public void setBindings(Binding[] bindings) {
		this.bindings = bindings;
	}

	public Binding getBindingByName(String name)
	{
		for(Binding bind:this.bindings)
		{
			if(bind.getName().equals(name))
				return bind;
		}
		
		return null;
	}
	
	public PortTypes getPortTypeByID(String id)
	{
		for(PortTypes port:ports)
		{
			if(port.getID().equals(id))
				return port;
		}
		
		return null;
	}

	public String getTargetNameSpace() {
		return targetNameSpace;
	}

	public void setTargetNameSpace(String targetNameSpace) {
		this.targetNameSpace = targetNameSpace;

        namespaces=new Hashtable<String,String>();
        namespaces.put("tns", targetNameSpace);
       
	}
	
	public String getNameSpaceValue(String name)
	{
		return namespaces.get("xmlns:"+name);
	}

    public Hashtable<String,String> getNameSpaces()
    {
        return namespaces;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public void addImport(String namespace,String location)
    {
        Import newImport;
        Import[] newArray;
        int count=0;

        newImport=new Import();
        newImport.setNamespace(namespace);
        newImport.setSchemaLocation(location);

        if(this.imports!=null)
            count=imports.length;

        count++;

        newArray=new Import[count];

        for(int i=0;i<imports.length;i++)
            newArray[i]=imports[i];

        newArray[count-1]=newImport;
        imports=newArray;
    }
}
