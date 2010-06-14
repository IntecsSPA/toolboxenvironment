/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.ItemRefDB;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.services.AutomaticItemDeleteService;
import java.util.Date;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class RegisterItemRef implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        String itemId;
        long expireDate=0;
        long arrivalDate=0;
        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
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
                expireDate=0;
            }
            
            ItemRefDB.register(itemId,arrivalDate,expireDate);
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
