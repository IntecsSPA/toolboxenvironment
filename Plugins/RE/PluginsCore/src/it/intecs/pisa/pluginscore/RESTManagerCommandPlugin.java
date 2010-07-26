/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pluginscore;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.InputStream;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public abstract class RESTManagerCommandPlugin implements IRESTManagerPlugin {



    protected File pluginDir;

    public RESTManagerCommandPlugin() {
    }

    public void setPluginDirectory(File dir) throws Exception {
        pluginDir = dir;
    }

    public JsonObject executeCommand(String method, JsonObject request,Hashtable<String,String> headers,Hashtable<String,String> parameters) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp,Hashtable<String,String> headers,Hashtable<String,String> parameters) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Document executeCommand(String cmd, Document inputDoc,Hashtable<String,String> headers,Hashtable<String,String> parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

     public InputStream executeCommand(String cmd, InputStream in,Hashtable<String,String> headers,Hashtable<String,String> parameters) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
