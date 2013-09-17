/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperationCommonTasks;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.TimerTask;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class PushRetryTimerTask extends TimerTask{
    protected long service_instance_id;
    
    PushRetryTimerTask(long service_instance_id) {
        this.service_instance_id=service_instance_id;
    }

    @Override
    public void run() {
        try
        {
            Document outputMessage;
            outputMessage=InstanceResources.getXMLResource(service_instance_id, InstanceResources.TYPE_OUTPUT_MESSAGE);

            TBXAsynchronousOperationCommonTasks.sendResponseToClient(service_instance_id, outputMessage);

            //InstanceStatuses.updateInstanceStatus(service_instance_id,InstanceStatuses.STATUS_COMPLETED);
        }
        catch(Exception e)
        {
            
        }
    }
}
