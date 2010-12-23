/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.util.DOMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class Script {

    public static final String SCRIPT_TYPE_FIRST_SCRIPT = "FIRST_SCRIPT";
    public static final String SCRIPT_TYPE_SECOND_SCRIPT = "SECOND_SCRIPT";
    public static final String SCRIPT_TYPE_THIRD_SCRIPT = "THIRD_SCRIPT";
    public static final String SCRIPT_TYPE_RESP_ACK = "RESP_ACK";
    public static final String SCRIPT_TYPE_GLOBAL_ERROR = "GLOBAL_ERROR";
    public static final String SCRIPT_TYPE_ERROR_ON_RESP_BUILDER = "ERROR_ON_RESP_BUILDER";
    public static final String SCRIPT_TYPE_RESPONSE_BUILDER = "RESP_BUILDER";
    protected static final String TAG_SCRIPT = "scriptFile";
    protected static final String ATTRIBUTE_TYPE = "type";
    protected static final String ATTRIBUTE_PATH = "path";
    protected static final String ATTRIBUTE_OVERRIDE_BY_USER = "overrideByUser";
    protected String type;
    protected String path;
    protected boolean overrideByUser = true;
    protected Document scriptDoc;
    protected Operation parentOper;

    public Script() {
    }

    public Script(Script s) {
        try {
            this.overrideByUser = s.isOverrideByUser();
            this.path = new String(s.getPath());
            this.type = new String(s.getType());
            this.scriptDoc = DOMUtil.getCopyOfDocument(s.getScriptDoc());
        } catch (Exception ex) {
            Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized Object clone() {
        try {
            Script newScript;
            newScript = new Script();
            newScript.setType(type);
            newScript.setPath(path);
            newScript.setOverrideByUser(overrideByUser);
            newScript.setScriptDoc(DOMUtil.getCopyOfDocument(scriptDoc));
            return newScript;
        } catch (Exception ex) {
            Logger.getLogger(Script.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the overrideByUser
     */
    public boolean isOverrideByUser() {
        return overrideByUser;
    }

    /**
     * @param overrideByUser the overrideByUser to set
     */
    public void setOverrideByUser(boolean userProvided) {
        this.overrideByUser = userProvided;
    }

    public void appendToDoc(Element operationEl) {
        Document doc;
        Element scriptEl;

        doc = operationEl.getOwnerDocument();
        scriptEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_SCRIPT);
        scriptEl.setAttribute(ATTRIBUTE_TYPE, type);
        scriptEl.setAttribute(ATTRIBUTE_PATH, path);
        scriptEl.setAttribute(ATTRIBUTE_OVERRIDE_BY_USER, Boolean.toString(overrideByUser));
        operationEl.appendChild(scriptEl);
    }

    public void initFromXMLDocument(Element scriptEl) {
        type = scriptEl.getAttribute(ATTRIBUTE_TYPE);
        path = scriptEl.getAttribute(ATTRIBUTE_PATH);
        overrideByUser = Boolean.valueOf(scriptEl.getAttribute(ATTRIBUTE_OVERRIDE_BY_USER));
    }

    /**
     * @return the scriptDoc
     */
    public Document getScriptDoc() {
        return scriptDoc;
    }

    /**
     * @param scriptDoc the scriptDoc to set
     */
    public void setScriptDoc(Document scriptDoc) {
        this.scriptDoc = scriptDoc;
    }

    public void dumpScript() {
    }

    void setParent(Operation aThis) {
        parentOper = aThis;
    }
}
