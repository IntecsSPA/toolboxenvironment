/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class StatisticsUtil {
    public static final String STAT_ARRIVED="Incoming instances";
    public static final String STAT_COMPLETED="Completed instances";


    protected static long getStatisticValue(String key) throws Exception
    {
        Statement stm=null;
        ResultSet rs=null;

        try
        {
            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT VALUE FROM T_STATISTICS WHERE KEY='" + key + "'");
            if(rs.next()==false)
            {
                initStatistic(key);
                return 0;
            }
            return rs.getLong("VALUE");
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }
    }

    protected static void initStatistic(String key) throws Exception
    {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;

        try {
            sql = "INSERT INTO T_STATISTICS VALUES('"+key+"',0)";

            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            stm.executeUpdate(sql);
            stm.close();
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static void incrementStatistic(String key)
    {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;

        try {
            long value;
            value=getStatisticValue(key);

            sql = "UPDATE T_STATISTICS SET VALUE="+(value+1)+" WHERE KEY='"+key+"'";

            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            stm.executeUpdate(sql);
            stm.close();
        }
        catch(Exception e)
        {
        }
        finally {
            if (stm != null) {
                try {
                    stm.close();
                } catch (SQLException ex) {
                    Logger.getLogger(StatisticsUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
