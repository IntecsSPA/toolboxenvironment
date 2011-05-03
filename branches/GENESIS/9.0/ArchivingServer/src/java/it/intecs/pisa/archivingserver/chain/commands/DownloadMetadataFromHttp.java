/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DownloadMetadataFromHttp implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        StoreItem storeItem;
        File outFile;
        File webappDir;
        String id;
        File dowloandDirFile;
        Document doc = null;
        try {
            storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            id=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            webappDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);

            if (storeItem.metadataUrl.startsWith("http://")) {
                dowloandDirFile=Prefs.getDownloadFolder(webappDir);
                outFile=new File(dowloandDirFile,id+"_metadata.xml");

                doc=download(storeItem.metadataUrl,outFile);
                cc.setAttribute(CommandsConstants.ITEM_METADATA, doc);
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

    protected Document download(String urlStr, File downloadedFile) throws Exception {
        URL url;
        InputStream stream=null;
        Document doc=null;
        DOMUtil util;

        try {
            url = new URL(urlStr);
            stream = url.openStream();

            util=new DOMUtil();
            doc=util.inputStreamToDocument(stream);
        } finally {
            if(stream!=null)
                stream.close();

            return doc;
        }
    }
}
