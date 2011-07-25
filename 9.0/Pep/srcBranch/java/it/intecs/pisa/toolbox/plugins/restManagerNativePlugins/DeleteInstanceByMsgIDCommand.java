/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import it.intecs.pisa.util.json.JsonErrorObject;
import it.intecs.pisa.util.json.JsonSuccessObject;
import java.util.StringTokenizer;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteInstanceByMsgIDCommand extends RESTManagerCommandPlugin{

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
            String operationName=tokenizer.nextToken();
            String messageID=tokenizer.nextToken();

            Long[] instances=InstanceLister.getInstancesByMessageID(serviceName, operationName, messageID);
            for(Long instance:instances)
            {
                InstanceHandler handler;
                handler=new InstanceHandler(instance);

                handler.deleteInstance();
            }

            return JsonSuccessObject.get();
        }
        catch(Exception e)
        {
            return JsonErrorObject.get("Unable to delete service");
        }
    }
}
