/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.services;

import it.intecs.pisa.archivingserver.chain.commands.CommandsConstants;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class AutomaticFolderPublishingService extends Thread{
    protected static final long ONE_HOUR=1000*60*60;

    protected boolean mustShutdown=false;
    protected File appdir;
    protected Timer timer=new Timer();
    protected static AutomaticFolderPublishingService instance=new AutomaticFolderPublishingService();

    public static AutomaticFolderPublishingService getInstance()
    {
        return instance;
    }

    @Override
    public void run() {
        long waitInterval=ONE_HOUR;
        String[] items;
        Properties prop;
        String intervalStr;

        try
        {
            prop = Prefs.load(appdir);
            intervalStr=prop.getProperty(Prefs.PUBLISH_LOCAL_FOLDER_INTERVAL);

            waitInterval=TimeInterval.getIntervalAsLong(intervalStr);

            while(mustShutdown==false)
            {
                try
                {
                    File folder=new File(prop.getProperty(Prefs.PUBLISH_LOCAL_FOLDER_DIR));
                    publishItemInFolder(folder);
                    try {
                        sleep(waitInterval);
                    } catch (InterruptedException ex) {}
                }
                catch(Exception e)
                {

                }
            }
        }
        catch(Exception e)
        {

        }
    }

    public void addRemovableItem(String item,Date expireDate)
    {
        DeleteItemTask tt;

        tt=new DeleteItemTask(item,appdir);
        timer.schedule(tt, expireDate);
    }

    public void setAppDir(File dir)
    {
        appdir=dir;
    }

    public void requestShutdown()
    {
        mustShutdown=true;
        this.interrupt();
    }

    private void publishItemInFolder(File folder) {
        for(File f:folder.listFiles())
        {
            publishItem(f);
        }
    }

    private void publishItem(File f) {
        String item;
        if(f.isFile())
        {
            item=DateUtil.getCurrentDateAsUniqueId();

            ChainManager cm = new ChainManager();
            ChainContext ct = cm.createContext();

            StoreItem storeitem = new StoreItem();
            storeitem.deleteAfter=0;
            storeitem.downloadUrl="";
            storeitem.metadataUrl="";
            storeitem.publishCatalogue=new String[0];
            storeitem.publishFtp=new String[0];
            storeitem.publishGeoserver=new String[0];

            ct.setAttribute(CommandsConstants.STORE_ITEM, storeitem);
            ct.setAttribute(CommandsConstants.ITEM_ID, item);
            ct.setAttribute(CommandsConstants.APP_DIR, appdir);
            cm.executeChain("Catalogue/localFolderstoreChain", ct);

            int success = ct.getResult().getCode();
            if (success == Result.FAIL) {
                System.out.println("Cannot publish item " + item);
            }
            else System.out.println("Item " + item+" published");
        }
    }
}
