/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.FTPAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.ftp.FTPLinkTokenizer;
import java.io.File;
import java.io.FileInputStream;
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
public class PublishToFTP implements Command{

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
            StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            File appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            String itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);

            for(String ftp: storeItem.publishFtp)
            {
                storeFile(ftp,itemId,appDir);
            }
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private void storeFile(String ftp, String itemId,File appDir) {
        String scheme;

        try
        {
        scheme=ftp.substring(0,ftp.indexOf(":"));
        if(scheme.equals("ftp"))
        {
            FTPLinkTokenizer tokenizer;

            tokenizer=new FTPLinkTokenizer(ftp);

            FTPClient f = new FTPClient();
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

            Properties prop = Prefs.load(appDir);
            File fromFile = new File(prop.getProperty("download.dir"), itemId);

            InputStream stream;
            stream=new FileInputStream(fromFile);
            f.storeFile(tokenizer.getPath(), stream);
            f.logout();

            FTPAccessible.add(itemId, ftp);
        }
        }
        catch(Exception e)
        {

        }
    }

}
