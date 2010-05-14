/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;

/**
 *
 * @author Massimiliano
 */
public class ConfigureToolboxCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        Hashtable<String, FileItem> mimeparts = this.parseMultiMime(request);

        String ftpAdminDir = getStringFromMimeParts(mimeparts, "ftpAdminDir");
        String ftpAdminPassword = getStringFromMimeParts(mimeparts, "ftpAdminPassword");
        String ftpPort = getStringFromMimeParts(mimeparts, "ftpPort");
        String ftpPoolPort = getStringFromMimeParts(mimeparts, "ftpPoolPort");
        String ftpServerHost = getStringFromMimeParts(mimeparts, "ftpServerHost");
        String logDir = getStringFromMimeParts(mimeparts, "logDir");
        String apacheAddress = getStringFromMimeParts(mimeparts, "apacheAddress");
        String proxyHost = getStringFromMimeParts(mimeparts, "proxyHost");
        String tomcatPort = (getStringFromMimeParts(mimeparts, "tomcatPort").length() == 0 ? "8080" : getStringFromMimeParts(mimeparts, "tomcatPort"));
        String apachePort = (apacheAddress.length() == 0 ? "" : (getStringFromMimeParts(mimeparts, "apachePort").length() == 0 ? "80" : getStringFromMimeParts(mimeparts, "apachePort")));
        String proxyPort = (proxyHost.length() == 0 ? "" : (getStringFromMimeParts(mimeparts, "proxyPort").length() == 0 ? "80" : getStringFromMimeParts(mimeparts, "proxyPort")));
        String tomcatSSLPort = (getStringFromMimeParts(mimeparts, "tomcatSSLPort").length() == 0 ? "" : getStringFromMimeParts(mimeparts, "tomcatSSLPort"));
        String sender = getStringFromMimeParts(mimeparts, "sender");
        String recipients = getStringFromMimeParts(mimeparts, "recipients");
        String smtpServer = getStringFromMimeParts(mimeparts, "smtpServer");
        String smtpServerPort = getStringFromMimeParts(mimeparts, "smtpServerPort");
        String smtpServerAuthUsername = getStringFromMimeParts(mimeparts, "smtpServerAuthUsername");
        String smtpServerAuthPassword = getStringFromMimeParts(mimeparts, "smtpServerAuthPassword");
        String companyName = getStringFromMimeParts(mimeparts, "companyName");
        String logFileSize = (getStringFromMimeParts(mimeparts, "logFileSize").length() == 0 ? "100KB" : getStringFromMimeParts(mimeparts, "logFileSize"));
        String ebRRRepoHome = getStringFromMimeParts(mimeparts, "ebRRRepoHome");
        String logLevel=getStringFromMimeParts(mimeparts, "logLevel");
        String companyContact=getStringFromMimeParts(mimeparts, "companyContact");
        String mailFrom=getStringFromMimeParts(mimeparts, "mailFrom");
        boolean mailErrorSSE = (getStringFromMimeParts(mimeparts, "mailErrorSSE") != null);
        boolean mailErrorSP = (getStringFromMimeParts(mimeparts, "mailErrorSP") != null);

        new File(logDir).mkdirs();
        new File(ftpAdminDir).mkdirs();

        ToolboxConfiguration toolboxConfiguration;
        toolboxConfiguration = ToolboxConfiguration.getInstance();

        if (getStringFromMimeParts(mimeparts, "globalQueuing") != null) {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.QUEUING, "true");
        } else {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.QUEUING, "false");
        }

        String boolValue;

        boolValue=getStringFromMimeParts(mimeparts, "toolboxVersionCheck") != null ? "true" :"false";
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.TOOLBOX_VERSION_CHECK, boolValue);

        boolValue=getStringFromMimeParts(mimeparts, "schemaVersionCheck") != null ? "true" :"false";
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SCHEMA_VERSION_CHECK, boolValue);

        boolValue=getStringFromMimeParts(mimeparts, "inputMessagesLog") != null ? "true" :"false";
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.INPUT_MESSAGES_LOG, boolValue);

        boolValue=getStringFromMimeParts(mimeparts, "outputMessagesLog") != null ? "true" :"false";
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.OUTPUT_MESSAGES_LOG, boolValue);
     
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.APACHE_ADDRESS, apacheAddress);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.APACHE_PORT, apachePort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.PROXY_HOST, proxyHost);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.PROXY_PORT, proxyPort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.TOMCAT_PORT, tomcatPort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.TOMCAT_SSL_PORT, tomcatSSLPort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.LOG_DIR, logDir);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.LOG_LEVEL, logLevel);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.LOG_FILE_SIZE, logFileSize);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.FTP_ADMIN_DIR, ftpAdminDir);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.FTP_ADMIN_PASSWORD, ftpAdminPassword);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.FTP_PORT, ftpPort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.FTP_POOL_PORT, ftpPoolPort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.FTP_SERVER_HOST, ftpServerHost);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SENDER, sender);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.RECIPIENTS, recipients);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.COMPANY_NAME, companyName);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.COMPANY_CONTACT, companyContact);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SMTP_SERVER, smtpServer);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SMTP_AUTH_USERNAME, smtpServerAuthUsername);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SMTP_AUTH_PASSWORD, smtpServerAuthPassword);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SMTP_SERVER, smtpServer);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.SMTP_PORT, smtpServerPort);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.MAIL_FROM, mailFrom);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.EBRR_REPO_HOME, ebRRRepoHome);
        toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.FIRST_TIME_CHECK, "false");

        if (mailErrorSSE && mailErrorSP) {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.MAIL_ERROR,"BOTH");
        } else if (mailErrorSSE) {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.MAIL_ERROR,"SSE");
        } else if (mailErrorSP) {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.MAIL_ERROR,"SP");
        } else {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.MAIL_ERROR,"");
        }

        File keyStore;
        Toolbox tbx;
        FileItem kfi;

        tbx=Toolbox.getInstance();

        kfi=mimeparts.get("keystore");

        if(kfi!=null && kfi.getSize() != 0)
        {
             keyStore= new File(tbx.getRootDir(),"WEB-INF/persistence/tbxLevelKeystore.jks");
             kfi.write(keyStore);
             toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE,"true");
        }


        if (getStringFromMimeParts(mimeparts, "keystorePwd") != null) {
            toolboxConfiguration.setConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD,getStringFromMimeParts(mimeparts, "keystorePwd"));
        }
        
        toolboxConfiguration.saveConfiguration();

        TBXService[] services;
        ServiceManager servMan;

        servMan=ServiceManager.getInstance();
        services=servMan.getServicesAsArray();
        for(TBXService ser:services)
        {
            ser.attemptToDeployWSDLAndSchemas();
        }

         setebRRRepoHomeOnPropertyFile(ebRRRepoHome);
         resp.sendRedirect("configureToolboxRequest.jsp?pageStatus=disabled&configurationChanged=true");
    }

    private void setebRRRepoHomeOnPropertyFile(String ebRRRepoHome) {
        Properties propfile;
        FileInputStream stream;
        FileOutputStream outstream;
        File props;
        Toolbox tbxServlet;

        try
        {
            tbxServlet=Toolbox.getInstance();
            props=new File(tbxServlet.getRootDir(),"WEB-INF/plugins/ebRRPlugin/resources/common.properties");

            stream=new FileInputStream(props);
            propfile=new Properties();
            propfile.load(stream);

            propfile.setProperty("repository.root", ebRRRepoHome);

            outstream=new FileOutputStream(props);
            propfile.store(outstream,"");
        }
        catch(Exception e)
        {
            logger.warn("Cannot save repository home for ergo");
        }
    }
}
