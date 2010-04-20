package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;

public class DbNextRowTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbNextRow) throws Exception {
          return new Boolean(((ResultSet) this.executeChildTag(DOMUtil.getFirstChild(
                dbNextRow))).next());
    }
}
