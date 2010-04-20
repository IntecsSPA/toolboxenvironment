/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pluginscore;

import java.io.File;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class ManagerPluginManager extends ToolboxPluginManager {

    public static final String EXTENSION_TYPE_METHOD_GET = "commandViaGet";
    public static final String EXTENSION_TYPE_METHOD_POST = "commandViaPost";
    protected String[][] commandClasses = new String[0][3];
    private static ManagerPluginManager manager = new ManagerPluginManager();

    @Override
    protected void handleTag(File dir, Element el) {
        int parsedInterfaces = 0;
        String[][] newCommands = null;
        String tagName;

        parsedInterfaces = commandClasses.length;
        newCommands = new String[parsedInterfaces + 1][4];


        for (int i = 0; i < parsedInterfaces; i++) {
            newCommands[i][0] = commandClasses[i][0];
            newCommands[i][1] = commandClasses[i][1];
            newCommands[i][2] = commandClasses[i][2];
            newCommands[i][3] = commandClasses[i][3];
        }

        newCommands[parsedInterfaces][0] = el.getAttribute("cmd");
        newCommands[parsedInterfaces][1] = el.getAttribute("class");
        newCommands[parsedInterfaces][3] = dir.getAbsolutePath();

        tagName = el.getTagName();
        if (tagName.equals(EXTENSION_TYPE_METHOD_GET)) {
            newCommands[parsedInterfaces][2] = "GET";
        } else if (tagName.equals(EXTENSION_TYPE_METHOD_POST)) {
            newCommands[parsedInterfaces][2] = "POST";
        }

        commandClasses = newCommands;
    }

    public IManagerPlugin getCommand(String cmd, String method) {
        String className = "";
        String path="";
        IManagerPlugin commandPlugin;
        try {
            for (String[] command : commandClasses) {
                if (command[0].equals(cmd) && command[2].equals(method)) {
                    className = command[1];
                    path=command[3];
                    break;
                }
            }

            commandPlugin=(IManagerPlugin) loader.loadClass(className).newInstance();
            commandPlugin.setPluginDirectory(new File(path));
            return commandPlugin;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected boolean isTagHandled(String tagname) {
        return tagname.equals(EXTENSION_TYPE_METHOD_GET) || tagname.equals(EXTENSION_TYPE_METHOD_POST);
    }

    public static ManagerPluginManager getInstance() {
        return manager;
    }
}
