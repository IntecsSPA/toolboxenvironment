/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.pluginscore;

import java.io.File;
import java.util.Vector;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class UIPluginManager extends ToolboxPluginManager{
    protected static final String TAG_UI="ui";
    protected String[][] uis;
    protected static UIPluginManager manager = new UIPluginManager();

    protected UIPluginManager()
    {
        uis=new String[0][6];
    }
    @Override
    protected void handleTag(File file, Element el) {
        int parsedUIS = 0;
        String[][] newCommands = null;
        String tagName;

        parsedUIS = uis.length;
        newCommands = new String[parsedUIS + 1][6];


        for (int i = 0; i < parsedUIS; i++) {
            newCommands[i][0] = uis[i][0];
            newCommands[i][1] = uis[i][1];
            newCommands[i][2] = uis[i][2];
            newCommands[i][3] = uis[i][3];
            newCommands[i][4] = uis[i][4];
            newCommands[i][5] = uis[i][5];
        }

        newCommands[parsedUIS][0] = el.getAttribute("id");
        newCommands[parsedUIS][1] = el.getAttribute("group");
        newCommands[parsedUIS][2] = el.getAttribute("userReadableId");
        newCommands[parsedUIS][3] = el.getAttribute("link");
        newCommands[parsedUIS][4] = el.getAttribute("run");
        newCommands[parsedUIS][5] = file.getAbsolutePath();
        
        uis = newCommands;
    }

    public String[] getIdForGroup(String groupId)
    {
        Vector<String> ids;
        String[] retIds;

        ids=new Vector<String>();
        for(String[] idArray:uis)
        {
            if(idArray[1].equals(groupId))
                ids.add(idArray[0]);
        }

        retIds=new String[ids.size()];
        for(int i=0;i<ids.size();i++)
        {
            retIds[i]=ids.get(i);
        }

        return retIds;
    }

     public String getUserReadableForid(String id)
    {
        for(String[] idArray:uis)
        {
            if(idArray[0].equals(id))
                return idArray[2];
        }

        return null;
    }

    public String getLinkForid(String id)
    {
        for(String[] idArray:uis)
        {
            if(idArray[0].equals(id))
                return idArray[3];
        }

        return null;
    }

    public String getCommandToExecuteForid(String id)
    {
        for(String[] idArray:uis)
        {
            if(idArray[0].equals(id))
                return idArray[4];
        }

        return null;
    }


    @Override
    protected boolean isTagHandled(String tagname) {
        return tagname.equals(TAG_UI);
    }

    public static UIPluginManager getInstance() {
        return manager;
    }
}
