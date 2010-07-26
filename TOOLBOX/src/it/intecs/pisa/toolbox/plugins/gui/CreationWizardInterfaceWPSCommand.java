/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardInterfaceWPSCommand extends RESTManagerCommandPlugin{
  
    @Override
     public Document executeCommand(String cmd, Document inputDoc,Hashtable<String,String> headers,Hashtable<String,String> parameters) {
        try {
            DOMUtil domUtil;

            domUtil=new DOMUtil();

            File panelXMLFile;
            panelXMLFile=new File(pluginDir,"resources/definitions/WPSServicePanel.xml");

            Document document = domUtil.inputStreamToDocument(new FileInputStream(panelXMLFile));
            return document;
        } catch (Exception ex) {
           return null;
        }
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
