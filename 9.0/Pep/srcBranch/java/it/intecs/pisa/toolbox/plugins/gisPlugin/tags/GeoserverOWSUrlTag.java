
package it.intecs.pisa.toolbox.plugins.gisPlugin.tags;

import it.intecs.pisa.gis.geoserver.util.GeoserverUtil;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */

/*
 * <geoserverOWSUrl>
 *      <serviceType></serviceType> // types supported WMS,WCS,WFS,OWS
                                                (defualt OWS)
 * </geoserverOWSUrl>
 *
 */
public class GeoserverOWSUrlTag extends NativeTagExecutor{

    protected static final String SERVICE_TYPE = "serviceType";

    protected static final String SERVICE_WMS = "WMS";
    protected static final String SERVICE_WFS = "WFS";
    protected static final String SERVICE_WCS = "WCS";


    @Override
    public Object executeTag(org.w3c.dom.Element geoserverOWSUrl) throws Exception{

     org.w3c.dom.Element serviceTypeElement= (Element) geoserverOWSUrl.getElementsByTagName(SERVICE_TYPE).item(0);
     String serviceType= serviceTypeElement!=null ? serviceTypeElement.getTextContent() : null;
     ToolboxConfiguration td = ToolboxConfiguration.getInstance();
     String geosURL=td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_URL);

     if(serviceType == null)
         return GeoserverUtil.getOWSReference(geosURL);
     else
       if(serviceType.equalsIgnoreCase(SERVICE_WMS))
          return GeoserverUtil.getWMSReference(geosURL);
       else
         if(serviceType.equalsIgnoreCase(SERVICE_WFS))
          return GeoserverUtil.getWFSReference(geosURL);
         else
          if(serviceType.equalsIgnoreCase(SERVICE_WCS))
             return GeoserverUtil.getWCSReference(geosURL);


     return GeoserverUtil.getOWSReference(geosURL);
    }
}
