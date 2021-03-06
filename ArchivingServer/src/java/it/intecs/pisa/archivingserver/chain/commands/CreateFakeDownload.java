/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreateFakeDownload implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        File localFile;
        File webappDir;

        Properties prop;
        try {
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            localFile=(File) cc.getAttribute(CommandsConstants.LOCAL_FILE);
            webappDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);

            File destFile;

            prop=Prefs.load(webappDir);
            destFile=new File(prop.getProperty("download.dir"),itemId);

            IOUtil.moveFile(localFile,destFile);

        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
