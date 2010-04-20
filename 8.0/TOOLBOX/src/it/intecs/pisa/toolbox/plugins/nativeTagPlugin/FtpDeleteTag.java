package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import com.enterprisedt.net.ftp.FTPClient;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FtpDeleteTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element ftpDelete) throws Exception {
        Object result = null;
        FTPClient ftpClient = null;
        Element remotePathTag;
        Document doc;

        try {
            ftpClient = getFTPClient(ftpDelete);
            
            doc = this.offlineDbgTag.getOwnerDocument();

            remotePathTag = doc.createElement("remotePath");

            this.offlineDbgTag.appendChild(remotePathTag);

            String remotePath = (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByLocalName(ftpDelete, REMOTE_PATH)), remotePathTag);

            ftpClient.delete(remotePath);

            result = new Boolean(true);

        } finally {
            ftpClient.quit();
        }

        return result;
    }
}
