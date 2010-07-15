/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetDefinitionCommand extends RESTManagerCommandPlugin{
    @Override
     public InputStream executeCommand(String cmd, InputStream in) {
        try {
            int index=cmd.lastIndexOf("/");
            String fileName=cmd.substring(index+1);
            String fullFileName="resources/definitions/"+fileName+".xml";

            return new FileInputStream(new File(pluginDir, fullFileName));
        } catch (FileNotFoundException ex) {
           return null;
        }
    }
}
