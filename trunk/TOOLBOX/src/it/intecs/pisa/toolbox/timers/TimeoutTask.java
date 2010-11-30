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
            byte status=InstanceStatuses.getInstanceStatus(service_instance_id);
            if (status == InstanceStatuses.STATUS_CHECKING ||
             status == InstanceStatuses.STATUS_EXECUTING ||
             status == InstanceStatuses.STATUS_PENDING ||
             status == InstanceStatuses.STATUS_QUEUED ||
             status == InstanceStatuses.STATUS_READY ){
            InstanceStatuses.updateInstanceStatus(service_instance_id, InstanceStatuses.STATUS_EXPIRED);
            TBXAsynchronousOperationCommonTasks.executeErrorScriptAndSendToClient(service_instance_id);            
            }
        }
        catch(Exception e)
        {
            
        }
    }

    
}
