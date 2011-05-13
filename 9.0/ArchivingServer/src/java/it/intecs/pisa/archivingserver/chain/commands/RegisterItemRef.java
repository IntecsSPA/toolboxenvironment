/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.ItemRefDB;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.services.AutomaticItemDeleteService;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class RegisterItemRef implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        long expireDate=0;
        long arrivalDate=0;
        Properties prop;
        try {
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);

            arrivalDate=(new Date()).getTime();

            if(storeItem.deleteAfter!=0)
            {             
                expireDate=arrivalDate+storeItem.deleteAfter;
                AutomaticItemDeleteService.getInstance().addRemovableItem(itemId, new Date(expireDate));
            }
            else
            {
                File appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
                prop = Prefs.load(appDir);
                String intervalStr = prop.getProperty("delete.after"); 
                expireDate=(new Date()).getTime() + 
                                TimeInterval.getIntervalAsLong(intervalStr);
            }
            
            ItemRefDB.register(itemId,arrivalDate,expireDate);
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
