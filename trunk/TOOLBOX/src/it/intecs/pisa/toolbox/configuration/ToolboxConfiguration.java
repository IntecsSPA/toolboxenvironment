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

import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class allows read/write of the configuration.
 * @author Massimiliano Fanciulli
 */
public  class ToolboxConfiguration {
    public static final String APACHE_ADDRESS = "apacheAddress";
    public static final String APACHE_PORT = "apachePort";
    public static final String INPUT_MESSAGES_LOG = "inputMessagesLog";
    public static final String OUTPUT_MESSAGES_LOG = "outputMessagesLog";
    public static final String LOG_LEVEL = "logLevel";
    public static final String LOG_PATTERN = "logPattern";
    public static final String LOG_DIR = "logDir";
    public static final String LOG_FILE_SIZE="logFileSize";
    public static final String PROXY_HOST = "proxyHost";
    public static final String PROXY_PORT = "proxyPort";
    public static final String QUEUING = "queuing";
    public static final String TOMCAT_PORT = "tomcatPort";
    public static final String TOMCAT_SSL_PORT = "tomcatSSLPort";
    public static final String TOOLBOX_VERSION_CHECK = "toolboxVersionCheck";
    public static final String SCHEMA_VERSION_CHECK = "schemaVersionCheck";
    public static final String GLOBAL_KEYSTORE="globalKeyStore";
    public static final String FIRST_TIME_CHECK = "firstTimeCheck";
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
    public static final String SMTP_PORT="smtpServerPort";
    public static final String MAIL_FROM="mailFrom";

    protected static final int CONFIG_VALUES_COUNT=37;

    protected Hashtable<String,String> configValues;
    protected static ToolboxConfiguration instance=new ToolboxConfiguration();

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
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+APACHE_ADDRESS+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+APACHE_PORT+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+INPUT_MESSAGES_LOG+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+OUTPUT_MESSAGES_LOG+"','true')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_DIR+"','/tmp')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_LEVEL+"','ALL')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_FILE_SIZE+"','100')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+LOG_PATTERN+"','<log level=\"%p\" thread=\"%t\" date=\"%d{yyyy-MMM-dd HH:mm:ss}\">%m</log>%n')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+PROXY_HOST+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+PROXY_PORT+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+QUEUING+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOMCAT_PORT+"','8080')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOMCAT_SSL_PORT+"','8443')");
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
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+FTP_SERVER_HOST+"','localhost')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+EBRR_REPO_HOME+"','/tmp')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SSE_PORTAL+"','services.eoportal.org')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOOLBOX_LEVEL_KEYSTORE+"','false')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+TOOLBOX_LEVEL_KEYSTORE_PASSWORD+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_AUTH_USERNAME+"','')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_AUTH_PASSWORD+"','')");
               //stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SSE_SUPPORT_TEAM_EMAIL+"','Service.Support.Environment.Operations.Team@esa.int')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SSE_SUPPORT_TEAM_EMAIL+"','toolbox@intecs.it')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+SMTP_PORT+"','25')");
               stm.executeUpdate("INSERT INTO T_TOOLBOX_CONFIGURATION VALUES('"+MAIL_FROM+"','')");
            }


        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
     }

   /* public void loadConfiguration() throws Exception
    {
        File configFile;
        Document doc;
        DOMUtil util;
        Element rootEl;
        Element ftpEl;
        Element errorEl;
        Element ebrrEl;

        util=new DOMUtil();
        configFile=getConfigurationFile();

        doc=util.fileToDocument(configFile);
        rootEl=doc.getDocumentElement();

        configValues.put(APACHE_ADDRESS, rootEl.getAttribute(APACHE_ADDRESS));
        configValues.put(APACHE_PORT, rootEl.getAttribute(APACHE_PORT));
        configValues.put(INPUT_MESSAGES_LOG, rootEl.getAttribute(INPUT_MESSAGES_LOG));
        configValues.put(OUTPUT_MESSAGES_LOG, rootEl.getAttribute(OUTPUT_MESSAGES_LOG));
        configValues.put(LOG_DIR, rootEl.getAttribute(LOG_DIR));
        configValues.put(LOG_LEVEL, rootEl.getAttribute(LOG_LEVEL));
        configValues.put(LOG_PATTERN, rootEl.getAttribute(LOG_PATTERN));
        configValues.put(PROXY_HOST, rootEl.getAttribute(PROXY_HOST));
        configValues.put(PROXY_PORT, rootEl.getAttribute(PROXY_PORT));
        configValues.put(QUEUING, rootEl.getAttribute(QUEUING));
        configValues.put(TOMCAT_PORT, rootEl.getAttribute(TOMCAT_PORT));
        configValues.put(TOMCAT_SSL_PORT, rootEl.getAttribute(TOMCAT_SSL_PORT));
        configValues.put(TOOLBOX_VERSION_CHECK, rootEl.getAttribute(TOOLBOX_VERSION_CHECK));
        configValues.put(SCHEMA_VERSION_CHECK, rootEl.getAttribute(SCHEMA_VERSION_CHECK));
        configValues.put(GLOBAL_KEYSTORE, rootEl.getAttribute(GLOBAL_KEYSTORE));

        ftpEl=DOMUtil.getChildByLocalName(rootEl, "FTPServer");
        configValues.put(FTP_ADMIN_DIR, ftpEl.getAttribute(FTP_ADMIN_DIR));
        configValues.put(FTP_ADMIN_PASSWORD, ftpEl.getAttribute(FTP_ADMIN_PASSWORD));
        configValues.put(FTP_PORT, ftpEl.getAttribute(FTP_PORT));
        configValues.put(FTP_POOL_PORT, ftpEl.getAttribute(FTP_POOL_PORT));

        errorEl=DOMUtil.getChildByLocalName(rootEl, "errorReport");
        configValues.put(COMPANY_CONTACT, errorEl.getAttribute(COMPANY_CONTACT));
        configValues.put(COMPANY_NAME, errorEl.getAttribute(COMPANY_NAME));
        configValues.put(MAIL_ERROR, errorEl.getAttribute(MAIL_ERROR));
        configValues.put(RECIPIENTS, errorEl.getAttribute(RECIPIENTS));
        configValues.put(SENDER, errorEl.getAttribute(SENDER));
        configValues.put(SMTP_SERVER, errorEl.getAttribute(SMTP_SERVER));

        ebrrEl=DOMUtil.getChildByLocalName(rootEl, "ebRR");
        configValues.put(COMPANY_CONTACT, errorEl.getAttribute(COMPANY_CONTACT));
    }

    public void saveConfiguration()
    {
        File configFile;
        DOMUtil util;
        Document doc;
        Element rootEl;
        Element ftpEl;
        Element errorEl;
        Element ebrrEl;

        util= new DOMUtil();
        doc=util.newDocument();

        rootEl=doc.createElement("toolboxConfiguration");
        doc.adoptNode(rootEl);

        rootEl.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/toolboxConfiguration");
        rootEl.setAttribute(APACHE_ADDRESS,configValues.get(APACHE_ADDRESS));
        rootEl.setAttribute(APACHE_PORT,configValues.get(APACHE_PORT));
        rootEl.setAttribute(INPUT_MESSAGES_LOG,configValues.get(INPUT_MESSAGES_LOG));
        rootEl.setAttribute(OUTPUT_MESSAGES_LOG,configValues.get(OUTPUT_MESSAGES_LOG));
        rootEl.setAttribute(LOG_DIR,configValues.get(LOG_DIR));
        rootEl.setAttribute(LOG_LEVEL,configValues.get(LOG_LEVEL));
        rootEl.setAttribute(LOG_PATTERN,configValues.get(LOG_PATTERN));
        rootEl.setAttribute(PROXY_HOST,configValues.get(PROXY_HOST));
        rootEl.setAttribute(PROXY_PORT,configValues.get(PROXY_PORT));
        rootEl.setAttribute(QUEUING,configValues.get(QUEUING));
        rootEl.setAttribute(TOMCAT_PORT,configValues.get(TOMCAT_PORT));
        rootEl.setAttribute(TOMCAT_SSL_PORT,configValues.get(TOMCAT_SSL_PORT));
        rootEl.setAttribute(TOOLBOX_VERSION_CHECK,configValues.get(TOOLBOX_VERSION_CHECK));
        rootEl.setAttribute(SCHEMA_VERSION_CHECK,configValues.get(SCHEMA_VERSION_CHECK));
        rootEl.setAttribute(GLOBAL_KEYSTORE,configValues.get(GLOBAL_KEYSTORE));

        ftpEl=doc.createElement("FTPServer");
        rootEl.appendChild(ftpEl);

        ftpEl.setAttribute(FTP_ADMIN_DIR, configValues.get(FTP_ADMIN_DIR));
        ftpEl.setAttribute(FTP_ADMIN_PASSWORD, configValues.get(FTP_ADMIN_PASSWORD));
        ftpEl.setAttribute(FTP_PORT, configValues.get(FTP_PORT));
        ftpEl.setAttribute(FTP_POOL_PORT, configValues.get(FTP_POOL_PORT));

        errorEl=doc.createElement("errorReport");
        rootEl.appendChild(errorEl);



        configFile=getConfigurationFile();

        
    }

    protected File getConfigurationFile()
    {
        Toolbox tbxServlet;
        File rootDir;
        File configFile;

        tbxServlet=Toolbox.getInstance();
        rootDir=tbxServlet.getRootDir();
        configFile=new File(rootDir,"WEB-INF/xml/toolboxConfiguration.xml");

        return configFile;
    }*/

}
