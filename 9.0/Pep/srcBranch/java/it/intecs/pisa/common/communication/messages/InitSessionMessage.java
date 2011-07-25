/**
 * 
 */
package it.intecs.pisa.common.communication.messages;

import it.intecs.pisa.util.DOMUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class InitSessionMessage extends StructuredMessage {

    protected String host = null;
    protected int port = 0;
    private String ATTR_CLIENT_HOST = "clientHost";
    private String ATTR_CLIENT_PORT = "clientPort";
    private static final String COMMAND_NAME = "initsession";

    public InitSessionMessage() {
    }

    public InitSessionMessage(String host, int port) {

        this.host = host;
        this.port = port;
    }

    @Override
    public Document getDoc() {
        DOMUtil util;
        Element rootEl;

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement(COMMAND_NAME);

        rootEl.setAttribute(ATTR_CLIENT_HOST, host);
        rootEl.setAttribute(ATTR_CLIENT_PORT, Integer.toString(port));

        doc.appendChild(rootEl);
        return doc;
    }

    public void initFromDocument(Document doc) {
        Element rootEl;

        rootEl = doc.getDocumentElement();
        this.host = rootEl.getAttribute(ATTR_CLIENT_HOST);
        this.port = Integer.parseInt(rootEl.getAttribute(ATTR_CLIENT_PORT));

    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }
}
