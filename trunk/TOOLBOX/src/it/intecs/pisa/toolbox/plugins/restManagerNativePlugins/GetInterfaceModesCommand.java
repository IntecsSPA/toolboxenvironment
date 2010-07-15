/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetInterfaceModesCommand extends RESTManagerCommandPlugin{

    @Override
    public InputStream executeCommand(String cmd, InputStream in) {
        InputStream documentAsInputStream = null;
        try {
            Document response;

            response = executeCommand(cmd, (Document) null);
            documentAsInputStream = DOMUtil.getDocumentAsInputStream(response);

            return documentAsInputStream;
        } catch (Exception ex) {
            return null;
        } finally {
            try {
                documentAsInputStream.close();
            } catch (IOException ex) {
                return null;
            }
        }
    }

    @Override
    public Document executeCommand(String cmd, Document inputDoc) {
        DOMUtil util=new DOMUtil();
        Document doc;
        String interfaceName, interfaceVersion, interfaceType;

        doc=util.newDocument();
        Element root=doc.createElement("modes");
        doc.appendChild(root);

        StringTokenizer tokenizer=new StringTokenizer(cmd,"/");
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        interfaceType=tokenizer.nextToken();
        interfaceName=tokenizer.nextToken();
        interfaceVersion=tokenizer.nextToken();
        
        InterfacePluginManager interfManager = InterfacePluginManager.getInstance();
        String[] interfaceModes = interfManager.getInterfacesModes(interfaceName, interfaceVersion, interfaceType);

        for(String mode:interfaceModes)
        {
            Element modeEl=doc.createElement("mode");
            modeEl.setAttribute("name", mode);
            root.appendChild(modeEl);
        }
        return doc;
    }

    


}
