/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.FTPAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.util.ftp.FTPLinkTokenizer;
import java.io.File;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteFromFTP implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        File itemDir;
        try {
            itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);

            String[] ftplinks;
            ftplinks = FTPAccessible.getUrls(itemId);

            for (String url : ftplinks) {
                delete(url);
            }

            FTPAccessible.delete(itemId);
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private void delete(String url) {
        String scheme;

        try {
            FTPLinkTokenizer tokenizer;

            tokenizer = new FTPLinkTokenizer(url);

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

            f.deleteFile(tokenizer.getPath());
            f.logout();
        } catch (Exception e) {
            Log.logException(e);
        }
    }
}
