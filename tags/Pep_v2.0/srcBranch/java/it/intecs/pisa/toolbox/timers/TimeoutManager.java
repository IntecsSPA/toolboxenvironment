/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TimeoutManager {

    protected static final String THREAD_NAME = "TimeoutManager";
    protected static TimeoutManager instance = new TimeoutManager();
    protected static Timer timer;

    protected TimeoutManager() {
        Long[] instances;
        Logger logger = null;
        ResultSet rs = null;
        Statement stm = null;

        try {
            logger = Toolbox.getInstance().getLogger();
            timer = new Timer(THREAD_NAME);

            ToolboxInternalDatabase db;
            String sql; 

            sql = "SELECT ID,EXPIRATION_DATE FROM T_SERVICE_INSTANCES WHERE STATUS=8";
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            while(rs.next())
            {
                scheduleTimeout(rs.getLong("ID"),rs.getLong("EXPIRATION_DATE"));
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.error("An error occurred while scheduling instance timeout timers. Details: " + e.getMessage());
            }
        }
        finally {
            try
            {
                if (rs != null) {
                    rs.close();
                }

                if (stm != null) {
                    stm.close();
                }
            }
            catch(Exception e){}
        }
    }

    public static TimeoutManager getInstance() {
        return instance;
    }

    public void tearDown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void removeTask() {
    }


    public final void scheduleTimeout(long id, long expireDate) {
        TimeoutTask tt;
        tt = new TimeoutTask(id);

        timer.schedule(tt, new Date(expireDate));
    }
}
