/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardSectionsCommand extends RESTManagerCommandPlugin{
   
    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        JsonObject outputJson = new JsonObject();
        JsonArray array=new JsonArray();

        array.add(new JsonPrimitive("rest/gui/creationWizardWPS"));

        String[] types = InterfacePluginManager.getInstance().getInterfacesTypes();
        for(String type:types)
        {
            array.add(new JsonPrimitive("rest/gui/creationWizard/"+type));
        }

        outputJson.add("sections", array);
        return outputJson;

    }


    
}
