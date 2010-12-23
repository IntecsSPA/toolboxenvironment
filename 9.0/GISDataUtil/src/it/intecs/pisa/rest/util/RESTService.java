
package it.intecs.pisa.rest.util;

import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.*;
import org.w3c.dom.Document;


/**
 * @author Andrea Marongiu
 *
 */
public class RESTService {
   
    private static final Logger LOGGER = Logger.getLogger(RESTService.class.toString());

    private static final String REST_PUT_OPERATION="PUT";
    private static final String REST_POST_OPERATION="POST";
    private static final String REST_DELETE_OPERATION="DELETE";
    private static final String REST_GET_OPERATION="GET";
 

 public static boolean putInputStream(URL restURL,
                    InputStream inputStream,
                    final String contentType,
                    final String user,
                    final String userPassword) {
        
        boolean res = sendInputStream(restURL, inputStream, REST_PUT_OPERATION,
                                            contentType, user, userPassword);
        return res;
    }


 public static boolean postInputStream(URL restURL,
                    InputStream inputStream,
                    final String contentType,
                    final String user,
                    final String userPassword) {

        boolean res = sendInputStream(restURL, inputStream, REST_POST_OPERATION,
                                            contentType, user, userPassword);
        return res;
    }



 public static boolean putString(URL restURL,
            String content,
            String contentType,
            final String user,
            final String userPassword) {

        boolean result=sendStringContent(restURL,content,REST_PUT_OPERATION,
                                            contentType, user, userPassword);
        return result;
    }


 public static boolean postString(URL restURL,
            String content,
            String contentType,
            final String user,
            final String userPassword) {

        boolean result=sendStringContent(restURL,content,REST_POST_OPERATION,
                                            contentType, user, userPassword);
        return result;
    }


 public static boolean putFile(final URL restURL,
                final File file,
                final String contentType,
                final String user,
                final String userPassword){

           InputStream inputStream=null;
           boolean res;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}",
                                                    ex.getLocalizedMessage());
                res = false;
            }
           res = sendInputStream(restURL, inputStream, REST_PUT_OPERATION,
                                            contentType, user, userPassword);
           return res;
	}


 public static boolean postFile(final URL restURL,
                final File file,
                final String contentType,
                final String user,
                final String userPassword){

           InputStream inputStream=null;
           boolean res;
            try {
                inputStream = new FileInputStream(file);
            } catch (FileNotFoundException ex) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}",
                                                    ex.getLocalizedMessage());
                res = false;
            }
           res = sendInputStream(restURL, inputStream, REST_POST_OPERATION,
                                            contentType, user, userPassword);
           return res;
	}


 public static boolean putDocument(final URL restURL,
                final Document doc,
                final String user,
                final String userPassword){

           InputStream inputStream=null;
           boolean res;
            try {
                inputStream = DOMUtil.getDocumentAsInputStream(doc);
            } catch (Exception ex) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}",
                                                    ex.getLocalizedMessage());
                res = false;
            }
           res = sendInputStream(restURL, inputStream, REST_PUT_OPERATION,
                                            "text/xml", user, userPassword);
           return res;
	}


 public static boolean postDocument(final URL restURL,
                final Document doc,
                final String user,
                final String userPassword){

           InputStream inputStream=null;
           boolean res;
            try {
                inputStream = DOMUtil.getDocumentAsInputStream(doc);
            } catch (Exception ex) {
                if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}",
                                                    ex.getLocalizedMessage());
                res = false;
            }
           res = sendInputStream(restURL, inputStream, REST_POST_OPERATION,
                                            "text/xml", user, userPassword);
           return res;
	}


 public static Object getInformation(URL rest_URL, 
            String acceptType,
            final String user,
            final String userPassword){

            String response= null;

            try{
                HttpURLConnection con = (HttpURLConnection) rest_URL.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Accept", acceptType);
                con.setRequestMethod(REST_GET_OPERATION);

                final String login = user;
                final String pass = userPassword;

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
                    response=IOUtil.inputToString(con.getInputStream());
                if (LOGGER.isLoggable(Level.INFO))
                	LOGGER.log(Level.INFO, "HTTP OK: {0}", response);
                } else {
                    if (LOGGER.isLoggable(Level.INFO))
            		LOGGER.log(Level.INFO, "HTTP ERROR: {0}",
                                                    con.getResponseMessage());

                }

                if(acceptType.equalsIgnoreCase("text/json")){
                    JSONObject jsonResponse= new JSONObject(response);
                    return jsonResponse;
                }else
                  if(acceptType.equalsIgnoreCase("text/xml")){
                    DOMUtil du= new DOMUtil();
                    Document documentResponse= du.stringToDocument(response);
                    return documentResponse;
                  }else
                    return response;

       } catch (MalformedURLException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}",
                                                    e.getLocalizedMessage());
            return null;
        } catch (Exception e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}",
                                                    e.getLocalizedMessage());
           return null;
        }

    }


 public static boolean delete (final URL restURL,
            final String user,
            final String userPassword) {
    	HttpURLConnection con=null;
        try {
            con = (HttpURLConnection) restURL.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod(REST_DELETE_OPERATION);
        } catch (IOException e) {
            if (LOGGER.isLoggable(Level.SEVERE))
        	LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}", e.getLocalizedMessage());
            return false;
	}

        final String login = user;
        final String password = userPassword;

        if ((login != null) && (login.trim().length() > 0)) {
            Authenticator.setDefault(new Authenticator() {
                @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(login, password.toCharArray());
                    }
                });
        }

        try {
             final int responseCode = con.getResponseCode();
             switch (responseCode){
                case HttpURLConnection.HTTP_OK:
                case HttpURLConnection.HTTP_ACCEPTED:
                case HttpURLConnection.HTTP_NO_CONTENT:{
                     if (LOGGER.isLoggable(Level.FINE))
                           LOGGER.fine("HTTP OK: " );
                     return true;
                    }
                default:{
                    if (LOGGER.isLoggable(Level.FINE))
            		LOGGER.log(Level.FINE, "HTTP ERROR: {0}", con.getResponseMessage());
                    return false;
                    }
            }

        } catch (IOException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}", e.getLocalizedMessage());
        	return false;
        }
    }

	
	


	

    private static boolean sendStringContent(URL restURL,
                    String content,
                    final String restMethod,
                    final String contentType,
                    final String user,
                    final String userPassword){

        boolean result=sendInputStream(restURL,
                            new ByteArrayInputStream(content.getBytes()),
                            restMethod, contentType, user, userPassword);

       return result;
    }

    private static boolean sendInputStream(URL restURL,
                    InputStream inputStream,
                    final String restMethod,
                    final String contentType,
                    final String user,
                    final String userPassword){

        boolean result=true;
        String response=null;

        try {
            HttpURLConnection con = (HttpURLConnection) restURL.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            if(contentType !=null)
                con.setRequestProperty("CONTENT-TYPE", contentType);
            con.setRequestMethod(restMethod);

            final String login = user;
            final String password = userPassword;

            if ((login != null) && (login.trim().length() > 0)) {
                Authenticator.setDefault(new Authenticator() {
                    @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(login,
                                password.toCharArray());
                        }
                    });
            }

            IOUtil.copy(inputStream, con.getOutputStream());

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                response=IOUtil.inputToString(con.getInputStream());
                if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.log(Level.FINE, "HTTP OK: {0}", response);
                result = true;
            } else {
            	if (LOGGER.isLoggable(Level.FINE))
                    LOGGER.log(Level.FINE, "HTTP ERROR: {0}", con.getResponseMessage());
                result = false;
            }
        } catch (MalformedURLException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}", e.getLocalizedMessage());
                result = false;
        } catch (Exception e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
                    LOGGER.log(Level.SEVERE, "HTTP ERROR: {0}", e.getLocalizedMessage());
                result = false;
        }


       return result;
    }
}