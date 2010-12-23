/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.rest;

import com.google.gson.JsonObject;
import it.intecs.pisa.util.http.HttpUtils;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class RestPost {

    public static Document postAsXML(URL rest_URL, Hashtable<String,String> headers, String user, String password,JsonObject content) throws Exception
    {
        /*JsonObject jsonResponse;
        InputStream inStream;
        InputStream contentStream;
        String jsonAsString;

        DOMUtil util;

        util=new DOMUtil();
        util.getDocumentAsInputStream(null)
        contentStream=JsonUtil.getJsonAsStream(content);

        inStream=post(rest_URL,headers, user,  password,contentStream);
        if(inStream!=null)
        {
            jsonResponse = JsonUtil.getInputAsJson(inStream);
            inStream.close();
        } else {
            jsonResponse = null;
        }

        contentStream.close();*/
        return null;
    }

    public static JsonObject postAsJSON(URL rest_URL, Hashtable<String,String> headers, String user, String password,JsonObject content) throws Exception
    {
        JsonObject jsonResponse;
        InputStream inStream;
        InputStream contentStream;
        String jsonAsString;
        
        contentStream=JsonUtil.getJsonAsStream(content);

        inStream=HttpUtils.post(rest_URL,headers, user,  password,contentStream);
        if(inStream!=null)
        {
            jsonResponse = JsonUtil.getInputAsJson(inStream);
            inStream.close();
        } else {
            jsonResponse = null;
        }

        contentStream.close();
        return jsonResponse;
    }

   

}
