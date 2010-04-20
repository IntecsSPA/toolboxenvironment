/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 *
 * @author Massimiliano
 */
public class ProxyRedirectCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse response) throws Exception {
        File f;

        try {
            if (logger.isDebugEnabled()) {
                Enumeration e = request.getHeaderNames();
                while (e.hasMoreElements()) {
                    String name = (String) e.nextElement();
                    String value = request.getHeader(name);
                    logger.debug("request header:" + name + ":" + value);
                }
            }

            // Transfer bytes from in to outFile
            String method = request.getParameter("method");
            if (method.equalsIgnoreCase("GET")) {
                //execute the GET
                logger.info("Proxy GET Request recived.");
                String serverUrl = request.getParameter("url");
                if (serverUrl.startsWith("http://")) {
                    logger.info("GET param serverUrl:" + serverUrl);
                    HttpClient client = new HttpClient();
                    GetMethod httpget = new GetMethod(serverUrl);
                    client.executeMethod(httpget);

                    if (logger.isDebugEnabled()) {
                        Header[] respHeaders = httpget.getResponseHeaders();
                        for (int i = 0; i < respHeaders.length; ++i) {
                            String headerName = respHeaders[i].getName();
                            String headerValue = respHeaders[i].getValue();
                            logger.debug("responseHeaders:" + headerName + "=" + headerValue);
                        }
                    }

                    //dump response to outFile
                    if (httpget.getStatusCode() == HttpStatus.SC_OK) {
                        //force the response to have XML content type 
                        response.setContentType("text/xml");
                        String responseBody = httpget.getResponseBodyAsString().trim();
                        response.setContentLength(responseBody.length());
                        logger.info("responseBody:" + responseBody);
                        PrintWriter out = response.getWriter();
                        out.print(responseBody);
                        response.flushBuffer();
                    } else {
                        logger.error("Unexpected failure: " + httpget.getStatusLine().toString());
                    }
                    httpget.releaseConnection();
                } else if (serverUrl.startsWith("file:/")) {
                    response.setContentType("text/xml");

                    f = new File(new java.net.URI(serverUrl));
                    IOUtil.copy(new FileInputStream(f), response.getOutputStream());
                } else {
                    //throw new ServletException("only HTTP protocol supported");
                    // Decidere cosa fare in caso di errore  
                }

            } else {
                if (method.equalsIgnoreCase("POST")) {
                    logger.info("Proxy POST Request recived.");
                    String serverUrl = request.getHeader("serverUrl");
                    if (serverUrl.startsWith("http://")) {
                        PostMethod httppost = new PostMethod(serverUrl);

                        // Transfer bytes from in to outFile
                        logger.info("HTTP POST transfering..." + serverUrl);
                        PrintWriter out = response.getWriter();
                        String body = it.intecs.pisa.util.IOUtil.inputToString(request.getInputStream());

                        HttpClient client = new HttpClient();

                        httppost.setRequestBody(body);
                        if (0 == httppost.getParameters().length) {
                            logger.debug("No Name/Value pairs found ... pushing as raw_post_data");
                            httppost.setParameter("raw_post_data", body);
                        }
                        if (logger.isDebugEnabled()) {
                            logger.debug("Body = " + body);
                            NameValuePair[] nameValuePairs = httppost.getParameters();
                            logger.debug("NameValuePairs found: " + nameValuePairs.length);
                            for (int i = 0; i < nameValuePairs.length; ++i) {
                                logger.debug("parameters:" + nameValuePairs[i].toString());
                            }
                        }
                        //httppost.setRequestContentLength(PostMethod.CONTENT_LENGTH_CHUNKED);

                        client.executeMethod(httppost);
                        if (logger.isDebugEnabled()) {
                            Header[] respHeaders = httppost.getResponseHeaders();
                            for (int i = 0; i < respHeaders.length; ++i) {
                                String headerName = respHeaders[i].getName();
                                String headerValue = respHeaders[i].getValue();
                                logger.debug("responseHeaders:" + headerName + "=" + headerValue);
                            }
                        }

                        if (httppost.getStatusCode() == HttpStatus.SC_OK) {
                            response.setContentType("text/xml");
                            String responseBody = httppost.getResponseBodyAsString();
                            response.setContentLength(responseBody.length());
                            logger.info("responseBody:" + responseBody);
                            out.print(responseBody);
                        } else {
                            logger.error("Unexpected failure: " + httppost.getStatusLine().toString());
                        }
                        httppost.releaseConnection();
                    } else {
                        // throw new ServletException("only HTTP protocol supported");
                        // Decidere cosa fare in caso di errore   
                    }

                }
            }
        } catch (Throwable e) {
            String errorMsg = "Cannot redirect through proxy";
            throw new GenericException(errorMsg);
        }
    }

}
