/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;

/**
 *
 * @author massi
 */
public class ChainExecutor extends Thread {

    private String chain;
    private ChainManager cm;
    private ChainContext ct;

    public ChainExecutor(String chainToExecute, StoreItem item, String id,File appDir) {
        this.chain = chainToExecute;

        cm = new ChainManager();
        ct = cm.createContext();

        ct.setAttribute(CommandsConstants.STORE_ITEM, item);
        ct.setAttribute(CommandsConstants.ITEM_ID, id);
        ct.setAttribute(CommandsConstants.APP_DIR, appDir);
    }

    @Override
    public void run() {
        cm.executeChain(chain, ct);
    }
}
