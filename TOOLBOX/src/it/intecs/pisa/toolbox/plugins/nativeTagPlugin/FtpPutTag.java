package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import com.enterprisedt.net.ftp.FTPClient;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FtpPutTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element ftpPut) throws Exception {
        FTPClient ftpClient = null;
        Element remotePathTag;
        Element localPathTag;
        Document doc;

        try {
            ftpClient = getFTPClient(ftpPut);

            doc = this.offlineDbgTag.getOwnerDocument();

            remotePathTag = doc.createElement("remotePath");
            localPathTag = doc.createElement("localPath");

            this.offlineDbgTag.appendChild(remotePathTag);
            this.offlineDbgTag.appendChild(localPathTag);


            ftpClient.put(
                    (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpPut, LOCAL_PATH)), remotePathTag),
                    (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpPut, REMOTE_PATH)), localPathTag));

            return null;
        } finally {
            ftpClient.quit();
        }
    }
}