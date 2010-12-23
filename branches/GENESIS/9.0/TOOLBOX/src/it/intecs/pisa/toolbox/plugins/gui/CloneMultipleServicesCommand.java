/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.plugins.restManagerNativePlugins.CloneServiceCommand;
import java.util.Iterator;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CloneMultipleServicesCommand extends RESTManagerCommandPlugin{

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        JsonObject response;

        JsonElement obj = request.get("value");
        JsonArray array=obj.getAsJsonArray();

        Iterator<JsonElement> iter=array.iterator();
        JsonObject commandObj;

        commandObj=new JsonObject();
        while(iter.hasNext())
        {
            JsonElement el=iter.next();
            JsonObject elObj=el.getAsJsonObject();

            commandObj.add(elObj.getAsJsonPrimitive("id").getAsString(), elObj.getAsJsonPrimitive("value"));
        }

        CloneServiceCommand restManCommand;
        restManCommand=new CloneServiceCommand();
        JsonObject resp = restManCommand.executeCommand("", commandObj);

        return resp;
    }

    @Override
    public boolean validateInput(String method, JsonObject request) {
        JsonElement obj = request.get("value");
        if(obj instanceof JsonArray)
        {
            return true;
        }else return false;
    }
    
}
