/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.DownloadsDB;
import it.intecs.pisa.archivingserver.log.Log;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteDownload implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        File appDir;
        File itemDir;
        try {

            appDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);

            DownloadsDB.delete(itemId);
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
