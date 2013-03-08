package it.intecs.pisa.toolbox.plugins.security;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;
import javax.xml.namespace.QName;

import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSDocInfo;
import org.apache.ws.security.WSSConfig;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;

import org.apache.ws.security.processor.EncryptedKeyProcessor;
import org.apache.ws.security.processor.X509Util;
import org.apache.ws.security.util.WSSecurityUtil;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;

import org.opensaml.XML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SAMLdecryptor {

    private WSSConfig wssConfig = null;

    public SAMLdecryptor() {
        wssConfig = WSSConfig.getDefaultWSConfig();
    }
    
    /**
     *
     * @author Stefano
     * @param element: the Element <Assertion xmlns="http://hma.spacebel.be/dail/saml" xmlns:env="http://schemas.xmlsoap.org/soap/envelope/"> owning the <xenc:EncryptedData>
     * @return
     */
    public Element decrypt(Element element, File keyStore, String alias, String keyStorePwd) {
        try {
            Properties prop;
            Crypto crypto;

            prop = new Properties();
            prop.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
            prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
            prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", keyStorePwd);
            prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias", alias);
            prop.setProperty("org.apache.ws.security.crypto.merlin.alias.password", keyStorePwd);
            prop.setProperty("org.apache.ws.security.crypto.merlin.file", keyStore.getAbsolutePath());

            crypto = CryptoFactory.getInstance(prop);

            Vector returnResults = new Vector();
            //element = (Element) element.getFirstChild().getNextSibling();

            final WSSConfig cfg = getWssConfig();
            WSDocInfo wsDocInfo = new WSDocInfo(element.getOwnerDocument().hashCode());
            wsDocInfo.setCrypto(crypto);
            TokenCallbackHandler tokenCallbackHandler = new TokenCallbackHandler();

            return this.handleToken(element, crypto, crypto, tokenCallbackHandler, wsDocInfo, returnResults, cfg);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public WSSConfig getWssConfig() {
        return (wssConfig == null) ? WSSConfig.getDefaultWSConfig() : wssConfig;
    }

    /***
     * 
     * @author Stefano
     * @param saml
     * @return
     */
    public boolean checkSignature(Element samlElement) {
        try {

            //samlElement.removeAttribute("Id");
            Element n = XML.getFirstChildElement(samlElement, XML.XMLSIG_NS, "Signature");
            XMLSignature sig = new XMLSignature((Element) n, "");
            //(new DOMUtil()).dumpXML(sig.getDocument(), new File("F:/Temp/sig.xml"));

            if (!checkSignature(sig)) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean checkSignature(XMLSignature signature) throws Exception {
        //signature.addResourceResolver(new OfflineResolver());
        // XMLUtils.outputDOMc14nWithComments(signature.getElement(), System.out);
        KeyInfo ki = signature.getKeyInfo();

        if (ki != null) {
            if (ki.containsX509Data()) {
                System.out.println("Could find a X509Data element in the KeyInfo");
            }

            X509Certificate cert = signature.getKeyInfo().getX509Certificate();

            if (cert != null) {
                /*
                System.out.println(
                "I try to verify the signature using the X509 Certificate: "
                + cert);
                 */
                return signature.checkSignatureValue(cert);

            } else {
                System.out.println("Did not find a Certificate");

                PublicKey pk = signature.getKeyInfo().getPublicKey();

                if (pk != null) {
                    /*
                    System.out.println(
                    "I try to verify the signature using the public key: "
                    + pk);
                     */
                    return signature.checkSignatureValue(pk);
                } else {

                    throw new Exception("Cannot check the SAML signature, public key is missing");
                }
            }
        } else {
            ToolboxConfiguration configuration;

            configuration = ToolboxConfiguration.getInstance();
            if (Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE)) == false) {
                throw new Exception("A Toolbox keystore has not been set");
            }

            Toolbox tbx;
            tbx = Toolbox.getInstance();

            File jksFile = new File(tbx.getRootDir(), "WEB-INF/persistence/tbxLevelKeystore.jks");
            String keyStorePath = jksFile.getAbsolutePath();
            String storePass = configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD);
            return checkSignature(signature, keyStorePath, storePass);
        }
    }
    
    public boolean checkSignature(XMLSignature signature, String keystorePath, String storePass) throws Exception {

        KeyStore keyStore = KeyStore.getInstance("JKS");

        File keystoreFile = new File(keystorePath);
        keyStore.load(new FileInputStream(keystoreFile), storePass.toCharArray());

        Enumeration<String> aliases = keyStore.aliases();

        boolean isSignValid = false;

        while (aliases.hasMoreElements() && (!isSignValid)) {
            String alias = aliases.nextElement();

            if (keyStore.getCertificate(alias) != null) {
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

                isSignValid = signature.checkSignatureValue(certificate);
            }
        }
        return isSignValid;
    }
    

    public Element handleToken(Element elem, Crypto crypto, Crypto decCrypto,
            CallbackHandler cb, WSDocInfo wsDocInfo, Vector returnResults,
            WSSConfig config) throws WSSecurityException, Exception {
        byte[] symmKey = null;
        Element kiElem = (Element) WSSecurityUtil.findElement(elem, "KeyInfo", WSConstants.SIG_NS);

        NodeList children = kiElem.getChildNodes();
        int len = children.getLength();

        for (int i = 0; i < len; i++) {
            Node child = children.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            QName el = new QName(child.getNamespaceURI(), child.getLocalName());
            if (el.equals(WSSecurityEngine.ENCRYPTED_KEY)) {
                EncryptedKeyProcessor encrKeyProc = new EncryptedKeyProcessor();
                encrKeyProc.handleToken((Element) child, crypto, decCrypto, cb, wsDocInfo, returnResults, config);
                symmKey = encrKeyProc.getDecryptedBytes();
                break;
            }
        }

        String encAlgo = X509Util.getEncAlgo(elem);
        SecretKey key = WSSecurityUtil.prepareSecretKey(encAlgo, symmKey);

        // initialize Cipher ....
        XMLCipher xmlCipher = null;
        try {
            xmlCipher = XMLCipher.getInstance(encAlgo);
            xmlCipher.init(XMLCipher.DECRYPT_MODE, key);
        } catch (XMLEncryptionException e1) {
            throw new WSSecurityException(
                    WSSecurityException.UNSUPPORTED_ALGORITHM, null, null, e1);
        }

        Node parent = elem.getParentNode();

        try {
            xmlCipher.doFinal(elem.getOwnerDocument(), elem);
        } catch (Exception e) {
            throw new WSSecurityException(WSSecurityException.FAILED_CHECK,
                    null, null, e);
        }

        // Element decryptedElem = (Element) parent.getFirstChild();
        NodeList decryptedList = parent.getChildNodes();
        Node decryptedElem = null;
        for (int index = 0; index < decryptedList.getLength(); index ++){
            decryptedElem = decryptedList.item(index);
            if (decryptedElem.getNodeType() == Node.ELEMENT_NODE)
                break;          
        }

        return (Element) decryptedElem;
    }
}
