/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.timers;

import it.intecs.pisa.soap.toolbox.AsynchScriptExecutor;
import it.intecs.pisa.toolbox.FTPServerManager;
import it.intecs.pisa.toolbox.db.Timers;
import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.io.File;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

/**
 *
 * @author massi
 */
public class ToolboxScriptExecutionTimerTask extends TimerTask{
    protected long service_instance_id;
    protected long script_id;

    ToolboxScriptExecutionTimerTask(long service_instance_id, long script_id) {
        this.service_instance_id=service_instance_id;
        this.script_id=script_id;
    }

    @Override
    public void run() {
        try
        {
            ToolboxEngine engine;

            TBXService service;
            Logger logger;
            service=ServiceManager.getService(service_instance_id);
            logger=service.getLogger();
            
            
            FTPServerManager ftpServerManager;
            ftpServerManager=FTPServerManager.getInstance();
            
            engine=new ToolboxEngine(logger,ftpServerManager,false);

            XMLResourcesPersistence xmlPers;
            xmlPers=XMLResourcesPersistence.getInstance();
            Document scriptDoc=xmlPers.retrieveXML(String.valueOf(script_id));

            File responseFile=xmlPers.getXMLFile();

            AsynchScriptExecutor asynch = new AsynchScriptExecutor((ToolboxEngine)((ToolboxEngine)engine).clone(), scriptDoc.getDocumentElement(), responseFile);
            asynch.start();

            logger.info("Timer started, logging output in "+responseFile.getAbsolutePath());
        }
        catch(Exception e)
        {
  
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
