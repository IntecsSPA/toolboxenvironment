/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetInterfacesTypesCommand extends RESTManagerCommandPlugin{

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception  {
        boolean success=true;
        JsonObject outputJson=null;
        try
        {
            String[] types = InterfacePluginManager.getInstance().getInterfacesTypes();

            outputJson=new JsonObject();
            JsonArray array = new JsonArray();
            for (String type : types) {
                array.add(new JsonPrimitive(type));
            }
            outputJson.add("types", array);
            success=true;
        }
        catch(Exception e)
        {

        }

        try
        {
           outputJson.addProperty("success", success);
        }catch(Exception e)
        {
           outputJson=null;
        }

        return outputJson;


    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
