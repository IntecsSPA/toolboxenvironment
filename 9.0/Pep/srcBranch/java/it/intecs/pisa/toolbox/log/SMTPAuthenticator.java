/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.log;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import javax.mail.PasswordAuthentication;

/**
 *
 * @author massi
 */
public class SMTPAuthenticator extends javax.mail.Authenticator {

    protected static SMTPAuthenticator instance = new SMTPAuthenticator();

    public static SMTPAuthenticator getInstance() {
        return instance;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        ToolboxConfiguration toolboxConfiguration;
        toolboxConfiguration = ToolboxConfiguration.getInstance();

        String username = toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_AUTH_USERNAME);
        String password = toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SMTP_AUTH_PASSWORD);
        return new PasswordAuthentication(username, password);
    }
}
