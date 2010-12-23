package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbGetValueTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbGetValue) throws Exception {
        Iterator children = DOMUtil.getChildren(dbGetValue).iterator();
        ResultSet resultSet = (ResultSet) this.executeChildTag((Element) children.next());
        Object index = executeChildTag((Element) children.next());
        if (index instanceof Number) {
            return resultSet.getString(((Number) index).intValue());
        } else {
            return resultSet.getString(String.valueOf(index));
        }
    }
}
