package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import com.enterprisedt.net.ftp.FTPClient;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FtpExistsTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element ftpExists) throws Exception {
        FTPClient ftpClient = null;
        Object result = null;
        Element remotePathTag;
        Document doc;

        try {
            ftpClient = getFTPClient(ftpExists);

            doc = this.offlineDbgTag.getOwnerDocument();

            remotePathTag = doc.createElement("remotePath");
            this.offlineDbgTag.appendChild(remotePathTag);

            String remotePath = (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpExists, REMOTE_PATH)), remotePathTag);

            boolean exists = (ftpClient.dir(remotePath).length > 0);

            result = new Boolean(exists);

            return result;
        } finally {
            ftpClient.quit();
        }
    }
}
