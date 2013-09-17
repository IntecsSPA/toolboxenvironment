
package it.intecs.pisa.toolbox.plugins.security;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class SAMLSignatureTag extends NativeTagExecutor{
    
    @Override
    public Object executeTag(org.w3c.dom.Element signatureTag) throws Exception {
        Element childEl;
        Document samlToken;
        String alias, keyStorePwd;
        File keystore;
        TBXService service;
        String serviceResourcePath;
        DOMUtil du=new DOMUtil();

        ToolboxConfiguration conf=ToolboxConfiguration.getInstance();
        Toolbox tbx=Toolbox.getInstance();
       
        alias = conf.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_IDP_KEY_ALIAS);
        keyStorePwd = conf.getConfigurationValue(ToolboxConfiguration.TOOLBOX_LEVEL_KEYSTORE_PASSWORD);
        
        keystore= new File(tbx.getRootDir(),"WEB-INF/persistence/tbxLevelKeystore.jks");
        childEl = DOMUtil.getFirstChild(signatureTag);
        samlToken = (Document) this.executeChildTag(childEl);
        Document newDoc;
        newDoc = du.inputStreamToDocument(DOMUtil.getNodeAsInputStream(samlToken));
        SAMLEncryptor.signSAML(newDoc.getDocumentElement(), keystore, alias, keyStorePwd);
        return newDoc;
    }
    
    
}
