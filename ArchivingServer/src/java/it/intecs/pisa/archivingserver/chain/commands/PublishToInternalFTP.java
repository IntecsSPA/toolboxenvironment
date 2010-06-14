/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.FTPAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.services.FTPService;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.StringUtil;
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
public class PublishToInternalFTP implements Command{

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
            StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            File appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            String itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            String fileName= (String) cc.getAttribute(CommandsConstants.DOWNLOADED_FILE_NAME);
            String username=itemId;
            String password=StringUtil.getRandom(8);

            Properties prop = Prefs.load(appDir);

            if(prop.getProperty("publish.local.ftp.enable").equals("true"))
            {
                File fromFile = new File(prop.getProperty("download.dir"), itemId);
                File userRoot=new File(prop.getProperty("publish.local.ftp.rootdir"), username);
                File toFile= new File(userRoot,fileName);

                userRoot.mkdirs();
                IOUtil.copyFile(fromFile, toFile);

                FTPService ftpServer;

                ftpServer=FTPService.getInstance();
                ftpServer.addUser(username, password, userRoot.getAbsolutePath());

                String url;

                url="ftp://"+username+":"+password+"@"+prop.getProperty("publish.local.ftp.ip")+":"+prop.getProperty("publish.local.ftp.port")+"/"+fileName;
                FTPAccessible.add(itemId, url);
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
