package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.toolbox.Toolbox;
import java.sql.ResultSet;
import java.sql.Statement;

public class AddOrderConfirmationTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element order) throws Exception {
         Object returnObj = null;
        String serviceName, operationName;
        Statement stm;
        String mnemonicId;
        String instanceKey;

        //getting mnemonicId
        mnemonicId = this.engine.evaluateString(order.getAttribute( "mnemonicId"),IEngine.EngineStringType.ATTRIBUTE);
        if (mnemonicId.length() > 256) {
            mnemonicId = mnemonicId.substring(0, 256);
        //getting variables
        }
        instanceKey = (String) this.get("instanceKey");
        if (instanceKey.length() > 256) {
            instanceKey = instanceKey.substring(0, 256);
        }
        serviceName = (String) this.get("serviceName");
        operationName = (String) this.get("operation");

        //opening connection
        stm = Toolbox.getDbStatement();

        //getting id
        String seqQuery = "SELECT count(id) as countid,NEXT VALUE FOR  syncSeq as sequence from t_sync_points;";
        ResultSet sequenceRS;

        sequenceRS = stm.executeQuery(seqQuery);
        sequenceRS.next();

        Integer seqID = new Integer(sequenceRS.getInt("sequence"));

        String insertQuery = "INSERT INTO T_SYNC_POINTS VALUES(" + seqID + ",'ORDER','" + serviceName + "','" + operationName + "','NO_SYNC',NOW(),'" + mnemonicId + "','" + instanceKey + "','','')";

        stm = Toolbox.getDbStatement();
        stm.executeUpdate(insertQuery);

        stm = Toolbox.getDbStatement();
        stm.execute("COMMIT");

        return seqID;
    }
    
 
}
