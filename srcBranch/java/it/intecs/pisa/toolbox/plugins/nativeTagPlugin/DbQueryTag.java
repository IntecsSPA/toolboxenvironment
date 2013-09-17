package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbQueryTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbQuery) throws Exception {
         Iterator children = DOMUtil.getChildren(dbQuery).iterator();
        Statement statement = ((Connection) this.executeChildTag((Element) children.next())).createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        ResultSet rS = statement.executeQuery((String) executeChildTag((Element) children.next()));
        ResultSetMetaData rSM;
        int numOfColumns = (rSM = rS.getMetaData()).getColumnCount();

        int i = 0;

        rS.beforeFirst();
        return rS;
    }
}
