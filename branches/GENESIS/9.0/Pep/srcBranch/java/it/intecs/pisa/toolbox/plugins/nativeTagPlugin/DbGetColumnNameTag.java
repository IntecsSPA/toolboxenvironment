package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;
import java.util.Iterator;
import org.w3c.dom.Element;

public class DbGetColumnNameTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbGetColumnName) throws Exception {
     Iterator children = DOMUtil.getChildren(dbGetColumnName).iterator();
        Object result = ((ResultSet) this.executeChildTag((Element) children.next())).getMetaData().
                getColumnName(getInt((Element) children.next()));
  
        return result;
    }
}
