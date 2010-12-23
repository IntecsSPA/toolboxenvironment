package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.toolbox.Toolbox;
import java.sql.ResultSet;
import java.sql.Statement;

public class GetOrderErrorTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element elem) throws Exception {
        Statement stm;
        ResultSet rs;

        stm = Toolbox.getDbStatement();

        String query = "select ERRORMSG from T_SYNC_POINTS where id=" + this.engine.evaluateString(elem.getAttribute( "id"),IEngine.EngineStringType.ATTRIBUTE);

        rs = stm.executeQuery(query);
        rs.next();
 
         return rs.getString("ERRORMSG");
    }
    
 
}
