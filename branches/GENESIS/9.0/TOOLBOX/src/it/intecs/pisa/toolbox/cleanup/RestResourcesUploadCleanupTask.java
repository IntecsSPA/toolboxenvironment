/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.cleanup;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.util.Date;
import java.util.TimerTask;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class RestResourcesUploadCleanupTask extends TimerTask{
    @Override
    public void run() {
        Date now,treshold;
       
        try
        {
            long interval=3600000; //1H

            now=new Date();
            treshold=new Date(now.getTime()-interval);

            File resourcesFolder=new File(Toolbox.getInstance().getRootDir(),"WEB-INF/plugins/RESTManagerCommandsPlugin/resources/storedData");
            IOUtil.deleteOlderThan(resourcesFolder, treshold);
        }
        catch(Exception e)
        {
            
        }
    }

}
