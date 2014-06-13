/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.lang.reflect.Type;

/**
 * This class deserializes the StoreItem class
 * @author massi
 */
public class StoreItemDeserializer implements JsonDeserializer<StoreItem> {
    
    private final String MAX_DELETE_AFTER="9999W";

    public StoreItem deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        StoreItem item;
        JsonObject obj;
        JsonElement el;
        JsonObject publishObj;
        JsonArray array;

        obj = je.getAsJsonObject();

        item = new StoreItem();
        item.downloadUrl = obj.get("downloadUrl").getAsString();

        el=obj.get("metadataUrl");
        if(el==null || el instanceof com.google.gson.JsonNull)
          item.metadataUrl="";  
        else 
          item.metadataUrl=el.getAsString();

        el=obj.get("type");
        if(el==null || el instanceof com.google.gson.JsonNull)
           item.type=""; 
        else item.type=el.getAsString();

        el=obj.get("deleteAfter");
        if(el==null || el instanceof com.google.gson.JsonNull)
           item.deleteAfter=TimeInterval.getIntervalAsLong(MAX_DELETE_AFTER); 
        else {
            if(el.getAsString().equals("-1"))
               item.deleteAfter=TimeInterval.getIntervalAsLong(MAX_DELETE_AFTER);
            else
               item.deleteAfter=TimeInterval.getIntervalAsLong(el.getAsString());
        }
        
        el=obj.get("geoserverType");
        if(el==null || el instanceof com.google.gson.JsonNull)
          item.geoserverType="";  
        else item.geoserverType=el.getAsString();
        
        el=obj.get("geoserverWorkspace");
        if(el==null || el instanceof com.google.gson.JsonNull)
          item.geoserverWorkspace="";  
        else item.geoserverWorkspace=el.getAsString();
        
        el=obj.get("geoserverDimensions");
        if(el==null || el instanceof com.google.gson.JsonNull)
          item.geoserverDimensions="";  
        else item.geoserverDimensions=el.getAsString();
        
        el=obj.get("geoserverFileNameTemplate");
        if(el==null || el instanceof com.google.gson.JsonNull)
          item.geoserverFileNameTemplate="";  
        else item.geoserverFileNameTemplate=el.getAsString();
        
        el=obj.get("geoserverStyle");
        if(el==null || el instanceof com.google.gson.JsonNull)
          item.geoserverStyle="";  
        else item.geoserverStyle=el.getAsString();
        
        
        publishObj = obj.getAsJsonObject("publish");
        if(publishObj == null)  //local folder (watch)
            publishObj=obj;

        if (publishObj != null) {
            array = publishObj.getAsJsonArray("Ftp");

            if (array != null) {
                item.publishFtp = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.publishFtp[i] = array.get(i).getAsString();
                }
            } else item.publishFtp = new String[0];

            array = publishObj.getAsJsonArray("ebRIMCatalogue");

            if (array != null) {
                item.publishCatalogue = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.publishCatalogue[i] = array.get(i).getAsString();
                }
            } else item.publishCatalogue = new String[0];
            
            
            array = publishObj.getAsJsonArray("openSearchCatalogueIngestion");
            if (array != null) {
                item.ingestionOpenSearchCatalogue = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.ingestionOpenSearchCatalogue[i] = array.get(i).getAsString();
                }
            } else item.ingestionOpenSearchCatalogue = new String[0];
            
            
            array = publishObj.getAsJsonArray("openSearchCatalogue");
            if (array != null) {
                item.publishOpenSearchCatalogue = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.publishOpenSearchCatalogue[i] = array.get(i).getAsString();
                }
            } else item.publishOpenSearchCatalogue = new String[0];
            

            array = publishObj.getAsJsonArray("GeoServer");

            if (array != null) {
                item.publishGeoserver = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.publishGeoserver[i] = array.get(i).getAsString();
                }
            } else item.publishGeoserver = new String[0];
            
            array = publishObj.getAsJsonArray("SOS");

            if (array != null) {
                item.publishSOS = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.publishSOS[i] = array.get(i).getAsString();
                }
            } else item.publishSOS = new String[0];

            if(publishObj.has("Http") && publishObj.get("Http").getAsBoolean()==true)
                item.publishHttp=true;
            else item.publishHttp=false;
        } 
        else
        {
            item.publishFtp = new String[0];
            item.publishCatalogue = new String[0];
            item.publishOpenSearchCatalogue = new String[0];
            item.ingestionOpenSearchCatalogue = new String[0];
            item.publishGeoserver = new String[0];
        }
       
        return item;
    }
}
