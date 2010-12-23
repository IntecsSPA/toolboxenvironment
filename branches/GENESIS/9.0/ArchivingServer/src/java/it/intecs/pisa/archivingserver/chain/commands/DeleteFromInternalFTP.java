/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.FTPAccessible;
import it.intecs.pisa.archivingserver.db.HttpAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.services.FTPService;
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
public class DeleteFromInternalFTP implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        Properties prop;
        File ftpUserDir;
        File itemDir;

        try {
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            File appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);

            try
            {
                FTPService ftp;
                ftp=FTPService.getInstance();
                ftp.removeUser(itemId);
                FTPAccessible.delete(itemId);

                prop=Prefs.load(appDir);
                ftpUserDir=new File(prop.getProperty("publish.local.ftp.rootdir"),itemId);
                IOUtil.rmdir(ftpUserDir);
            }
            catch(Exception e){
                Log.logException(e);
            }

            HttpAccessible.delete(itemId);
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
