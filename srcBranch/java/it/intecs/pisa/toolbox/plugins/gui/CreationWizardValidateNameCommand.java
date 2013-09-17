/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.service.ServiceManager;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardValidateNameCommand extends RESTManagerCommandPlugin{

  
   
    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        JsonObject outputJson = new JsonObject();
        boolean success=false;
        String reason="";

        String serviceName=inputParameters.get("value");
        serviceName=URLDecoder.decode(serviceName, "UTF-8");

        String regex = "[a-zA-Z0-9-_]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(serviceName);

        if(serviceName==null || serviceName.equals(""))
        {
            success=false;
            reason="Service name is not provided";
        }else
            if(!matcher.matches())
            {
                success=false;
                reason="Service name contains an invalid character";
            }
        else
        {
            ServiceManager servMan;

            servMan=ServiceManager.getInstance();
            if(servMan.isServiceDeployed(serviceName))
            {
                success=false;
                reason="A service with this name is already available";
            }
            else
            {
                success=true;
            }
        }

        outputJson.addProperty("success", success);
        if(success==false)
            outputJson.addProperty("reason", reason);
       
        return outputJson;

    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
