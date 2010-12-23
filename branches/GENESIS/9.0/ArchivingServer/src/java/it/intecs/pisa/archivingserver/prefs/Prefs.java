/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class Prefs {
    public static final String PUBLISH_LOCAL_FTP_PORT="publish.local.ftp.port";
    public static final String PUBLISH_LOCAL_FTP_IP="publish.local.ftp.ip";
    public static final String DOWNLOAD_DIR="download.dir";
    public static final String PUBLISH_LOCAL_HTTP_URL="http.public.url";
    public static final String DELETE_AFTER="delete.after";
    public static final String PUBLISH_LOCAL_HTTP_ENABLE="publish.local.http.enable";
    public static final String PUBLISH_LOCAL_FTP_ENABLE="publish.local.ftp.enable";
    public static final String PUBLISH_LOCAL_FTP_ROOT_DIR="publish.local.ftp.rootdir";
    public static final String PUBLISH_LOCAL_FOLDER_DIR="publish.local.folder.dir";
    public static final String PUBLISH_LOCAL_FOLDER_INTERVAL="publish.local.folder.interval";
 
    public static Properties load(File webappDir) throws FileNotFoundException, IOException
    {
        Properties prop;
        File propFile;

        propFile=new File(webappDir,"WEB-INF/classes/config.properties");
        
        prop=new Properties();
        prop.load(new FileInputStream(propFile));

        return prop;
    }
}
