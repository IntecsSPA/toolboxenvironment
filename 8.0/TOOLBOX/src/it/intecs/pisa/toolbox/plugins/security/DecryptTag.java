package it.intecs.pisa.toolbox.plugins.security;

import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.*;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import org.apache.ws.security.WSSConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class DecryptTag extends NativeTagExecutor {

    WSSConfig wssConfig = null;

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

        Document response;

        response = new DOMUtil().inputStreamToDocument(DOMUtil.getNodeAsInputStream(samlToken));

        SAMLdecryptor decryptor;

        decryptor=new SAMLdecryptor();
        Element res = decryptor.decrypt(response.getDocumentElement(), keystore, alias, keyStorePwd);

        return res.getOwnerDocument();
    }

   
}
