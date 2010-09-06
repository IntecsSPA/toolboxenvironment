/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.db.GeoServerAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.geoserver.GeoServerUnpublisher;
import it.intecs.pisa.util.http.HTTPLinkTokenizer;
import it.intecs.pisa.util.rest.RestGet;
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
public class DeleteFromGeoServer implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        String itemId;
        File appDir;
        File itemDir;

        try {
            Log.log("Executing class " + this.getClass().getCanonicalName());
            appDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            itemId = (String) cc.getAttribute(CommandsConstants.ITEM_ID);

            Properties prop = Prefs.load(appDir);
            String workspaceName = prop.getProperty("publish.geoserver.workspace");

            String[] urls = GeoServerAccessible.getUrls(itemId);
            for (String urlStr : urls) {
                //Parsing url like http://192.168.31.5:8023/geoserver/rest/layers/armsvector.json
                HTTPLinkTokenizer tokenizer;
                tokenizer = new HTTPLinkTokenizer(urlStr);

                URL hostUrl;
                String deployName;

                String path = tokenizer.getPath();
                deployName = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("."));


                String username,password,locationUrl;

                username=tokenizer.getUsername();
                password=tokenizer.getPassword();

                //retrieving the layer description to check it still exists
                if (username != null
                        && password != null) {
                    locationUrl = "http://" + tokenizer.getUsername()
                            + ":" + tokenizer.getPassword()
                            + "@" + tokenizer.getHost();
                    if (tokenizer.getPort().equals("80") == false) {
                        locationUrl += ":" + tokenizer.getPort();
                    }

                    locationUrl += tokenizer.getPath() + "/rest/layers/" + deployName + ".json";
                } else {
                    locationUrl = "http://" + tokenizer.getHost() + ":" + tokenizer.getPort() + "/geoserver/rest/layers/" + deployName + ".json";
                }

                JsonObject layerJson = RestGet.getAsJSON(new URL(locationUrl), null, username, password);

                if(layerJson!=null)
                {
                    JsonElement layer,type;
                    layer=layerJson.get("layer");
                    type=layer.getAsJsonObject().get("type");

                    if(type.getAsString().equals("VECTOR"))
                    {
                         hostUrl = new URL("http://" + tokenizer.getHost() + ":" + tokenizer.getPort() + "/geoserver");

                        GeoServerUnpublisher.unpublishVector(hostUrl, workspaceName, deployName, username, password);
                    }
                    else if(type.getAsString().equals("RASTER"))
                    {

                        hostUrl = new URL("http://" + tokenizer.getHost() + ":" + tokenizer.getPort() + "/geoserver");

                        GeoServerUnpublisher.unpublishCoverage(hostUrl, workspaceName, deployName, username, password);
                    }
               }
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
