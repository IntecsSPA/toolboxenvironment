/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.net.MalformedURLException;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import javax.xml.transform.TransformerException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import org.w3c.dom.Document;

/**
 *
 * @author massi
 */
public class PublishToSOS implements Command {
    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        File downloadedFile, appDir;
        String itemId;
        Properties prop;
        Document SOSmessage;
        String downloadFileName;

        try {
            appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            // check if this is needed
            downloadFileName = (String) cc.getAttribute(CommandsConstants.DOWNLOADED_FILE_NAME);
            itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);

            if(storeItem.publishSOS.length>0)
            {
                prop = Prefs.load(appDir);
                downloadedFile = new File(prop.getProperty("download.dir"), itemId);
                SOSmessage = DOMUtil.loadXML(downloadedFile, null);

                for (String url : storeItem.publishSOS) {
                    try {
                        harvestData(itemId, url, SOSmessage);
                    } catch (Exception e) {
                        Log.logException(e);
                    }
                }
            }
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    private boolean harvestData(String id, String url, Document doc) throws Exception, MalformedURLException {
        URL to = new URL(url);
        HttpURLConnection conn;
        Document response;
        try {
            conn = (HttpURLConnection) to.openConnection();
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-Type", "text/xml; charset=utf-8");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            OutputStream outStream = conn.getOutputStream();
            IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), outStream);

            DOMUtil util;
            InputStream inStream = conn.getInputStream();
            util = new DOMUtil();
            response = util.inputStreamToDocument(inStream);
        } catch (Exception e) {
            throw new Exception("Cannot insert data on the SOS server. Details: " + e.getLocalizedMessage());
        }

        return isSuccessful(response);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private Boolean isSuccessful(Document resp) throws TransformerException {
        return false;
    }
}
