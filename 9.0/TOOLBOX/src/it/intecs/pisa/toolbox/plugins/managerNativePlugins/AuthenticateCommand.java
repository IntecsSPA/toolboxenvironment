/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.Random;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Massimiliano
 */
public class AuthenticateCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        ServletContext context;
        Document userDoc;
        DOMUtil util;
        String username,password;
        String nodeUsername,nodePassword;
        boolean isValidAccount = false;
        String token="";
        Document responseDoc;
        Element rootEl;
        try
        {
            context=tbxServlet.getServletContext();
            util=new DOMUtil();
            
            username = req.getParameter("username");
            password = req.getParameter("password");
                        
            userDoc=util.fileToDocument(new File(context.getRealPath("WEB-INF/xml/adminUsers.xml")));
            
            NodeList adminUsers = userDoc.getDocumentElement().getElementsByTagName("adminUser");
            
		for (int i = 0; i < adminUsers.getLength(); i++ ) {
                        nodeUsername=((Element) adminUsers.item(i)).getAttribute("userName");
                        nodePassword=((Element) adminUsers.item(i)).getAttribute("password");
                        
			if (nodeUsername.equals(username) && nodePassword.equals(password)) {
				isValidAccount = true;
                                
                                break;
			}
		}
                
                if(isValidAccount==true)
                {
                    Random rand;
                    
                    rand=new Random();
                    
                    token=Integer.toString(rand.nextInt());
                    tbxServlet.setDeployAdministrationToken(token);
                }
            
                 responseDoc=util.newDocument();
                 rootEl=responseDoc.createElement("authentication");
                 responseDoc.appendChild(rootEl);
                 
                 rootEl.setAttribute("success", Boolean.toString(isValidAccount));
                 if(isValidAccount)
                 {
                     rootEl.setAttribute("token", token);
                 }
                 
                 IOUtil.copy(DOMUtil.getDocumentAsInputStream(responseDoc), resp.getOutputStream());
        }
        catch(Exception e)
        {
            e.printStackTrace();
            String errorMsg = "Cannot authenticate";
            throw new GenericException(errorMsg);
        }
    }

}
