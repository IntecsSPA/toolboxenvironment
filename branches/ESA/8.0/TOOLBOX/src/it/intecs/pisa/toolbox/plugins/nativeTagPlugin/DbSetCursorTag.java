package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbSetCursorTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbSetCursor) throws Exception {
        Iterator children = DOMUtil.getChildren(dbSetCursor).iterator();
        Integer position = (Integer) this.executeChildTag((Element) children.next());
        ResultSet resultSet = (ResultSet) executeChildTag((Element) children.next());
        return new Boolean(resultSet.absolute(position.intValue()));
    }
}
