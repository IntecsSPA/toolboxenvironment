/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import it.intecs.pisa.toolbox.plugins.security.SAMLdecryptor;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;


import javax.xml.namespace.QName;

//import javax.xml.soap.*;

import org.apache.axiom.om.OMElement;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 *
 * @author mariarosaria
 */
public class DecryptSAMLToken implements Command {

    static public String ENCRYPTED_DATA_NAMESPACE = "http://www.w3.org/2001/04/xmlenc#";
    static public String ENCRYPTED_DATA = "EncryptedData";
    public static final String WS_SECURITY_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WS_SECURITY = "Security";
    /*
     * logger for the class
     */
    static Logger logger = Logger.getLogger(DecryptSAMLToken.class);

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);

    }

    @Override
    public Result execute(ChainContext cc) {

        try {

            logger.info("Token decryption process started");

            MessageContext msgCtx = (MessageContext) cc.getAttribute(CommandsConstants.MESSAGE_CONTEXT);
            SOAPEnvelope envelope = msgCtx.getEnvelope();
            SOAPHeader soapHeader = envelope.getHeader();
            OMElement wsSecurityOM = soapHeader.getFirstChildWithName(new QName(WS_SECURITY_NAMESPACE, WS_SECURITY));

            Element wsSecurityDOM = XMLUtils.toDOM(wsSecurityOM);

            String serviceName = msgCtx.getAxisService().getName();
            TBXService service = ServiceManager.getInstance().getService(serviceName);

            String keyStorePath = ToolboxSecurityConfigurator.getJKSlocation(service);
            String keyStorePwd = ToolboxSecurityConfigurator.getJKSpassword(service);
            String alias = ToolboxSecurityConfigurator.getJKSuser(service);

            File keystoreFile = new File(keyStorePath);

            SAMLdecryptor decryptor = new SAMLdecryptor();
            Element tokenDecrypted = decryptor.decrypt(wsSecurityDOM, keystoreFile, alias, keyStorePwd);

            if (tokenDecrypted == null) {
                logger.error("Error while decrypting SAML token");
                return new Result(Result.FAIL, "Error while decrypting SAML token.");
            }

            OMElement tokenEncrypted = wsSecurityOM.getFirstChildWithName(new QName(ENCRYPTED_DATA_NAMESPACE, ENCRYPTED_DATA));
            tokenEncrypted.detach();
            wsSecurityOM.addChild(XMLUtils.toOM(tokenDecrypted));

            logger.info("Token decryption process finished successfully");

            return new Result(Result.SUCCESS);
        } catch (Exception e) {
            logger.error("Error while decrypting SAML token: " + e.getMessage());
            return new Result(Result.FAIL, "Error while decrypting SAML token.");
        }

    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
