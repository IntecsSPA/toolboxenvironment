package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.w3c.dom.Element;

public class SendMailTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element sendMail) throws Exception {
        String smtpServer;
        String authUsername;
        String authPassword;


        smtpServer = this.engine.evaluateString(sendMail.getAttribute(SMTP_SERVER),IEngine.EngineStringType.ATTRIBUTE);
        authUsername = this.engine.evaluateString(sendMail.getAttribute("authUsername"),IEngine.EngineStringType.ATTRIBUTE);
        authPassword = this.engine.evaluateString(sendMail.getAttribute( "authPassword"),IEngine.EngineStringType.ATTRIBUTE);

        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", smtpServer);

        Session session = Session.getInstance(properties, null);

        if ((authUsername.equals("") && authPassword.equals("")) == false) {
            session.getProperties().put("mail.smtp.auth", "true");
        }


        MimeMessage message = new MimeMessage(session);
        Iterator children = DOMUtil.getChildren(sendMail).iterator();
        message.setFrom(new InternetAddress(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild((Element) children.next())))));
        message.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild((Element) children.next())))));
        Element child = (Element) children.next();
        if (!child.getLocalName().equals(SUBJECT)) {
            Iterator recipients = DOMUtil.getChildren(child).iterator();
            Element recipient;
            String recipientTypeString;
            javax.mail.Message.RecipientType recipientType;
            while (recipients.hasNext()) {
                recipient = (Element) recipients.next();
                recipientTypeString = recipient.getLocalName();
                if (recipientTypeString.equals(TO)) {
                    recipientType = javax.mail.Message.RecipientType.TO;
                } else if (recipientTypeString.equals(CC)) {
                    recipientType = javax.mail.Message.RecipientType.CC;
                } else {
                    recipientType = javax.mail.Message.RecipientType.BCC;
                }
                message.addRecipient(recipientType,
                        new InternetAddress(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(recipient)))));
            }
            child = (Element) children.next();

        }
        message.setSubject(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(child))));
        MimeMultipart multipart;
        BodyPart part = new MimeBodyPart();
        child = (Element) children.next();

        if (child.getLocalName().equals("text")) {
            multipart = new MimeMultipart();
            part.setText(String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(child))));
        } else {
            multipart = new MimeMultipart("alternative");
            part.setContent(this.executeChildTag(DOMUtil.getFirstChild(child)), "text/html");
        }
        multipart.addBodyPart(part);
        String filePath, fileName;
        while (children.hasNext()) {
            child = (Element) children.next();
            filePath = String.valueOf(this.executeChildTag(DOMUtil.getFirstChild(child)));
            part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(new FileDataSource(filePath)));

            fileName = this.engine.evaluateString(child.getAttribute(NAME),IEngine.EngineStringType.ATTRIBUTE);
            part.setFileName(fileName.length() == 0 ? new File(filePath).getName() : fileName);
            multipart.addBodyPart(part);
        }
        message.setContent(multipart);
        message.setSentDate(new Date());

        Transport tr = session.getTransport("smtp");
        tr.connect(smtpServer, 25, authUsername, authPassword);
        tr.sendMessage(message, message.getAllRecipients());
        tr.close();

        return null;
    }
}
