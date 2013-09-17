/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util.ftp;

/**
 * This class tokenizes the FTP URL
 * @author Massimiliano Fanciulli
 */
public class FTPLinkTokenizer {

    protected String port;
    protected String username = null;
    protected String password = null;
    protected String host = null;
    protected String schemaLessHost = null;
    protected String authLessHost = null;
    protected String path = null;

    public FTPLinkTokenizer(String url) {
        schemaLessHost = url.substring(6);
        if (schemaLessHost.indexOf("@") == -1) {
            authLessHost = schemaLessHost;
        } else {
            authLessHost = schemaLessHost.substring(schemaLessHost.indexOf("@") + 1);
            username = schemaLessHost.substring(0, schemaLessHost.indexOf(":"));
            password = schemaLessHost.substring(schemaLessHost.indexOf(":") + 1, schemaLessHost.indexOf("@"));
        }

        if (authLessHost.indexOf(":") == -1) {
            path = authLessHost.substring(authLessHost.indexOf("/"));
            host = authLessHost.substring(0, authLessHost.indexOf("/"));
            port = "21";
        } else {
            path = authLessHost.substring(authLessHost.indexOf("/"));
            host = authLessHost.substring(0, authLessHost.indexOf(":"));
            port = authLessHost.substring(authLessHost.indexOf(":")+1, authLessHost.indexOf("/"));
        }
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getPath() {
        return path;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }
}
