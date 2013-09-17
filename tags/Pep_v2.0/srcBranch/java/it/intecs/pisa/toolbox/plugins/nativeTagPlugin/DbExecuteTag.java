package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbExecuteTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbExecute) throws Exception {
       Iterator children = DOMUtil.getChildren(dbExecute).iterator();
        Statement statement = ((Connection) this.executeChildTag((Element) children.next())).createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        return new Boolean(statement.execute((String) executeChildTag((Element) children.next())));
    }
}
