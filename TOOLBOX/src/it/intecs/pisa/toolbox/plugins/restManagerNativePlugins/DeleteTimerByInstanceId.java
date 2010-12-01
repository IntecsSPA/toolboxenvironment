/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.timers.TimerManager;
import it.intecs.pisa.util.json.JsonErrorObject;
import it.intecs.pisa.util.json.JsonSuccessObject;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;

/**
 *
 * @author simone
 */
public class DeleteTimerByInstanceId extends RESTManagerCommandPlugin {

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        StringTokenizer tokenizer;
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet res = null;
        try {
            tokenizer = new StringTokenizer(method, "/");
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            String instanceID = tokenizer.nextToken();


            sql = "select EXTRA_VALUE from T_TIMERS where TYPE='TIMER' and INSTANCE_ID=" + instanceID;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            res = stm.executeQuery(sql);
            String timerID;
            JsonObject timers = null;

            int resSize = 0;

            while (res.next()) {
                timerID = res.getString("extra_value");
                TimerManager.getInstance().deleteTask(timerID);

            }
            sql = "DELETE FROM T_TIMERS WHERE TYPE='TIMER' and INSTANCE_ID=" + instanceID;

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);

            return JsonSuccessObject.get();
        } catch (Exception e) {
            return JsonErrorObject.get("Unable to delete timer task");
        }
    }
}