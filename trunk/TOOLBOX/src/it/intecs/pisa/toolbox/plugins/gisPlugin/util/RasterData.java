

package it.intecs.pisa.toolbox.plugins.gisPlugin.util;

/**
 *
 * @author Andrea Marongiu
 */
public interface RasterData {
    static final String RASTER_DATA_CLASSES_PACKAGE = "it.pisa.intecs.raster.instance.";
    static final String SOURCE_NODE = "source";
    static final String SOURCE_TYPE = "type";
    static final String SOURCE_NAME = "name";
    static final String SOURCE_URL = "url";
    static final String DIMENSION_NODE = "dimension";
    static final String DIMENSION_NAME = "name";
    static final String DIMENSION_VALUE = "value";
    static final String LAYER_NODE = "layer";
    static final String LAYER_NAME = "name";
    static final String LAYER_VALUE_CONTENT_NODE = "content";
    static final String LAYER_NO_DATA_ATTRIBUTE = "noData";
    static final String LAYER_VALUE_CONTENT_RASTER_NODE = "raster";
    static final String LAYER_VALUE_CONTENT_RASTER_NAME = "name";
    static final String LAYER_VALUE_CONTENT_RASTER_POSITION = "position";
    static final String LAYER_VALUE = "value";
    static final String LAYER_TYPE = "type";
    static final String LAYER_ATTRIBUTE_NODE = "attribute";
    static final String LAYER_ATTRIBUTE_TYPE = "type";
    static final String LAYER_ATTRIBUTE_NAME = "name";
    static final String LAYER_ATTRIBUTE_VALUE= "value";
    static final String LAYER_ATTRIBUTE_DIMENSION_LIST = "dimensionList";
    static final String GENERAL_INFORMATION_NODE = "generalInformation";
    static final String GENERAL_INFORMATION_NAME = "name";
    static final String GENERAL_INFORMATION_TYPE = "type";
    static final String GENERAL_INFORMATION_VALUE = "value";
    static final char PREFIX_SOURCE_REFERENCE = '%';
    static final String PREFIX_ATTRIBUTE_SOURCE_REFERENCE = "@";


    public void create(org.w3c.dom.Element content) throws Exception;

    public void setRasterPath(String RasterPath) throws Exception;


}
