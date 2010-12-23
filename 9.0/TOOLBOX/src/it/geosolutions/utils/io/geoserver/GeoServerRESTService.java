/*
 * $Header: $fileName$ $
 * $Revision: 0.1 $
 * $Date: $date$ $time.long$ $
 *
 * ====================================================================
 *
 * Copyright (C) 2007-2008 GeoSolutions S.A.S.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.
 *
 * ====================================================================
 *
 * This software consists of voluntary contributions made by developers
 * of GeoSolutions.  For more information on GeoSolutions, please see
 * <http://www.geo-solutions.it/>.
 *
 */
package it.geosolutions.utils.io.geoserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Alessio Fabiani
 *
 */
public class GeoServerRESTService {
    /**
     *
     */
    private static final Logger LOGGER = Logger.getLogger(GeoServerRESTService.class.toString());

		/** Default size of element for {@link FileChannel} based copy method. */
    private static final int DEFAULT_SIZE = 10 * 1024 * 1024;

    /**
     * 30 seconds is the default period beteen two checks.
     */
    private static long DEFAULT_PERIOD = 5L;

    /**
     * The default number of attempts is 50
     */
    private final static int DEF_MAX_ATTEMPTS = 50;

    private static final long ATOMIC_WAIT = 5000;

    /**
     * The max time the node will wait for, prior to stop to attempt for acquiring a lock on a
     * <code>File</code>.
     */
    private static final long MAX_WAITING_TIME_FOR_LOCK = 300 * 1000;
        
    /**
     *
     * @param geoserverREST_URL
     * @param file
     * @return
     */
    public static boolean deleteURL(final URL geoserverREST_URL, final String gsUser, final String gsPassword) {
    	HttpURLConnection con=null;
        try {
            con = (HttpURLConnection) geoserverREST_URL.openConnection();
            con.setDoOutput(true);          	
			con.setRequestMethod("DELETE");
		} catch (IOException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
        	return false;
		}

        final String login = gsUser;
        final String password = gsPassword;

        if ((login != null) && (login.trim().length() > 0)) {
            Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(login, password.toCharArray());
                    }
                });
        }

        
        try {
        	final int responseCode = con.getResponseCode();
            switch (responseCode)
            {
              case HttpURLConnection.HTTP_OK:
              case HttpURLConnection.HTTP_ACCEPTED:
              case HttpURLConnection.HTTP_NO_CONTENT:
              {
                  if (LOGGER.isLoggable(Level.FINE))
                  	LOGGER.fine("HTTP OK: " );
                  return true;
              }
              default:
              {
              	if (LOGGER.isLoggable(Level.FINE))
            		LOGGER.fine("HTTP ERROR: " + con.getResponseMessage());
            	return false;
              }
            }
        	
        } catch (IOException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
        	return false;
		}
        

    }

    /**
     *
     * @param geoserverREST_URL
     * @param inputStream
     * @return
     */
    public static boolean putTextFileTo(URL geoserverREST_URL, InputStream inputStream, final String gsUser, final String gsPassword) {
        boolean res = false;

        try {
            HttpURLConnection con = (HttpURLConnection) geoserverREST_URL.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("PUT");

            final String login = gsUser;
            final String password = gsPassword;

            if ((login != null) && (login.trim().length() > 0)) {
                Authenticator.setDefault(new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(login,
                                password.toCharArray());
                        }
                    });
            }

            InputStreamReader inReq = new InputStreamReader(inputStream);
            OutputStreamWriter outReq = new OutputStreamWriter(con.getOutputStream());
            char[] buffer = new char[1024];
            int len;

            while ((len = inReq.read(buffer)) >= 0)
                outReq.write(buffer, 0, len);

            outReq.flush();
            outReq.close();
            inReq.close();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader is = new InputStreamReader(con.getInputStream());
                String response = readIs(is);
                is.close();
                if (LOGGER.isLoggable(Level.FINE))
                	LOGGER.fine("HTTP OK: " + response);
                res = true;
            } else {
            	if (LOGGER.isLoggable(Level.FINE))
            		LOGGER.fine("HTTP ERROR: " + con.getResponseMessage());
                res = false;
            }
        } catch (MalformedURLException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
            res = false;
        } catch (IOException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
            res = false;
        }
        return res;

    }

    /**
     *
     * @param geoserverREST_URL
     * @param content
     * @return
     */
    public static boolean putContent(URL geoserverREST_URL, String content, final String gsUser, final String gsPassword) {
        boolean res = false;

        try {
            HttpURLConnection con = (HttpURLConnection) geoserverREST_URL.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("PUT");

            final String login = gsUser;
            final String password = gsPassword;

            if ((login != null) && (login.trim().length() > 0)) {
                Authenticator.setDefault(new Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(login,
                                password.toCharArray());
                        }
                    });
            }

            OutputStreamWriter outReq = new OutputStreamWriter(con.getOutputStream());
            outReq.write(content);
            outReq.flush();
            outReq.close();

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStreamReader is = new InputStreamReader(con.getInputStream());
                String response = readIs(is);
                is.close();
                if (LOGGER.isLoggable(Level.INFO))
                	LOGGER.info("HTTP OK: " + response);
                res = true;
            } else {
            	if (LOGGER.isLoggable(Level.INFO))
            		LOGGER.info("HTTP ERROR: " + con.getResponseMessage());
                res = false;
            }
        } catch (MalformedURLException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
            res = false;
        } catch (IOException e) {
        	if (LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
            res = false;
        } 
            
        return res;

    }

    /**
     *
     * @param is
     * @return
     */
    private static String readIs(InputStreamReader is) {
        char[] inCh = new char[1024];
        StringBuffer input = new StringBuffer();
        int r;

        try {
            while ((r = is.read(inCh)) > 0) {
                input.append(inCh, 0, r);
            }
        } catch (IOException e) {
        	if(LOGGER.isLoggable(Level.SEVERE))
        		LOGGER.log(Level.SEVERE,e.getLocalizedMessage(),e);
        }

        return input.toString();
    }
    
    /**
	 * @param queryParams
	 * @return
	 */
	public static String getQueryString(Map<String, String> queryParams) {
		String queryString = "";

		if (queryParams != null)
			for (String key : queryParams.keySet()) {
				queryString += (queryString.length() == 0 ? "" : "&") + key + "=" + queryParams.get(key);
			}
		
		return queryString;
	}

	/**
	 *
	 * @param geoserverREST_URL
	 * @param file
	 * @return
	 */
	public static boolean putBinaryFileTo(final URL geoserverREST_URL, final File file, final String gsUser, final String gsPassword) {
		HttpURLConnection con=null;
	    try {
	        con = (HttpURLConnection) geoserverREST_URL.openConnection();
	        con.setDoOutput(true);
	        con.setDoInput(true);            	
			con.setRequestMethod("PUT");
		} catch (IOException e) {
	    	if (LOGGER.isLoggable(Level.SEVERE))
	    		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
	    	return false;
		}
	
	    final String login = gsUser;
	    final String password = gsPassword;
	
	    if ((login != null) && (login.trim().length() > 0)) {
	        Authenticator.setDefault(new Authenticator() {
	                protected PasswordAuthentication getPasswordAuthentication() {
	                    return new PasswordAuthentication(login, password.toCharArray());
	                }
	            });
	    }
	
	    OutputStream outputStream = null;
	    InputStream inputStream= null;
	    try {
	        outputStream = con.getOutputStream();
	        inputStream= new FileInputStream(file);
	        copyStream(inputStream, outputStream,true,true);
	    } catch (IOException e) {
	    	if (LOGGER.isLoggable(Level.SEVERE))
	    		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
	    	return false;
		}
	    finally{
	    	if(outputStream!=null)
	    		try{
	    			outputStream.close();
	    		}catch (Exception e) {
	    		}
	    		
	    	if(inputStream!=null)
	    		try{
	    			inputStream.close();
	    		}catch (Exception e) {
	    		}            		
	    }
	    
	    try {
	    	final int responseCode = con.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            InputStreamReader is = new InputStreamReader(con.getInputStream());
	            String response = readIs(is);
	            is.close();
	            if (LOGGER.isLoggable(Level.FINE))
	            	LOGGER.fine("HTTP OK: " + response);
	            return true;
	        } else {
	        	if (LOGGER.isLoggable(Level.FINE))
	        		LOGGER.fine("HTTP ERROR: " + con.getResponseMessage());
	        	return false;
	        }
	    } catch (IOException e) {
	    	if (LOGGER.isLoggable(Level.SEVERE))
	    		LOGGER.severe("HTTP ERROR: " + e.getLocalizedMessage());
	    	return false;
		}
	    
	
	}


	    /**
     * Copy {@link InputStream} to {@link OutputStream}.
     * 
     * @param sourceStream
     *            {@link InputStream} to copy from.
     * @param destinationStream
     *            {@link OutputStream} to copy to.
     * @param closeInput
     *            quietly close {@link InputStream}.
     * @param closeOutput
     *            quietly close {@link OutputStream}
     * @throws IOException
     *             in case something bad happens.
     */
    public static void copyStream(InputStream sourceStream, OutputStream destinationStream,
            boolean closeInput, boolean closeOutput) throws IOException {
        copyStream(sourceStream, destinationStream, DEFAULT_SIZE, closeInput, closeOutput);
    }

    /**
     * Copy {@link InputStream} to {@link OutputStream}.
     * 
     * @param sourceStream
     *            {@link InputStream} to copy from.
     * @param destinationStream
     *            {@link OutputStream} to copy to.
     * @param size
     *            size of the buffer to use internally.
     * @param closeInput
     *            quietly close {@link InputStream}.
     * @param closeOutput
     *            quietly close {@link OutputStream}
     * @throws IOException
     *             in case something bad happens.
     */
    public static void copyStream(InputStream sourceStream, OutputStream destinationStream,
            int size, boolean closeInput, boolean closeOutput) throws IOException {

        inputNotNull(sourceStream, destinationStream);
        byte[] buf = new byte[size];
        int n = -1;
        try {
            while (-1 != (n = sourceStream.read(buf))) {
                destinationStream.write(buf, 0, n);
                destinationStream.flush();
            }
        } finally {
            // closing streams and connections
            try {
                destinationStream.flush();
            } finally {
                try {
                    if (closeOutput)
                        destinationStream.close();
                } finally {
                    try {
                        if (closeInput)
                            sourceStream.close();
                    } finally {

                    }
                }
            }
        }
    }
    
    /**
     * Checks if the input is not null.
     * 
     * @param oList
     *            list of elements to check for null.
     */
    private static void inputNotNull(Object... oList) {
        for (Object o : oList)
            if (o == null)
                throw new NullPointerException("Input objects cannot be null");
    }
}