/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.GeoServerAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.geoserver.GeoServerUnpublisher;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.StringTokenizer;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteFromGeoServer implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        String itemId;
        File appDir;
        File itemDir;
        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
            appDir=(File) cc.getAttribute(CommandsConstants.APP_DIR);
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);
            
            Properties prop = Prefs.load(appDir);
            String workspaceName = prop.getProperty("publish.geoserver.workspace");

            String[] urls = GeoServerAccessible.getUrls(itemId);
            for(String urlStr:urls)
            {
                URL url=new URL(urlStr);
                StringTokenizer tokenizer;
                
                tokenizer=new StringTokenizer(url.getPath(),"/");
                tokenizer.nextElement();
                tokenizer.nextElement();
                tokenizer.nextElement();
                tokenizer.nextElement();
                tokenizer.nextElement();
                                
                URL hostUrl;
                hostUrl=new URL("http://"+url.getHost()+":"+url.getPort()+"/geoserver");
                
                GeoServerUnpublisher.unpublishCoverage(hostUrl, workspaceName, (String) tokenizer.nextElement(), "admin","geoserver");
            }

            GeoServerAccessible.delete(itemId);
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }
}
