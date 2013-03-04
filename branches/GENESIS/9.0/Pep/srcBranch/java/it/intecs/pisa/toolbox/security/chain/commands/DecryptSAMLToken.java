/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain.commands;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.plugins.security.SAMLdecryptor;
import it.intecs.pisa.toolbox.security.ToolboxSecurityConfigurator;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

//import javax.xml.soap.*;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.util.XMLUtils;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

//            Element tokenEncrypted = (Element) wsSecurityDOM.getElementsByTagNameNS(ENCRYPTED_DATA_NAMESPACE, ENCRYPTED_DATA).item(0);
//            wsSecurityDOM.replaceChild(tokenDecrypted, tokenEncrypted);
//
//            wsSecurityOM.detach();
//            soapHeader.addChild(XMLUtils.toOM(wsSecurityDOM));

            OMElement tokenEncrypted = wsSecurityOM.getFirstChildWithName(new QName(ENCRYPTED_DATA_NAMESPACE, ENCRYPTED_DATA));
            tokenEncrypted.detach();
            wsSecurityOM.addChild(XMLUtils.toOM(tokenDecrypted));

            return new Result(Result.SUCCESS);
        } catch (Exception e) {
            logger.error("Error when decrypting token: " + e.getMessage());
            return new Result(Result.FAIL);
        }

    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
