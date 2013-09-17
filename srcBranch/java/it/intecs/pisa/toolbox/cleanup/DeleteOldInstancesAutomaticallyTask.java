/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.cleanup;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author massi
 */
public class DeleteOldInstancesAutomaticallyTask extends TimerTask{
    protected long interval; 

    @Override
    public void run() {
        Date now,treshold;
        Long[] oldInstances;

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
                oldInstances=InstanceLister.getInstancesOlderThan(treshold);

                for(Long instanceId:oldInstances)
                {
                    InstanceHandler handler;
                    handler=new InstanceHandler(instanceId);
                    handler.deleteInstance();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("An error occurred while trying to delete old instances");
        }
    }

}
