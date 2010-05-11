/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.log;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.resources.TextResourcesPersistence;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import it.intecs.pisa.util.DateUtil;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ErrorMailer {
    private static final String TOOLBOX = "TOOLBOX";
    private static final String ERROR_REPORT = "ERROR REPORT";
    private static final String ERROR_MESSAGE = "Error message: ";
    /**
     *
     * @param errorMsg
     */
    public static void send(long serviceInstanceId, String errorMsg) {
        Logger tbxLogger;
        Toolbox tbx;

        tbx=Toolbox.getInstance();
        tbxLogger=tbx.getLogger();

        try {
            ToolboxConfiguration toolboxConfiguration;
            toolboxConfiguration = ToolboxConfiguration.getInstance();

            String destinations=toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR);
            if(destinations!=null && destinations.equals("")==false)
            {
                boolean authenabled= isAuthEnabled();

                Properties properties = System.getProperties();
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.host", toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_SERVER));
                properties.put("mail.smtp.auth", authenabled?"true":"false");
                properties.put("mail.smtp.starttls.enable","true");
                
                String port;
                port=toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_PORT);
                properties.put("mail.smtp.port", port);

                if(port.equals("465"))
                {
                    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    properties.put("mail.smtp.socketFactory.fallback", "false");
                    properties.put("mail.smtp.socketFactory.port", "465");
                    properties.put("mail.smtp.ssl","true");
                }
                
                SMTPAuthenticator auth=SMTPAuthenticator.getInstance();
                Session session = Session.getInstance(properties, auth);
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("massimiliano.fanciulli@intecs.it"));

                setRecipients(message);

                message.setSubject(TOOLBOX + " " + Toolbox.getInstance().getToolboxVersion()+ " - " + ERROR_REPORT);
                MimeMultipart multipart = new MimeMultipart();
                MimeBodyPart part = new MimeBodyPart();

                String emailText;

                emailText=getContent(serviceInstanceId, errorMsg);
                part.setText(emailText);
                multipart.addBodyPart(part);
                message.setContent(multipart);
                message.setSentDate(new Date());
                Transport.send(message);

                tbxLogger.info("Error email sent to selected recipients");


                storeEmailOnDisk(serviceInstanceId,emailText);
            }
        } catch (Exception e) {
            e.printStackTrace();
            tbxLogger.error("Cannot send error email. Cause:"+ e.getMessage());
        }
    }

     /**
     *
     * @param errorMsg
     */
    public static void send(String serviceName, String soapAction, String messageId, String orderId, String errorMsg) {
        Logger tbxLogger;
        Toolbox tbx;

        tbx=Toolbox.getInstance();
        tbxLogger=tbx.getLogger();

        try {
            ToolboxConfiguration toolboxConfiguration;
            toolboxConfiguration = ToolboxConfiguration.getInstance();

            String destinations=toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR);
            if(destinations!=null && destinations.equals("")==false)
            {
                boolean authenabled= isAuthEnabled();

                Properties properties = System.getProperties();
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.host", toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_SERVER));
                properties.put("mail.smtp.auth", authenabled?"true":"false");
                properties.put("mail.smtp.starttls.enable","true");

                String port;
                port=toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_PORT);
                properties.put("mail.smtp.port", port);

                if(port.equals("465"))
                {
                    properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    properties.put("mail.smtp.socketFactory.fallback", "false");
                    properties.put("mail.smtp.socketFactory.port", "465");
                    properties.put("mail.smtp.ssl","true");
                }

                SMTPAuthenticator auth=SMTPAuthenticator.getInstance();
                Session session = Session.getInstance(properties, auth);
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress("massimiliano.fanciulli@intecs.it"));

                setRecipients(message);

                message.setSubject(TOOLBOX + " " + Toolbox.getInstance().getToolboxVersion()+ " - " + ERROR_REPORT);
                MimeMultipart multipart = new MimeMultipart();
                MimeBodyPart part = new MimeBodyPart();

                String emailText;
                emailText=getContent(serviceName, soapAction, messageId, orderId, errorMsg);
                part.setText(emailText);
                multipart.addBodyPart(part);
                message.setContent(multipart);
                message.setSentDate(new Date());
                Transport.send(message);

                tbxLogger.info("Error email sent to selected recipients");
            }
        } catch (Exception e) {
            e.printStackTrace();
            tbxLogger.error("Cannot send error email. Cause:"+ e.getMessage());
        }
    }

    private static void setRecipients(MimeMessage message) throws AddressException, MessagingException {
        ToolboxConfiguration toolboxConfiguration;
        toolboxConfiguration = ToolboxConfiguration.getInstance();

        String destinations = toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.MAIL_ERROR);

        if (destinations.equals("SP") || destinations.equals("BOTH")) {
            StringTokenizer sT = new StringTokenizer( toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.RECIPIENTS));
            while (sT.hasMoreTokens()) {
                message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(sT.nextToken()));
            }
        }

        if (destinations.equals("SSE") || destinations.equals("BOTH")) {

            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SSE_SUPPORT_TEAM_EMAIL)));
        }
    }

    private static String getContent(long instanceId, String errorMsg) {
        String content;
        try
        {
            content=DateUtil.getCurrentDateAsString("dd MM yyyy HH:mm:ss.SSS")+'\n';

            String serviceName;
            serviceName=InstanceInfo.getServiceNameFromInstanceId(instanceId);
            content+="Service name: "+serviceName+'\n';

            String soapAction;
            soapAction=InstanceInfo.getSOAPActionFromInstanceId(instanceId);
            content+="SOAP action: "+soapAction+'\n';

            String instancekey;
            instancekey=InstanceInfo.getInstanceKeyFromInstanceId(instanceId);
            content+="Message ID: "+instancekey+'\n';

            String orderId;
            orderId=InstanceInfo.getOrderIdFromInstanceId(instanceId);
            content+="Order ID: "+orderId+'\n';
        }
        catch(Exception e)
        {
            content=new String();
        }
        content+=ERROR_MESSAGE+errorMsg;
        return content;
    }

    private static String getContent(String serviceName, String soapAction, String instancekey, String orderId, String errorMsg) {
        String content;
        try
        {
            content=DateUtil.getCurrentDateAsString("dd MM yyyy HH:mm:ss.SSS")+'\n';

            if(serviceName!=null)
                content+="Service name: "+serviceName+'\n';
            
            if(soapAction!=null)
                content+="SOAP action: "+soapAction+'\n';

            if(instancekey!=null)
                content+="Message ID: "+instancekey+'\n';

            if(orderId!=null)
                content+="Order ID: "+orderId+'\n';
        }
        catch(Exception e)
        {
            content=new String();
        }
        content+=ERROR_MESSAGE+errorMsg;
        return content;
    }

    private static boolean isAuthEnabled() {
        try
        {
            ToolboxConfiguration toolboxConfiguration;
            toolboxConfiguration = ToolboxConfiguration.getInstance();

            String username = toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_AUTH_USERNAME);
            String password = toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_AUTH_PASSWORD);

            return username!= null && username.equals("")==false && password !=null && password.equals("")==false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    private static void storeEmailOnDisk(long serviceInstanceId,String emailText) {
        TextResourcesPersistence  instance;
        String id;
        try
        {
            instance=TextResourcesPersistence.getInstance();
            id= instance.storeText(emailText);

            InstanceResources.storeResourceEntry(id, serviceInstanceId, InstanceResources.TYPE_ERROR_EMAIL);
        }
        catch(Exception e)
        {

        }
    }

    
}
