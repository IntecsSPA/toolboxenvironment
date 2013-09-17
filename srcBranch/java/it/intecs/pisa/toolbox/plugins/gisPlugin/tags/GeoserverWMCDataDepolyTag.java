package it.intecs.pisa.toolbox.plugins.gisPlugin.tags;

import it.intecs.pisa.gis.geoserver.rest.RestAPI;
import it.intecs.pisa.gis.geoserver.rest.RestUtil;
import it.intecs.pisa.gis.geoserver.util.GeoserverUtil;
import it.intecs.pisa.gis.ogc.format.WMC;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */

/*
 * <geoserverWMCDataDeploy>
 *      <geoserverURL></geoserverURL>
 *      <geoserverVersion></geoserverVersion>
 *      <geoserverUsername></geoserverUsername>
 *      <geoserverPassword></geoserverPassword>
 *      <workspace></workspace>
 *      <wmcVersion></wmcVersion>
 *      <wmcRasterDataFolder></wmcRasterDataFolder>
 *      <wmcVectorDataFolder></wmcVectorDataFolder>
 *      <wmcStyleFolder></wmcStyleFolder>
 *      <wmcTitle></wmcTitle>
 *      <wmcAbstract><wmcAbstract>
 *      <wmcWindow height="331" width="560"/>
 *      <wmcBoundingBox srs="4326" west="" south="" east="" north=""/>
 * </geoserverWMCDataDeploy>
 *
 */
public class GeoserverWMCDataDepolyTag {
    protected static final String GEOSERVER_URL = "geoserverURL";
    protected static final String GEOSERVER_VERSION = "geoserverVersion";
    protected static final String GEOSERVER_USERNAME = "geoserverUsername";
    protected static final String GEOSERVER_PASSWORD = "geoserverPassword";
    protected static final String RASTER_FOLDER = "wmcRasterDataFolder";
    protected static final String VECTOR_FOLDER = "wmcVectorDataFolder";
    protected static final String STYLE_FOLDER = "wmcStyleFolder";
    protected static final String WORKSPACE="workspace";
    protected static final String REST_API_PACKAGE = "it.intecs.pisa.toolbox.plugins.gisPlugin.geoserver.rest.";
    protected static final String WMC_PACKAGE = "it.intecs.pisa.gis.ogc.format.";
    protected static final String REST_API_CLASS_NAME = "RestAPIVersion";
    protected static final String WMC_CLASS_NAME = "WMCVersion";
    protected static final String WMC_LAYER_TEXT_PROPERTIES_FILE_SUFFIX = "wmcprop";
    protected static final String WMC_VERSION = "wmcVersion";
    protected static final String WMC_TITLE = "wmcTitle";
    protected static final String WMC_ABSTRACT = "wmcAbstract";
    protected static final String WMC_WINDOW = "wmcWindow";
    protected static final String WMC_WINDOW_HEIGHT_ATTRIBUTE = "height";
    protected static final String WMC_WINDOW_WIDTH_ATTRIBUTE = "width";
    protected static final String WMC_BOUNDING_BOX = "wmcBoundingBox";
    protected static final String WMC_BOUNDING_BOX_SRS_ATTRIBUTE = "srs";
    protected static final String WMC_BOUNDING_BOX_WEST_ATTRIBUTE = "west";
    protected static final String WMC_BOUNDING_BOX_SOUTH_ATTRIBUTE = "south";
    protected static final String WMC_BOUNDING_BOX_EAST_ATTRIBUTE = "east";
    protected static final String WMC_BOUNDING_BOX_NORTH_ATTRIBUTE = "north";
    protected static final String WMC_VERSION_DEFUALT = "1.0.0";

    /* WMC Layer text file propeties */
    protected static final String PROP_QUERYABLE = "queryable";
    protected static final String PROP_HIDEEN = "hidden";
    protected static final String PROP_WFS_VERSION = "wfsVersion";
    protected static final String PROP_WMS_VERSION = "wmsVersion";
    protected static final String PROP_WCS_VERSION = "wcsVersion";
    protected static final String PROP_NAME = "layerName";
    protected static final String PROP_TITLE = "layerTitle";
    protected static final String PROP_SRS = "srs";
    protected static final String PROP_WMS_FORMAT = "wmsFormat";
    protected static final String PROP_WFS_FORMAT = "wfsFormat";
    protected static final String PROP_WCS_FORMAT = "wcsFormat";
    protected static final String PROP_STYLE_NAME = "style";
    protected static final String PROP_EXTENSION = "extension";

    protected static final String DEFAULT_WMS_VERSION = "1.0.0";
    protected static final String DEFAULT_WFS_VERSION = "1.0.0";
    protected static final String DEFAULT_WCS_VERSION = "1.0.0";

    protected static final String DEFAULT_WMS_FORMAT="image/png";
    protected static final String DEFAULT_WFS_FORMAT="GML2";
    protected static final String DEFAULT_WCS_FORMAT="geotiff";

    protected static final String DEFAULT_SRS="4326";
    protected static final String DEFAULT_QUERYABLE="false";
    protected static final String DEFAULT_HIDDEN="false";

    public Object executeTag(org.w3c.dom.Element geoserverDataDeploy) throws Exception{
       String geoserverWorkspace,geoserverURL, geoserverVersion, geoserverUsername, geoserverPassword;
       File resterDir=null, styleDir=null, vectorDir=null;
       String wmcVersion=null, wmcTitle, wmcAbstract;
       int winWidth=-1, winHeight=-1, srs=-1;
       float west=-1,south=-1,north=-1, east=-1;


        Element geoserverURLElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_URL).item(0);
        Element geoserverVersionElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_VERSION).item(0);
        Element geoserverUsernameElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_USERNAME).item(0);
        Element geoserverPasswordElement = (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_PASSWORD).item(0);
        Element resterFolderElement = (Element) geoserverDataDeploy.getElementsByTagName(RASTER_FOLDER).item(0);
        Element vectorFolderElement = (Element) geoserverDataDeploy.getElementsByTagName(VECTOR_FOLDER).item(0);
        Element styleFolderElement = (Element) geoserverDataDeploy.getElementsByTagName(STYLE_FOLDER).item(0);
        Element workspaceElement = (Element) geoserverDataDeploy.getElementsByTagName(WORKSPACE).item(0);
        Element wmcVersionElement = (Element) geoserverDataDeploy.getElementsByTagName(WMC_VERSION).item(0);
        Element wmcTitleElement = (Element) geoserverDataDeploy.getElementsByTagName(WMC_TITLE).item(0);
        Element wmcAbstractElement = (Element) geoserverDataDeploy.getElementsByTagName(WMC_ABSTRACT).item(0);
        Element wmcWindowElement = (Element) geoserverDataDeploy.getElementsByTagName(WMC_WINDOW).item(0);
        Element wmcBoundingBoxElement = (Element) geoserverDataDeploy.getElementsByTagName(WMC_BOUNDING_BOX).item(0);

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
        wmcVersion= wmcVersionElement != null ? wmcVersionElement.getTextContent() : WMC_VERSION_DEFUALT;
        wmcTitle= wmcTitleElement != null ? wmcTitleElement.getTextContent() : "";
        wmcAbstract= wmcAbstractElement != null ? wmcAbstractElement.getTextContent() : "";
        winWidth= wmcWindowElement != null ? new Integer(wmcWindowElement.getAttribute(WMC_WINDOW_WIDTH_ATTRIBUTE)) : -1;
        winHeight= wmcWindowElement != null ? new Integer(wmcWindowElement.getAttribute(WMC_WINDOW_HEIGHT_ATTRIBUTE)) : -1;
        srs= wmcBoundingBoxElement != null ? new Integer(wmcBoundingBoxElement.getAttribute(WMC_BOUNDING_BOX_SRS_ATTRIBUTE)) : -1;
        west= wmcBoundingBoxElement != null ? new Float(wmcBoundingBoxElement.getAttribute(WMC_BOUNDING_BOX_WEST_ATTRIBUTE)) : -1;
        south= wmcBoundingBoxElement != null ? new Float(wmcBoundingBoxElement.getAttribute(WMC_BOUNDING_BOX_SOUTH_ATTRIBUTE)) : -1;
        east= wmcBoundingBoxElement != null ? new Float(wmcBoundingBoxElement.getAttribute(WMC_BOUNDING_BOX_EAST_ATTRIBUTE)) : -1;
        north= wmcBoundingBoxElement != null ? new Float(wmcBoundingBoxElement.getAttribute(WMC_BOUNDING_BOX_NORTH_ATTRIBUTE)) : -1;
        resterDir=new File(resterFolderElement.getTextContent());
        vectorDir=new File(vectorFolderElement.getTextContent());
        styleDir=new File(styleFolderElement.getTextContent());

        Class wmcClass= Class.forName(WMC_PACKAGE
                + WMC_CLASS_NAME + wmcVersion);
        WMC webMapContext=(WMC) wmcClass.newInstance();
        webMapContext.newWMC();

        webMapContext.addContextTitle(wmcTitle);
        webMapContext.addContextAbstract(wmcAbstract);
        
        if (winHeight !=-1 && winWidth!=-1)
             webMapContext.addWindowElement(winHeight, winWidth);
        if (srs !=-1 && west!=-1 && south!=-1 && east!=-1 && north!=-1)
             webMapContext.addContextBoundingBox(srs, west, south, east, north);

        int version = Integer.parseInt("" + geoserverVersion.charAt(0));
        Class geoserverREST = Class.forName(REST_API_PACKAGE
                + REST_API_CLASS_NAME + version);
        RestAPI geoserverRestAPI = (RestAPI) geoserverREST.newInstance();
        geoserverRestAPI.setGeoserverURL(geoserverURL);


        deployRasterFolderData(resterDir,geoserverRestAPI, geoserverURL,
                geoserverWorkspace,geoserverUsername,geoserverPassword, webMapContext);

        deployVectorFolderData(vectorDir,geoserverRestAPI, geoserverURL,
                geoserverWorkspace,geoserverUsername,geoserverPassword, webMapContext);

        deployStyleFolderData (styleDir,geoserverRestAPI,geoserverUsername,
                geoserverPassword);
         

      return webMapContext.getWMCDocument();
    }



  

    private boolean deployRasterFolderData (File rasterFolder,
            RestAPI geosRestAPI,
            String geoserverURL,
            String geosWorkspace,
            String username,
            String password,
            WMC webMapcontext) throws Exception{
        boolean result=true;
        int i;
        File wmcLayerInfo=null;
        boolean allDeploy=true;
        String[] files = rasterFolder.list();
        if (files == null) {
            return false;
        } else
            for(i=0;i<files.length;i++) {
              String deployName = files[i].substring(0,files[i].indexOf("."));
              String suffix = files[i].substring(files[i].indexOf(".")+1, files[i].length());
              if(!suffix.equalsIgnoreCase(WMC_LAYER_TEXT_PROPERTIES_FILE_SUFFIX)){
                  wmcLayerInfo=new File(rasterFolder, deployName+"."+WMC_LAYER_TEXT_PROPERTIES_FILE_SUFFIX);

                  String dataPath= new File (rasterFolder, files[i]).getAbsolutePath();
                  result = RestUtil.deployRasterData(geosRestAPI,
                        username, password,
                        geosWorkspace, dataPath, deployName, "geotiff");
                  allDeploy=allDeploy && result;
                  if(result){
                     addWmcLayer(webMapcontext, webMapcontext.WCS_SERVICE,
                             deployName, geoserverURL, wmcLayerInfo);
                     addWmcLayer(webMapcontext, webMapcontext.WMS_SERVICE,
                             deployName, geoserverURL, wmcLayerInfo);
                  }
              }
            }
 
      return allDeploy;
    }


    private boolean deployVectorFolderData (File vectorFolder,
            RestAPI geosRestAPI,
            String geoserverURL,
            String geosWorkspace,
            String username,
            String password,
            WMC webMapcontext) throws Exception{
        boolean result=true;
        int i,u;
        File temp=null, newShpZip=null;
        String currentName;
        File wmcLayerInfo=null;
        boolean allDeploy=true;
        String[] files = vectorFolder.list();
        if (files == null) {
            return false;
        } else
            for(i=0;i<files.length;i++) {
              String deployName = files[i].substring(0,files[i].indexOf("."));
              String suffix = files[i].substring(files[i].indexOf(".")+1, files[i].length());
              if(!suffix.equalsIgnoreCase(WMC_LAYER_TEXT_PROPERTIES_FILE_SUFFIX)){
                  wmcLayerInfo=new File(vectorFolder, deployName+"."+WMC_LAYER_TEXT_PROPERTIES_FILE_SUFFIX);
                  temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
                  newShpZip=new File(temp, deployName+".zip");
                  ZipOutputStream out = new ZipOutputStream(new FileOutputStream(newShpZip));
                  //  ZIP
                  String fullpath=null;
                  FileInputStream in=null;
                  for(u=0;u<files.length;u++){
                     currentName= files[u].substring(0,files[i].indexOf("."));
                     if(currentName.equals(deployName)){
                        fullpath = new File(vectorFolder, files[u]).getAbsolutePath();
                        in = new FileInputStream(fullpath);
                        out.putNextEntry(new ZipEntry(files[u]));
                        IOUtil.copy(in, out);
                        out.closeEntry();
                        in.close();
                     }
                  }
                  String dataPath= newShpZip.getAbsolutePath();
                  result = RestUtil.deployVectorData(geosRestAPI,
                        username, password,
                        geosWorkspace, dataPath, deployName, "shp");
                  allDeploy=allDeploy && result;
                  newShpZip.delete();
                  if(result){
                     addWmcLayer(webMapcontext, webMapcontext.WFS_SERVICE,
                             deployName, geoserverURL, wmcLayerInfo);
                     addWmcLayer(webMapcontext, webMapcontext.WMS_SERVICE,
                             deployName, geoserverURL, wmcLayerInfo);
                  }
              }
            }

      return allDeploy;
    }


    private boolean deployStyleFolderData (File styleFolder,
            RestAPI geosRestAPI,
            String username,
            String password) throws Exception{
        boolean result=true;
        int i;
        boolean allDeploy=true;
        String[] files = styleFolder.list();
        if (files == null) {
            return false;
        } else
            for(i=0;i<files.length;i++) {
              String deployName = files[i].substring(0,files[i].indexOf("."));
              String dataPath= new File (styleFolder, deployName).getAbsolutePath();
              result = RestUtil.deployStyle(geosRestAPI, username, password, dataPath, deployName);
              allDeploy=allDeploy && result;
            }
      return allDeploy;
    }

    
    public void addWmcLayer(WMC webMapcontext,
            short layerType,
            String deployName,
            String geoserverURL,
            File wmcLayerPropertires) throws Exception{

       String layerProperties;
       String hidden=null, queryable=null, layerTitle=null, layerName=null;
       String wfsVersion=null, wmsVersion=null, wfsFormat=null, wmsFormat=null;
       String wcsVersion=null, wcsFormat=null, srs=null, serviceVersion=null;
       String serviceURL=null, style=null, extension=null;
       Element owsServer=null, formatList=null, styleEl=null, extensionEl=null;
       String format=null;

       if(wmcLayerPropertires.exists()){
         layerProperties=IOUtil.inputToString(
                           new FileInputStream(wmcLayerPropertires));
         queryable=readProperty(layerProperties,PROP_QUERYABLE);
         hidden=readProperty(layerProperties,PROP_HIDEEN);
         wfsVersion=readProperty(layerProperties,PROP_WFS_VERSION);
         wmsVersion=readProperty(layerProperties,PROP_WMS_VERSION);
         wcsVersion=readProperty(layerProperties,PROP_WCS_VERSION);
         wfsFormat=readProperty(layerProperties,PROP_WFS_FORMAT);
         wmsFormat=readProperty(layerProperties,PROP_WMS_FORMAT);
         wcsFormat=readProperty(layerProperties,PROP_WCS_FORMAT);
         layerTitle=readProperty(layerProperties,PROP_TITLE);
         layerName=readProperty(layerProperties,PROP_NAME);
         srs=readProperty(layerProperties,PROP_SRS);
         style=readProperty(layerProperties,PROP_STYLE_NAME);
         extension=readProperty(layerProperties,PROP_EXTENSION);
       }

       layerName= layerName != null ? layerName : deployName;
       layerTitle= layerTitle != null ? layerTitle : layerName;
       srs= srs != null ? srs : DEFAULT_SRS;
       queryable= queryable != null ? queryable : DEFAULT_QUERYABLE;
       hidden= hidden != null ? hidden : DEFAULT_HIDDEN;
       if(extension != null){

       }else
          extensionEl=null;

       if (layerType==webMapcontext.WMS_SERVICE){
          serviceURL=GeoserverUtil.getWMSReference(geoserverURL);
          serviceVersion= wmsVersion != null ? wmsVersion : DEFAULT_WMS_VERSION;
          format= wmsFormat != null ? wmsFormat : DEFAULT_WMS_FORMAT;
       }else
          if (layerType==webMapcontext.WFS_SERVICE){
              serviceURL=GeoserverUtil.getWFSReference(geoserverURL);
              serviceVersion= wfsVersion != null ? wfsVersion : DEFAULT_WFS_VERSION;
              format= wfsFormat != null ? wfsFormat : DEFAULT_WFS_FORMAT;
              styleEl=null;
          }else
            if (layerType==webMapcontext.WCS_SERVICE){
              serviceURL=GeoserverUtil.getWCSReference(geoserverURL); 
              serviceVersion= wcsVersion != null ? wcsVersion : DEFAULT_WCS_VERSION;
              format= wcsFormat != null ? wcsFormat : DEFAULT_WCS_FORMAT;
              styleEl=null;
            }

      owsServer=webMapcontext.newOWSServer(serviceURL, layerType, serviceVersion);
      String [] fl={format};
      formatList=webMapcontext.newFormatList(fl, 0);
      if(style!=null){
        String [] sl={style};
        styleEl=webMapcontext.newStyleList(sl, 0);
      }
      webMapcontext.addLayer(layerName, layerTitle, new Integer(srs), 
               Boolean.valueOf(hidden), Boolean.valueOf(queryable),
               owsServer, formatList, styleEl, extensionEl);
    }

    private String readProperty (String layerProperties, String prop){
        int indexProp=layerProperties.indexOf(prop);
        if(indexProp == -1)
            return null;
        else{
            String property=layerProperties.substring(indexProp);
            property=property.substring(prop.length()+1, property.indexOf(" "));
            return property;
        }
    }
    

}
