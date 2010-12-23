package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPFile;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FtpGetTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element ftpGet) throws Exception {
        FTPClient ftpClient = null;
        Element remotePathTag;
        Element localPathTag;
        String localPath, remotePath;
        FTPFile[] dirListing;
        Document doc;

        try {
            ftpClient = getFTPClient(ftpGet);
            
            doc = this.offlineDbgTag.getOwnerDocument();

            remotePathTag = doc.createElement("remotePath");
            localPathTag = doc.createElement("localPath");

            this.offlineDbgTag.appendChild(remotePathTag);
            this.offlineDbgTag.appendChild(localPathTag);

            localPath = (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpGet, LOCAL_PATH)),remotePathTag);
            remotePath = (String) executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpGet, REMOTE_PATH)),localPathTag);

            downloadDir(ftpClient, remotePath, localPath);
        } catch (com.enterprisedt.net.ftp.FTPException e) {
            if (DOMUtil.getBool(ftpGet.getAttribute("pasv"))) {
            } else {
                throw e;
            }
        } finally {
            ftpClient.quit();
        }
        return null;
    }
}