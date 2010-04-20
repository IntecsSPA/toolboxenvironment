package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.Connection;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbUpdateTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbUpdate) throws Exception {
         Iterator children = DOMUtil.getChildren(dbUpdate).iterator();
        ((Connection) this.executeChildTag((Element) children.next())).createStatement().
                executeUpdate((String) executeChildTag((Element) children.next()));
        return null;
    }
}
