/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import com.google.gson.JsonObject;
import java.io.InputStream;
import java.util.Map;
import org.w3c.dom.Document;

/**
 *  This interface provides all methods to be implemented in a REST command.
 * @author Massimiliano Fanciulli
 */
public interface IRESTManagerPlugin extends IManagerPlugin{
    public void setInputHeaders(Map<String,String> inputHeaders);
    public void setInputParameters(Map<String,String> inputParameters);
    public Map<String,String> getOutputHeaders();

    public abstract JsonObject executeCommand(String method,JsonObject request) throws Exception;
    public abstract Document executeCommand(String cmd, Document inputDoc);
    public abstract InputStream executeCommand(String cmd, InputStream in);

    public abstract boolean validateInput(String method,JsonObject request);
    public abstract boolean validateInput(String cmd, Document inputDoc);
    public abstract boolean validateInput(String cmd, InputStream in);

    public abstract boolean authenticate(String method,JsonObject request);
    public abstract boolean authenticate(String cmd, Document inputDoc);
    public abstract boolean authenticate(String cmd, InputStream in);

}
