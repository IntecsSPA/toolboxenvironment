/**
 * 
 */
package it.intecs.pisa.util.wsdl;

import java.util.LinkedList;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class PortType {
	private static final String TAG_OPERATION = "operation";
	private static final String ATTRIBUTE_NAME = "name";

	private String name=null;
	private DOMUtil domutil=null;
	private WSDL wsdl=null;
	
	private Operation operations[]=null;

	private Document myXMLsnippet=null;
	
	
	public PortType(WSDL wsdl)
	{
        super();
		this.wsdl=wsdl;
		
	}

    public PortType() {
        domutil=new DOMUtil();
    }
	
	/**
	 * This method creates a port type class, parsing its information from a xml snippet
	 * @param portTypeTag
	 * @return
	 * @throws Exception 
	 */
	public void createFromXMLSnippet(Element portTypeTag) throws WSDLException
	{
		LinkedList portList=null;
		int operationCount=0;
		Element rootImportedNode=null;
		Element rootEl=null;
		
		//copying the xml snippet
		myXMLsnippet=domutil.newDocument();
		rootImportedNode=(Element)myXMLsnippet.importNode(portTypeTag, true);
		
		myXMLsnippet.appendChild(rootImportedNode);
		rootEl=myXMLsnippet.getDocumentElement();
		
		//setting port name
		name=rootEl.getAttribute(ATTRIBUTE_NAME);
		if(name==null || name.equals(""))
			throw new WSDLException("No port type name");
		
		//parsing operations
		portList=DOMUtil.getChildrenByTagName(rootEl,TAG_OPERATION);
		operationCount=portList.size();
		
		operations=new Operation[operationCount];
		
		for(int i=0; i< operationCount;i++)
		{
			Element operationTag=(Element)portList.get(i);
		
			DOMUtil.copyNamespaces(portTypeTag,operationTag);
			
			operations[i]=new Operation();
			operations[i].createFromXMLSnippet(operationTag);
			
			rootEl.removeChild(operationTag);
			
		}
	}
	
	public Element getXML()
	{
		Element root=null;
		Element oper=null;
		Element importedNode=null;
		
		if(myXMLsnippet!=null)
			{
				root=(Element)myXMLsnippet.getDocumentElement().cloneNode(true);
				
				for(int i=0;i<operations.length;i++)
				{
					oper=operations[i].getXML();
					importedNode=(Element)root.getOwnerDocument().importNode(oper,true);
					root.appendChild(importedNode);
				}
				
				return root;
			}
		else return null;
	}
	
	public String getName() {
		return name;
	}

    public void setName(String name)
    {
        this.name=name;
    }

	public Operation[] getOperations() {
		return operations;
	}

    public void setOperations(Operation[] ops)
    {
        operations=ops;
    }
	
	public Operation getOperationByName(String name)
	{
		for(Operation op:operations)
		{
			if(op.getName().equals(name))
				return op;
		}
		
		return null;
	}
	
	public String getID()
	{
		String targetNameSpace=null;
		
		targetNameSpace=wsdl.getTargetNameSpace();
			
		return targetNameSpace+":"+name;
	}

    void appendToXML(Document wsdl) {
        Element portEl;
        Element rootEl;

        rootEl=wsdl.getDocumentElement();
        portEl=wsdl.createElement("wsdl:portType");
        portEl.setAttribute("name", this.name);
        rootEl.appendChild(portEl);

        for(Operation op:this.operations)
        {
            op.appendToXML(portEl);
        }
    }
}
