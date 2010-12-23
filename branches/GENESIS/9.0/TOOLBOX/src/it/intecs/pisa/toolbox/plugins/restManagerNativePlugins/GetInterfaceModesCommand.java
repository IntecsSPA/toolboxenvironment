/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DOMUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Override
    public JsonObject executeCommand(String cmd, JsonObject request) throws Exception {
        String interfaceName, interfaceVersion, interfaceType;
        JsonObject outputJson = new JsonObject();
        JsonArray array = new JsonArray();

        StringTokenizer tokenizer=new StringTokenizer(cmd,"/");
        tokenizer.nextToken();
        tokenizer.nextToken();
        tokenizer.nextToken();
        interfaceType=tokenizer.nextToken();
        interfaceName=tokenizer.nextToken();
        interfaceVersion=tokenizer.nextToken();

        InterfacePluginManager interfManager = InterfacePluginManager.getInstance();
        String[] interfaceModes = interfManager.getInterfacesModes(interfaceName, interfaceVersion, interfaceType);

        for (String mode : interfaceModes) {
                array.add(new JsonPrimitive(mode));
        }
        outputJson.add("types", array);
        outputJson.addProperty("success", true);
        return outputJson;
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    


}
