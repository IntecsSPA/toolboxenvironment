/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.cleanup;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.resources.TextResourcesPersistence;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author massi
 */
public class CleanupUnlinkedResourcesTask extends TimerTask{
    protected long interval; 

    @Override
    public void run() {
        Date now,treshold;
       
        try
        {
            String configuredInterval;
            ToolboxConfiguration config = ToolboxConfiguration.getInstance();
            configuredInterval=config.getConfigurationValue(ToolboxConfiguration.CLEANUP_EVERY);

            if(configuredInterval!=null && configuredInterval.equals("")==false)
            {
                interval=TimeInterval.getIntervalAsLong(configuredInterval);

                now=new Date();
                treshold=new Date(now.getTime()-interval);

                XMLResourcesPersistence.getInstance().deleteOlderThan(treshold);
                TextResourcesPersistence.getInstance().deleteOlderThan(treshold);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("An error occurred while trying to delete old instances");
        }
    }

}
