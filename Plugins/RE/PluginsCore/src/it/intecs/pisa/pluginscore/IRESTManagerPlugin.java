/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import com.google.gson.JsonObject;
import java.io.InputStream;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public interface IRESTManagerPlugin extends IManagerPlugin{
    public JsonObject executeCommand(String method,JsonObject request) throws Exception;
    public Document executeCommand(String cmd, Document inputDoc);
    public InputStream executeCommand(String cmd, InputStream in);
}
