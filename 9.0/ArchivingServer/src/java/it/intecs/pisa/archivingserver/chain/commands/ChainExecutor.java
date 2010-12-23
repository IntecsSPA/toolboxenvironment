/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.log.Log;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;
import javawebparts.misc.chain.Result;

/**
 *
 * @author massi
 */
public class ChainExecutor extends Thread {

    private String chain;
    private String rollbackChain=null;
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

    public ChainExecutor(String chainToExecute,String chainToExecuteInRoolback, StoreItem item, String id,File appDir) {
        this(chainToExecute,item,id,appDir);

        this.rollbackChain=chainToExecuteInRoolback;
    }

    @Override
    public void run() {
        cm.executeChain(chain, ct);

        Result res = ct.getResult();
        if(res.getCode()==Result.FAIL && rollbackChain!=null)
        {
            Log.log("Chain failed, executing rollback chain");
            cm.executeChain(rollbackChain, ct);
        }
    }
}
