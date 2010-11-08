/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.util.json.JsonSuccessObject;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class SetConfigurationCommand extends RESTManagerCommandPlugin{ 

    @Override
    public JsonObject executeCommand(String cmd, JsonObject request) throws Exception {
        JsonArray array;

        array=request.getAsJsonArray("values");
        Iterator<JsonElement> iter=array.iterator();

        ToolboxConfiguration config;
        config=ToolboxConfiguration.getInstance();

        while(iter.hasNext())
        {
            JsonElement el = iter.next();
            JsonObject obj=el.getAsJsonObject();

            Iterator<Entry<String, JsonElement>> eiter = obj.entrySet().iterator();
            Entry<String, JsonElement> elem = eiter.next();

            String key=elem.getKey();
            String value=elem.getValue().getAsString();

            config.setConfigurationValue(key, value);
        }

        config.saveConfiguration();
        return JsonSuccessObject.get();
    }

    @Override
    public boolean authenticate(String method, JsonObject request) {
        return super.authenticate(method, request);
    }

}
