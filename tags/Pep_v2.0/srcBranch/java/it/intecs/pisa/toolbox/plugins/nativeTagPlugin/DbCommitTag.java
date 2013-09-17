package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import java.sql.Connection;
import it.intecs.pisa.util.DOMUtil;

public class DbCommitTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbCommit) throws Exception {
       ((Connection) this.executeChildTag(DOMUtil.getFirstChild(dbCommit))).commit();
        return null;
    }
}
