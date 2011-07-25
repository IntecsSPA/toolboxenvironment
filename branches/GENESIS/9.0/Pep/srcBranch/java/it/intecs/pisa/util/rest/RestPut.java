/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.rest;

import com.google.gson.JsonObject;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class RestPut{
   public static JsonObject putAsJSON(URL rest_URL,HashMap<String,String> headers, String user, String password,JsonObject content) throws Exception
    {
        JsonObject jsonResponse;
        InputStream inStream;
        InputStream contentStream;
         
        contentStream=JsonUtil.getJsonAsStream(content);

        inStream=put(rest_URL, headers, user,  password,contentStream);
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

    /**
     *
     * @param rest_URL
     * @param acceptType
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public static InputStream put(URL rest_URL, HashMap<String,String> headers, String user, String password,InputStream content) throws Exception {
        HttpURLConnection con = (HttpURLConnection) rest_URL.openConnection();

        if(headers!=null)
        {
            String[] headerskeys = headers.keySet().toArray(new String[0]);
           for(String key:headerskeys)
                con.setRequestProperty(key,headers.get(key));
        }

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("PUT");


        final String login = user;
        final String pass = password;

        if ((login != null) && (login.trim().length() > 0)) {
            Authenticator.setDefault(new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(login,
                            pass.toCharArray());
                }
            });
        }

        IOUtil.copy(content, con.getOutputStream());

        int respCode = con.getResponseCode();
        if ( respCode== HttpURLConnection.HTTP_OK || respCode== HttpURLConnection.HTTP_CREATED) {
            InputStream inStream;
            return  inStream = con.getInputStream();
        }
        else return null;
    }

}
