/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import com.google.gson.JsonObject;
import java.io.InputStream;
import java.util.Hashtable;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public interface IRESTManagerPlugin extends IManagerPlugin{
    public JsonObject executeCommand(String method,JsonObject request,Hashtable<String,String> headers,Hashtable<String,String> parameters) throws Exception;
    public Document executeCommand(String cmd, Document inputDoc,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public InputStream executeCommand(String cmd, InputStream in,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public boolean validateInput(String method,JsonObject request,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public boolean validateInput(String cmd, Document inputDoc,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public boolean validateInput(String cmd, InputStream in,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public boolean authenticate(String method,JsonObject request,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public boolean authenticate(String cmd, Document inputDoc,Hashtable<String,String> headers,Hashtable<String,String> parameters);
    public boolean authenticate(String cmd, InputStream in,Hashtable<String,String> headers,Hashtable<String,String> parameters);

}
