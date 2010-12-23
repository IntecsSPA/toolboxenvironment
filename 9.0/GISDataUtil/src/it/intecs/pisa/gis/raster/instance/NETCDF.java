
package it.intecs.pisa.gis.raster.instance;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import ucar.ma2.Array;
import ucar.ma2.DataType;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Attribute;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayObject;
import ucar.ma2.ArrayShort;
import ucar.ma2.Index;
import ucar.ma2.IndexIterator;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFileWriteable;
import ucar.nc2.Variable;


/**
 *
 * @author Andrea Marongiu
 */
public class NETCDF implements RasterInstance {

   private NetcdfFileWriteable ncfile=null;
   private String netCDFPathFile=null;
   private ArrayList currentlyDim=new ArrayList();
   private ArrayList writeLayersData=new ArrayList();
   private ArrayList writeLayersName=new ArrayList();

   public NETCDF(String ncPathFile, boolean fillNetCDF) {
       this.netCDFPathFile=ncPathFile;
        try {
            this.ncfile = NetcdfFileWriteable.createNew(ncPathFile);
            
        } catch (IOException ex) {
            Logger.getLogger(NETCDF.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

   public void setDimensionAttribute(String dimensionName, int length){
        Dimension temp=this.ncfile.addDimension(dimensionName, length);
        this.currentlyDim.add(temp);
    }

   public void addLayer(String layerName, String layerType , ArrayList dimensions) throws IOException{
        DataType dataType=getVariableDataTypeFromStringLayerType(layerType);
        this.addVariable(layerName, dataType, dimensions);
    }

   public void setLayerValue(String layerName, String layerType, String value) throws IOException, InvalidRangeException, Exception {
        Array arrayValue=null;
        int dimensions[]=this.getVariabileDimension(layerName);
        //arrayValue=new ArrayObject(getClassForType(layerType),dimensions);

        if(dimensions.length <=2)
            arrayValue=this.copyBasicTypesStringTo2DArray(value, layerType, dimensions);
        else
            throw new IOException("Not yet implemented for dimensions > 2.");
        this.writeLayersData.add(arrayValue);
        this.writeLayersName.add(layerName);

    }

   public void addLayerAttribute (String variableName, String attributeName, Object attributeValue){
        Attribute newAttribute=null;
        if(attributeValue instanceof String)
            newAttribute= new Attribute(attributeName,(String)attributeValue);
        if(attributeValue instanceof Attribute)
            newAttribute= new Attribute(attributeName,(Attribute)attributeValue);
        if(attributeValue instanceof List)
            newAttribute= new Attribute(attributeName,(List)attributeValue);
        if(attributeValue instanceof Array)
            newAttribute= new Attribute(attributeName,(Array)attributeValue);
        if(attributeValue instanceof Number) {
            newAttribute = new Attribute(attributeName, (Number) attributeValue);
        }
        this.ncfile.addVariableAttribute(variableName, newAttribute);
    }

   public void addLayerAttribute (String variableName, String attributeName, String attributeType, String attributeValue){
        Attribute newAttribute=this.getNewAttributeByStringType(attributeName, attributeType, attributeValue);
        this.ncfile.addVariableAttribute(variableName, newAttribute);
    }


   public void addGlobalInformation(String attributeName, Object attributeValue){
        Attribute newAttribute=null;
        if(attributeValue instanceof String)
            newAttribute= new Attribute(attributeName,(String)attributeValue);
        if(attributeValue instanceof Attribute)
            newAttribute= new Attribute(attributeName,(Attribute)attributeValue);
        if(attributeValue instanceof List)
            newAttribute= new Attribute(attributeName,(List)attributeValue);
        if(attributeValue instanceof Array)
            newAttribute= new Attribute(attributeName,(Array)attributeValue);
        if(attributeValue instanceof Number) {
            newAttribute = new Attribute(attributeName, (Number) attributeValue);
        }
        this.ncfile.addGlobalAttribute(newAttribute);
    }

   public void addGlobalInformation (String attributeName, String attributeType, String attributeValue){
        Attribute newAttribute=this.getNewAttributeByStringType(attributeName, attributeType, attributeValue);
        this.ncfile.addGlobalAttribute(newAttribute);
    }

   public void add2DLayerfromRaster(String variableName, ArrayList dimensions, String layerType, Raster rasterData) throws FileNotFoundException, IOException, InvalidRangeException, ClassNotFoundException{
         DataType variableDataType=null;
         DataBuffer rasterDataBuffer=rasterData.getDataBuffer();
         Array dataArray=null;
         int i;
         int[] raster2D = new int[2];
         for (i=0; i<dimensions.size(); i++)
              raster2D[i] = ((Dimension)dimensions.get(i)).getLength();
     
             int dataType=rasterDataBuffer.getDataType();
             switch(dataType){
               case DataBuffer.TYPE_BYTE:
                   dataArray=new ArrayInt(raster2D);
                   variableDataType=DataType.INT;
                 break;
               case DataBuffer.TYPE_DOUBLE:
                   dataArray=new ArrayDouble(raster2D);
                   variableDataType=DataType.DOUBLE;
                 break;
               case DataBuffer.TYPE_FLOAT:
                    dataArray=new ArrayFloat(raster2D);
                    variableDataType=DataType.FLOAT;
                 break;
               case DataBuffer.TYPE_INT:
                    dataArray=new ArrayInt(raster2D);
                    variableDataType=DataType.INT;
                 break;
               case DataBuffer.TYPE_SHORT:
                    dataArray=new ArrayShort(raster2D);
                    variableDataType=DataType.SHORT;
                 break;
               case DataBuffer.TYPE_UNDEFINED:
                    dataArray=new ArrayDouble(raster2D);
                    variableDataType=DataType.DOUBLE;
                 break;
               case DataBuffer.TYPE_USHORT:
                    dataArray=new ArrayInt(raster2D);
                    variableDataType=DataType.INT;
                 break;
             }


         this.addVariable(variableName, variableDataType, dimensions);
         this.copyDataBufferTo2DArray(rasterDataBuffer, dataArray, raster2D);
         this.writeLayersData.add(dataArray);
          this.writeLayersName.add(variableName);
    }

    public void addMultiDimLayerfromDataAndRasterArray(String layerName, String layerType, ArrayList dimensions, Raster[] rastersData, int[][] rasterDataPositions, String noData) throws FileNotFoundException, IOException, InvalidRangeException, ClassNotFoundException, Exception {
      Array dataArray=null;
      Array noDataArray=null;
      int i;
      int[] rasterMultiD = new int[dimensions.size()];
      boolean t = rasterMultiD instanceof int[];

      for (i=0; i<dimensions.size(); i++)
              rasterMultiD[i] = ((Dimension)dimensions.get(i)).getLength();
       Class dataArrayClass=this.getClassArrayForType(layerType);
       dataArray=(Array) dataArrayClass.getConstructors()[0].newInstance(rasterMultiD);
       ToolboxConfiguration tc=ToolboxConfiguration.getInstance();
       

       for (i=0; i< rastersData.length; i++){
            this.copyRasterToMultiDArray(rastersData[i], layerType, dataArray, rasterMultiD, rasterDataPositions[i], noData);
       }

       this.addVariable(layerName, this.getVariableDataTypeFromStringLayerType(layerType), dimensions);
       this.writeLayersData.add(dataArray);
       this.writeLayersName.add(layerName);
    }

   public ArrayList getDimensions(){
     return this.currentlyDim;
   }

   public Object getInformation(String informationReference) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Raster getRaster() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSource(URL sourceURL) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    public void create () throws IOException, InvalidRangeException{
        this.ncfile.create();
        ToolboxConfiguration tc=ToolboxConfiguration.getInstance();
        for(int i=0; i<this.writeLayersName.size(); i++){
            String logLevel=tc.getConfigurationValue(ToolboxConfiguration.LOG_LEVEL);
            if(logLevel.equalsIgnoreCase("DEBUG"))
                System.out.println("LAYER: " + i);
            this.ncfile.write((String)this.writeLayersName.get(i), (Array)this.writeLayersData.get(i));
        }

        this.ncfile.close();
    }

    public ArrayList getDimensionsByName(String dimensionListString) {
      ArrayList dimensions= new ArrayList();
      int i,u;

      String arrayDimString[]=dimensionListString.split(" ");
      if(arrayDimString[0].contains(","))
        arrayDimString=dimensionListString.split(",");
      for (u=0; u<arrayDimString.length; u++)
          for(i=0; i<this.currentlyDim.size(); i++)
             if(arrayDimString[u].equals(((Dimension)this.currentlyDim.get(i)).getName()))
                dimensions.add(u,this.currentlyDim.get(i));
      return dimensions;
    }

   private void copyDataBufferTo2DArray(DataBuffer db, Array dataArray, int[] arrayDimensions){
    int i,x,y;
    Index dataArrayIndex;
    dataArrayIndex=dataArray.getIndex();
    for(i=0; i<db.getSize(); i++){
      x= i%arrayDimensions[1];
      y= i/arrayDimensions[1];
      if(y<arrayDimensions[0])
         dataArray.setObject(dataArrayIndex.set(y, x),db.getElem(i));
      else
         break;
    }

   }

   private void copyRasterToMultiDArray(Raster raster, String layerType, Array dataArray, int[] arrayDimensions, int[] rangeIndex, String noData) throws Exception{
    int i,u,x,y;
    Index dataArrayIndex;
    int indexDataBuffer[]=new int [2];
    int indexArray[]= new int[arrayDimensions.length];
    x=0;
    for(i=0; i<arrayDimensions.length; i++){
        if(rangeIndex[i] != -1)
           indexArray[i]=rangeIndex[i];
        else{
          indexDataBuffer[x]=i;
          x++;
        }
    }

    Class typeClass=this.getClassForType(layerType);

  /*  if(noData != null){
         IndexIterator iter = dataArray.getIndexIterator();
         while (iter.hasNext()) {
            iter.setObjectCurrent(typeClass.getConstructor(String.class).newInstance(noData));
        }
    }*/

    dataArrayIndex=dataArray.getIndex();
    //for(i=0; i<db.getSize(); i++){
    for(i=0; i<(arrayDimensions[indexDataBuffer[1]]); i++)
       for(u=0; u<(arrayDimensions[indexDataBuffer[0]]); u++){
           x= i;
           y= (arrayDimensions[indexDataBuffer[0]])-u;
           indexArray[indexDataBuffer[0]]=y-1;
           indexArray[indexDataBuffer[1]]=x;
           dataArray.setObject(dataArrayIndex.set(indexArray), typeClass.getConstructor(String.class).newInstance(""+raster.getSampleDouble(i, u, 0))/*typeClass.getConstructor(String.class).newInstance(""+db.getElem(i))*/);

    }

  /*    x= (arrayDimensions[indexDataBuffer[1]]-1)-i%arrayDimensions[indexDataBuffer[1]];
      y= (arrayDimensions[indexDataBuffer[0]]-1)-i/arrayDimensions[indexDataBuffer[1]];*/

    /*  if(y>0){
        indexArray[indexDataBuffer[0]]=y;
        indexArray[indexDataBuffer[1]]=x;
        dataArray.setObject(dataArrayIndex.set(indexArray), typeClass.getConstructor(String.class).newInstance(""+raster.getSampleDouble(i, i, i))typeClass.getConstructor(String.class).newInstance(""+db.getElem(i)));
      }
      else
         break;
    }*/

   }


   private Array copyBasicTypesStringTo2DArray(String basicTypesString, String layerType, int[] arrayDimensions) throws Exception{
    int i,x,y;

    Array dataArray=null;
    if(layerType.equalsIgnoreCase("int"))
          dataArray=new ArrayInt(arrayDimensions);
      else if(layerType.equalsIgnoreCase("short"))
          dataArray=new ArrayShort(arrayDimensions);
      else if(layerType.equalsIgnoreCase("float"))
           dataArray=new ArrayFloat(arrayDimensions);
      else if(layerType.equalsIgnoreCase("double"))
           dataArray=new ArrayDouble(arrayDimensions);
      else if(layerType.equalsIgnoreCase("String"))
           dataArray=new ArrayObject(this.getClassForType(layerType),arrayDimensions);
    Index dataArrayIndex=dataArray.getIndex();
    String splitBTS[]=basicTypesString.split(",");
    if(arrayDimensions.length == 2)
        for(i=0; i<splitBTS.length; i++){
          x= i%arrayDimensions[0];
          y= i/arrayDimensions[0];
          if(y<arrayDimensions[1])
              if(layerType.equalsIgnoreCase("int"))
               dataArray.setInt(dataArrayIndex.set(x, y), new Integer(splitBTS[i]));
                else if(layerType.equalsIgnoreCase("short"))
                        dataArray.setObject(dataArrayIndex.set(x, y), new Short(splitBTS[i]));
                else if(layerType.equalsIgnoreCase("float"))
                        dataArray.setObject(dataArrayIndex.set(x, y), new Integer(splitBTS[i]).floatValue());
                else if(layerType.equalsIgnoreCase("double"))
                        dataArray.setObject(dataArrayIndex.set(x, y), new Double(splitBTS[i]));
                else if(layerType.equalsIgnoreCase("String"))
                        dataArray.setObject(dataArrayIndex.set(x, y), splitBTS[i]);
          else
             break;
        }
    else
       for(i=0; i<splitBTS.length; i++){
           if(layerType.equalsIgnoreCase("int"))
              dataArray.setInt(dataArrayIndex.set(i), new Integer(splitBTS[i]));
               else if(layerType.equalsIgnoreCase("short"))
                  dataArray.setObject(dataArrayIndex.set(i), new Short(splitBTS[i]));
               else if(layerType.equalsIgnoreCase("float"))
                  dataArray.setObject(dataArrayIndex.set(i), new Float(splitBTS[i]));
               else if(layerType.equalsIgnoreCase("double"))
                  dataArray.setObject(dataArrayIndex.set(i), new Double(splitBTS[i]));
               else if(layerType.equalsIgnoreCase("String"))
                  dataArray.setObject(dataArrayIndex.set(i), splitBTS[i]);

       }
    return dataArray;
   }

 


   

    private void addVariable(String variableName, DataType dataType , ArrayList dimensions){

        this.ncfile.addVariable(variableName, dataType, dimensions);

    }

   

    private DataType getVariableDataTypeFromStringLayerType (String layerType){
        DataType type=null;
        if (layerType.equalsIgnoreCase("INT"))
            type=DataType.INT;
        else if(layerType.equalsIgnoreCase("BOOLEAN"))
            type=DataType.BOOLEAN;
        else if(layerType.equalsIgnoreCase("BYTE"))
            type=DataType.BYTE;
        else if(layerType.equalsIgnoreCase("CHAR"))
            type=DataType.CHAR;
        else if(layerType.equalsIgnoreCase("DOUBLE"))
            type=DataType.DOUBLE;
        else if(layerType.equalsIgnoreCase("FLOAT"))
            type=DataType.FLOAT;
        else if(layerType.equalsIgnoreCase("LONG"))
            type=DataType.LONG;
        else if(layerType.equalsIgnoreCase("SHORT"))
            type=DataType.SHORT;
        
     return type;
    }



 private int[] getVariabileDimension(String varibleName) throws IOException{
 this.ncfile.create();
    List <Variable> listV=this.ncfile.getVariables();
    List <Dimension> listD=null;
    for(int i=0; i<listV.size();i++){
        if(listV.get(i).getName().equals(varibleName)){
            listD=listV.get(i).getDimensions();
            int dim[]=new int[listD.size()];
            for(i=0;i<listD.size();i++){
                dim[i]=listD.get(i).getLength();
            }
            this.ncfile.setRedefineMode(true);
            return dim;
        }
    }
    return null;
 }



private Class getClassForType (String type) throws ClassNotFoundException{
    String classForType="java.lang.";
    String uppCase=type.toUpperCase();
    String lowCase=type.toLowerCase();
    classForType+=uppCase.charAt(0)+lowCase.substring(1);
    return Class.forName(classForType);
}


private Class getClassArrayForType (String type) throws ClassNotFoundException{
    String classForType="ucar.ma2.Array";
    String uppCase=type.toUpperCase();
    String lowCase=type.toLowerCase();
    classForType+=uppCase.charAt(0)+lowCase.substring(1);
    return Class.forName(classForType);
}

private Attribute getNewAttributeByStringType (String attributeName, String attributeType, String attributeValue){
    Attribute newAttribute=null;
    String type=attributeType.toUpperCase();
        if (type.equalsIgnoreCase("INT"))
                  newAttribute= new Attribute(attributeName, new Integer(attributeValue));
        else if (type.equalsIgnoreCase("FLOAT"))
                  newAttribute= new Attribute(attributeName, new Float(attributeValue));
        else if (type.equalsIgnoreCase("DOUBLE"))
                  newAttribute= new Attribute(attributeName, new Double(attributeValue));
        else if (type.equalsIgnoreCase("LONG"))
                  newAttribute= new Attribute(attributeName, new Long(attributeValue));
        else if (type.equalsIgnoreCase("SHORT"))
                  newAttribute= new Attribute(attributeName, new Short(attributeValue));
        else if (type.equalsIgnoreCase("STRING"))
                  newAttribute= new Attribute(attributeName, attributeValue);
  return newAttribute;
}


    







}
