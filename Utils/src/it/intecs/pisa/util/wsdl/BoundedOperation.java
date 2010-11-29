/**
 * 
 */
package it.intecs.pisa.util.wsdl;



import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class BoundedOperation {
	protected static final String TAG_OPERATION = "operation";
	protected static final String ATTRIBUTE_NAME = "name";
	
	private String name=null;
    private String soapAction;
    private Boolean isAsynchronous = false;
    private Boolean isCallBack = false;


	public BoundedOperation()
	{
		
	}
	
	public void createFromXMLSnippet(Element operationTag) throws WSDLException
	{
		name=operationTag.getAttribute(ATTRIBUTE_NAME);
		if(name==null || name.equals(""))
			throw new WSDLException("No binding name");
	}

	public void setCallBack() {
		isCallBack = true;
	}

	public void setSynchronous() {
		isAsynchronous = false;
	}

	public void setAsynchronous() {
		isAsynchronous = true;
	}


    public Boolean isAsyncronous() {
		return isAsynchronous;
	}


    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public void setSoapAction(String action)
    {
        this.soapAction=action;
    }

    public String getSoapAction() {
        return this.soapAction;
    }

    void appendToXML(Element bindingEl) {
        Element soapActionEl;
        Element bopEl;
        Element inputEl;
        Element outputEl;
        Element bodyEl;
        Element headerEl;
        Document wsdl;

        wsdl=bindingEl.getOwnerDocument();

        bopEl=wsdl.createElement("wsdl:operation");
        bopEl.setAttribute("name", this.name);
        bindingEl.appendChild(bopEl);

        soapActionEl=wsdl.createElement("soap:operation");
        soapActionEl.setAttribute("soapAction",this.soapAction);
        bopEl.appendChild(soapActionEl);

        inputEl=wsdl.createElement("wsdl:input");
        bopEl.appendChild(inputEl);

        if (isAsynchronous == true) {
            headerEl = wsdl.createElement("soap:header");
            headerEl.setAttribute("message", "tns:StartHeader");
            headerEl.setAttribute("part", "MessageID");
            headerEl.setAttribute("use", "literal");
            inputEl.appendChild(headerEl);
        }

        if (isCallBack == true) {
            headerEl = wsdl.createElement("soap:header");
            headerEl.setAttribute("message", "tns:ContinueHeader");
            headerEl.setAttribute("part", "RelatesTo");
            headerEl.setAttribute("use", "literal");
            inputEl.appendChild(headerEl);
        }


        bodyEl=wsdl.createElement("soap:body");
        bodyEl.setAttribute("use", "literal");
        inputEl.appendChild(bodyEl);
        
        outputEl=wsdl.createElement("wsdl:output");
        bopEl.appendChild(outputEl);

        bodyEl=wsdl.createElement("soap:body");
        bodyEl.setAttribute("use", "literal");
        outputEl.appendChild(bodyEl);
    }


}
