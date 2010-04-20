package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.Connection;

public class DbRollbackTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbRollback) throws Exception {
         ((Connection) this.executeChildTag(DOMUtil.getFirstChild(dbRollback))).rollback();
        return null;
    }
}
