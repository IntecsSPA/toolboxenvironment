package it.intecs.pisa.gis.geoserver.rest;

import java.net.URL;

/**
 *
 * @author Andrea Marongiu
 */
public class RestAPIVersion1 implements RestAPI {

    private static String REST_ROOT="/rest";
    private static String LAYERS_PATH="/layers";
    private static String FOLDERS_PATH="/folders";
    private static String FILE_PREFIX="/file.";

    private static String GEOTIFF_FORMAT="geotiff";
    private static String SHP_FORMAT="shp";
    private static String SLD_FORMAT="style";

    private String [] rasterSupportedFormats=null;
    private String [] styleSupportedFormats=null;
    private String [] vectorSupportedFormats=null;

    private String geoserverURL;

    RestAPIVersion1(){
       rasterSupportedFormats=new String[1];
       styleSupportedFormats=new String[1];
       vectorSupportedFormats=new String[1];
       rasterSupportedFormats[0]=GEOTIFF_FORMAT;
       styleSupportedFormats[0]=SHP_FORMAT;
       vectorSupportedFormats[0]=SLD_FORMAT;
    }


    public URL getCreateRasterURL(String workspaceName, String newStoreName, String newStoreType) throws Exception {
       return(new URL(this.geoserverURL+REST_ROOT+FOLDERS_PATH+"/"
              +workspaceName
              +LAYERS_PATH+"/"+newStoreName
              +FILE_PREFIX+newStoreType));
    }

    public URL getCreateVectorURL(String workspaceName, String newStoreName, String newStoreType) throws Exception {
        return(new URL(this.geoserverURL+REST_ROOT+FOLDERS_PATH+"/"
              +workspaceName
              +LAYERS_PATH+"/"+newStoreName
              +FILE_PREFIX+newStoreType));
    }


    public URL getStylesURL() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getCreateWorkspaceURL(String newWorkspaceName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getLayerURL(String layerName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setGeoserverURL(String geoserverURL) {
        this.geoserverURL=geoserverURL;

    }

    public URL getWorkspacesURL() throws Exception {
        return(new URL(this.geoserverURL+REST_ROOT+FOLDERS_PATH));
    }

    /**
     * @return the rasterSupportedFormats
     */
    public String[] getRasterSupportedFormats() {
        return rasterSupportedFormats;
    }

    /**
     * @return the styleSupportedFormats
     */
    public String[] getStyleSupportedFormats() {
        return styleSupportedFormats;
    }

    /**
     * @return the vectorSupportedFormats
     */
    public String[] getVectorSupportedFormats() {
        return vectorSupportedFormats;
    }

    public boolean isSupported(String format) {
        int i=0;
        for(i=0;i<rasterSupportedFormats.length;i++)
            if(rasterSupportedFormats[1].equalsIgnoreCase(format))
                return true;
        for(i=0;i<vectorSupportedFormats.length;i++)
            if(vectorSupportedFormats[1].equalsIgnoreCase(format))
                return true;
        for(i=0;i<styleSupportedFormats.length;i++)
            if(styleSupportedFormats[1].equalsIgnoreCase(format))
                return true;

        return false;
    }

    public boolean isVectorFormat(String format) {
        for(int i=0;i<vectorSupportedFormats.length;i++)
            if(vectorSupportedFormats[1].equalsIgnoreCase(format))
                return true;
        return false;
    }

    public boolean isRasterFormat(String format) {
        for(int i=0;i<rasterSupportedFormats.length;i++)
            if(rasterSupportedFormats[1].equalsIgnoreCase(format))
                return true;
        return false;
    }

    public boolean isStyleFormat(String format) {
        for(int i=0;i<styleSupportedFormats.length;i++)
            if(styleSupportedFormats[1].equalsIgnoreCase(format))
                return true;
        return false;
    }

}
