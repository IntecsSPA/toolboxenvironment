package it.intecs.pisa.toolbox.plugins.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class TokenCallbackHandler implements CallbackHandler {
	public void handle(Callback[] callbacks) 
    throws IOException, UnsupportedCallbackException {
	
		/*for (int i = 0; i < callbacks.length; i++) {
            WSPasswordCallback pwcb = (WSPasswordCallback)callbacks[i];
            String id = pwcb.getIdentifer();
            if("cumg_keys".equals(id)) {
                pwcb.setPassword("siseneg");
            } else if("...".equals(id)) {
                pwcb.setPassword("ghjjf");
            }else if(".....".equals(id)) {
                pwcb.setPassword("hjgfghj");
            }
        }*/
	}
}
