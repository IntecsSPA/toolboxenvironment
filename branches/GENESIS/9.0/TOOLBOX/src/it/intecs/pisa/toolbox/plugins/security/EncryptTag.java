package it.intecs.pisa.toolbox.plugins.security;

import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.*;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;

import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecEncrypt;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.SAMLAssertion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

public class EncryptTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element encryptTag) throws Exception {
        Element childEl;
        Document samlToken;
        String alias, keyStorePwd;
        File keystore;
        TBXService service;
        String serviceResourcePath;

        serviceResourcePath = (String) engine.getConfigurationVariablesStore().getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_RESOURCE_DIR);
        alias = evaluateAttribute(encryptTag, "alias");
        keyStorePwd = evaluateAttribute(encryptTag, "keyStorePwd");

        keystore = new File(serviceResourcePath, "Security/service.jks");

        childEl = DOMUtil.getFirstChild(encryptTag);
        samlToken = (Document) this.executeChildTag(childEl);

        Document newDoc;

        newDoc = new DOMUtil().inputStreamToDocument(DOMUtil.getNodeAsInputStream(samlToken));

        SAMLAssertion saml = new SAMLAssertion(newDoc.getDocumentElement());

        //signSAML(newDoc.getDocumentElement(), keystore, alias, keyStorePwd);
        Element res = encrypt(newDoc,keystore, alias, keyStorePwd);
 
        return res.getOwnerDocument();
    }

    protected Element encrypt(Document doc, File keystore, String alias,String keyStorePwd) {
        try {
            Properties prop;
            Crypto crypto;
            
            prop=new Properties();
            prop.setProperty("org.apache.ws.security.crypto.provider", "org.apache.ws.security.components.crypto.Merlin");
            prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.type", "JKS");
            prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.password", keyStorePwd);
            prop.setProperty("org.apache.ws.security.crypto.merlin.keystore.alias", alias);
            prop.setProperty("org.apache.ws.security.crypto.merlin.alias.password", keyStorePwd);
            prop.setProperty("org.apache.ws.security.crypto.merlin.file", keystore.getAbsolutePath());

            crypto= CryptoFactory.getInstance(prop);

            SAMLAssertion saml = new SAMLAssertion(doc.getDocumentElement());
            String samlId = saml.getId();
            WSSecEncrypt encr = new WSSecEncrypt();
            Vector encrParts = new Vector();
            encrParts.add(new WSEncryptionPart("#" + samlId, "Element"));
            encr.setParts(encrParts);
            //encr.setWsConfig(rmd.getConfig());
            encr.setDocument(doc);
            //RampartUtil.setEncryptionUser(rmd, encr);

            encr.setSymmetricEncAlgorithm(org.apache.ws.security.WSConstants.AES_128); //http://www.w3.org/2001/04/xmlenc#aes128-cbc

            encr.setKeyEncAlgo(org.apache.ws.security.WSConstants.KEYTRANSPORT_RSA15); //http://www.w3.org/2001/04/xmlenc#rsa-1_5

            encr.setUserInfo(alias);

            encr.prepare(doc, crypto);

            //Element bstElem = encr.getBinarySecurityTokenElement();

            Element encrTokenElement = encr.getEncryptedKeyElement();

            Element e = encr.encryptForInternalRef(null, encrParts);
            Node keyinfo = doc.getDocumentElement().getFirstChild().getNextSibling();
            //doc.getDocumentElement().insertBefore(encrTokenElement,samlcipher);
            keyinfo.appendChild(encrTokenElement);

            return doc.getDocumentElement();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void signSAML(Element samlElem, File jks4signature, String user, String pwd) throws Exception{

    	SAMLAssertion saml = new SAMLAssertion(samlElem);
    	// sign the assertion

        KeyStore caKs = KeyStore.getInstance("JKS");
        //char[] pwd = new String("apache").toCharArray();
        caKs.load(new FileInputStream(jks4signature), pwd.toCharArray());

        X509Certificate cert = (X509Certificate) caKs.getCertificate(user);
        X509Certificate[] issuerCerts = {cert};

        String sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_RSA;
        String pubKeyAlgo = issuerCerts[0].getPublicKey().getAlgorithm();
        if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
            sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_DSA;
        }

        java.security.Key issuerPK = caKs.getKey(user, pwd.toCharArray());

        //System.out.println("getting SAML token, issuerPK = "+issuerPK);

        saml.sign(sigAlgo, issuerPK, Arrays.asList(issuerCerts));
        //saml.signWithTransformComments(sigAlgo, null, issuerPK, Arrays.asList(issuerCerts));
        saml.checkValidity();
    }
}
