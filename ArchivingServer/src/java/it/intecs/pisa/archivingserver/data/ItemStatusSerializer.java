/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author massi
 */
public class ItemStatusSerializer implements JsonSerializer<ItemStatus>{

    public JsonElement serialize(ItemStatus t, Type type, JsonSerializationContext jsc) {
        JsonObject obj=null;
        JsonArray array;

        obj=new JsonObject();
        obj.addProperty("id", t.id);
        obj.addProperty("downloadStatus", t.downloadStatus);
        
        array=new JsonArray();
        try {
            for (String url : t.http) {
                array.add(new JsonPrimitive(url));
            }
        } catch (Exception ex) {
            Logger.getLogger(ItemStatusSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(t.http.length>0)
            obj.add("http", array);

        array=new JsonArray();
        try {
            for (String url : t.catalogues) {
                array.add(new JsonPrimitive(url));
            }
        } catch (Exception ex) {
            Logger.getLogger(ItemStatusSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(t.catalogues.length>0)
            obj.add("catalogues", array);

        array=new JsonArray();
        try {
            for (String url : t.ftp) {
                array.add(new JsonPrimitive(url));
            }
        } catch (Exception ex) {
            Logger.getLogger(ItemStatusSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(t.ftp.length>0)
            obj.add("ftp", array);

        array=new JsonArray();
        try {
            for (String url : t.geoserver) {
                array.add(new JsonPrimitive(url));
            }
        } catch (Exception ex) {
            Logger.getLogger(ItemStatusSerializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if(t.geoserver.length>0)
            obj.add("geoserver", array);
        return obj;
    }


}
