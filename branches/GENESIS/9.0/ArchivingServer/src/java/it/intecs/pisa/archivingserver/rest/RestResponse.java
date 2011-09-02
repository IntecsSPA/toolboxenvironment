package it.intecs.pisa.archivingserver.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.util.json.JsonUtil;

/**
 *
 * @author Andrea Marongiu
 */
public class RestResponse {

    private static String INFORMATION_PROPERTY="info";
    private static String SUCCESS_PROPERTY="success";
    private static String DETAILS_PROPERTY="details";

  
    private JsonObject restResponse;
   


    public RestResponse(String information){
        restResponse= new JsonObject();
        restResponse.addProperty(INFORMATION_PROPERTY, information); 
    }
    
    
   
    public void addJsonElement (String propertyName, JsonElement jsonObj){
        restResponse.add(propertyName, jsonObj);
    }
    
    public void addJsonProperty(String propertyName, String property){
        restResponse.addProperty(propertyName, property);
    }
    
    public void addJsonProperty(String propertyName, boolean property){
        restResponse.addProperty(propertyName, property);
    }
    
    public void addJsonProperty(String propertyName, Number property){
        restResponse.addProperty(propertyName, property);
    }
    
    public void setSuccess (boolean success){
        restResponse.addProperty(SUCCESS_PROPERTY, success); 
    
    }
    
    public void setDetails (String  details){
        restResponse.addProperty(DETAILS_PROPERTY, details); 
    
    }

    /**
     * @return the restResponse
     */
    public JsonObject getJsonRestResponse() {
        return restResponse;
    }

    /**
     * @return the restResponse
     */
    public String getStringRestResponse() {
        String response= JsonUtil.getJsonAsString(restResponse);
        return response/*.replaceAll("\\\\", "\\\\\\\\")*/;
    }

}
