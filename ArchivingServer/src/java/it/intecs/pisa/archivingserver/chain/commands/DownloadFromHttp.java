/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.DownloadsDB;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DownloadFromHttp implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        StoreItem storeItem;
        File outFile;
        File webappDir;
        String fileName;
        String id;
        Properties prop;

        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
            storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            id=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            webappDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);

            if (storeItem.downloadUrl.startsWith("http://")) {
                DownloadsDB.addDownload(id, storeItem.downloadUrl);
                DownloadsDB.updateStatus(id, "DOWNLOADING");

                prop=Prefs.load(webappDir);
                outFile=new File(prop.getProperty("download.dir"),id);

                download(storeItem.downloadUrl,outFile);

                DownloadsDB.updateStatus(id, "DOWNLOAD COMPLETE");

                fileName=storeItem.downloadUrl.substring(storeItem.downloadUrl.lastIndexOf("/")+1);
                cc.setAttribute(CommandsConstants.DOWNLOADED_FILE_NAME, fileName);
            }
        } catch (Exception e) {
            id=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            try {
                DownloadsDB.updateStatus(id, "DOWNLOAD ERROR");
            } catch (Exception ex) {
                Log.log(ex.getMessage());
            }
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    protected void download(String urlStr, File downloadedFile) throws Exception {
        URL url;
        InputStream stream=null;
        byte[] buffer;
        int count = 0;
        FileOutputStream outStream=null;

        try {
            buffer = new byte[1024];

            url = new URL(urlStr);
            stream = url.openStream();

            outStream = new FileOutputStream(downloadedFile);
            IOUtil.copy(stream, outStream);
        } finally {
            if(stream!=null)
                stream.close();

            if(outStream!=null)
            {
                outStream.flush();
                outStream.close();
            }
        }
    }
}
