/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.toolbox.db.Timers;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.util.TimerTask;
import org.apache.log4j.Logger;

/**
 *
 * @author massi
 */
public class ToolboxFTPTimerTask extends TimerTask{
    protected String username;
    protected long service_instance_id;
    
    ToolboxFTPTimerTask(long service_instance_id,String user) {
        username=user;
        this.service_instance_id=service_instance_id;
    }

    @Override
    public void run() {
        try
        {
            TBXService service;
            Logger logger;
            service=ServiceManager.getService(service_instance_id);
            logger=service.getLogger();

            FTPServerManager ftpMan;
            ftpMan=FTPServerManager.getInstance();

            ftpMan.removeUser(username);
            logger.info("Removed FTP user "+username);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try {
                Timers.deleteExecutedTimers();
            } catch (Exception ex) {
                
            }
        }
    }

}
