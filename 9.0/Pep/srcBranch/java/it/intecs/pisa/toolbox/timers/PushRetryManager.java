/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import it.intecs.pisa.util.DateUtil;
import java.util.Timer;
import org.apache.log4j.Logger;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class PushRetryManager {

    protected static final String THREAD_NAME = "PushRetryManager";
    protected static PushRetryManager instance = new PushRetryManager();
    protected static Timer timer;

    protected PushRetryManager() {
        Long[] instances;
        Logger logger = null;

        try {
            logger = Toolbox.getInstance().getLogger();
            timer = new Timer(THREAD_NAME);

            instances = InstanceLister.getInstancesToRetryPushing();
            for (Long instance_id : instances) {
                scheduleInstance(instance_id, 0);
            }
        } catch (Exception e) {
            if (logger != null) {
                logger.error("An error occurred while executing the Second Script WakeUp Manager. Details: " + e.getMessage());
            }
        }
    }

    public static PushRetryManager getInstance() {
        return instance;
    }

    public void tearDown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void removeTask() {
    }

    public final void scheduleInstance(long serviceInstanceId, long delay) throws Exception {
        PushRetryTimerTask tt;
        tt = new PushRetryTimerTask(serviceInstanceId);

        timer.schedule(tt, DateUtil.getFutureDate(delay));
    }
}
