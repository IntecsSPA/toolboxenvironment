/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.service.TBXAsynchronousOperationSecondThirdScriptExecutor;
import java.util.TimerTask;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class SecondScriptExecutionTimerTask extends TimerTask{
    protected long service_instance_id;
    
    SecondScriptExecutionTimerTask(long service_instance_id) {
        this.service_instance_id=service_instance_id;
    }

    @Override
    public void run() {
        try
        {
            TBXAsynchronousOperationSecondThirdScriptExecutor secondThirdExec;
            secondThirdExec=new TBXAsynchronousOperationSecondThirdScriptExecutor(service_instance_id);
            secondThirdExec.start();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
