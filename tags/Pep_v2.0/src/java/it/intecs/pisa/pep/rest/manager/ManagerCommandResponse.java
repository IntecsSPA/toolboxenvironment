package it.intecs.pisa.pep.rest.manager;

import it.intecs.pisa.toolbox.configuration.*;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 *
 * @author Andrea Marongiu
 */
public class ManagerCommandResponse {

    private static String INFORMATION_PROPERTY="info";
    private static String SUCCESS_PROPERTY="success";
    private static String DETAILS_PROPERTY="details";

  
    private JsonObject gatewayResponse;
   


    public ManagerCommandResponse(String information){
        gatewayResponse= new JsonObject();
        gatewayResponse.addProperty(INFORMATION_PROPERTY, information); 
    }
    
    
   
    public void addJsonElement (String propertyName, JsonElement jsonObj){
        gatewayResponse.add(propertyName, jsonObj);
    }
    
    public void addJsonProperty(String propertyName, String property){
        gatewayResponse.addProperty(propertyName, property);
    }
    
    public void addJsonProperty(String propertyName, boolean property){
        gatewayResponse.addProperty(propertyName, property);
    }
    
    public void addJsonProperty(String propertyName, Number property){
        gatewayResponse.addProperty(propertyName, property);
    }
    
    public void setSuccess (boolean success){
        gatewayResponse.addProperty(SUCCESS_PROPERTY, success); 
    
    }
    
    public void setDetails (String  details){
        gatewayResponse.addProperty(DETAILS_PROPERTY, details); 
    
    }

    /**
     * @return the gatewayResponse
     */
    public JsonObject getConfigurationResponse() {
        return gatewayResponse;
    }



}
