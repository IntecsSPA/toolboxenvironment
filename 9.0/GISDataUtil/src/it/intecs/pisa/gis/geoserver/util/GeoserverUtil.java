
package it.intecs.pisa.gis.geoserver.util;

/**
 *
 * @author Andrea Marongiu
 */
public class GeoserverUtil {
    
    private static String geoserverWMS="/wms";
    private static String geoserverWFS="/wfs";
    private static String geoserverWCS="/wcs";
    private static String geoserverOWS="/ows";

    public static String getWMSReference(String geoserverURL){
        return getGeoUrl(geoserverURL)+geoserverWMS;
    }
    
    public static String getWFSReference(String geoserverURL){
        return getGeoUrl(geoserverURL)+geoserverWFS;
    }
    
    public static String getWCSReference(String geoserverURL){
        return getGeoUrl(geoserverURL)+geoserverWCS;
    }
    
    public static String getOWSReference(String geoserverURL){
        return getGeoUrl(geoserverURL)+geoserverOWS;
    }

    private static String getGeoUrl (String geoURL){
        if(geoURL.charAt(geoURL.length()-1) == '/')
            return geoURL.substring(0, geoURL.length()-1);
        else
            return geoURL;
    }

}
