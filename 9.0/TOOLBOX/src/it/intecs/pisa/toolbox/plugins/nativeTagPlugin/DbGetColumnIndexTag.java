package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbGetColumnIndexTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbGetColumnIndex) throws Exception {
       Iterator children = DOMUtil.getChildren(dbGetColumnIndex).iterator();
        Object result = new Integer(((ResultSet) this.executeChildTag((Element) children.next())).findColumn((String) executeChildTag((Element) children.next())));
       
        return result;
    }
}
