package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.toolbox.Toolbox;
import java.sql.ResultSet;
import java.sql.Statement;

public class CheckOrderConfirmationTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element elem) throws Exception {
        Statement stm;
        ResultSet rs;

        stm = Toolbox.getDbStatement();

        String query = "select status from T_SYNC_POINTS where id=" + this.engine.evaluateString(elem.getAttribute( "id"),IEngine.EngineStringType.ATTRIBUTE);

        rs = stm.executeQuery(query);
        rs.next();

        if (rs.getString("status").equals("NO_SYNC")) {
            return new Boolean(false);
        } else {
            return new Boolean(true);
        }
    }
}
