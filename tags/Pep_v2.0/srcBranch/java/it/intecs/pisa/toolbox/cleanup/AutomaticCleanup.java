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
        long interval=60000*5; //5min
        DeleteOldInstancesAutomaticallyTask dt;
        CleanupUnlinkedResourcesTask ct;
        RestResourcesUploadCleanupTask rrut;
        WPSUploadCleanupTask wpsct;

        dt=new DeleteOldInstancesAutomaticallyTask();
        ct= new CleanupUnlinkedResourcesTask();
        rrut=new RestResourcesUploadCleanupTask();
        wpsct=new WPSUploadCleanupTask();

        timer.scheduleAtFixedRate(dt, 0, interval);
        timer.scheduleAtFixedRate(ct, 0, interval);
        timer.scheduleAtFixedRate(rrut, 0, interval);
        timer.scheduleAtFixedRate(wpsct, 0, interval);
    }

    public static void stop()
    {
        timer.cancel();
        timer.purge();
    }
}
