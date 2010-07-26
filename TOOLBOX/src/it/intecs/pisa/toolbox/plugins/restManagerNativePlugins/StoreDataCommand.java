/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Hashtable;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class StoreDataCommand extends RESTManagerCommandPlugin{

    @Override
    public InputStream executeCommand(String cmd, InputStream in, Hashtable<String, String> headers, Hashtable<String, String> parameters) {
        try {
            InputStream response;
            File outputFile;
            String id = DateUtil.getCurrentDateAsUniqueId();
            outputFile = new File(pluginDir, "resources/storedData/" + id);
            
            IOUtil.copy(in, new FileOutputStream(outputFile));

            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("success", true);
            outputJson.addProperty("id", id);

            return JsonUtil.getJsonAsStream(outputJson);
        } catch (Exception ex) {
            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("success", false);
            outputJson.addProperty("reason", ex.getMessage());

            return JsonUtil.getJsonAsStream(outputJson);
        }
    }



    


}
