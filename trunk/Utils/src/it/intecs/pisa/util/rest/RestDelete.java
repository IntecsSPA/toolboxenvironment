/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.rest;

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
public class RestDelete {

    public static int del(URL rest_URL, Hashtable<String,String> headers, String user, String password) throws Exception
    {
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

        con.setRequestMethod("DELETE");

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


        return con.getResponseCode();

    }


}
