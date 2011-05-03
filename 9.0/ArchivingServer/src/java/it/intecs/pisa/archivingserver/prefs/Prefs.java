/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.prefs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 *
 * @author Massimiliano Fanciulli, Andrea Marongiu
 */
public class Prefs {
    public static final String WORKSPACE_DIR="workspace.dir";
    public static final String PUBLISH_LOCAL_FTP_PORT="publish.local.ftp.port";
    public static final String PUBLISH_LOCAL_FTP_IP="publish.local.ftp.ip";
    public static final String DOWNLOAD_DIR="download";
    public static final String STORE_DIR="storeData";
    public static final String PUBLISH_LOCAL_HTTP_URL="http.public.url";
    public static final String DELETE_AFTER="delete.after";
    public static final String PUBLISH_LOCAL_HTTP_ENABLE="publish.local.http.enable";
    public static final String PUBLISH_LOCAL_FTP_ENABLE="publish.local.ftp.enable";
    public static final String PUBLISH_LOCAL_FTP_ROOT_DIR="publish.local.ftp.rootdir";
    public static final String PUBLISH_LOCAL_FOLDER_DIR="publish.local.folder.dirs";
    public static final String PUBLISH_LOCAL_FOLDER_TYPES="publish.local.folder.types";
    public static final String PUBLISH_LOCAL_FOLDER_INTERVAL="publish.local.folder.interval";
    
    private static final String CONFIG_PROPERTIES_FILE_PATH="WEB-INF/classes/config.properties";
 
    public static Properties load(File webappDir) throws FileNotFoundException, IOException
    {
        Properties prop;
        File propFile;

        propFile=new File(webappDir,CONFIG_PROPERTIES_FILE_PATH);
        
        prop=new Properties();
        prop.load(new FileInputStream(propFile));

        return prop;
    }
    
    
    /**
     * 
     * @return Properties read from config.properties file
     */
    public static void save(File webappDir, JsonObject obj) throws IOException {
        Properties oldprops;
        File propFile;
        String deleteAfterUOM="",publisIntervalUOM="", propValue;

        oldprops = load(webappDir);
        propFile = new File(webappDir,CONFIG_PROPERTIES_FILE_PATH);

        Set<Entry<String, JsonElement>> entryset = obj.entrySet();
        Iterator<Entry<String, JsonElement>> iterator = entryset.iterator();

        while (iterator.hasNext()) {
            Entry<String, JsonElement> entry = iterator.next();
            if(entry.getKey().equalsIgnoreCase(DELETE_AFTER+".uom"))
                deleteAfterUOM=entry.getValue().getAsString();
            if(entry.getKey().equalsIgnoreCase(PUBLISH_LOCAL_FOLDER_INTERVAL+".uom"))
                publisIntervalUOM=entry.getValue().getAsString();
            if(oldprops.getProperty(entry.getKey())!=null)
             oldprops.setProperty(entry.getKey(), entry.getValue().getAsString());
        }

        if(!publisIntervalUOM.equals("")){
            propValue=oldprops.getProperty(PUBLISH_LOCAL_FOLDER_INTERVAL);
            if(!propValue.equals(""))
               oldprops.setProperty(PUBLISH_LOCAL_FOLDER_INTERVAL, propValue+publisIntervalUOM); 
        }
        
        if(!deleteAfterUOM.equals("")){
            propValue=oldprops.getProperty(DELETE_AFTER);
            if(!propValue.equals(""))
               oldprops.setProperty(DELETE_AFTER, propValue+deleteAfterUOM); 
        }
           
        oldprops.store(new FileOutputStream(propFile), null);
    }
    
    
     /**
     *
     * @param webappDir File
     * @return JSON String
     */
    public static String getJSONStringConfiguration(File webappDir)
                                    throws FileNotFoundException, IOException {
        String jsonStr;
        JsonObject obj = null;
        Gson gson;
        Properties props;
        Enumeration<Object> keys;

        props = Prefs.load(webappDir);
        keys = props.keys();

        obj = new JsonObject();
        while (keys.hasMoreElements()) {
            String key;
            key = (String) keys.nextElement();
           if(key.equalsIgnoreCase(DELETE_AFTER)){
                    obj.addProperty(key, 
                      props.getProperty(key).substring(0, props.getProperty(key).length()-1));
                    obj.addProperty(key+".uom", 
                      props.getProperty(key).charAt(props.getProperty(key).length()-1));
           }else
             if(key.equalsIgnoreCase(PUBLISH_LOCAL_FOLDER_INTERVAL)){
                    obj.addProperty(key, 
                      props.getProperty(key).substring(0, props.getProperty(key).length()-1));
                    obj.addProperty(key+".uom", 
                      props.getProperty(key).charAt(props.getProperty(key).length()-1));
             }else
                obj.addProperty(key, props.getProperty(key));
        }
        gson = new Gson();
        jsonStr = gson.toJson(obj);
        return jsonStr;
    }
    
    /**
     *
     * @param webappDir File
     * @return Workspace Directory File
     */
    public static File getWorkspaceFolder(File webappDir) throws FileNotFoundException, IOException{
        Properties props;
        props = Prefs.load(webappDir);
        File workspaceFolder=new File(props.getProperty(WORKSPACE_DIR));
        
        if(!workspaceFolder.exists())
           workspaceFolder.mkdirs(); 
        return workspaceFolder;
    }

    
    /**
     *
     * @param webappDir File
     * @return Dowload Directory File
     */
    public static File getDownloadFolder(File webappDir) throws FileNotFoundException, IOException{
        Properties props;
        props = Prefs.load(webappDir);
        File dowloadFolder=new File(props.getProperty(WORKSPACE_DIR),DOWNLOAD_DIR);
        if(!dowloadFolder.exists())
           dowloadFolder.mkdirs(); 
        return dowloadFolder;
    }
    
    
    /**
     *
     * @param webappDir File
     * @return Dowload Directory File
     */
    public static File getStoreFolder(File webappDir) throws FileNotFoundException, IOException{
        Properties props;
        props = Prefs.load(webappDir);
        File dowloadFolder=new File(props.getProperty(WORKSPACE_DIR),STORE_DIR);
        if(!dowloadFolder.exists())
           dowloadFolder.mkdirs(); 
        return dowloadFolder;
    }
  
}
