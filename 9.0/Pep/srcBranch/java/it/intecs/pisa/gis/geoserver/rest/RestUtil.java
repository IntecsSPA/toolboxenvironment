package it.intecs.pisa.gis.geoserver.rest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.intecs.pisa.gis.geoserver.util.GISGeoserverDOM;
import it.intecs.pisa.gis.util.VectorUtil;
import it.intecs.pisa.util.rest.RESTService;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrea Marongiu
 */
public class RestUtil {

    public static boolean createWorkspace(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String newWorkspaceName) {

        int i;
        boolean result = true;

        JsonObject resp = null;
        try {
            resp =  (JsonObject) RESTService.getInformation(
                    geoserverRestAPI.getWorkspacesURL(),
                    "text/json", geoserverUsername, geoserverPassword);
            JsonObject workspaces = resp.getAsJsonObject("workspaces");
            JsonArray workArr = workspaces.getAsJsonArray("workspace");
            String currName;
            for (i = 0; i < workArr.size(); i++) {
                currName = ((JsonObject) workArr.get(i)).get("name").getAsString();
                if (currName.equals(newWorkspaceName)) {
                    return false;

                }
            }
            RESTService.postDocument(geoserverRestAPI.getWorkspacesURL(),
                    GISGeoserverDOM.createCreateWorkspaceDocument(newWorkspaceName),
                    geoserverUsername, geoserverPassword);

        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static boolean deployVectorData(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String geoserverWorkspace,
            String dataPath,
            String deployName,
            String dataType) {

        URL restRequest = null;
        try {
            restRequest = geoserverRestAPI.getCreateVectorURL(geoserverWorkspace, deployName, dataType.toLowerCase());
        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        String zipFilePath = null;
        try {
            zipFilePath = VectorUtil.createSHPZipDeployName(dataPath, deployName);
        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        boolean sent = RESTService.putFile(restRequest,
                new File(zipFilePath), "application/zip",
                geoserverUsername, geoserverPassword);

        new File(zipFilePath).delete();

        return sent;
    }

    public static boolean deleteVectorData(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String geoserverWorkspace,
            String layerName) {


        URL restRequest=null;
        try {
            restRequest = geoserverRestAPI.getLayerURL(layerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        boolean sent = RESTService.delete(restRequest,
                geoserverUsername, geoserverPassword);

        try {
            restRequest = geoserverRestAPI.getFeatureTypeURL(geoserverWorkspace, layerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

         sent = sent && RESTService.delete(restRequest,
                geoserverUsername, geoserverPassword);

        try {
            restRequest = geoserverRestAPI.getDataStoreURL(geoserverWorkspace, layerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

         sent = sent && RESTService.delete(restRequest,
                geoserverUsername, geoserverPassword);    

        return sent;
    }

    public static boolean deployRasterData(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String geoserverWorkspace,
            String dataPath,
            String deployName,
            String dataType) {

        URL restRequest = null;
        try {
            restRequest = geoserverRestAPI.getCreateRasterURL(geoserverWorkspace, deployName, dataType.toLowerCase());
        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        boolean sent = RESTService.putFile(restRequest,
                new File(dataType), "image/tiff",
                geoserverUsername, geoserverPassword);
        return sent;
    }

    public static boolean deleteRasterData(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String geoserverWorkspace,
            String layerName) {


        URL restRequest=null;
        try {
            restRequest = geoserverRestAPI.getLayerURL(layerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

        boolean sent = RESTService.delete(restRequest,
                geoserverUsername, geoserverPassword);

        try {
            restRequest = geoserverRestAPI.getCoverageURL(geoserverWorkspace, layerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

         sent = sent && RESTService.delete(restRequest,
                geoserverUsername, geoserverPassword);

        try {
            restRequest = geoserverRestAPI.getCoverageStoreURL(geoserverWorkspace, layerName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }

         sent = sent && RESTService.delete(restRequest,
                geoserverUsername, geoserverPassword);

        return sent;
    }

    
    public static boolean deployData(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String geoserverWorkspace,
            String dataPath,
            String deployName,
            String dataType) {

        boolean result=false;
        if(geoserverRestAPI.isRasterFormat(dataType))
            result=deployRasterData(geoserverRestAPI, geoserverUsername,
             geoserverPassword,geoserverWorkspace,dataPath,deployName,dataType);
        else
          if(geoserverRestAPI.isVectorFormat(dataType))
            result=deployVectorData(geoserverRestAPI, geoserverUsername,
             geoserverPassword,geoserverWorkspace,dataPath,deployName,dataType);
          else
          if(geoserverRestAPI.isStyleFormat(dataType))
            result=deployStyle(geoserverRestAPI, geoserverUsername,
             geoserverPassword,dataPath,deployName);
          else{
                try {
                     throw new Exception("Deploy Data Type: "+dataType+" not supported");
                    } catch (Exception ex) {
                       Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
                       return false;
                    }

          }
        return result;
    }

    public static boolean deployStyle(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String sdlPath,
            String styleName) {

        URL restRequest = null;
        try {
            restRequest = geoserverRestAPI.getStylesURL();
        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        String sldFilePath = null;
        try {
            sldFilePath = VectorUtil.createSLDDeployName(sdlPath, styleName);
        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        boolean sent = RESTService.postFile(restRequest,
                new File(sldFilePath), "application/vnd.ogc.sld+xml",
                geoserverUsername, geoserverPassword);

        new File(sldFilePath).delete();

        return sent;
    }

    public static boolean setLayerDefaultStyle(RestAPI geoserverRestAPI,
            String geoserverUsername,
            String geoserverPassword,
            String styleName,
            String layerName) {

        URL restRequest = null;
        try {
            restRequest = geoserverRestAPI.getLayerURL(layerName);
        } catch (Exception ex) {
            Logger.getLogger(RestUtil.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        boolean sent = RESTService.postDocument(restRequest,
                GISGeoserverDOM.createSetDefaultStyleDocument(styleName),
                geoserverUsername, geoserverPassword);


        return sent;
    }
}
