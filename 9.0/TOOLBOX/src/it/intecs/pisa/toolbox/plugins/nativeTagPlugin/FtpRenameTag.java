package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import com.enterprisedt.net.ftp.FTPClient;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FtpRenameTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element ftpElement) throws Exception {
        FTPClient ftpClient = null;
        Element remotePathTag;
        Element newRemotePathTag;
        String oldPath, newPath;
        Document doc;

        try {
            ftpClient = getFTPClient(ftpElement);

            doc = this.offlineDbgTag.getOwnerDocument();

            remotePathTag = doc.createElement("remotePath");
            newRemotePathTag = doc.createElement("newRemotePath");

            this.offlineDbgTag.appendChild(remotePathTag);
            this.offlineDbgTag.appendChild(newRemotePathTag);

            oldPath = (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpElement, REMOTE_PATH)), remotePathTag);
            newPath = (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpElement, NEW_REMOTE_PATH)), newRemotePathTag);

            ftpClient.rename(oldPath, newPath);

            return null;
        } finally {
            ftpClient.quit();
        }
    }
}