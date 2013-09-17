/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class Timers {
    public static Long[] getTimers() throws Exception
    {
        Statement stm=null;
        ResultSet rs=null;
        ArrayList<Long> values;
        try
        {
            values=new ArrayList<Long>();

            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT ID FROM T_TIMERS");
            while(rs.next())
            {
                values.add(rs.getLong("ID"));
            }

            return values.toArray(new Long[0]);
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }
    }

    public static void deleteExecutedTimers() throws Exception
    {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String sql;
        Date now;
        try {
            now=new Date();

            sql = "DELETE FROM T_TIMERS WHERE DUE_DATE<=" + now.getTime();

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);
        }  finally {
            if (stm != null) {
                stm.close();
            }
        }
    }
}
