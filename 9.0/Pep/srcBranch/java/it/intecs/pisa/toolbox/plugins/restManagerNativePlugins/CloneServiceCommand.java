/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.util.json.JsonErrorObject;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CloneServiceCommand extends RESTManagerCommandPlugin{

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        try
        {
            Set<Entry<String, JsonElement>> set = request.entrySet();
            Iterator<Entry<String, JsonElement>> iter = set.iterator();
            JsonObject responseObj;
            ServiceManager servMan=ServiceManager.getInstance();

            responseObj=new JsonObject();
            JsonArray cloned=new JsonArray();

            responseObj.add("created", cloned);
            responseObj.addProperty("success", true);
            while(iter.hasNext())
            {
                Entry<String, JsonElement> ith = iter.next();

                String fromName=ith.getKey();
                String toName=ith.getValue().getAsString();

                if(servMan.cloneService(fromName,toName)==true)
                {
                    JsonObject added=new JsonObject();
                    added.addProperty("name",toName);
                    cloned.add(added);
                }
            }

            return responseObj;
        }
        catch(Exception e)
        {
            return JsonErrorObject.get("Cannot complete service cloning");
        }
    }

   
    
}
