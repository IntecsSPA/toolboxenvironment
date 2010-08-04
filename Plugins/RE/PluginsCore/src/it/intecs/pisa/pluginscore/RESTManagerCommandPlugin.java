/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pluginscore;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public abstract class RESTManagerCommandPlugin implements IRESTManagerPlugin {
    protected File pluginDir;
    protected Map<String, String> inputHeaders;
    protected Map<String, String> outputHeaders;
    protected Map<String, String> inputParameters;

    public RESTManagerCommandPlugin() {
        inputHeaders=new HashMap<String, String>();
        outputHeaders=new HashMap<String, String>();
        inputParameters=new HashMap<String, String>();
    }

    public void setInputParameters(Map<String, String> parameters) {
        inputParameters=parameters;
    }

    public Map<String, String> getOutputHeaders() {
        return outputHeaders;
    }

    public void setInputHeaders(Map<String, String> headers) {
        inputHeaders=headers;
    }

    public void setPluginDirectory(File dir) throws Exception {
        pluginDir = dir;
    }

    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp,Hashtable<String,String> headers,Hashtable<String,String> parameters) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Document executeCommand(String cmd, Document inputDoc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

     public InputStream executeCommand(String cmd, InputStream in) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

     public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

      public boolean authenticate(String method, JsonObject request) {
        return true;
    }

    public boolean authenticate(String cmd, Document inputDoc) {
        return true;
    }

    public boolean authenticate(String cmd, InputStream in) {
        return true;
    }

    public boolean validateInput(String method, JsonObject request) {
        return true;
    }

    public boolean validateInput(String cmd, Document inputDoc) {
        return true;
    }

    public boolean validateInput(String cmd, InputStream in) {
        return true;
    }

    public boolean authenticate(HttpServletRequest req, HttpServletResponse resp) {
       return true;
    }

    public boolean validateInput(HttpServletRequest req, HttpServletResponse resp) {
        return true;
    }
}
