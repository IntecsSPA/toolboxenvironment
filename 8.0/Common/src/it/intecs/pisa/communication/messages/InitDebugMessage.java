/**
 * 
 */
package it.intecs.pisa.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class is used to parse the STARTDEBUG command
 * @author Massimiliano
 *
 */
public class InitDebugMessage extends StructuredMessage {

    protected String serviceName = null;
    protected String operationName = null;
    protected String token = null;
    private String ATTR_TOKEN = "token";
    private static final String ATTR_OPERATION = "operation";
    private static final String ATTR_SERVICE = "service";
    private static final String COMMAND_NAME = "initdebug";

    public InitDebugMessage() {
    }

    public InitDebugMessage(String token, String serviceName, String operationName) {
        this.token = token;
        this.serviceName = serviceName;
        this.operationName = operationName;
    }

    /**
     * @return the operationName
     */
    public String getOperationName() {
        return operationName;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public void initFromDocument(Document doc) {
        Element rootEl;

        rootEl = doc.getDocumentElement();

        serviceName = rootEl.getAttribute(ATTR_SERVICE);
        operationName = rootEl.getAttribute(ATTR_OPERATION);
        token = rootEl.getAttribute(ATTR_TOKEN);
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);
        rootEl.setAttribute(ATTR_TOKEN, token);
        rootEl.setAttribute(ATTR_SERVICE, serviceName);
        rootEl.setAttribute(ATTR_OPERATION, operationName);

        doc.appendChild(rootEl);
        return doc;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }
}
