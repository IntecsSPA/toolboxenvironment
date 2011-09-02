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
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.TemplateStringUtil;
import it.intecs.pisa.util.geoserver.GeoServerPublisher;
import it.intecs.pisa.util.geoserver.GeoServerWorkspaces;
import it.intecs.pisa.util.http.HTTPLinkTokenizer;
import it.intecs.pisa.util.rest.RestGet;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class PublishToGeoServer implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        File downloadedFile, appDir;
        String workspaceName;
        String itemId;
        String deployName;
        Properties prop;
        String location;
        String[] dim = null;
        try {
            appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);

            if (storeItem.publishGeoserver.length > 0) {
                prop = Prefs.load(appDir);
                downloadedFile = new File(Prefs.getDownloadFolder(appDir), itemId + "_processed");
                if (!downloadedFile.exists()) {
                    downloadedFile = new File(Prefs.getDownloadFolder(appDir), itemId);
                }

                if (storeItem.geoserverWorkspace != null) {
                    workspaceName = storeItem.geoserverWorkspace;
                } else {
                    workspaceName = prop.getProperty("publish.geoserver.workspace");
                }

                if ("".equals(storeItem.geoserverFileNameTemplate)) {
                    deployName = DateUtil.getCurrentDateAsUniqueId();
                } else {
                    HashMap<String,String> metadataFromNameFile=TemplateStringUtil.getMetadataString(
                    ((File)cc.getAttribute(CommandsConstants.LOCAL_FILE)).getName(),
                    storeItem.geoserverFileNameTemplate);
                    deployName = metadataFromNameFile.get("LAYER_NAME");
                }

                if (!"".equals(storeItem.geoserverDimensions)) {
                    if (storeItem.geoserverDimensions.contains(",")) {
                        dim = storeItem.geoserverDimensions.split(",");
                    } else {
                        dim = new String[1];
                        dim[0] = storeItem.geoserverDimensions;
                    }
                }

                for (String url : storeItem.publishGeoserver) {
                    try {
                        location = publish(downloadedFile, url, workspaceName, deployName, storeItem.geoserverType, dim, storeItem.geoserverStyle);

                        if (location != null) {
                            GeoServerAccessible.add(itemId, location);
                        }
                    } catch (Exception e) {
                        Log.logException(e);
                    }
                }
            }
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private String getDataType(String filename) {
        if (filename.endsWith(".zip")) {
            return "shp";
        } else if (filename.endsWith(".tiff")) {
            return "tiff";
        } else {
            return null;
        }
    }

    private String publish(File fileToPublish, String url, String workspacename, String publishName, String dataType, String dimensions[], String style) throws Exception {
        String geoserverUrl;
        String username, password;
        String locationUrl = null;
        Document responseDoc = null;

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

        if (dataType.equals("shp")) {
            GeoServerPublisher.publishShape(new URL(geoserverUrl), fileToPublish, workspacename, publishName, username, password);
        } else if (dataType.equals("tiff")) {
            GeoServerPublisher.publishTiff(new URL(geoserverUrl), fileToPublish, workspacename, publishName, username, password);
        } else if (dataType.equals("imagemosaic")) {
            GeoServerPublisher.publishImageMosaic(new URL(geoserverUrl), fileToPublish, workspacename, publishName, username, password, dimensions, style);
        }

        if (username != null
                && password != null) {
            locationUrl = "http://" + tokenizer.getUsername()
                    + ":" + tokenizer.getPassword()
                    + "@" + tokenizer.getHost();
            if (tokenizer.getPort().equals("80") == false) {
                locationUrl += ":" + tokenizer.getPort();
            }

            locationUrl += tokenizer.getPath() + "/rest/layers/" + publishName + ".json";
        } else {
            locationUrl = geoserverUrl + "/rest/layers/" + publishName + ".json";
        }

        JsonObject layerJson = RestGet.getAsJSON(new URL(locationUrl), null, username, password);
        if (layerJson == null) {
            return null;
        } else {
            return locationUrl;
        }
    }
}
