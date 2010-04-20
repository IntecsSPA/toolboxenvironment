/**
 * 
 */
package it.intecs.pisa.util.wsdl;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class Operation {
	private static final String ATTRIBUTE_MESSAGE = "message";
	private static final String TAG_INPUT = "input";
	private static final String TAG_OUTPUT = "output";
	private static final String ATTRIBUTE_NAME = "name";
	
	private String name;
	private String inputName=null;
	private String outputName=null;
	private String inputNameType=null;
	private String outputNameType=null;
	private String inputNameNameSpace=null;
	private String outputNameNameSpace=null;
	
	
	private DOMUtil domutil=null;
	private Document myXMLsnippet=null;
	
	public Operation()
	{
		domutil=new DOMUtil();
	}
	
	public void createFromXMLSnippet(Element operationTag) throws WSDLException
	{
		Element rootImportedNode=null;
		Element rootEl=null;
		Element inputEl=null;
		Element outputEl=null;
		String messageString=null;
		int index=-1;
				
		//copying the xml snippet
		myXMLsnippet=domutil.newDocument();
		
		rootImportedNode=(Element)myXMLsnippet.importNode(operationTag, true);
		myXMLsnippet.appendChild(rootImportedNode);
		rootEl=myXMLsnippet.getDocumentElement();
		
		//setting port name
		name=rootEl.getAttribute(ATTRIBUTE_NAME);
		if(name==null || name.equals(""))
			throw new WSDLException("No operation name");
		
		//parsing input
		inputEl=DOMUtil.getChildByTagName(operationTag, TAG_INPUT);
		inputName=inputEl.getAttribute(ATTRIBUTE_NAME);	
		
		messageString=inputEl.getAttribute(ATTRIBUTE_MESSAGE);
		index=messageString.indexOf(":");
		
		if(index>-1)
		{
			inputNameType=messageString.substring(index+1);
			inputNameNameSpace=rootEl.getAttribute("xmlns:"+messageString.substring(0,index));
		}
		else
		{
			inputNameType=messageString;
			inputNameNameSpace="";
		}
		
		outputEl=DOMUtil.getChildByTagName(operationTag, TAG_OUTPUT);
		outputName=inputEl.getAttribute(ATTRIBUTE_NAME);	
		
		messageString=outputEl.getAttribute(ATTRIBUTE_MESSAGE);
		index=messageString.indexOf(":");
		
		if(index>-1)
		{
			outputNameType=messageString.substring(index+1);
			outputNameNameSpace=rootEl.getAttribute("xmlns:"+messageString.substring(0,index));
		}
		else
		{
			outputNameType=messageString;
			outputNameNameSpace="";
		}
		
	}
	
	public Element getXML()
	{
		if(myXMLsnippet!=null)
			return (Element)myXMLsnippet.getDocumentElement().cloneNode(true);
		else return null;
	}
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public String getInputNameNameSpace() {
		return inputNameNameSpace;
	}

	public void setInputNameNameSpace(String inputNameNameSpace) {
		this.inputNameNameSpace = inputNameNameSpace;
	}

	public String getInputNameType() {
		return inputNameType;
	}

	public void setInputNameType(String inputNameType) {
		this.inputNameType = inputNameType;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getOutputNameNameSpace() {
		return outputNameNameSpace;
	}

	public void setOutputNameNameSpace(String outputNameNameSpace) {
		this.outputNameNameSpace = outputNameNameSpace;
	}

	public String getOutputNameType() {
		return outputNameType;
	}

	public void setOutputNameType(String outputNameType) {
		this.outputNameType = outputNameType;
	}

    void appendToXML(Element portEl) {
        Document doc;
        Element operEl;
        Element inputEl;
        Element outputEl;

        doc=portEl.getOwnerDocument();
        operEl=doc.createElement("wsdl:operation");
        operEl.setAttribute("name", this.name);
        portEl.appendChild(operEl);

        inputEl=doc.createElement("wsdl:input");
        inputEl.setAttribute("message", "tns:"+this.inputNameType);
        operEl.appendChild(inputEl);

        outputEl=doc.createElement("wsdl:output");
        outputEl.setAttribute("message", "tns:"+this.outputNameType);
        operEl.appendChild(outputEl);
    }
	
}
