/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package it.intecs.pisa.toolbox.configuration;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.resources.LogResourcesPersistence;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class allows read/write of the configuration.
 * @author Massimiliano Fanciulli
 */
public  class ToolboxConfiguration {
    public static final String ENDPOINT_ADDRESS = "Address";
    public static final String ENDPOINT_PORT = "Port";
    public static final String ENDPOINT_SSL_PORT = "SSLPort";
    public static final String INPUT_MESSAGES_LOG = "inputMessagesLog";
    public static final String OUTPUT_MESSAGES_LOG = "outputMessagesLog";
    public static final String LOG_LEVEL = "logLevel";
    public static final String LOG_TYPE = "logType";
    public static final String LOG_PATTERN = "logPattern";
    public static final String LOG_PATTERN_XML = "logPatternXML";
    public static final String LOG_DIR = "logDir";
    public static final String LOG_FILE_SIZE="logFileSize";
    public static final String PROXY_HOST = "proxyHost";
    public static final String PROXY_PORT = "proxyPort";
    public static final String QUEUING = "queuing";
    public static final String TOOLBOX_VERSION_CHECK = "toolboxVersionCheck";
    public static final String SCHEMA_VERSION_CHECK = "schemaVersionCheck";
    public static final String GLOBAL_KEYSTORE="globalKeyStore";
    public static final String FIRST_TIME_CHECK = "firstTimeCheck";
    public static final String CLASSIC_HEADER = "classicHeader";
    public static final String FTP_ADMIN_DIR = "adminDir";
    public static final String FTP_ADMIN_PASSWORD = "adminPassword";
    public static final String FTP_PORT = "port";
    public static final String FTP_POOL_PORT = "poolPort";
    public static final String FTP_SERVER_HOST = "serverHost";
    public static final String COMPANY_CONTACT = "companyContact";
    public static final String COMPANY_NAME = "companyName";
    public static final String MAIL_ERROR = "mailError";
    public static final String RECIPIENTS = "recipients";
    public static final String SENDER = "sender";
    public static final String SMTP_SERVER = "smtpServer";
    public static final String SMTP_AUTH_USERNAME="smtpServerAuthUsername";
    public static final String SMTP_AUTH_PASSWORD="smtpServerAuthPassword";
    public static final String EBRR_REPO_HOME="repoHomeDir";
    public static final String SSE_PORTAL="portalSSE";
    public static final String SSE_SUPPORT_TEAM_EMAIL="sseTeamEmail";
    public static final String TOOLBOX_LEVEL_KEYSTORE="tbxLevelKeystore";
    public static final String TOOLBOX_LEVEL_KEYSTORE_PASSWORD="tbxLevelKeystorePwd";
    public static final String TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS="tbxLevelKeystoreIdPAlias";
    public static final String SMTP_PORT="smtpServerPort";
    public static final String MAIL_FROM="mailFrom";
    public static final String CLEANUP_EVERY="cleanupEvery";

    public static final String GEOSERVER_URL="geoserverURL";
    public static final String GEOSERVER_VERSION="geoserverVersion";
    public static final String GEOSERVER_USERNAME="geoserverUsername";
    public static final String GEOSERVER_PASSWORD="geoserverPassword";
    public static final String GEOSERVER_TOOLBOX_WORKSPACE="geoserverToolboxWorkspace";



    protected static final int CONFIG_VALUES_COUNT=46;

    protected Hashtable<String,String> configValues;
    protected static ToolboxConfiguration instance=new ToolboxConfiguration();
    private String applicationName=null;
    
    public static ToolboxConfiguration getInstance()
    {
        return instance;
    }

    protected ToolboxConfiguration()
    {
        configValues=new Hashtable<String,String>();
    }

    public void setConfigurationValue(String key,String value)
    {
        configValues.put(key, value);
    }

    public void setConfigurationValue(String key,int value)
    {
        setConfigurationValue(key,Integer.toString(value));
    }

    public String getConfigurationValue(String key)
    {
        return configValues.get(key);
    }
    
    
   

    public void loadConfiguration() throws Exception
    {
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        ResultSet rs=null;

        try
        {
            db=ToolboxInternalDatabase.getInstance();
            stm=db.getStatement();
            rs=stm.executeQuery("SELECT * FROM T_TOOLBOX_CONFIGURATION");

            configValues.clear();
            while(rs.next())
            {
               configValues.put(rs.getString("NAME"), rs.getString("VALUE"));
            }
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }


    }
    
    public JsonObject getConfiguration(){
        
        // TODO Change with all configuration parameters
        JsonObject jsonConf=new JsonObject();
        /*---------- GET Configuration Check --------------------*/
        jsonConf.addProperty(ToolboxConfiguration.FIRST_TIME_CHECK, 
                getConfigurationValue(ToolboxConfiguration.FIRST_TIME_CHECK));
        /*------------------------------------------------------*/
        
        /*---------- GET EndPoint Configuration -----------------*/
        jsonConf.addProperty(ToolboxConfiguration.ENDPOINT_ADDRESS, 
                getConfigurationValue(ToolboxConfiguration.ENDPOINT_ADDRESS));
        
        jsonConf.addProperty(ToolboxConfiguration.ENDPOINT_PORT, 
                getConfigurationValue(ToolboxConfiguration.ENDPOINT_PORT));
        
        jsonConf.addProperty(ToolboxConfiguration.ENDPOINT_SSL_PORT, 
                getConfigurationValue(ToolboxConfiguration.ENDPOINT_SSL_PORT));
        /*------------------------------------------------------*/
        
        /*---------- GET Proxy Configuration -----------------*/
        jsonConf.addProperty(ToolboxConfiguration.PROXY_HOST, 
                getConfigurationValue(ToolboxConfiguration.PROXY_HOST));
        
        jsonConf.addProperty(ToolboxConfiguration.PROXY_PORT, 
                getConfigurationValue(ToolboxConfiguration.PROXY_PORT));
        /*-----------------------------------------------------*/
        
        /*---------- GET LOG Configuration ---------------------*/
        jsonConf.addProperty(ToolboxConfiguration.LOG_DIR, 
                getConfigurationValue(ToolboxConfiguration.LOG_DIR));
        
        jsonConf.addProperty(ToolboxConfiguration.LOG_FILE_SIZE, 
                getConfigurationValue(ToolboxConfiguration.LOG_FILE_SIZE));
        
        jsonConf.addProperty(ToolboxConfiguration.LOG_LEVEL, 
                getConfigurationValue(ToolboxConfiguration.LOG_LEVEL));
        
        jsonConf.addProperty(ToolboxConfiguration.LOG_PATTERN, 
                getConfigurationValue(ToolboxConfiguration.LOG_PATTERN));
        /*-----------------------------------------------------*/
        
        /*---------- GET FTP Configuration -----------------*/
        jsonConf.addProperty(ToolboxConfiguration.FTP_ADMIN_DIR, 
                getConfigurationValue(ToolboxConfiguration.FTP_ADMIN_DIR));
        
        jsonConf.addProperty(ToolboxConfiguration.FTP_ADMIN_PASSWORD, 
                getConfigurationValue(ToolboxConfiguration.FTP_ADMIN_PASSWORD));
        
        jsonConf.addProperty(ToolboxConfiguration.FTP_POOL_PORT, 
                getConfigurationValue(ToolboxConfiguration.FTP_POOL_PORT));
        
        jsonConf.addProperty(ToolboxConfiguration.FTP_PORT, 
                getConfigurationValue(ToolboxConfiguration.FTP_PORT));
       
        jsonConf.addProperty(ToolboxConfiguration.FTP_SERVER_HOST, 
                getConfigurationValue(ToolboxConfiguration.FTP_SERVER_HOST));
        /*--------------------------------------------------*/
        
        /*---------- GET Contact Information -----------------*/
        jsonConf.addProperty(ToolboxConfiguration.COMPANY_NAME, 
                getConfigurationValue(ToolboxConfiguration.COMPANY_NAME));
        
        jsonConf.addProperty(ToolboxConfiguration.COMPANY_CONTACT, 
                getConfigurationValue(ToolboxConfiguration.COMPANY_CONTACT));
        /*------------------------------------------------------*/
        
        /*---------- GET GEOSERVER Configuration -----------------*/
        jsonConf.addProperty(ToolboxConfiguration.GEOSERVER_PASSWORD, 
                getConfigurationValue(ToolboxConfiguration.GEOSERVER_PASSWORD));
        
        jsonConf.addProperty(ToolboxConfiguration.GEOSERVER_TOOLBOX_WORKSPACE, 
                getConfigurationValue(ToolboxConfiguration.GEOSERVER_TOOLBOX_WORKSPACE));
        
        jsonConf.addProperty(ToolboxConfiguration.GEOSERVER_URL, 
                getConfigurationValue(ToolboxConfiguration.GEOSERVER_URL));
        
        jsonConf.addProperty(ToolboxConfiguration.GEOSERVER_USERNAME, 
                getConfigurationValue(ToolboxConfiguration.GEOSERVER_USERNAME));
        
        jsonConf.addProperty(ToolboxConfiguration.GEOSERVER_VERSION, 
                getConfigurationValue(ToolboxConfiguration.GEOSERVER_VERSION));
        /*--------------------------------------------------------*/
        
        /*---------- GET SECURITY Configuration -----------------*/
        jsonConf.addProperty(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE, 
                getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE));
        
        jsonConf.addProperty(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD, 
                getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD));
        
        jsonConf.addProperty(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS, 
                getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS));
        /*------------------------------------------------------*/


       return jsonConf;
    }
    
    public void setAndSaveConfiguration(JsonObject jsonConfiguration) throws Exception{
    
        JsonElement jsonEl=null;
        JsonObject jsonOb=null;
        
        /*---------- First Time Configuration Check --------------------*/
        setConfigurationValue(ToolboxConfiguration.FIRST_TIME_CHECK, "false");
        /*--------------------------------------------------------------*/
        
        /*---------- EndPoint Configuration -----------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.ENDPOINT_ADDRESS);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.ENDPOINT_ADDRESS, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.ENDPOINT_PORT);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.ENDPOINT_PORT, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.ENDPOINT_SSL_PORT);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.ENDPOINT_SSL_PORT, jsonEl.getAsString());
        /*--------------------------------------------------*/
        
        /*---------- Proxy Configuration -----------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.PROXY_HOST);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.PROXY_HOST, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.PROXY_PORT);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.PROXY_PORT, jsonEl.getAsString());
        /*--------------------------------------------------*/
        
        /*---------- LOG Configuration ---------------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.LOG_DIR);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull)){
            setConfigurationValue(ToolboxConfiguration.LOG_DIR, jsonEl.getAsString());
            LogResourcesPersistence logResPersistence = LogResourcesPersistence.getInstance();
           // logResPersistence.setDirectory(resourcePersistenceDir);
        }
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.LOG_FILE_SIZE);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.LOG_FILE_SIZE, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.LOG_LEVEL);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.LOG_LEVEL, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.LOG_PATTERN);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.LOG_PATTERN, jsonEl.getAsString());
        /*--------------------------------------------------*/
        
        /*---------- FTP Configuration -----------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.FTP_ADMIN_DIR);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.FTP_ADMIN_DIR, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.FTP_ADMIN_PASSWORD);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.FTP_ADMIN_PASSWORD, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.FTP_POOL_PORT);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.FTP_POOL_PORT, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.FTP_PORT);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.FTP_PORT, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.FTP_SERVER_HOST);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.FTP_SERVER_HOST, jsonEl.getAsString());
        /*----------------------------------------------*/
        
        /*---------- Contact Information -----------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.COMPANY_NAME);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.COMPANY_NAME, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.COMPANY_CONTACT);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.COMPANY_CONTACT, jsonEl.getAsString());
        
       /* jsonEl=jsonConfiguration.get(ToolboxConfiguration.c);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.COMPANY_CONTACT, jsonEl.getAsString());*/
        /*--------------------------------------------------*/
        
        /*---------- GEOSERVER Configuration -----------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.GEOSERVER_PASSWORD);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.GEOSERVER_PASSWORD, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.GEOSERVER_TOOLBOX_WORKSPACE);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.GEOSERVER_TOOLBOX_WORKSPACE, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.GEOSERVER_URL);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.GEOSERVER_URL, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.GEOSERVER_USERNAME);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.GEOSERVER_USERNAME, jsonEl.getAsString());
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.GEOSERVER_VERSION);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.GEOSERVER_VERSION, jsonEl.getAsString());
        /*----------------------------------------------*/
        
        /*---------- SECURITY Configuration -----------------*/
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE); //TODO: Local Path KEYSTORE File
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull)){
            Toolbox tbx=Toolbox.getInstance();
            File keyStore= new File(tbx.getRootDir(),"WEB-INF/persistence/tbxLevelKeystore.jks");
            keyStore.createNewFile();
            String filePath=((JsonObject)jsonEl).get("uploadID").getAsString();
            IOUtil.copy(new FileInputStream(filePath), new FileOutputStream(keyStore));
            new File(filePath).delete();
            setConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE, "true");
        }
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD, jsonEl.getAsString()); 
        
        jsonEl=jsonConfiguration.get(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS);
        if(!(jsonEl==null || jsonEl instanceof com.google.gson.JsonNull))
            setConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS, jsonEl.getAsString());
        /*----------------------------------------------*/
        
         saveConfiguration();
        
       
    }

     public void saveConfiguration() throws Exception
     {
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        Enumeration<String> keys;
        String sql;
        String key;

        try
        {
            db=ToolboxInternalDatabase.getInstance();
            stm=db.getStatement();
            stm.executeUpdate("DELETE FROM T_TOOLBOX_CONFIGURATION");

            keys=configValues.keys();
            while(keys.hasMoreElements())
            {
                key=keys.nextElement();
                sql="INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+key+"','"+configValues.get(key)+"')";
                stm.executeUpdate(sql);
            }
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
     }

     /**
      * This method is used to override a weird behaviour of the DB when
      * INSERT statements are put in the TOOLOX.script file
      */
     public void initializeConfigTable() throws Exception
     {
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        ResultSet rs=null;
        Enumeration<String> keys;
        String sql;
        String key;

        try
        {
            db=ToolboxInternalDatabase.getInstance();
            stm=db.getStatement();
            rs=stm.executeQuery("SELECT COUNT(NAME) AS C FROM T_TOOLBOX_CONFIGURATION");
            rs.next();

            if(rs.getInt("C")!=CONFIG_VALUES_COUNT)
            {
               stm.executeUpdate("DELETE FROM T_TOOLBOX_CONFIGURATION");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+ENDPOINT_ADDRESS+"','"+java.net.InetAddress.getLocalHost().getHostAddress()+"')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+ENDPOINT_PORT+"','8080')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+ENDPOINT_SSL_PORT+"','8443')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+INPUT_MESSAGES_LOG+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+OUTPUT_MESSAGES_LOG+"','true')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_DIR+"','/tmp')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_LEVEL+"','ALL')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_FILE_SIZE+"','100')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_TYPE+"','json')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_PATTERN_XML+"','<log level=\"%p\" thread=\"%t\" date=\"%d{yyyy-MM-dd HH:mm:ss}\">%m</log>%n')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_PATTERN+"',',{ \"level\":\"%p\", \"thread\":\"%t\", \"date\":\"%d{yyyy-MM-dd HH:mm:ss}\", \"text\":\"%m\" }')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+PROXY_HOST+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+PROXY_PORT+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+QUEUING+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOOLBOX_VERSION_CHECK+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SCHEMA_VERSION_CHECK+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+GLOBAL_KEYSTORE+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FTP_ADMIN_DIR+"','/tmp')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FTP_ADMIN_PASSWORD+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FTP_PORT+"','2120')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FTP_POOL_PORT+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+COMPANY_CONTACT+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+COMPANY_NAME+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+MAIL_ERROR+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+RECIPIENTS+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SENDER+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_SERVER+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FIRST_TIME_CHECK+"','true')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+CLASSIC_HEADER+"','true')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FTP_SERVER_HOST+"','localhost')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+EBRR_REPO_HOME+"','/tmp')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SSE_PORTAL+"','services.eoportal.org')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOOLBOX_LEVEL_KEYSTORE+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOOLBOX_LEVEL_KEYSTORE_PASSWORD+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_AUTH_USERNAME+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_AUTH_PASSWORD+"','')");
               //stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SSE_SUPPORT_TEAM_EMAIL+"','Service.Support.Environment.Operations.Team@esa.int')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SSE_SUPPORT_TEAM_EMAIL+"','toolbox@intecs.it')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_PORT+"','25')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+MAIL_FROM+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+CLEANUP_EVERY+"','1W')");

               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+GEOSERVER_URL+"','http://localhost:8080/geoserver')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+GEOSERVER_VERSION+"','2.0.1')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+GEOSERVER_USERNAME+"','admin')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+GEOSERVER_PASSWORD+"','geoserver')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+GEOSERVER_TOOLBOX_WORKSPACE+"','toolbox')");


            }


        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
     }

    public String[] getConfigurationKeys() throws Exception {
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        ResultSet rs=null;

        try
        {
            db=ToolboxInternalDatabase.getInstance();
            stm=db.getStatement();
            rs=stm.executeQuery("SELECT NAME FROM T_TOOLBOX_CONFIGURATION");

            ArrayList<String> keys;
            keys=new ArrayList<String>();

            while(rs.next())
            {
               keys.add(rs.getString("NAME"));
            }

            return keys.toArray(new String[0]);
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }
    }

    /**
     * @return the applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * @param applicationName the applicationName to set
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

}
