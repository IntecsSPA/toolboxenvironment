/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.HttpAccessible;
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
public class PublishToInternalHttp implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        File fromFile,toDir,toFile,appDir;
        String downloadFileName;
        String itemId;
        Properties prop;
        StoreItem storeItem;

        try {
            appDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            downloadFileName=(String) cc.getAttribute(CommandsConstants.DOWNLOADED_FILE_NAME);
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            storeItem=(StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);

            prop=Prefs.load(appDir);
            fromFile=new File(Prefs.getDownloadFolder(appDir),itemId);

            if(prop.getProperty(Prefs.PUBLISH_LOCAL_HTTP_ENABLE).equals("true") || storeItem.publishHttp==true )
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
