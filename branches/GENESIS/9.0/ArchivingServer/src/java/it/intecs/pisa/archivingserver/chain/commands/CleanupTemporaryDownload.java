
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CleanupTemporaryDownload implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        File appDir;
        try {
            appDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);

            File downloadedFile;
            downloadedFile=new File(Prefs.getDownloadFolder(appDir),itemId);
            downloadedFile.delete();
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
