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
        String interfaceType=method.substring(index+1);

        outputJson.addProperty("xmlUrl", "rest/gui/creationWizardInterfaceWPS.xml");
        outputJson.addProperty("icon", "images/order_blk.png");
        outputJson.addProperty("title", "Create WPS service");
        outputJson.addProperty("name", "WPS Service");
        outputJson.addProperty("actionMethod", "createToolboxService(this)");
        return outputJson;

    }


    
}
