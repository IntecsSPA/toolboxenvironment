package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import java.sql.Connection;
import it.intecs.pisa.util.DOMUtil;

public class DbCloseTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbClose) throws Exception {
        ((Connection) this.executeChildTag(DOMUtil.getFirstChild(dbClose))).close();
        return null;
    }
}
