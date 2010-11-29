/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.restManagerNativePlugins;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.util.json.JsonErrorObject;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

/**
 *
 * @author simone
 */
public class GetServiceTimerList extends RESTManagerCommandPlugin {

    @Override
    public JsonObject executeCommand(String method, JsonObject request) throws Exception {
        StringTokenizer tokenizer;
        SimpleDateFormat toFormatter = new SimpleDateFormat();
        JsonObject outputJson = new JsonObject();
        JsonArray array = new JsonArray();
        ToolboxInternalDatabase db;
        Statement stm = null;
        Long duedate;
        Long extra;
        Long instanceID;
        String dueDate="";
        String description="";

        String sql;
        ResultSet res = null;

        try {
            tokenizer = new StringTokenizer(method, "/");
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            String serviceName = tokenizer.nextToken();

            sql = "select EXTRA_VALUE,DUE_DATE,SCRIPT,INSTANCE_ID,DESCRIPTION from T_TIMERS where TYPE='TIMER' and INSTANCE_ID in (select ID from T_SERVICE_INSTANCES where SERVICE_NAME='" + serviceName+"')";
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            res = stm.executeQuery(sql);

            JsonObject timers = null;
            while (res.next()) {

                extra=(Long) res.getLong("extra_value");
                instanceID=(Long) res.getLong("instance_id");
                duedate = (Long) res.getLong("due_date");
                description=res.getString("description");

                dueDate = toFormatter.format(new Date(duedate)).toString();
                timers = new JsonObject();

                timers.add("dueDate", new JsonPrimitive(dueDate));
                timers.add("timerId", new JsonPrimitive(extra));
                timers.add("instanceId", new JsonPrimitive(instanceID));
                timers.add("description", new JsonPrimitive(description));

                array.add(timers);
                outputJson.add("values", array);
            }
            stm.close();
            outputJson.addProperty("success", true);
            return outputJson;

        } catch (Exception e) {
            return JsonErrorObject.get("Unable to retrieve the timer list");
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }
}
