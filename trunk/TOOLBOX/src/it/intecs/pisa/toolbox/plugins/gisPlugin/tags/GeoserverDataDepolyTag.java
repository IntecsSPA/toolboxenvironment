package it.intecs.pisa.toolbox.plugins.gisPlugin.tags;


import it.intecs.pisa.gis.geoserver.rest.RestAPI;
import it.intecs.pisa.gis.geoserver.rest.RestUtil;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */

/*
 * <geoserverDataDeploy>
 *      <geoserverURL></geoserverURL>
 *      <geoserverVersion></geoserverVersion>
 *      <geoserverUsername></geoserverUsername>
 *      <geoserverPassword></geoserverPassword>
 *      <data type=""> // types supported geotiff,shp,style,
 *      </data>
 *      <deployName></deployName> 
 *      <applyTo></applyTo> // only for dataType="style" but not mandatory
 *      <workspace></workspace>
 * </geoserverDataDeploy>
 *
 */
/*CURL EXAMPLE

VECTOR
curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' --data-binary @admin98.zip http://192.168.31.5:8023/geoserver/rest/workspaces/acme/datastores/admin98/file.shp

RASTER
curl -u admin:geoserver -XPUT -H 'Content-type: image/tiff' --data-binary @pippo.tiff http://192.168.31.5:8023/geoserver/rest/workspaces/toolbox/coveragestores/pippo/file.geotiff

 */
public class GeoserverDataDepolyTag extends NativeTagExecutor {

    protected static final String GEOSERVER_URL = "geoserverURL";
    protected static final String GEOSERVER_VERSION = "geoserverVersion";
    protected static final String GEOSERVER_USERNAME = "geoserverUsername";
    protected static final String GEOSERVER_PASSWORD = "geoserverPassword";
    protected static final String DATA = "data";
    protected static final String DATA_TYPE = "type";
    protected static final String LAYER_NAME = "deployName";
    protected static final String WORKSPACE = "workspace";
    protected static final String APPLY_TO = "applyTo";
    protected static final String REST_API_PACKAGE = "it.intecs.pisa.toolbox.plugins.gisPlugin.geoserver.rest.";
    protected static final String REST_API_CLASS_NAME = "RestAPIVersion";

    @Override
    public Object executeTag(org.w3c.dom.Element geoserverDataDeploy) throws Exception {
        String geoserverWorkspace, deployName, dataPath, geoserverURL, geoserverVersion, geoserverUsername, geoserverPassword;
        String applyLayerName;
        Element geoserverURLElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_URL).item(0);
        Element geoserverVersionElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_VERSION).item(0);
        Element geoserverUsernameElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_USERNAME).item(0);
        Element geoserverPasswordElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_PASSWORD).item(0);
        Element dataElement = (Element) geoserverDataDeploy.getElementsByTagName(DATA).item(0);
        String dataType = dataElement.getAttribute(DATA_TYPE).toLowerCase();
        Element layerNameElement = (Element) geoserverDataDeploy.getElementsByTagName(LAYER_NAME).item(0);
        Element workspaceElement = (Element) geoserverDataDeploy.getElementsByTagName(WORKSPACE).item(0);
        Element applyToElement = (Element) geoserverDataDeploy.getElementsByTagName(APPLY_TO).item(0);

        ToolboxConfiguration td = ToolboxConfiguration.getInstance();
        geoserverURL = geoserverURLElement != null ? geoserverURLElement.getTextContent() :
                                        td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_URL);
        geoserverVersion = geoserverVersionElement != null ? geoserverVersionElement.getTextContent() :
                                                td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_VERSION);
        geoserverWorkspace = workspaceElement != null ? workspaceElement.getTextContent() :
                                                td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_TOOLBOX_WORKSPACE);
        geoserverUsername = geoserverUsernameElement != null ? geoserverUsernameElement.getTextContent() :
                                                td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_USERNAME);
        geoserverPassword = geoserverPasswordElement != null ? geoserverPasswordElement.getTextContent() :
                                                    td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_PASSWORD);
        deployName = layerNameElement != null ? layerNameElement.getTextContent() : null;
        applyLayerName = applyToElement != null ? applyToElement.getTextContent() : null;
        dataPath = dataElement.getTextContent();
        boolean result = false;
        int version = Integer.parseInt("" + geoserverVersion.charAt(0));
        Class geoserverREST = Class.forName(REST_API_PACKAGE
                + REST_API_CLASS_NAME + version);
        RestAPI geoserverRestAPI = (RestAPI) geoserverREST.newInstance();
        geoserverRestAPI.setGeoserverURL(geoserverURL);

        boolean createWorkspace = RestUtil.createWorkspace(geoserverRestAPI, geoserverUsername,
                geoserverPassword, geoserverWorkspace);


        if (dataType.equalsIgnoreCase("shp")) {
            result = RestUtil.deployVectorData(geoserverRestAPI,
                    geoserverUsername, geoserverPassword,
                    geoserverWorkspace, dataPath, deployName, "shp");
        } else if (dataType.equalsIgnoreCase("geotiff")) {
            result = RestUtil.deployRasterData(geoserverRestAPI,
                    geoserverUsername, geoserverPassword,
                    geoserverWorkspace, dataPath, deployName, "geotiff");
        } else if (dataType.equalsIgnoreCase("style")) {
            result = RestUtil.deployStyle(geoserverRestAPI,
                    geoserverUsername, geoserverPassword,
                    dataPath, deployName);
            if (applyLayerName != null) {
                result = result && RestUtil.setLayerDefaultStyle(geoserverRestAPI,
                        geoserverUsername, geoserverPassword,
                        deployName, applyLayerName);
            }
        }

        // return Web Map Context ...
        return result;
    }
}
