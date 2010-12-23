/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.security.validator;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axis2.context.MessageContext;
import org.apache.rampart.PolicyBasedResultsValidator;
import org.apache.rampart.RampartException;
import org.apache.rampart.ValidatorData;
import org.apache.rampart.policy.RampartPolicyData;
import org.apache.rampart.util.RampartUtil;
import org.apache.ws.secpolicy.model.IssuedToken;
import org.apache.ws.secpolicy.model.SupportingToken;
import org.apache.ws.secpolicy.model.Token;
import org.apache.ws.secpolicy.model.UsernameToken;
import org.apache.ws.secpolicy.model.X509Token;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.util.WSSecurityUtil;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.opensaml.SAMLAssertion;
import org.opensaml.SAMLException;
import org.opensaml.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Enumeration;


/**
 * @author Stefano
 * This class extends the Rampart PolicyBasedResultValidator class.
 * It can be used to perform additional control for the SAML token
 */
public class SAMLValidator extends PolicyBasedResultsValidator{

    MessageContext msgCtx = null; //Is this safe???
    SupportingToken policySuppTok = null;

    public void validate(ValidatorData data, Vector results) throws RampartException {

        msgCtx =  data.getRampartMessageData().getMsgContext();
        policySuppTok = data.getRampartMessageData().getPolicyData().getEncryptedSupportingTokens();
        if (policySuppTok == null){
        	policySuppTok = data.getRampartMessageData().getPolicyData().getSignedEncryptedSupportingTokens();
        }
        super.validate(data, results);
    }


    protected void handleSupportingTokens(Vector results, SupportingToken suppTok) throws RampartException {

        super.handleSupportingTokens(results, suppTok);

        if(suppTok == null) {
            return;
        }


        ArrayList tokens = suppTok.getTokens();
        for (Iterator iter = tokens.iterator(); iter.hasNext();) {
            Token token = (Token) iter.next();
            if(token instanceof UsernameToken) {
                /*//Check presence of a UsernameToken
                WSSecurityEngineResult utResult = WSSecurityUtil.fetchActionResult(results, WSConstants.UT);
                if(utResult == null) {
                    throw new RampartException("usernameTokenMissing");
                }*/

            } else if ( token instanceof X509Token) {
                /*WSSecurityEngineResult x509Result = WSSecurityUtil.fetchActionResult(results, WSConstants.BST);
                if(x509Result == null) {
                    throw new RampartException("binaryTokenMissing");
                }*/
            }else if ( token instanceof IssuedToken ) {
                //TODO is is enough to check for ST_UNSIGNED results ??
                WSSecurityEngineResult samlResult = WSSecurityUtil.fetchActionResult(results, WSConstants.ST_UNSIGNED);
                if(samlResult == null) {
                	throw new RampartException("samlTokenMissing");
                }

                Object res = samlResult.get(samlResult.TAG_SAML_ASSERTION);
                if (res instanceof SAMLAssertion == false){
                    return;
                }
                SAMLAssertion assertion = (SAMLAssertion) res;

                if (isCheckIssuerRequired()){
	                //check if the issuer is the same as the one specified in the service policy
	                //this check is only performed if the issuer is specified in the security policy in the following way:
	                /**
	                  <sp:SignedEncryptedSupportingTokens>
						<wsp:Policy>
							<sp:SamlToken sp:IncludeToken="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702/IncludeToken/AlwaysToRecipient">
								<Issuer xmlns="http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702">
									<Address xmlns="http://www.w3.org/2005/08/addressing">*****HERE THE ISSUER ID, E.G. ITS URL*****</Address>
								</Issuer>
								<sp:WssSamlV11Token11/>
							</sp:SamlToken>
						</wsp:Policy>
						</sp:SignedEncryptedSupportingTokens>
	                 */
	                IssuedToken issuedToken = null;
	                String issuer = null;
	                for (Iterator tokeniter = policySuppTok.getTokens().iterator(); tokeniter.hasNext(); ){
	                	Object temp = tokeniter.next();
	                	if (temp instanceof IssuedToken == false)
	                		continue;
	                	issuedToken = (IssuedToken) temp;
	                	OMElement issuerExpr = issuedToken.getIssuerEpr();
	                	issuer = ((OMTextImpl) issuerExpr.getFirstOMChild()).getText();
	                }

	                if (issuer != null){
	                	if (assertion.getIssuer().compareToIgnoreCase(issuer) != 0){
	                		throw new RampartException("samlTokenWithBadIssuer", new String[] {issuer});
	                	}

	                }
	                //end check
                }

                if (isCheckSignatureRequired() || isCheckSignatureWithAliasRequired()){
                	//first try to remove the Id attribute, this attribute has been added used when using Rampart to build an encrypted SAML token
                	/*try{
                		String ns = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
                		//((Element) assertion.toDOM()).removeAttributeNS(ns, "Id");
                	}
                	catch(Exception ex){
                		//do nothing
                	}
                	try{
                		String ns = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
                		//((Element) assertion.toDOM()).removeAttribute("Id");
                	}
                	catch(Exception ex){
                		//do nothing
                	}*/


	                if (isCheckSignatureRequired()){
	                	///check that the sign of the SAML content is valid, i.e. the content of the SAML has not been altered
	                	//ATTENTION: currently the verify method of opensaml doens't work if the incoming assertion has a namespace prefix in the top tag, for instance <saml:Assertion ...>, it has to be <Assertion ...> only.
	                    try {
                                javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
                                 OMElement securityOM = msgCtx.getEnvelope().getHeader().getFirstChildWithName(qname);
	                    	//ASSERTION: SAML Assertion is direct child of Security

                                //System.out.println("SECURITY: " + securityOM.toString());
                                OMElement assertionOM = (OMElement) securityOM.getChildrenWithLocalName("Assertion").next();
//                                OMElement assertionOM = (OMElement) securityOM.getChildrenWithName(new QName("*","Assertion")).next();

	                        Document soapDoc = (new DOMUtil()).stringToDocument(assertionOM.toString());
	                        soapDoc = (new DOMUtil()).stringToDocument(assertionOM.toString());
                                //System.out.println("SOAP Document: " + soapDoc.toString());

	                        //REMOVE ANY UNNECESSARY INFORMATION FOR THE SIGNATURE CHECK
	                        //soapDoc.getDocumentElement().removeAttribute("Id");
	                        soapDoc.getDocumentElement().removeAttribute("wsu:Id");

	                        //new DOMUtil().dumpXML(soapDoc, new File("F:/Projects/Toolbox/ToolboxSecurity_HMAT/acceptance/SAML/daDAIL/decryptedTokenWithToolbox.xml"));

	                    	Element n = XML.getFirstChildElement(soapDoc.getDocumentElement(), XML.XMLSIG_NS, "Signature");
	    					XMLSignature sig = new XMLSignature((Element)n,"");
	    					boolean signatureOk = false;
	    					try{
	    						signatureOk = checkSignature(sig);
	    						System.out.println("SIGNATURE CHECK: "+signatureOk);
	    					}catch(Exception ex){
	    						Object[] arr = {ex.getMessage()};
		    					throw new RampartException("Failed to validate SAML signature value", arr);
	    					}

	    					if (! signatureOk){
	    						Object[] arr = {"Invalid SAML signature"};
		    					throw new RampartException("Failed to validate SAML signature value", arr);
	    					}

	    					/*

	    					Key k=sig.getKeyInfo().getPublicKey();
	    					if (!sig.checkSignatureValue(k)){
	    						Object[] arr = {"Failed to validate SAML signature value"};
	    						throw new RampartException("errorExtractingToken", arr);
	    					}*/

	    				} catch (Exception e) {
	    					Object[] arr = {e.getMessage()};
	    					throw new RampartException("Failed to validate SAML signature value", arr);
	    				}

		                /*
		                try{

		                    //ATTENTION: currently the verify method of opensaml doens't work if the incoming assertion has a namespace prefix in the top tag, for instance <saml:Assertion ...>, it has to be <Assertion ...> only.
		                	//also some problem with namespace declaration can occur: opensaml work with the following namespace:
		                	//xmlns="urn:oasis:names:tc:SAML:1.0:assertion"
		                	//xmlns:saml="urn:oasis:names:tc:SAML:1.0:assertion"
		                	//xmlns:samlp="urn:oasis:names:tc:SAML:1.0:protocol"
		                	//xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		                	//xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		                	assertion.verify();

		                }catch (SAMLException ex){
		                	ex.printStackTrace();
		            		Object[] arr = {ex.getMessage()};
		                	throw new RampartException("errorExtractingToken", arr);
		                }*/

		                //end check sign
	                }

	                if (isCheckSignatureWithAliasRequired()){

		                //check that the sign of the SAML content is valid, i.e. the content of the SAML has not been altered and that
		                //it has been signed with the (trusted) certificate
		                /**
		                 * In the case that the content of the Assertion has to be signed by the issuer, then the following code checks the
		                 * signature coming inside the Assertion. The signature is valid if the content of the SAML has not been altered
		                 * and if it has been signed with the (trusted) certificate of the entity which is in charge to release the token.
		                 */
		                RampartPolicyData policyData = (RampartPolicyData) msgCtx.getProperty("rampartPolicyData");
		                String alias = policyData.getRampartConfig().getStsAlias();

		                if (alias == null){
		                	throw new RampartException("rampartConigMissing");
		                }

		                X509Certificate[] certs = null;
		                try{
			                certs = RampartUtil.getSignatureCrypto(
			                		policyData.getRampartConfig(),
			                        this.getClass().getClassLoader()).getCertificates(alias);
		                }catch (Exception ex){
		                	ex.printStackTrace();
		                }

		                if (certs == null){
		                	Object[] arr = {alias};
		                	throw new RampartException("noCertForAlias", arr);
		                }

		                try{

		                    //ATTENTION: currently the verify method of opensaml doens't work if the incoming assertion has a namespace prefix in the top tag, for instance <saml:Assertion ...>, it has to be <Assertion ...> only.
		                	//also some problem with namespace declaration can occur: opensaml work with the following namespace:
		                	//xmlns="urn:oasis:names:tc:SAML:1.0:assertion"
		                	//xmlns:saml="urn:oasis:names:tc:SAML:1.0:assertion"
		                	//xmlns:samlp="urn:oasis:names:tc:SAML:1.0:protocol"
		                	//xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		                	//xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		                	assertion.verify();
		                	assertion.verify(certs[0]);
		                }catch (SAMLException ex){
		                	ex.printStackTrace();
		            		Object[] arr = {"SAML with untrusted signature."};
		                	throw new RampartException("errorExtractingToken", arr);
		                }

		                //end check sign
	                }
                }

                /* here I could retrieve the SAML attributes in the following way, and perform any check on them...
                Iterator statemiter = assertion.getStatements();

                SAMLAttributeStatement statm = null;
                Object tmp = null;
                while (statemiter.hasNext()){
                    tmp = statemiter.next();
                    if (tmp instanceof SAMLAttributeStatement == false)
                        continue;
                    statm = (SAMLAttributeStatement) tmp;
                    SAMLAttribute attr = null;
                    Iterator attrIter = statm.getAttributes();
                    while (attrIter.hasNext()){
                        attr = (SAMLAttribute) attrIter.next();
                        if (attr.getName().equals("organization")){
                            Iterator valueIter= attr.getValues();
                            while (valueIter.hasNext()){
                                String organization = (String) valueIter.next();
                                System.out.println("PolicyValidator.handleSupportingTokens organization = "+organization);
                                //checkPolicy(organization);
                            }

                        }
                    }

                }*/
            }
        }

    }

    /***
     * any control here to enable/disable signature check
     * @author Stefano
     * @return
     */
    private boolean isCheckSignatureRequired(){
    	return true;
    }

    /***
     * any control here to enable/disable issuer check
     * @author Stefano
     * @return
     */
    private boolean isCheckIssuerRequired(){
    	return false;
    }

    /***
     * any control here to enable/disable issuer check
     * @author Stefano
     * @return
     */
    private boolean isCheckSignatureWithAliasRequired(){
    	return false;
    }

   public boolean checkSignature(XMLSignature signature) throws Exception {

        KeyInfo ki = signature.getKeyInfo();

        if (ki != null) {
            // Try to verify the signature using a X509Certificate
            X509Certificate cert = signature.getKeyInfo().getX509Certificate();

            if (cert != null) {
                  return signature.checkSignatureValue(cert);
            } else {
                System.out.println("Did not find a Certificate");

                // try to verify the signature using the public key:
                PublicKey pk = signature.getKeyInfo().getPublicKey();

                if (pk != null) {
                    return signature.checkSignatureValue(pk);
                } else {
                    throw new Exception("Cannot check the SAML signature, public key is missing");
                }
            }
        } else {
            ToolboxConfiguration configuration;

            configuration=ToolboxConfiguration.getInstance();
            if(Boolean.parseBoolean(configuration.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE))==false)
                throw new Exception("A Toolbox keystore has not been set");

            Toolbox tbx;
            tbx=Toolbox.getInstance();

            File jksFile=new File(tbx.getRootDir(),"WEB-INF/persistence/tbxLevelKeystore.jks");
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

            if (keyStore.isCertificateEntry(alias)) {
                X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

                isSignValid = signature.checkSignatureValue(certificate);
            }
        }
        return isSignValid;
    }


}
