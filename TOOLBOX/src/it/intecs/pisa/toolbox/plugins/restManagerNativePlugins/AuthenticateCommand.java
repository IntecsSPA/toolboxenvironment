/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class AuthenticateCommand extends RESTManagerCommandPlugin{

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        ServletContext context = Toolbox.getInstance().getServletContext();
        DOMUtil util=new DOMUtil();
        boolean isValidAccount=false;
        
        if(method.equals("POST")==true)
        {
            String username = request.get("username").getAsString();
            String password = request.get("password").getAsString();

            Document userDoc=util.fileToDocument(new File(context.getRealPath("WEB-INF/xml/adminUsers.xml")));

            NodeList adminUsers = userDoc.getDocumentElement().getElementsByTagName("adminUser");

            for (int i = 0; i < adminUsers.getLength(); i++ ) {
                    String nodeUsername=((Element) adminUsers.item(i)).getAttribute("userName");
                    String nodePassword=((Element) adminUsers.item(i)).getAttribute("password");

                    if (nodeUsername.equals(username) && nodePassword.equals(password)) {
                            isValidAccount = true;

                            break;
                    }
            }

            JsonObject outputJson;

            outputJson=new JsonObject();
            outputJson.addProperty("success", isValidAccount);

            return outputJson;
        }
        else throw new Exception("Method not supported");
    }


}
