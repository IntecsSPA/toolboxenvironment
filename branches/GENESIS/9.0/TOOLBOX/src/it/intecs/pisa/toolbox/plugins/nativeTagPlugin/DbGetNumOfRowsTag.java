package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.util.DOMUtil;
import java.sql.ResultSet;

public class DbGetNumOfRowsTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element dbGetNumOfRows) throws Exception {
        ResultSet rS = (ResultSet) this.executeChildTag(DOMUtil.getFirstChild(dbGetNumOfRows));
        int numOfRows = 0;
        while (rS.next()) {
            numOfRows++;
        }
        rS.beforeFirst();
        Object result = new Integer(numOfRows);
   
        return result;
    }
}
