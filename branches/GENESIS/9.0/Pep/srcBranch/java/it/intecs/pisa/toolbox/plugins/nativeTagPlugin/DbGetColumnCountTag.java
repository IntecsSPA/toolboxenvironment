package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;

public class DbGetColumnCountTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbGetColumnCount) throws Exception {
       Object result = new Integer(((ResultSet) this.executeChildTag(DOMUtil.getFirstChild(
                dbGetColumnCount))).getMetaData().getColumnCount());
       
        return result;
    }
}
