/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.services;

import it.intecs.pisa.archivingserver.chain.commands.CommandsConstants;
import it.intecs.pisa.archivingserver.db.InternalDatabase;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.datetime.TimeInterval;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.Vector;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class AutomaticItemDeleteService extends Thread{
    protected static final long ONE_HOUR=1000*60*60;

    protected boolean mustShutdown=false;
    protected File appdir;
    protected Timer timer=new Timer();
    protected static AutomaticItemDeleteService instance=new AutomaticItemDeleteService();

    public static AutomaticItemDeleteService getInstance()
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
            intervalStr=prop.getProperty("delete.after");

            waitInterval=TimeInterval.getIntervalAsLong(intervalStr);

            while(mustShutdown==false)
            {
                try
                {
                    long maxExpires;
                    Log.log("Automatic item deletion");
                    maxExpires=(new Date()).getTime()-waitInterval;
                    items = getItemsToDelete(maxExpires);

                    for (String item : items) {
                        Log.log("Automatically delete item "+item);
                        deleteItem(item);
                    }

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
        timer.purge();
    }

   private String[] getItemsToDelete(long maxExpires) throws SQLException {
        InternalDatabase db;
        Statement stm = null;
        ResultSet rs;
        Date now = new Date();

        try {
            db = InternalDatabase.getInstance();
            stm = db.getStatement();

            rs = stm.executeQuery("SELECT I.ID FROM T_ITEMS AS I,T_DOWNLOADS AS D WHERE I.EXPIRES=0 AND I.ARRIVAL_DATE<=" + maxExpires+
                    " AND D.ID=I.ID AND D.STATUS!='DOWNLOADING'");

            Vector<String> ids;

            ids = new Vector<String>();

            while (rs.next()) {
                ids.add(rs.getString("ID"));
            }

            return ids.toArray(new String[0]);

        } catch (Exception e) {
            return new String[0];
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    private void deleteItem(String item) {
        ChainManager cm = new ChainManager();
        ChainContext ct = cm.createContext();

        ct.setAttribute(CommandsConstants.ITEM_ID, item);
        ct.setAttribute(CommandsConstants.APP_DIR, appdir);
        cm.executeChain("Catalogue/deleteChain", ct);

        int success = ct.getResult().getCode();
        if (success == Result.FAIL) {
            System.out.println("Not delete item " + item);
        }
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
}
