/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.json;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.StringTokenizer;

/**
 *
 * @author massi
 */
public class JsonXpath {
    public static String getString(JsonObject obj,String xpath)
    {
        StringTokenizer tokenizer;

        tokenizer=new StringTokenizer(xpath,"/");

        Object selectedObject;
        selectedObject=get(obj,tokenizer);

        JsonPrimitive primitive;
        primitive=(JsonPrimitive) selectedObject;

        return (String) primitive.getAsString();
    }

    private static Object get(JsonObject obj, StringTokenizer tokenizer) {
        String tok;

        tok=tokenizer.nextToken();

        if(tokenizer.hasMoreTokens())
            return get(obj.get(tok).getAsJsonObject(),tokenizer);
        else return obj.get(tok);
  
    }
}
