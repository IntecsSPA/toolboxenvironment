/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetConfigurationCommand extends RESTManagerCommandPlugin{ 

    @Override
    public JsonObject executeCommand(String cmd, JsonObject request) throws Exception {
        JsonObject outputJson = new JsonObject();
        JsonArray array = new JsonArray();

        ToolboxConfiguration config;
        config=ToolboxConfiguration.getInstance();

        String[] configKeys=config.getConfigurationKeys();

        for (String key : configKeys) {
            JsonObject setting=new JsonObject();
            setting.add(key, new JsonPrimitive(config.getConfigurationValue(key)));
            array.add(setting);
        }
        outputJson.add("values", array);
        outputJson.addProperty("success", true);
        return outputJson;
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    


}
