/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperationCommonTasks;
import java.util.TimerTask;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class TimeoutTask extends TimerTask{
    protected long service_instance_id;
    
    TimeoutTask(long id) {
        service_instance_id=id;
    }

    @Override
    public void run() {
        try
        {
            InstanceStatuses.updateInstanceStatus(service_instance_id, InstanceStatuses.STATUS_EXPIRED);
            TBXAsynchronousOperationCommonTasks.executeErrorScriptAndSendToClient(service_instance_id);
        }
        catch(Exception e)
        {
            
        }
    }

    
}
