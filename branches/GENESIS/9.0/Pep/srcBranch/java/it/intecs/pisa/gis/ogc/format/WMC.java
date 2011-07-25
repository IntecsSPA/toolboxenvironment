
package it.intecs.pisa.gis.ogc.format;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author Andrea Marongiu
 */
public interface WMC {

    final static short WMS_SERVICE=0;
    public String WMS_SERVICE_NAME="wms";
    public String WMS_SERVICE_TITLE="OGC:WMS";
    public String WMS_SERVICE_DEFAULT_VERSION="1.0.0";
    final short WFS_SERVICE=1;
    public String WFS_SERVICE_NAME="wfs";
    public String WFS_SERVICE_TITLE="OGC:WFS";
    public String WFS_SERVICE_DEFAULT_VERSION="1.0.0";
    final short WCS_SERVICE=2;
    public String WCS_SERVICE_NAME="wcs";
    public String WCS_SERVICE_TITLE="OGC:WCS";
    public String WCS_SERVICE_DEFAULT_VERSION="1.0.0";



    public void newWMC()throws Exception;

    public Document getWMCDocument();

    public String getWMCVersion();

    public void addContextTitle(String title);

    public void addContextAbstract(String abstractText);

    public void addWindowElement(int height, int width);

    public void addContextBoundingBox(int srs,
            float west,
            float south,
            float east,
            float north);

    public void addLayer(String layerName,
            String layerTitle,
            int srs,
            boolean hidden,
            boolean quesyable,
            Element server,
            Element formatList,
            Element styleList,
            Element extension);

    public Element newOWSServer(String serverURL,
            short service,
            String version) throws Exception;

    public Element newFormatList(String [] formatList,
            int indexDefaultFormat);

    public Element newStyleList(Object [] styleList,
            int indexDefaultFormat);

    public Element newExtension(Node [] extension);
}

