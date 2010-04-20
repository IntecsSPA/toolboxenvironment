/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util.rest;

import com.google.gson.JsonObject;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class RestGet {

    /**
     *
     * @param rest_URL
     * @param acceptType
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public static Document getAsXMLDocument(URL rest_URL, Hashtable<String,String> headers,String user, String password) throws Exception {
        InputStream inStream;
        
        inStream=getAsInputStream(rest_URL,headers, user,  password);
        if(inStream!=null)
        {
            DOMUtil du = new DOMUtil();
            Document documentResponse = du.inputStreamToDocument(inStream);
            return documentResponse;
        } else {
            return null;
        }
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
    public static JsonObject getAsJSON(URL rest_URL,Hashtable<String,String> headers, String user, String password) throws Exception {
        JsonObject jsonResponse;
        InputStream inStream;

        inStream=getAsInputStream(rest_URL,headers, user,  password);
        if(inStream!=null)
        {
            jsonResponse = JsonUtil.getInputAsJson(inStream);
            inStream.close();
        } else {
            jsonResponse = null;
        }

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
    public static InputStream getAsInputStream(URL rest_URL, Hashtable<String,String> headers, String user, String password) throws Exception {
        HttpURLConnection con = (HttpURLConnection) rest_URL.openConnection();

        if(headers!=null)
        {
            Enumeration<String> headerskeys = headers.keys();
            while(headerskeys.hasMoreElements())
            {
                String key=headerskeys.nextElement();
                con.setRequestProperty(key,headers.get(key));
            }
        }

        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestMethod("GET");


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

        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inStream;
            return  inStream = con.getInputStream();
        }
        else return null;
    }
}
