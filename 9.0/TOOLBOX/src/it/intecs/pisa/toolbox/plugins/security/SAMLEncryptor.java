package it.intecs.pisa.toolbox.plugins.security;

import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import org.apache.ws.security.WSEncryptionPart;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.message.WSSecEncrypt;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLAttribute;
import org.opensaml.SAMLAttributeStatement;
import org.opensaml.SAMLStatement;
import org.opensaml.SAMLSubject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class SAMLEncryptor {
	
	final Crypto crypto =
		CryptoFactory.getInstance("crypto.properties");
	
	/**
	 * Loads a SAML token from the file system, signs the content and then encrypt it. 
	 * Be sure to set the proper crypto.properties for the encryption.
	 * @author Stefano
	 * @param samlTokenFile : the file owning the SAML token
	 * @param jks4SigFile : the keystore which owns the certificate to be used for signature
	 * @param user4Sig : the username to retrieve the certificate for the signature
	 * @param pwd4Sig : the password to retrieve the private key 
	 * @param user4Encr : the username for the encryption
	 * @return
	 */
	public Element signEncryptSAMLtokenFromFile(File samlTokenFile, File jks4SigFile, String user4Sig, String pwd4Sig, String user4Encr, String pwd4Encr){
		try{
			SAMLAssertion saml = null;
			
			Document samlFromFileDoc = getSAMLfromFile(samlTokenFile);
			
			saml = new SAMLAssertion(samlFromFileDoc.getDocumentElement());
			//saml.fromDOM(samlFromFileDoc.getDocumentElement());
			
			//add signature here
			signSAML(samlFromFileDoc.getDocumentElement(), jks4SigFile, user4Sig, pwd4Sig);
			//saml.verify();
			return encrypt(samlFromFileDoc, user4Encr);
		}catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @author Stefano
	 * @param doc the Document owning the SAML to be encrypted
	 * @param samlid
	 * @param user : the entry for the public key
	 * @return
	 */
	public Element encrypt(Document doc, String user){
		try{
			
			SAMLAssertion saml = new SAMLAssertion(doc.getDocumentElement());
			String samlId = saml.getId();
			WSSecEncrypt encr = new WSSecEncrypt();
			Vector encrParts = new Vector();
			encrParts.add(new WSEncryptionPart("#"+samlId,"Element"));
	        encr.setParts(encrParts);
	        //encr.setWsConfig(rmd.getConfig());
	        encr.setDocument(doc);
	        //RampartUtil.setEncryptionUser(rmd, encr);
	        
	        encr.setSymmetricEncAlgorithm(org.apache.ws.security.WSConstants.AES_128); //http://www.w3.org/2001/04/xmlenc#aes128-cbc
	       
	        encr.setKeyEncAlgo(org.apache.ws.security.WSConstants.KEYTRANSPORT_RSA15); //http://www.w3.org/2001/04/xmlenc#rsa-1_5
	        
	        encr.setUserInfo(user);

	        encr.prepare(doc, crypto);
	        
	        //Element bstElem = encr.getBinarySecurityTokenElement();
	       
	        Element encrTokenElement = encr.getEncryptedKeyElement();
	        
	        Element e = encr.encryptForInternalRef(null, encrParts);
	        Node keyinfo = doc.getDocumentElement().getFirstChild().getNextSibling();
	        //doc.getDocumentElement().insertBefore(encrTokenElement,samlcipher);
	        keyinfo.appendChild(encrTokenElement);
	       
	        return doc.getDocumentElement();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}

    private static Document getSAMLfromFile(File f) {
    	try {
			return (new DOMUtil()).fileToDocument(f);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
      }
    
    public void signSAML(SAMLAssertion saml, File jks4signature, String user, String pwd) throws Exception{
    	this.signSAML((Element) saml.toDOM(), jks4signature, user, pwd);
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
     
    /**
	 * builds a new SAML token, signs the content and then encrypt it. 
	 * Be sure to set the proper crypto.properties for the encryption.
	 * WARNING: an Id attribute is added to the XML SAML token, this is required by the wss4j library to perform encryption (note that such Id is available in SAML v2.0). The Id attribute has to be removed before performing signature authentication.
	 * @author Stefano
	 * @param jks4SigFile : the keystore which owns the certificate to be used for signature
	 * @param user4Sig : the username to retrieve the certificate for the signature
	 * @param pwd4Sig : the password to retrieve the private key 
	 * @param user4Encr : the username for the encryption
	 * @return the encrypted SAML token.
	 */
	public Element buildAndEncryptSAMLtoken(File jks4SigFile, String user4Sig, String pwd4Sig, String user4Encr, String pwd4Encr){
		SAMLAssertion saml = null;
		
		saml = getBearerConfSAML("http://www.spacebel.be", true, jks4SigFile, user4Sig, pwd4Sig);
		String ss = saml.toString();
		Document doc = null;
		try{
			doc = (new DOMUtil()).stringToDocument(ss);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return encrypt(doc, user4Encr);
		
	}
    
	/**
     * Builds a SAML Token with Bearer confirmation method.
     * @param msgCtx the current MessageContext
     * @param sign true to sign the SAML token content
     * @return the org.apache.rahas.Token token which embeds the SAML token
     */
    protected SAMLAssertion getBearerConfSAML(String organization, boolean signAssertion, File jks4signature, String user, String pwd ){
        try{
            SAMLAttribute attributeName = new SAMLAttribute("Name",
                            "https://rahas.apache.org/saml/attrns", null, -1, Arrays
                                    .asList(new String[] { "NameX" }));
            SAMLAttribute attributeOrganization = new SAMLAttribute("organization",
                    "https://rahas.apache.org/saml/attrns", null, -1, Arrays
                            .asList(new String[] { organization }));
            
            SAMLAttribute[] attrs = new SAMLAttribute[]{attributeName,attributeOrganization};
            String[] confirmationMethods = new String[] { SAMLSubject.CONF_BEARER};
            SAMLSubject subject = new SAMLSubject(null, Arrays
                .asList(confirmationMethods), null, null);
   
            SAMLAttributeStatement attrStmt = new SAMLAttributeStatement(
                    subject, Arrays.asList(attrs ));
            SAMLStatement[] statements = { attrStmt };
            Date creationTime = new Date();
            Date expirationTime = new Date();
            expirationTime.setTime(creationTime.getTime() + 300000);
            
            SAMLAssertion assertion = new SAMLAssertion("intecs", creationTime, expirationTime, null, null, Arrays.asList(statements));
            assertion.setIssuer(organization);
            String samlId = getDefaultSAMLid();
            assertion.setId(samlId);
            
            //
            if(signAssertion){
	            signSAML((Element)assertion.toDOM(), jks4signature, user, pwd);
            }
         
            return assertion;
        }
         catch (Exception ex){
             ex.printStackTrace();
         }
         return null;
    }
    
    public String getDefaultSAMLid(){
    	return "oracle.secuity.xmlsec.saml.Assertion@1f84b5e";
    }
}
