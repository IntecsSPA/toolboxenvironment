/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.DownloadsDB;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.ftp.FTPLinkTokenizer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author massi
 */
public class DownloadFromFtp implements Command {

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
            storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            id = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            webappDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);

            if (storeItem.downloadUrl.startsWith("ftp://")) {
                DownloadsDB.addDownload(id, storeItem.downloadUrl);
                DownloadsDB.updateStatus(id, "DOWNLOADING");

                prop = Prefs.load(webappDir);
                outFile = new File(prop.getProperty("download.dir"), id);

                download(storeItem.downloadUrl, outFile);

                DownloadsDB.updateStatus(id, "DOWNLOAD COMPLETE");

                fileName = storeItem.downloadUrl.substring(storeItem.downloadUrl.lastIndexOf("/") + 1);
                cc.setAttribute(CommandsConstants.DOWNLOADED_FILE_NAME, fileName);
            }
        } catch (Exception e) {
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private void download(String downloadUrl, File outFile) {
        FTPClient f;
        FTPLinkTokenizer tokenizer;

        try {
            tokenizer=new FTPLinkTokenizer(downloadUrl);

            f = new FTPClient();
            f.setDefaultPort(Integer.parseInt(tokenizer.getPort()));
            f.connect(tokenizer.getHost());
            f.setFileType(FTP.BINARY_FILE_TYPE);

            if (tokenizer.getUsername() != null) {
                f.login(tokenizer.getUsername(), tokenizer.getPassword());
            }

            int reply = f.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                f.disconnect();
                return;
            }
            InputStream stream=f.retrieveFileStream(tokenizer.getPath());
            IOUtil.copy(stream, new FileOutputStream(outFile));

            f.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
