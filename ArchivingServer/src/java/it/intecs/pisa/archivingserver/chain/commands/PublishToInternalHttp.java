/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.HttpAccessible;
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
public class PublishToInternalHttp implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        File fromFile,toDir,toFile,appDir;
        String downloadFileName;
        String itemId;
        Properties prop;
        try {
            appDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            downloadFileName=(String) cc.getAttribute(CommandsConstants.DOWNLOADED_FILE_NAME);
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);

            prop=Prefs.load(appDir);
            fromFile=new File(prop.getProperty("download.dir"),itemId);

            if(prop.getProperty("publish.local.http").equals("true"))
            {
                toDir=new File(appDir,"storagearea/"+itemId);
                toDir.mkdirs();

                toFile=new File(toDir,downloadFileName);

                IOUtil.copyFile(fromFile, toFile);

                String publicUrl;
                publicUrl=prop.getProperty("http.public.url");

                String fullUrl;
                fullUrl=publicUrl+"/storagearea/"+itemId+"/"+downloadFileName;
                HttpAccessible.add(itemId, fullUrl);
            }
        } catch (Exception e) {
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}