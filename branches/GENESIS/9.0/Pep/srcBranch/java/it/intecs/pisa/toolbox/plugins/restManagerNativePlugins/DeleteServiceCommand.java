/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.util.json.JsonErrorObject;
import java.util.StringTokenizer;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteServiceCommand extends RESTManagerCommandPlugin{

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        StringTokenizer tokenizer;

        try
        {
            tokenizer=new StringTokenizer(method,"/");
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            String serviceName=tokenizer.nextToken();

            ServiceManager servMan;

            servMan=ServiceManager.getInstance();
            servMan.stopService(serviceName);
            servMan.deleteService(serviceName);

            JsonObject outputJson = new JsonObject();
            outputJson.addProperty("success", true);
            return outputJson;
        }
        catch(Exception e)
        {
            return JsonErrorObject.get("Unable to delete service");
        }
    }

    @Override
    public boolean validateInput(String method, JsonObject request) {
        StringTokenizer tokenizer = new StringTokenizer(method, "/");
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        String serviceName=tokenizer.nextToken();

        ServiceManager servMan;
        servMan=ServiceManager.getInstance();

        return servMan.isServiceDeployed(serviceName);
    }



   
    
}
