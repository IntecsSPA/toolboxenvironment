
package it.intecs.pisa.toolbox.plugins.gisPlugin.util;

import it.intecs.pisa.gis.raster.instance.RasterInstance;
import it.intecs.pisa.gis.raster.instance.NETCDF;
import java.awt.image.Raster;
import java.net.URL;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andrea Marongiu
 */

public class NETCDFRaster implements RasterData{
    private String netCdfPathFile;
    private NETCDF netcdfRaster=null;

    public void create(Element content) throws Exception{
        NodeList sourceNodes=content.getElementsByTagName(SOURCE_NODE);
        NodeList dimensionNodes=content.getElementsByTagName(DIMENSION_NODE);
        NodeList layerNodes=content.getElementsByTagName(LAYER_NODE);
        NodeList generalInformationNodes=content.getElementsByTagName(GENERAL_INFORMATION_NODE);
        int i,u;
        try{
            this.netcdfRaster=new NETCDF(this.netCdfPathFile, true);
        }catch(Throwable t){
            t.printStackTrace();
        }
        System.out.println("GeoTiff: " + sourceNodes.getLength());
        System.out.println("****************************PARSE For DIMENSION: *****************************************************************");
        this.setNETCDFDimensions (sourceNodes, dimensionNodes);
        System.out.println("****************************PARSE For SIMPLE LAYER: *****************************************************************");
        this.setNETCDFLayersAndInfo(sourceNodes, layerNodes, generalInformationNodes);
        System.out.println("****************************PARSE For COMPLEX LAYER: *****************************************************************");
        this.setNETCDFComplexLayer(sourceNodes, layerNodes);
        System.out.println("****************************CREATE NETCDF: *****************************************************************");
        this.netcdfRaster.create();
    }

    public void setRasterPath(String RasterPath) throws Exception {
        this.netCdfPathFile=RasterPath;
    }


    private RasterInstance getSourceRasterByNameRef(NodeList sourceNodes, String rasterRefName) throws Exception{
        int i;
        String sourceName,dataClassName;
        Element currentSource=null;
        Object classInstance;
        Class sourceRasterClass = null;
        RasterInstance sourceRaster=null;
        int informationIntValue;

        // Source Parser  -- Start
        for(i=0;i<sourceNodes.getLength();i++){
           currentSource=(Element) sourceNodes.item(i);
           sourceName=currentSource.getAttribute(SOURCE_NAME);
           if(rasterRefName.equalsIgnoreCase(PREFIX_SOURCE_REFERENCE+sourceName)){
              dataClassName=RASTER_DATA_CLASSES_PACKAGE.concat(currentSource.getAttribute(SOURCE_TYPE));
              sourceRasterClass = Class.forName(dataClassName);
              try{
                  classInstance= sourceRasterClass.newInstance();
                  sourceRaster= (RasterInstance) classInstance;
              }catch(Throwable t){
                 t.printStackTrace();
              }
              //System.out.println("TIFF " +sourceName+": "+currentSource.getAttribute(SOURCE_URL).replaceAll("&amp;", "&"));
              sourceRaster.setSource(new URL(currentSource.getAttribute(SOURCE_URL).replaceAll("&amp;","&").replace("demo1", "demo")));
              break;
           }
         }
        // Source Parser -- End


     return sourceRaster;

    }
    
    private void setNETCDFDimensions (NodeList sourceNodes, NodeList dimensionNodes) throws ClassNotFoundException, Exception{
        int u,informationIntValue;
        Element currentNode=null;
        String nodeAttributeValue;
        RasterInstance sourceRaster=null;

        // Dimension Parser -- Start
           for(u=0;u<dimensionNodes.getLength();u++){
              currentNode=(Element) dimensionNodes.item(u);
              nodeAttributeValue=currentNode.getAttribute(DIMENSION_VALUE);
              if(nodeAttributeValue.contains(PREFIX_SOURCE_REFERENCE+"")){
                 sourceRaster=this.getSourceRasterByNameRef(sourceNodes, nodeAttributeValue.split(PREFIX_ATTRIBUTE_SOURCE_REFERENCE)[0]);
                 informationIntValue=(Integer) sourceRaster.getInformation(nodeAttributeValue.split(PREFIX_ATTRIBUTE_SOURCE_REFERENCE)[1]);
                 this.netcdfRaster.setDimensionAttribute(currentNode.getAttribute(DIMENSION_NAME), informationIntValue);
              }else
                this.netcdfRaster.setDimensionAttribute(currentNode.getAttribute(DIMENSION_NAME), new Integer(nodeAttributeValue));
          }
        // Dimension Parser -- End
    }


     private void setNETCDFLayersAndInfo (NodeList sourceNodes, NodeList layerNodes, NodeList generalInformationNodes) throws ClassNotFoundException, Exception{
        int i,u,j;
        Element currentSource=null, currentNode=null, currentAttribute=null;
        String sourceName,className,nodeAttributeValue,informationStringValue,layerDimensionsList, stringType;
        ArrayList dimensionsList=null;
        Object classInstance;
        Class sourceRasterClass=null;
        RasterInstance sourceRaster=null;

        // Source Parser for Layers and General Information -- Start
        

           // Layer Parser -- Start
           for(u=0;u<layerNodes.getLength();u++){
              currentNode=(Element) layerNodes.item(u);
              nodeAttributeValue=currentNode.getAttribute(LAYER_VALUE);
              if(!( nodeAttributeValue.equals(null)||nodeAttributeValue.equals("")) ){
                  if(nodeAttributeValue.contains(PREFIX_SOURCE_REFERENCE+"")){
                     sourceRaster=this.getSourceRasterByNameRef(sourceNodes, nodeAttributeValue);
                     layerDimensionsList=currentNode.getAttribute(LAYER_ATTRIBUTE_DIMENSION_LIST);
                     if(layerDimensionsList.equals(null)||layerDimensionsList.equals(""))
                        dimensionsList=netcdfRaster.getDimensions();
                     else
                        dimensionsList=netcdfRaster.getDimensionsByName(layerDimensionsList);
                     stringType=currentNode.getAttribute(LAYER_TYPE);
                     netcdfRaster.add2DLayerfromRaster(currentNode.getAttribute(LAYER_NAME), dimensionsList, stringType, sourceRaster.getRaster());
                  }else{
                     stringType=currentNode.getAttribute(LAYER_TYPE);
                     layerDimensionsList=currentNode.getAttribute(LAYER_ATTRIBUTE_DIMENSION_LIST);
                     if(layerDimensionsList.equals(null)||layerDimensionsList.equals(""))
                        dimensionsList=netcdfRaster.getDimensions();
                     else
                        dimensionsList=netcdfRaster.getDimensionsByName(layerDimensionsList);
                      netcdfRaster.addLayer(currentNode.getAttribute(LAYER_NAME), stringType, dimensionsList);
                      informationStringValue=(String) sourceRaster.getInformation(nodeAttributeValue.split(PREFIX_ATTRIBUTE_SOURCE_REFERENCE)[1]);
                      netcdfRaster.setLayerValue(currentNode.getAttribute(LAYER_NAME), stringType, informationStringValue);
                  }
              }
              
              // Layer Attribute Parser -- Start
              NodeList layerAttributes=((Element)layerNodes.item(u)).getElementsByTagName(LAYER_ATTRIBUTE_NODE);
              for(j=0;j<layerAttributes.getLength();j++){
                 currentAttribute=(Element) layerAttributes.item(j);
                 nodeAttributeValue=currentAttribute.getAttribute(LAYER_ATTRIBUTE_VALUE);
                 if(nodeAttributeValue.contains(PREFIX_SOURCE_REFERENCE+"")){
                    sourceRaster=this.getSourceRasterByNameRef(sourceNodes, nodeAttributeValue);
                    stringType=currentAttribute.getAttribute(LAYER_ATTRIBUTE_TYPE);
                    informationStringValue=(String) sourceRaster.getInformation(nodeAttributeValue.split(PREFIX_ATTRIBUTE_SOURCE_REFERENCE)[1]);
                          netcdfRaster.addLayerAttribute(currentNode.getAttribute(LAYER_NAME),
                            currentAttribute.getAttribute(LAYER_ATTRIBUTE_NAME),
                            stringType,informationStringValue);
                }else{
                  stringType=currentAttribute.getAttribute(LAYER_ATTRIBUTE_TYPE);
                  netcdfRaster.addLayerAttribute(currentNode.getAttribute(LAYER_NAME),
                            currentAttribute.getAttribute(LAYER_ATTRIBUTE_NAME),
                            stringType,nodeAttributeValue);
                }
              }
              // Layer Attribute Parser -- End
           }
           
           // General Information Parser -- Start
           for(u=0;u<generalInformationNodes.getLength();u++){
              currentNode=(Element) generalInformationNodes.item(u);
              nodeAttributeValue=currentNode.getAttribute(GENERAL_INFORMATION_VALUE);
              if(nodeAttributeValue.contains(PREFIX_SOURCE_REFERENCE+"")){
                 sourceRaster=this.getSourceRasterByNameRef(sourceNodes, nodeAttributeValue);
                 stringType=currentAttribute.getAttribute(LAYER_ATTRIBUTE_TYPE);
                 informationStringValue=(String) sourceRaster.getInformation(nodeAttributeValue.split(PREFIX_ATTRIBUTE_SOURCE_REFERENCE)[1]);
                 netcdfRaster.addGlobalInformation(currentNode.getAttribute(GENERAL_INFORMATION_NAME),
                             stringType,informationStringValue);
              }else{
                    stringType=currentNode.getAttribute( GENERAL_INFORMATION_TYPE );
                    netcdfRaster.addGlobalInformation(currentNode.getAttribute(GENERAL_INFORMATION_NAME),
                             stringType,nodeAttributeValue);
              }
           }
           // General Information Parser -- End

    }



     private void setNETCDFComplexLayer(NodeList sourceNodes, NodeList layerNodes ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, Exception{
      String stringType,layerDimensionsList, currentRasterName, currentRasterPosition, sourceName;
      NodeList contentLayerList=null, rasterNodeList=null;
      Element currentNode=null, currentRaster=null, currentSource=null;
      Element currentAttribute=null;
      String nodeAttributeValue,informationStringValue;
      int [][] rastersArrayIndex;
      String [] currentRasterPositionSplit;
      Raster [] rasterArray;
      ArrayList dimensionsList=null;
      int u,i,j,k;
      String noData=null;

        // Complex Value Layer Parser-- Start
        for(j=0;j<layerNodes.getLength();j++){
             currentNode=(Element) layerNodes.item(j);
             noData=currentNode.getAttribute(LAYER_NO_DATA_ATTRIBUTE);
             contentLayerList=currentNode.getElementsByTagName(LAYER_VALUE_CONTENT_NODE);
             if(contentLayerList.getLength() >0){
                stringType=currentNode.getAttribute(LAYER_TYPE);
                layerDimensionsList=currentNode.getAttribute(LAYER_ATTRIBUTE_DIMENSION_LIST);
                if(layerDimensionsList == null||layerDimensionsList.equals(""))
                    dimensionsList=netcdfRaster.getDimensions();
                else
                    dimensionsList=netcdfRaster.getDimensionsByName(layerDimensionsList);

                rasterNodeList=( (Element) contentLayerList.item(0)).getElementsByTagName(LAYER_VALUE_CONTENT_RASTER_NODE);
                rastersArrayIndex=new int[rasterNodeList.getLength()][dimensionsList.size()];
                rasterArray= new Raster [rasterNodeList.getLength()];
                for(i=0; i<rasterNodeList.getLength(); i++){
                    currentRaster=(Element) rasterNodeList.item(i);
                    currentRasterName=currentRaster.getAttribute(LAYER_VALUE_CONTENT_RASTER_NAME);
                    currentRasterPosition=currentRaster.getAttribute(LAYER_VALUE_CONTENT_RASTER_POSITION);
                    currentRasterPositionSplit=currentRasterPosition.split(",");
                    for(u=0; u<dimensionsList.size(); u++)
                        rastersArrayIndex[i][u]=new Integer(currentRasterPositionSplit[u]);
                    for(u=0; u<sourceNodes.getLength();u++){
                        currentSource= (Element) sourceNodes.item(u);
                        sourceName=currentSource.getAttribute(SOURCE_NAME);
                        if(sourceName.equalsIgnoreCase(currentRasterName)){
                           Class sourceRasterClass = Class.forName(RASTER_DATA_CLASSES_PACKAGE+currentSource.getAttribute(SOURCE_TYPE));
                            RasterInstance sourceRaster=(RasterInstance) sourceRasterClass.newInstance();
                            //System.out.println("TIFF " +i+": "+currentSource.getAttribute(SOURCE_URL).replaceAll("&amp;", "&"));
                            sourceRaster.setSource(new URL(currentSource.getAttribute(SOURCE_URL).replaceAll("&amp;", "&")));
                            rasterArray[i]=sourceRaster.getRaster();
                            break;
                        }
                    }
                }
                netcdfRaster.addMultiDimLayerfromDataAndRasterArray(currentNode.getAttribute(LAYER_NAME), stringType, dimensionsList, rasterArray, rastersArrayIndex, noData);


             
           // Layer Attribute Parser -- Start
              NodeList layerAttributes=((Element)layerNodes.item(j)).getElementsByTagName(LAYER_ATTRIBUTE_NODE);
              for(k=0;k<layerAttributes.getLength();k++){
                 currentAttribute=(Element) layerAttributes.item(k);
                 nodeAttributeValue=currentAttribute.getAttribute(LAYER_ATTRIBUTE_VALUE);
             
                 if(nodeAttributeValue.contains(PREFIX_SOURCE_REFERENCE+"")){
                  currentSource= (Element) sourceNodes.item(0);
                  sourceName=currentSource.getAttribute(SOURCE_NAME);
                  Class sourceRasterClass = Class.forName(RASTER_DATA_CLASSES_PACKAGE+currentSource.getAttribute(SOURCE_TYPE));
                  RasterInstance sourceRaster=(RasterInstance) sourceRasterClass.newInstance();
                  stringType=currentAttribute.getAttribute(LAYER_ATTRIBUTE_TYPE);
                    
                    informationStringValue=(String) sourceRaster.getInformation(nodeAttributeValue.split(PREFIX_ATTRIBUTE_SOURCE_REFERENCE)[1]);
         
                    netcdfRaster.addLayerAttribute(currentNode.getAttribute(LAYER_NAME),
                            currentAttribute.getAttribute(LAYER_ATTRIBUTE_NAME),
                            stringType,informationStringValue);
                 }
                 else{
                  stringType=currentAttribute.getAttribute(LAYER_ATTRIBUTE_TYPE);
                         netcdfRaster.addLayerAttribute(currentNode.getAttribute(LAYER_NAME),
                            currentAttribute.getAttribute(LAYER_ATTRIBUTE_NAME),
                            stringType,nodeAttributeValue);
                 }

              }
              // Layer Attribute Parser -- End
           }
        }
     }
}
