/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import com.google.gson.JsonObject;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public interface IManagerPlugin {
    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception;
    public JsonObject executeCommand(String method,JsonObject request) throws Exception;
    public void setPluginDirectory(File dir) throws Exception;
}