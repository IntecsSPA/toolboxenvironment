
package it.intecs.pisa.gis.raster.instance;

import java.awt.image.Raster;
import java.net.URL;
import java.util.ArrayList;


/**
 *
 * @author Andrea Marongiu
 */
public interface RasterInstance <T> {

    // Dimension Names
    static final String DIMENSION_LAT = "lat";
    static final String DIMENSION_LON = "lon";

   // Layer Types
    static final short INT_LAYER_TYPE = 1;
    static final short FLOAT_LAYER_TYPE = 2;
    static final short DOUBLE_LAYER_TYPE = 3;
    static final short BOOLEAN_LAYER_TYPE = 4;
    static final short BYTE_LAYER_TYPE = 5;
    static final short SHORT_LAYER_TYPE = 6;
    static final short CHAR_LAYER_TYPE = 7;
    static final short LONG_LAYER_TYPE = 8;
   // static final short STRING_LAYER_TYPE = 9;

   // Attribute Types
    static final short STRING_ATTRIBUTE_TYPE = 1;
    static final short INT_ATTRIBUTE_TYPE = 2;
    static final short FLOAT_ATTRIBUTE_TYPE = 3;
    static final short DOUBLE_ATTRIBUTE_TYPE = 4;
    static final short SHORT_ATTRIBUTE_TYPE = 5;
    static final short LONG_ATTRIBUTE_TYPE = 6;
    static final short ARRAY_STRING_ATTRIBUTE_TYPE = 7;


    public void setSource(URL sourceURL)throws Exception;

    public void setDimensionAttribute(String dimensionName, int length);

    public void addLayer(String layerName, String layerType, ArrayList dimensions) throws Exception;

    public void setLayerValue(String layerName, String layerType, String value) throws Exception;

    public void addLayerAttribute (String layerName, String attributeName, Object attributeValue);

    public void addLayerAttribute (String variableName, String attributeName, String attributeType, String attributeValue);

    public void add2DLayerfromRaster(String layerName, ArrayList dimensions, String layerType, Raster rasterData) throws Exception;

    public void addMultiDimLayerfromDataAndRasterArray(String layerName, String layerType, ArrayList dimensions, Raster[] rastersData, int[][] rasterDataPositions, String noData) throws Exception;

    public void addGlobalInformation (String attributeName, String attributeType, String attributeValue);

    public void addGlobalInformation(String attributeName, Object attributeValue);

    public Object getInformation(String informationReference);

    public Raster getRaster();

    public ArrayList getDimensions();

    public ArrayList getDimensionsByName(String dimensionListString);

}
