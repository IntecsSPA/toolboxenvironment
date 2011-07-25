package it.intecs.pisa.toolbox.security.handler;

import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import java.io.IOException;

public class PWCBHandler implements CallbackHandler {

	/**
	 * This method is called by WSS4J to retrieve the password from a given identifier.
	 * For instance the identifier is obtained from a given encrypted key coming with an ecnrypted data (AES128); 
	 * once obtained the identifier (certificate alias) then WSS4J needs to
	 * know the correspondent password in order to be able to get the right certificate from the current keystore 
	 * and then decrypt the encrypted data.
	 * Currently the following method is not used: it is assumed that the incoming encrypted data is always
	 * encrypted with the default certificate specified in the Rampart policy associated to the current secured service.
	 * */
    public void handle(Callback[] callbacks) throws IOException,
            UnsupportedCallbackException {
    	/*
        for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
            String id = pwcb.getIdentifer();
            if("client".equals(id)) {
                pwcb.setPassword("apache");
            } else if("service".equals(id)) {
                pwcb.setPassword("apache");
            }else if("stefano".equals(id)) {
                pwcb.setPassword("intecs");
            }
        }*/
    }

}
