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
import java.sql.Statement;
import java.util.StringTokenizer;

/**
 *
 * @author simone
 */
public class DeleteTimerByInstanceId extends RESTManagerCommandPlugin{
     @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception  {
        StringTokenizer tokenizer;
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        try {
            tokenizer = new StringTokenizer(method, "/");
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            String instanceID=tokenizer.nextToken();
            TimerManager.getInstance().deleteTask(instanceID);

            sql = "DELETE FROM T_TIMERS WHERE TYPE='TIMER' and INSTANCE_ID="+ instanceID;

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);

            return JsonSuccessObject.get();
        }
        catch(Exception e)
        {
            return JsonErrorObject.get("Unable to delete service");
        }
    }
}