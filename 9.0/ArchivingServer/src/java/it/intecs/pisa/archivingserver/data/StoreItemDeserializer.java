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
import java.lang.reflect.Type;

/**
 * This class deserializes the StoreItem class
 * @author massi
 */
public class StoreItemDeserializer implements JsonDeserializer<StoreItem> {

    public StoreItem deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        StoreItem item;
        JsonObject obj;
        JsonObject publishObj;
        JsonArray array;

        obj = je.getAsJsonObject();

        item = new StoreItem();
        item.downloadUrl = obj.get("downloadUrl").getAsString();

        if(obj.get("metadataUrl")!=null)
            item.metadataUrl=obj.get("metadataUrl").getAsString();
        else item.metadataUrl="";

        if(obj.get("type")!=null)
            item.type=obj.get("type").getAsString();
        else item.type="";

        if(obj.get("deleteAfter")!=null)
            item.deleteAfter=obj.get("deleteAfter").getAsLong();
        else item.deleteAfter=0;
        
        publishObj = obj.getAsJsonObject("publish");

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

            array = publishObj.getAsJsonArray("GeoServer");

            if (array != null) {
                item.publishGeoserver = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    item.publishGeoserver[i] = array.get(i).getAsString();
                }
            } else item.publishGeoserver = new String[0];

            if(publishObj.has("Http") && publishObj.get("Http").getAsBoolean()==true)
                item.publishHttp=true;
            else item.publishHttp=false;
        } 
        else
        {
            item.publishFtp = new String[0];
            item.publishCatalogue = new String[0];
            item.publishGeoserver = new String[0];
        }
       
        return item;
    }
}
