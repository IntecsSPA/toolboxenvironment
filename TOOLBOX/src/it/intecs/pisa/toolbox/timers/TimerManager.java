/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.db.TimerInstance;
import it.intecs.pisa.toolbox.db.Timers;
import java.util.Date;
import java.util.Timer;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TimerManager {
    protected static final String THREAD_NAME="Timer Manger";

    protected static TimerManager instance=new TimerManager();
    protected static Timer timer;

    protected TimerManager()
    {
        Long[] instances;
        try
        {
            timer=new Timer(THREAD_NAME);

            instances=Timers.getTimers();
            for(Long instance_id:instances)
            {
                TimerInstance instance;
                instance=new TimerInstance(instance_id);
                instance.load();

                if(instance.getType().equals(TimerInstance.TYPE_TIMER))
                {
                    ToolboxScriptExecutionTimerTask tt;
                    tt=new ToolboxScriptExecutionTimerTask(instance.getService_instance_id(), instance.getScript_id());

                    timer.schedule(tt, new Date(instance.getDue_date()));
                }
            }
            Timers.deleteExecutedTimers();
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
    }

    public static TimerManager getInstance()
    {
       return instance;
    }

    public void tearDown()
    {
        if(timer!=null)
            timer.cancel();
    }

    public void addTimerInstance(long service_instance_id, long script_id, long due_date, String extra) throws Exception {
        TimerInstance timerInstance=new TimerInstance(0);
        timerInstance.setType(TimerInstance.TYPE_TIMER);
        timerInstance.setService_instance_id(service_instance_id);
        timerInstance.setScript_id(script_id);
        timerInstance.setDue_date(due_date);
        timerInstance.setExtraValue(extra);
        timerInstance.store();

        ToolboxScriptExecutionTimerTask tt;
        tt=new ToolboxScriptExecutionTimerTask(service_instance_id,script_id);

        timer.schedule(tt, new Date(due_date));
    }

    public void addFTPTimer(long service_instance_id, long due_date, String username) throws Exception {
        TimerInstance timerInstance=new TimerInstance(0);
        timerInstance.setType(TimerInstance.TYPE_FTP);
        timerInstance.setService_instance_id(service_instance_id);
        timerInstance.setScript_id(0);
        timerInstance.setDue_date(due_date);
        timerInstance.setExtraValue(username);
        timerInstance.store();

        ToolboxFTPTimerTask tt;
        tt=new ToolboxFTPTimerTask(service_instance_id,username);

        timer.schedule(tt, new Date(due_date));
    }


}
