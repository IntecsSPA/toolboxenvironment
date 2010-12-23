/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.http;

import it.intecs.pisa.util.IOUtil;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class HttpUtils {
    /**
     *
     * @param rest_URL
     * @param acceptType
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public static InputStream post(URL rest_URL, Hashtable<String,String> headers, String user, String password,InputStream content) throws Exception {
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
        con.setRequestMethod("POST");


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
