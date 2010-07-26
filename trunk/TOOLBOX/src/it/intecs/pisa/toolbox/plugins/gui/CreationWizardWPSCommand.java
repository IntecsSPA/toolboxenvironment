/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardWPSCommand extends RESTManagerCommandPlugin{
   
    @Override
    public JsonObject executeCommand(String method, JsonObject request,Hashtable<String,String> headers,Hashtable<String,String> parameters) throws Exception {
        JsonObject outputJson = new JsonObject();

        int index=method.lastIndexOf("/");
      

        outputJson.addProperty("xmlUrl", "rest/gui/creationWizardInterfaceWPS.xml");
        outputJson.addProperty("icon", "images/wps_blk.png");
        outputJson.addProperty("title", "Create Web Processing Service (WPS)");
        outputJson.addProperty("name", "wpsService");
        outputJson.addProperty("actionMethod", "createWPSRequest(this)");
        return outputJson;

    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
