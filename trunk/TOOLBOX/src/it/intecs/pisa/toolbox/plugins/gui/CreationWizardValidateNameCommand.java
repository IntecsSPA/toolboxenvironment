/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.service.ServiceManager;
import java.net.URLDecoder;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardValidateNameCommand extends RESTManagerCommandPlugin{

  
   
    @Override
    public JsonObject executeCommand(String method, JsonObject request,Hashtable<String,String> headers,Hashtable<String,String> parameters) throws Exception {
        JsonObject outputJson = new JsonObject();
        boolean success=false;
        String reason="";

        String serviceName=parameters.get("value");
        serviceName=URLDecoder.decode(serviceName, "UTF-8");

        if(serviceName==null || serviceName.equals(""))
        {
            success=false;
            reason="Service name is not provided";
        }
        if(serviceName.contains("./,:;+'()&%$£\"!*§°Ç "))
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
