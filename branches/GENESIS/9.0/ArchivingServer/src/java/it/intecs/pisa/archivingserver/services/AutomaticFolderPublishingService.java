/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.chain.commands.CommandsConstants;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.data.StoreItemDeserializer;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.prefs.WatchPrefs;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;
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
    //
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
        String intervalStr,watchFolder;
        JsonObject watchListJson,watchObject=null;
        JsonArray watchArray;

        try
        {
            prop = Prefs.load(appdir);
            intervalStr=prop.getProperty(Prefs.PUBLISH_LOCAL_FOLDER_INTERVAL);

            waitInterval=TimeInterval.getIntervalAsLong(intervalStr);

            while(mustShutdown==false)
            {
                try
                {
                    
                    watchListJson = WatchPrefs.loadJson(appdir);
                    
                    watchArray=watchListJson.getAsJsonArray(WatchPrefs.JSON_WATCH_LIST_PROPERTY);
                    
                   for(int i=0; i<watchArray.size(); i++){
                        watchObject=(JsonObject) watchArray.get(i);

                        publishItemInFolder(watchObject);
                        
                    }
                    
                    
                   /* String folderDirs;
                    folderDirs=prop.getProperty(Prefs.PUBLISH_LOCAL_FOLDER_DIR);

                    StringTokenizer tokenizer=new StringTokenizer(folderDirs,";");

                    String types;
                    types=prop.getProperty(Prefs.PUBLISH_LOCAL_FOLDER_TYPES);
                    StringTokenizer typesTokenizer=new StringTokenizer(types,";");

                    while(tokenizer.hasMoreTokens())
                    {
                        File folder=new File(tokenizer.nextToken());
                        publishItemInFolder(folder, typesTokenizer.nextToken());
                    }*/

                    try {
                            sleep(waitInterval);
                        }
                        catch (InterruptedException ex) {}
                }
                catch(Exception e)
                {
                    Log.log("An exception occurred while publishing data from disk... Details: "+e.getMessage());
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

    private void publishItemInFolder(File folder,String type) {
        File[] files=null;

        files=folder.listFiles();
        if(files==null)
            files=new File[0];

        for(File f:files){
            
            publishItem(f,type);
        }
    }
    
    
    private void publishItemInFolder(JsonObject watchConfiguration) {
        
        String watchFolderPath=watchConfiguration.getAsJsonPrimitive(
                           WatchPrefs.WATCH_JSON_FOLDER_PROPERTY).getAsString();
     
        File watchFolder=new File(watchFolderPath);
        
        File[] files=null;
        
        files=watchFolder.listFiles();
        if(files==null)
            files=new File[0];

        for(File f:files)
        {
            publishItem(f,watchConfiguration);
        }
    }
    
    
     private void publishItem(File itemFile, JsonObject configuration) {
        String item;
        StoreItem storeitem;
        Gson gson;
        GsonBuilder gsonBuilder;
        if(itemFile.isFile())
        {
            item=DateUtil.getCurrentDateAsUniqueId();
            gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(StoreItem.class, new StoreItemDeserializer());
            gson=gsonBuilder.create();
            storeitem=gson.fromJson(configuration, StoreItem.class);
            
            ChainManager cm = new ChainManager();
            ChainContext ct = cm.createContext();

            /* storeitem = new StoreItem();
            storeitem.deleteAfter=0;
            storeitem.downloadUrl="";
            storeitem.metadataUrl="";
            storeitem.type=type;
            storeitem.publishCatalogue=new String[0];
            storeitem.publishFtp=new String[0];
            storeitem.publishGeoserver=new String[0];*/

            ct.setAttribute(CommandsConstants.STORE_ITEM, storeitem);
            ct.setAttribute(CommandsConstants.ITEM_ID, item);
            ct.setAttribute(CommandsConstants.APP_DIR, appdir);
            ct.setAttribute(CommandsConstants.LOCAL_FILE, itemFile);
            cm.executeChain("Catalogue/localFolderstoreChain", ct);

            int success = ct.getResult().getCode();
            if (success == Result.FAIL) {
                Log.log("Cannot publish item " + item);
            }
            else Log.log("Item " + item+" published");
        }
    }
     
     

    private void publishItem(File f,String type) {
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
            storeitem.type=type;
            storeitem.publishCatalogue=new String[0];
            storeitem.publishFtp=new String[0];
            storeitem.publishGeoserver=new String[0];

            ct.setAttribute(CommandsConstants.STORE_ITEM, storeitem);
            ct.setAttribute(CommandsConstants.ITEM_ID, item);
            ct.setAttribute(CommandsConstants.APP_DIR, appdir);
            ct.setAttribute(CommandsConstants.LOCAL_FILE, f);
            cm.executeChain("Catalogue/localFolderstoreChain", ct);

            int success = ct.getResult().getCode();
            if (success == Result.FAIL) {
                System.out.println("Cannot publish item " + item);
            }
            else System.out.println("Item " + item+" published");
        }
    }
}
