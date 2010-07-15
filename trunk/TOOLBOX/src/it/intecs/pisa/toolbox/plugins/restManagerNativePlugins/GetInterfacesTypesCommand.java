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
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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


}
