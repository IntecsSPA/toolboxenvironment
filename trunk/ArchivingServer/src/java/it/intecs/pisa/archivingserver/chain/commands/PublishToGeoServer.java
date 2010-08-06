/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.GeoServerAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.geoserver.GeoServerPublisher;
import it.intecs.pisa.util.geoserver.GeoServerWorkspaces;
import it.intecs.pisa.util.http.HTTPLinkTokenizer;
import java.io.File;
import java.net.URL;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
            Log.log("Executing class "+this.getClass().getCanonicalName());
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
            Log.log(e.getMessage());
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
        Document responseDoc=null;
        
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
            responseDoc=GeoServerPublisher.publishTiff(new URL(geoserverUrl), fileToPublish, workspacename, publishName, username, password);

        if(responseObj!=null)
            return getLocation(responseObj);
        else if(responseDoc!=null)
            return getLocation(responseDoc);
        else return null;
    }

    protected String getLocation(JsonObject obj)
    {
        JsonObject coverageStore;
        JsonObject coverages;

        coverageStore=obj.getAsJsonObject("coverageStore");
        coverages=coverageStore.getAsJsonObject("coverages");

        return coverages.getAsString();
    }

    
    /**
     * <coverageStore>
  <name>armscoverage</name>
  <type>GeoTIFF</type>
  <enabled>true</enabled>
  <workspace>
    <name>archivingserver</name>
    <href>http://192.168.31.5:8023/geoserver/rest/workspaces/WorkspaceInfoImpl--30b7bf2e:12a419e40d2:-8000.xml</href>
  </workspace>
  <url>file:data/armscoverage/armscoverage.geotiff</url>
  <coverages>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://192.168.31.5:8023/geoserver/rest/workspaces/archivingserver/coveragestores/armscoverage/file/coverages.xml" type="application/xml"/>
  </coverages>
</coverageStore>
     * @param responseDoc
     * @return
     */
    private String getLocation(Document doc) {
        Element rootEl;

        rootEl=doc.getDocumentElement();
        Element coverages=DOMUtil.getChildByLocalName(rootEl, "coverages");

        Element linkEl=DOMUtil.getChildByLocalName(coverages, "link");
        return linkEl.getAttribute("href");
    }
}
