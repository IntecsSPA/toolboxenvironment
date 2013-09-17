package it.intecs.pisa.util.wsdl;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SOAPBoundedOperation extends BoundedOperation {
	private static final String ATTRIBUTE_SOAP_ACTION = "soapAction";
	private static final String NAMESPACE_SOAP = "http://schemas.xmlsoap.org/wsdl/soap/";
	
	private String SOAPAction=null;
	
	public SOAPBoundedOperation()
	{
		
	}
	
	public void createFromXMLSnippet(Element operationTag) throws WSDLException
	{
		NodeList children=null;
		Element soapActionTag=null;
		
		super.createFromXMLSnippet(operationTag);
		
		children=operationTag.getElementsByTagNameNS(NAMESPACE_SOAP, TAG_OPERATION);
		soapActionTag=((Element)children.item(0));
			
		this.SOAPAction=soapActionTag.getAttribute(ATTRIBUTE_SOAP_ACTION);
	}
	
	public String getSOAPAction() {
		return SOAPAction;
	}

	public void setSOAPAction(String action) {
		SOAPAction = action;
	}
}
