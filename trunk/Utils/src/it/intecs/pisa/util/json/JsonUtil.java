/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.intecs.pisa.util.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class JsonUtil {
     public static JsonObject getInputAsJson(InputStream in) {
        JsonParser parser;
        JsonObject obj;

        try
        {
            parser = new JsonParser();
            
            String jsonAsString=IOUtil.inputToString(in);
            if(jsonAsString==null || jsonAsString.equals(""))
                return new JsonObject();
            
            JsonElement parsedEl = parser.parse(jsonAsString);
            obj = (JsonObject) parsedEl.getAsJsonObject();
        }
        catch(Throwable t)
        {
            obj=null;
        }
        return obj;
    }

    public static String getJsonAsString(JsonObject outputJson) {
        Gson gson = new Gson();
        return gson.toJson(outputJson);
    }

    public static InputStream getJsonAsStream(JsonObject obj)
    {
        return new ByteArrayInputStream(getJsonAsString(obj).getBytes());
    }
}
