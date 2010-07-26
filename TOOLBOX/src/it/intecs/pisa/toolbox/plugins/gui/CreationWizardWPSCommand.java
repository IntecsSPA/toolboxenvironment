/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardWPSCommand extends RESTManagerCommandPlugin{
   
    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        JsonObject outputJson = new JsonObject();

        int index=method.lastIndexOf("/");
      

        outputJson.addProperty("xmlUrl", "rest/gui/creationWizardInterfaceWPS.xml");
        outputJson.addProperty("icon", "images/wps_blk.png");
        outputJson.addProperty("title", "Create Web Processing Service (WPS)");
        outputJson.addProperty("name", "wpsService");
        outputJson.addProperty("actionMethod", "createWPSRequest(this)");
        return outputJson;

    }


    
}
