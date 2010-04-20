package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.util.DOMUtil;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ThrowTag extends NativeTagExecutor {
    protected static final String FORMAT = "format";

    @Override
    public Object executeTag(org.w3c.dom.Element throwstm) throws Exception {
        String errorMessage = new String();
        LinkedList params = DOMUtil.getChildren(throwstm);
        Document details;
        Element errorMessageDebug=null;
        Element detailsDebug=null;
        Document ownerDocument=null;
        
        ownerDocument=this.offlineDbgTag.getOwnerDocument();
        errorMessageDebug=ownerDocument.createElement("erroMessage");
        detailsDebug=ownerDocument.createElement("details");
        
        offlineDbgTag.appendChild(errorMessageDebug);
        offlineDbgTag.appendChild(detailsDebug);
        
        errorMessage = (String) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByTagName(throwstm, "errorMessage")),errorMessageDebug);
    
        details = (Document) this.executeChildTag(DOMUtil.getFirstChild(DOMUtil.getChildByTagName(throwstm, "details")),detailsDebug);
    
        throw new ToolboxException(errorMessage, details.getDocumentElement());
    }
}
