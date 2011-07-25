/**
 * 
 */
package it.intecs.pisa.util.wsdl;

import it.intecs.pisa.util.DOMUtil;

import java.util.Hashtable;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Massimiliano
 *
 */
public class Binding {
	private static final String ATTRIBUTE_SOAP_ACTION = "soapAction";
	private static final String NAMESPACE_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
	private static final String TAG_OPERATION = "operation";
	private final static String ATTRIBUTE_NAME="name";
	private final static String ATTRIBUTE_TYPE="type";
	public static final String BINDINGDATA_SOAP_ACTION=ATTRIBUTE_SOAP_ACTION;
	public static final String WSSPOLICY="http://schemas.xmlsoap.org/ws/2004/09/policy";
	
	private WSDL wsdl;
	
	private String name;
	private String type;
	private BoundedOperation[] boundedOperations=null;
	
	private Element wsspolicy = null;

    public Binding()
    {

    }
	
	public Binding(WSDL wsdl)
	{
		this.wsdl=wsdl;
	}
	
	public void createFromXMLSnippet(Element bindingTag) throws WSDLException
	{
		String unresolvedType=null;
		String namespace=null;
		int boundedOperationsCount=0;
		int index=0;
		LinkedList children=null;
		Element ithTag=null;
		
		try
		{
			//setting binding name and type
			name=bindingTag.getAttribute(ATTRIBUTE_NAME);	
			unresolvedType=bindingTag.getAttribute(ATTRIBUTE_TYPE);
			namespace=wsdl.getNameSpaceValue(unresolvedType.substring(0,unresolvedType.indexOf(":")));
			type=namespace+unresolvedType.substring(unresolvedType.indexOf(":"));
					
			children=DOMUtil.getChildrenByTagName(bindingTag, TAG_OPERATION);
						
			boundedOperationsCount=children.size();
			boundedOperations=new BoundedOperation[boundedOperationsCount];
			
			for(int i=0;i<boundedOperationsCount;i++)
			{
				 ithTag = ((Element)children.get(i));
				
				 boundedOperations[i]=new SOAPBoundedOperation();
				 
				 try
				 {
					 boundedOperations[i].createFromXMLSnippet(ithTag);
				 }
				 catch(WSDLException wsdlExc)
				 {
					 //not a SOAP binding
					 boundedOperations[i]=new BoundedOperation();
					 boundedOperations[i].createFromXMLSnippet(ithTag);
				 }
			}
			
			//check WSS policy
			NodeList nl = bindingTag.getElementsByTagNameNS("Policy", WSSPOLICY);
			if (nl != null && nl.getLength() > 0){
				wsspolicy = (Element) nl.item(0);
			}
			
		}catch(Exception ecc)
		{
			throw new WSDLException("Error while creating Binding from XML");
		}
		
	}
	
	/*public PortTypes getPort()
	{
		PortTypes[] ports=null;
		
		for(PortTypes port:ports)
		{
			if(port.
		}
	}*/
	
	public String getID()
	{
		String targetNameSpace=null;
		
		targetNameSpace=wsdl.getTargetNameSpace();
			
		return targetNameSpace+":"+name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BoundedOperation[] getBoundedOperations() {
		return boundedOperations;
	}

	public void setBoundedOperations(BoundedOperation[] boundedOperations) {
		this.boundedOperations = boundedOperations;
	}

	public SOAPBoundedOperation getSOAPOperation(String SOAPaction)
	{
		SOAPBoundedOperation sbop=null;
		
		for(BoundedOperation bop:this.boundedOperations)
		{
			if(bop instanceof SOAPBoundedOperation)
			{
				sbop=(SOAPBoundedOperation)bop;
				
				if(sbop.getSOAPAction().equals(SOAPaction))
				{
						return sbop;
				}
			}
		}
		return null;
	}

    void appendToXML(Document wsdl) {
        Element rootEl;
        Element bindingEl;
        
        Element soapEl;
        

        rootEl=wsdl.getDocumentElement();

        bindingEl=wsdl.createElement("wsdl:binding");
        bindingEl.setAttribute("name", this.name);
        bindingEl.setAttribute("type", this.type);
        rootEl.appendChild(bindingEl);


        soapEl=wsdl.createElement("soap:binding");
        soapEl.setAttribute("style", "document");
        soapEl.setAttribute("transport", "http://schemas.xmlsoap.org/soap/http");
        bindingEl.appendChild(soapEl);

        for(BoundedOperation bop:this.boundedOperations)
        {
            bop.appendToXML(bindingEl);
        }
        
        //add WS-Security policy to this binding element
        if (wsspolicy != null){
        	Element elem = (Element) wsdl.importNode(wsspolicy, true);
        	bindingEl.appendChild(elem);
        }

    }
    
    public void setWSSPolicy(Element elem){
    	this.wsspolicy = elem;
    }
	
}
