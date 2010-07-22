/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pluginscore;

import it.intecs.pisa.toolbox.Toolbox;
import java.io.File;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class ManagerPluginManager extends ToolboxPluginManager {

    public static final String EXTENSION_TYPE_METHOD_GET = "commandViaGet";
    public static final String EXTENSION_TYPE_METHOD_POST = "commandViaPost";
    public static final String EXTENSION_TYPE_METHOD_REST_GET = "restCommandViaGet";
    public static final String EXTENSION_TYPE_METHOD_REST_POST = "restCommandViaPost";
    public static final String EXTENSION_TYPE_METHOD_REST_PUT = "restCommandViaPut";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_REST_GET = "REST_GET";
    public static final String METHOD_REST_POST = "REST_POST";
     public static final String METHOD_REST_PUT = "REST_PUT";
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
            newCommands[parsedInterfaces][2] = METHOD_GET;
        } else if (tagName.equals(EXTENSION_TYPE_METHOD_POST)) {
            newCommands[parsedInterfaces][2] = METHOD_POST;
        } else if(tagName.equals(EXTENSION_TYPE_METHOD_REST_GET)) {
            newCommands[parsedInterfaces][2] = METHOD_REST_GET;
        }else if(tagName.equals(EXTENSION_TYPE_METHOD_REST_POST)) {
            newCommands[parsedInterfaces][2] = METHOD_REST_POST;
        }else if(tagName.equals(EXTENSION_TYPE_METHOD_REST_PUT)) {
            newCommands[parsedInterfaces][2] = METHOD_REST_PUT;
        }
            
        commandClasses = newCommands;
    }

    public IManagerPlugin getCommand(String cmd, String method) {
        String className = "";
        String path="";
        IManagerPlugin commandPlugin;
        try {
            for (String[] command : commandClasses) {
                if(command[0].indexOf("*")>-1 && command[2].equals(method) && matchWithWildCard(command[0],cmd))
                {
                    className = command[1];
                    path=command[3];
                    break;
                }
                else if(command[0].equals(cmd) && command[2].equals(method)) {
                    className = command[1];
                    path=command[3];
                    break;
                }
            }

            commandPlugin=(IManagerPlugin) loader.loadClass(className).newInstance();
            commandPlugin.setPluginDirectory(new File(path));
            return commandPlugin;
        } catch (Exception e) {
            Toolbox tbx=Toolbox.getInstance();
            Logger logger=tbx.getLogger();

            logger.error("Cannot find proper class for manager command "+cmd);

            return null;
        }
    }



    @Override
    protected boolean isTagHandled(String tagname) {
        return tagname.equals(EXTENSION_TYPE_METHOD_GET)
                || tagname.equals(EXTENSION_TYPE_METHOD_POST)
                || tagname.equals(EXTENSION_TYPE_METHOD_REST_GET)
                || tagname.equals(EXTENSION_TYPE_METHOD_REST_POST)
                || tagname.equals(EXTENSION_TYPE_METHOD_REST_PUT);
    }

    public static ManagerPluginManager getInstance() {
        return manager;
    }

    private boolean matchWithWildCard(String stringWithCard, String cmd) {
        boolean matches=true;

        try
        {
            StringTokenizer tokenizer,cmdTokenizer;

            tokenizer=new StringTokenizer(stringWithCard,"/");
            cmdTokenizer=new StringTokenizer(cmd,"/");

            while(tokenizer.hasMoreElements() && cmdTokenizer.hasMoreElements())
            {
                String tok,cmdtok;

                tok=tokenizer.nextToken();
                cmdtok=cmdTokenizer.nextToken();

                if(tok.equals("*")==false && tok.equals(cmdtok)==false)
                    return false;
            }

            if(tokenizer.hasMoreElements() != cmdTokenizer.hasMoreElements())
                return false;
        }
        catch(Exception e)
        {
            matches=false;
        }

        return matches;
    }
}
