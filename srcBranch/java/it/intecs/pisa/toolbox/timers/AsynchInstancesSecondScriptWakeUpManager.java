/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import it.intecs.pisa.util.DateUtil;
import java.util.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class AsynchInstancesSecondScriptWakeUpManager {
    protected static final String THREAD_NAME="AsynchInstancesSecondScriptWakeUpManager";

    protected static AsynchInstancesSecondScriptWakeUpManager instance=new AsynchInstancesSecondScriptWakeUpManager();
    protected static Timer timer;

    protected AsynchInstancesSecondScriptWakeUpManager()
    {
        Long[] instances;
        Logger logger=null;

        try
        {
            logger=Toolbox.getInstance().getLogger();
            timer=new Timer(THREAD_NAME);

            instances=InstanceLister.getInstancesWithStatusAndNotExpired(InstanceStatuses.STATUS_PENDING);
            for(Long instance_id:instances)
            {
                    scheduleInstance(instance_id,0);
            }
        }
        catch(Exception e)
        {
            if(logger!=null)
                logger.error("An error occurred while executing the Second Script WakeUp Manager. Details: "+e.getMessage());
        }
    }

    public static AsynchInstancesSecondScriptWakeUpManager getInstance()
    {
       return instance;
    }

    public void tearDown()
    {
        if(timer!=null)
            timer.cancel();
    }

    public final void scheduleInstance(long serviceInstanceId,long delay) throws Exception {
        SecondScriptExecutionTimerTask tt;
        tt=new SecondScriptExecutionTimerTask(serviceInstanceId);

        timer.schedule(tt, DateUtil.getFutureDate(delay));
    }
}
