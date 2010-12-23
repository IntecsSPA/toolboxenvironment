/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.services;

import it.intecs.pisa.archivingserver.chain.commands.CommandsConstants;
import java.io.File;
import java.util.TimerTask;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteItemTask extends TimerTask{
    protected String id;
    protected File appdir;

    public DeleteItemTask(String item,File dir)
    {
        id=item;
        appdir=dir;
    }

    @Override
    public void run() {
        System.out.println("Deleting item "+id);
        ChainManager cm = new ChainManager();
        ChainContext ct = cm.createContext();

        ct.setAttribute(CommandsConstants.ITEM_ID, id);
        ct.setAttribute(CommandsConstants.APP_DIR, appdir);
        cm.executeChain("Catalogue/deleteChain", ct);

        int success = ct.getResult().getCode();
        if(success==Result.FAIL)
        {
            System.out.println("Not delete item "+id);
        }
    }

}
