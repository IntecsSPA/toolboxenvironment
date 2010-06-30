
package it.intecs.pisa.gis.ogc.format;

import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */
public class WMCVersion1_1_0 extends WMCVersion1{

  private String schemaLocation="http://www.opengis.net/context http://schemas.opengis.net/context/1.1.0/context.xsd";
  private String version="1.1.0";

  private String NAMESPACE_PREFIX="xmlns:";
  private String SLD_NAMESPACE="http://www.opengis.net/sld";
  private String SLD_PREFIX="sld";

  private String MIN_SCALE_DENOMINATOR_ELEMENT="MinScaleDenominator";
  private String MAX_SCALE_DENOMINATOR_ELEMENT="MaxScaleDenominator";

  
  public void addMinScaleDenominatorToLayer(String layerName,
          double minScale){

      Element layerElement=this.getLayerElement(layerName);
      Element minScaleElement=this.getWMCDocument().createElement(
                                  SLD_PREFIX+":"+MIN_SCALE_DENOMINATOR_ELEMENT);
      minScaleElement.setAttribute(NAMESPACE_PREFIX+SLD_PREFIX,
              SLD_NAMESPACE);
      minScaleElement.setTextContent(new Double(minScale).toString());

      layerElement.appendChild(minScaleElement);
  }

  public void addMaxScaleDenominatorToLayer(String layerName,
          double maxScale){
      Element layerElement=this.getLayerElement(layerName);
      Element maxScaleElement=this.getWMCDocument().createElement(
                                  SLD_PREFIX+":"+MAX_SCALE_DENOMINATOR_ELEMENT);
      maxScaleElement.setAttribute(NAMESPACE_PREFIX+SLD_PREFIX,
              SLD_NAMESPACE);
      maxScaleElement.setTextContent(new Double(maxScale).toString());

      layerElement.appendChild(maxScaleElement);

  }



}
