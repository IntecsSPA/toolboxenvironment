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
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetJsCommand extends RESTManagerCommandPlugin{
    @Override
     public InputStream executeCommand(String cmd, InputStream in) {
        try {
            int index=cmd.lastIndexOf("/");
            String fileName=cmd.substring(index+1);
            String fullFileName="resources/js/"+fileName+".js";

            return new FileInputStream(new File(pluginDir, fullFileName));
        } catch (FileNotFoundException ex) {
           return null;
        }
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
