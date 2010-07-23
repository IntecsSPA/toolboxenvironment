/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardTemplateCommand extends RESTManagerCommandPlugin{
    @Override
     public InputStream executeCommand(String cmd, InputStream in) {
        try {
            int index=cmd.lastIndexOf("/");
            String interfaceType=cmd.substring(index+1);

            String jsString="servicesXMLInterface.push({xmlUrl:\"/rest/gui/creationWizardTemplate/"+interfaceType+"\","+
                           "icon:\"images/order_blk.png\", title:\"Create Ordering Service\", name:\"orderingService\","+
                            "actionMethod: \"createToolboxService(this)\"});";

            return new ByteArrayInputStream(jsString.getBytes());
        } catch (Exception ex) {
           return null;
        }
    }
    
}
