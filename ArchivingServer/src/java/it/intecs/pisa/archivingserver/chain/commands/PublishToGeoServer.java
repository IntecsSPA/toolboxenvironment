/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.GeoServerAccessible;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.geoserver.GeoServerPublisher;
import it.intecs.pisa.util.geoserver.GeoServerWorkspaces;
import it.intecs.pisa.util.http.HTTPLinkTokenizer;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class PublishToGeoServer implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        File downloadedFile, appDir;
        String downloadFileName;
        String workspaceName;
        String itemId;
        String dataType;
        String deployName;
        Properties prop;
        String location;
        try {
            appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            downloadFileName = (String) cc.getAttribute(CommandsConstants.DOWNLOADED_FILE_NAME);
            itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);

            prop = Prefs.load(appDir);
            downloadedFile = new File(prop.getProperty("download.dir"), itemId);

            workspaceName = prop.getProperty("publish.geoserver.workspace");
            dataType=getDataType(downloadFileName);
            deployName=downloadFileName.substring(0, downloadFileName.lastIndexOf("."));
            
            for (String url : storeItem.publishGeoserver) {
                try {
                    location=publish(downloadedFile, url, workspaceName, deployName, dataType);

                    if(location!=null)
                        GeoServerAccessible.add(itemId, location);
                } catch (Exception e) {
                }
            }

        } catch (Exception e) {
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private String getDataType(String filename)
    {
        if(filename.endsWith(".zip"))
            return "shp";
        else if(filename.endsWith(".tiff"))
            return "tiff";
        else return null;
    }

    private String publish(File fileToPublish,  String url, String workspacename,String publishName, String dataType ) throws Exception {
        String geoserverUrl;
        String username, password;
        JsonObject responseObj=null;
        
        HTTPLinkTokenizer tokenizer;

        tokenizer = new HTTPLinkTokenizer(url);
        geoserverUrl = "http://" + tokenizer.getHost();

        if (tokenizer.getPort().equals("80") == false) {
            geoserverUrl += ":" + tokenizer.getPort();
        }

        geoserverUrl += tokenizer.getPath();

        username = tokenizer.getUsername();
        password = tokenizer.getPassword();

        GeoServerWorkspaces.create(new URL(geoserverUrl), workspacename, username, password);

        if(dataType.equals("shp"))
            responseObj=GeoServerPublisher.publishShape(new URL(geoserverUrl), fileToPublish, workspacename, publishName, username, password);
        else  if(dataType.equals("tiff"))
            responseObj=GeoServerPublisher.publishTiff(new URL(geoserverUrl), fileToPublish, workspacename, publishName, username, password);

        if(responseObj!=null)
            return getLocation(responseObj);

        return null;
    }

    protected String getLocation(JsonObject obj)
    {
        JsonObject coverageStore;
        JsonObject coverages;

        coverageStore=obj.getAsJsonObject("coverageStore");
        coverages=coverageStore.getAsJsonObject("coverages");

        return coverages.getAsString();
    }
}
