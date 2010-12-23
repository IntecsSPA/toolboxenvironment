package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.exceptions.ReturnTagException;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import org.w3c.dom.Element;

public class TryTag extends NativeTagExecutor {
    private static final String ERROR_MESSAGE_NAME = "errorMessageName";
    
    @Override
    public Object executeTag(org.w3c.dom.Element tryElement) throws Exception {
        Iterator children = DOMUtil.getChildren(tryElement).iterator();
        Element body = (Element) children.next();
        Element ifError = (Element) children.next();
        Element ifErrorDebug=null;
        Element finallyDebug=null;
        
        if (children.hasNext()) {
            try {
                this.executeChildTag(body);
            } catch (ReturnTagException rtEcc) {
                throw rtEcc;
            } catch (Exception e) {
                put(ifError.getAttribute(ERROR_MESSAGE_NAME), e.getMessage());
                
                ifErrorDebug=this.offlineDbgTag.getOwnerDocument().createElement("ifError");
                this.offlineDbgTag.appendChild(ifErrorDebug);
                
                this.executeChildTag(DOMUtil.getFirstChild(ifError),ifErrorDebug);
            } finally {
                 finallyDebug=this.offlineDbgTag.getOwnerDocument().createElement("finally");
                this.offlineDbgTag.appendChild(finallyDebug);
                
                return this.executeChildTag(DOMUtil.getFirstChild((Element) children.next()),finallyDebug);
            }
        } else {
            try {
                return this.executeChildTag(body);
            } catch (ReturnTagException rtEcc) {
                throw rtEcc;
            } catch (Exception e) {
                put(ifError.getAttribute(ERROR_MESSAGE_NAME), e.getMessage());
                
                ifErrorDebug=this.offlineDbgTag.getOwnerDocument().createElement("ifError");
                this.offlineDbgTag.appendChild(ifErrorDebug);
                
                return this.executeChildTag(DOMUtil.getFirstChild(ifError),ifErrorDebug);
            }
        }
    }
}
