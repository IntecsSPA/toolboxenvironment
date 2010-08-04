
package it.intecs.pisa.toolbox.plugins.wpsPlugin.commands;

import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

/**
 *
 * @author Andrea Marongiu
 */
public class GetResourceStoredCommand extends RESTManagerCommandPlugin{



    @Override
    public InputStream executeCommand(String cmd, InputStream in, Hashtable<String, String> headers, Hashtable<String, String> parameters) {
        File storedFile;
        int index=cmd.lastIndexOf("/");
        String fileName=cmd.substring(index+1);
        storedFile = new File(pluginDir, "resources/storedData/" + fileName);
        storedFile.deleteOnExit();
        
        try {
            return new FileInputStream(storedFile);
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

}
