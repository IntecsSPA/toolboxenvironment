
package it.intecs.pisa.toolbox.plugins.gisPlugin.tags;

import it.intecs.pisa.toolbox.plugins.gisPlugin.util.RasterData;
import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */

/*
 *   <createRasterData type="">
 *       <rasterPath>.. all toolbox tags..</rasterPath>
 *       <rasterContent>
 *          <sources>
 *              <source type="" name="" url=""/>
 *              ...
 *              <source type="" name="" url=""/>
 *          </sources>
 *          <dimensions>
 *              <dimension name="" value=""/>
 *              ...
 *          </dimensions>
 *          <layers>
 *              <layer name="" type="" dimensionList="">
 *                  <attribute name="" type="" value=""/>
 *                  <content>
 *                          <raster name="" position="">
 *                          ....
 *                          <raster name="" position="">
 *                  <content>
 *              </layer>
 *              ...
 *              <layer name="" type="" value="" dimensionList="">
 *                  <attribute name="" type="" value=""/>
 *              </layer>
 *          </layers>
 *          <generalInformation name="" type="" value=""/>
 *          <generalInformation name="" type="" value=""/>
 *          ...
 *      </rasterContent>
 *
 *   </CreateRasterData>
 *
 *
 *  // Layer Types
    INT_LAYER_TYPE = "INT";
    FLOAT_LAYER_TYPE = "FLOAT";
    DOUBLE_LAYER_TYPE = "DOUBLE";
    BOOLEAN_LAYER_TYPE = "BOOLEAN";
    BYTE_LAYER_TYPE = "BYTE";
    SHORT_LAYER_TYPE = "SHORT";
    CHAR_LAYER_TYPE = "CHAR";
    LONG_LAYER_TYPE = "LONG";
    STRING_LAYER_TYPE = "STRING";
 *
 *
 *  // Attribute Types
    STRING_ATTRIBUTE_TYPE = "STRING";
    INT_ATTRIBUTE_TYPE = "INT";
    FLOAT_ATTRIBUTE_TYPE = "FLOAT";
    DOUBLE_ATTRIBUTE_TYPE = "DOUBLE";
    SHORT_ATTRIBUTE_TYPE = "SHORT";
    LONG_ATTRIBUTE_TYPE = "LONG";
    ARRAY_STRING_ATTRIBUTE_TYPE = "ARRAY";

 */
public class CreateRasterDataTag extends NativeTagExecutor {

    protected static final String RASTER_TYPE = "type";
    protected static final String RASTER_PATH = "rasterPath";
    protected static final String RASTER_CONTENT = "rasterContent";
    protected static final String CREATE_RASTER_CLASSES_PACKAGE = "it.intecs.pisa.toolbox.plugins.gisPlugin.";
    protected static final String CREATE_RASTER_CLASS_NAME_SUFFIX="Raster";


    @Override
    public Object executeTag(org.w3c.dom.Element createRasterData) throws Exception{
      String dataType=createRasterData.getAttribute(RASTER_TYPE).toUpperCase();
      org.w3c.dom.Element path= (Element) createRasterData.getElementsByTagName(RASTER_PATH).item(0);

      org.w3c.dom.Element content= (Element) createRasterData.getElementsByTagName(RASTER_CONTENT).item(0);
      Class createRasterClass = Class.forName(CREATE_RASTER_CLASSES_PACKAGE+dataType+CREATE_RASTER_CLASS_NAME_SUFFIX);
      RasterData rasterData = (RasterData) createRasterClass.newInstance();
      rasterData.setRasterPath((String)executeChildTag(DOMUtil.getFirstChild(path)));
      rasterData.create(content);
      return null;
    }

}
