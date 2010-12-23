/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.gisclient.util;



/**
 *
 * @author Maro
 */
public class OGCUtil {
    
    public static final String [] SLD_ENCODED = {"StyledLayerDescriptor",
                            "NamedLayer",
                            "NamedStyle",
                            "Name",
                            "UserStyle",
                            "IsDefault",
                            "FeatureTypeStyle",
                            "Rule",
                            "Title",
                            "Abstract",
                            "ElseFilter",
                            "MinScaleDenominator",
                            "MaxScaleDenominator",
                            "LineSymbolizer",
                            "PolygonSymbolizer",
                            "PointSymbolizer",
                            "Stroke",
                            "Fill",
                            "CssParameter",
                            "Graphic",
                            "ExternalGraphic",
                            "Mark",
                            "WellKnownName",
                            "Opacity",
                            "Size",
                            "Rotation",
                            "OnlineResource",
                            "Format",
                            "Filter",
                            "FeatureId",
                            "And",
                            "Or",
                            "Not",
                            "PropertyIsEqualTo",
                            "PropertyIsNotEqualTo",
                            "PropertyIsLessThanOrEqualTo",
                            "PropertyIsLessThan",
                            "PropertyIsGreaterThanOrEqualTo",
                            "PropertyIsGreaterThan",
                            "PropertyIsBetween",
                            "PropertyIsLike",
                            "Literal",
                            "PropertyName",
                            "LowerBoundary",
                            "UpperBoundary",
                            "gml:Box",
                            "gml:lowerCorner",
                            "gml:upperCorner",
                            "gml:coordinates",
                            "gml:Envelope"};
    
    public static final String [] FILTER_ENCODED = {
                            "Filter",
                            "FeatureId",
                            "And",
                            "Or",
                            "Not",
                            "BBOX",
                            "PropertyIsEqualTo",
                            "PropertyIsNotEqualTo",
                            "PropertyIsLessThanOrEqualTo",
                            "PropertyIsLessThan",
                            "PropertyIsGreaterThanOrEqualTo",
                            "PropertyIsGreaterThan",
                            "PropertyIsBetween",
                            "PropertyIsLike",
                            "Literal",
                            "PropertyName",
                            "LowerBoundary",
                            "UpperBoundary",
                            "gml:Box",
                            "gml:lowerCorner",
                            "gml:upperCorner",
                            "gml:coordinates",
                            "gml:Envelope"};
    
    public static String getSLDEncoded(String sld){
       String sldEncoded=sld;
       int i;
       for(i=0; i<SLD_ENCODED.length; i++){
          sldEncoded=sldEncoded.replaceAll("<"+i+">", "<"+SLD_ENCODED[i]+">");
          sldEncoded=sldEncoded.replaceAll("<"+i+" ", "<"+SLD_ENCODED[i]+" ");
          sldEncoded=sldEncoded.replaceAll("<"+i+"/>", "<"+SLD_ENCODED[i]+"/>");
          sldEncoded=sldEncoded.replaceAll("</"+i+">", "</"+SLD_ENCODED[i]+">");
       }
       return(sldEncoded); 
    }
    
     public static String getFilterEncoded(String filter){
       String filterEncoded=filter;
       int i;
       for(i=0; i<FILTER_ENCODED.length; i++){
          filterEncoded=filterEncoded.replaceAll("<"+i+">", "<"+FILTER_ENCODED[i]+">");
          filterEncoded=filterEncoded.replaceAll("<"+i+" ", "<"+FILTER_ENCODED[i]+" ");
          filterEncoded=filterEncoded.replaceAll("<"+i+"/>", "<"+FILTER_ENCODED[i]+"/>");
          filterEncoded=filterEncoded.replaceAll("</"+i+">", "</"+FILTER_ENCODED[i]+">");
       }
       return(filterEncoded); 
    }

}
