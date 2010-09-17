/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author massi
 */
public class TimerInstance {
    public static final String TYPE_TIMER="TIMER";
    protected long id;
    protected String type;
    protected long service_instance_id;
    protected long script_id;
    protected long due_date;
    protected String description;

    public TimerInstance(long instance_id) throws Exception
    {
        id=instance_id;
        
    }

    public void load() throws Exception
    {
        Statement stm=null;
        ResultSet rs=null;

        try
        {
            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT * FROM T_TIMERS WHERE ID=" + id );
            rs.next();

            type=rs.getString("TYPE");
            service_instance_id=rs.getLong("INSTANCE_ID");
            script_id=rs.getLong("SCRIPT");
            due_date=rs.getLong("DUE_DATE");
            description=rs.getString("DESCRIPTION");
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }
    }
    
    public void store() throws Exception
    {
        ToolboxInternalDatabase db;
        Statement stm=null;
        String sql;

        try
        {
            sql = "INSERT INTO T_TIMERS VALUES (null,'" + type + "'," + service_instance_id + ","+script_id+","+due_date+",'"+description+"')";
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            stm.executeUpdate(sql);
            stm.close();
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public long getDue_date() {
        return due_date;
    }

    public void setDue_date(long due_date) {
        this.due_date = due_date;
    }

    public void setScript_id(long script_id) {
        this.script_id = script_id;
    }

    public void setService_instance_id(long service_instance_id) {
        this.service_instance_id = service_instance_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getScript_id() {
        return script_id;
    }

    public long getService_instance_id() {
        return service_instance_id;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
