/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.cleanup;

import java.util.Timer;

/**
 *
 * @author massi
 */
public class AutomaticCleanup {
    protected static Timer timer=new Timer();

    public static void start()
    {
        DeleteOldInstancesAutomaticallyTask dt;
        CleanupUnlinkedResourcesTask ct;

        dt=new DeleteOldInstancesAutomaticallyTask();
        ct= new CleanupUnlinkedResourcesTask();

        timer.scheduleAtFixedRate(dt, 0, 60000);
        timer.scheduleAtFixedRate(ct, 0, 60000);
    }

    public static void stop()
    {
        timer.cancel();
        timer.purge();
    }
}
