package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VariablesListMessage extends StructuredMessage {
	private static final String COMMAND_NAME = "variableslist";

	private static final String TAG_VARIABLE = "variable";

	private static final String TAG_NAME = "name";

	private static final String TAG_TYPE = "type";

	protected String[] variables;

	public VariablesListMessage() {

	}
        
        public VariablesListMessage(String[] varList) {
            setVariables( varList) ;
	}

	public void setVariables(String[] varList) {
		this.variables = varList;
	}

	public String[] getVariables() {
		return variables;
	}

	@Override
	public Document getDoc() {
		DOMUtil util;
		Element rootEl;
		Element variableEl;

		util = new DOMUtil();
		doc = util.newDocument();

		rootEl = doc.createElement(COMMAND_NAME);
		doc.appendChild(rootEl);

		for (String variable : variables) {
			variableEl = doc.createElement(TAG_VARIABLE);
			variableEl.setAttribute(TAG_NAME, variable);
			rootEl.appendChild(variableEl);
		}
		return doc;
	}

	@Override
	public void initFromDocument(Document doc) {
		Element rootEl;
		Element variableEl;
		LinkedList children;
		int count = 0;

		rootEl = doc.getDocumentElement();
		children = DOMUtil.getChildren(rootEl);
		count = children.size();

		variables = new String[count];

		for (int i = 0; i < count; i++) {
			variableEl = (Element) children.get(i);
			variables[i] = variableEl.getAttribute(TAG_NAME);
		}
	}

}
