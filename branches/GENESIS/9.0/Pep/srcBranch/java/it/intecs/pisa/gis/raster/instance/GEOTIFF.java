

package it.intecs.pisa.gis.raster.instance;

import java.awt.image.Raster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import org.geotools.coverage.grid.GridCoverage2D;

import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.geotiff.adapters.GeoTiffIIOMetadataDecoder;
import org.opengis.geometry.DirectPosition;


/**
 *
 * @author Andrea Marongiu
 */

/*Aggiungere parte in scrittura*/
public class GEOTIFF implements RasterInstance{
    private GridCoverage2D geoTiffCoverage;
    private Hashtable geoTiffInformation;
    private GeoTiffIIOMetadataDecoder metadata;

    public GEOTIFF(){
        this.geoTiffCoverage=null;
        this.geoTiffInformation=null;
    }

    public GEOTIFF (String geotiffPath) throws FileNotFoundException, IOException{
       FileInputStream geoTiffFile = new FileInputStream(geotiffPath);
       GeoTiffReader geoTiffReader= new GeoTiffReader(geoTiffFile.getChannel(),
               new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));
       this.geoTiffCoverage = (GridCoverage2D) geoTiffReader.read(null);
       this.metadata=geoTiffReader.getMetadata();
       geoTiffReader.dispose();
       geoTiffFile.close();

     
    }



    public Raster getRaster(){
        Raster r=this.geoTiffCoverage.getRenderedImage().getData();
        return r;
    }

    public void getLatitudeArray(){
         // Da implementare
    }

    
    private void readInformation (){
        this.geoTiffInformation = new Hashtable();
        int geotTiffDimensions=this.geoTiffCoverage.getEnvelope().getDimension();
        DirectPosition lowerCorner=this.geoTiffCoverage.getEnvelope().getLowerCorner();
        DirectPosition upperCorner=this.geoTiffCoverage.getEnvelope().getUpperCorner();
        String geoTiffEPSG=this.geoTiffCoverage.getCoordinateReferenceSystem2D().getCoordinateSystem().getName().getCodeSpace();
        String envelopeEPSG=this.geoTiffCoverage.getEnvelope().getCoordinateReferenceSystem().getCoordinateSystem().getName().getCodeSpace();

         this.geoTiffInformation.put("dimensions", geotTiffDimensions);
         this.geoTiffInformation.put("width", this.geoTiffCoverage.getRenderedImage().getWidth());
         this.geoTiffInformation.put("height", this.geoTiffCoverage.getRenderedImage().getHeight());
         this.geoTiffInformation.put("EPSG", geoTiffEPSG);
         this.geoTiffInformation.put("bboxWest", lowerCorner.getCoordinate()[0]);
         this.geoTiffInformation.put("bboxEast", upperCorner.getCoordinate()[0]);
         this.geoTiffInformation.put("bboxNorth", upperCorner.getCoordinate()[1]);
         this.geoTiffInformation.put("bboxSouth", lowerCorner.getCoordinate()[1]);
         this.geoTiffInformation.put("bboxEPSG", envelopeEPSG);
         
         for(int i=2; i<geotTiffDimensions;i++){
            this.geoTiffInformation.put("lowDim"+i, lowerCorner.getCoordinate()[i]);
            this.geoTiffInformation.put("uppDim"+i, upperCorner.getCoordinate()[i]);
         }
         this.geoTiffInformation.put("lat", this.arrayDoubleToString(this.getLatArray()));
         this.geoTiffInformation.put("lon", this.arrayDoubleToString(this.getLonArray()));
         if(this.metadata!= null){
         this.geoTiffInformation.put("noData", ""+this.metadata.getNoData());
         }
         
    }

    
    public double [] getLatArray(){
      /*if(this.geoTiffInformation == null)
            this.readInformation();*/
      int height=(Integer)this.geoTiffInformation.get("height");
      double latArray[]= new double[height];
     // double firstValueLat=(Double) this.geoTiffInformation.get("bboxNorth");
      double firstValueLat=(Double) this.geoTiffInformation.get("bboxSouth");
      double dimLat=(Double) this.geoTiffInformation.get("bboxNorth")-
                    firstValueLat;
      double resY=dimLat/height;
      int i;
      for(i=0; i<height; i++ )
          latArray[i]=firstValueLat+resY*i;
      return latArray;
    }

   public double [] getLonArray(){
      /*if(this.geoTiffInformation == null)
            this.readInformation();*/
      int width=(Integer) this.geoTiffInformation.get("width");
      double lonArray[]= new double[width];
      double firstValueLon=(Double) this.geoTiffInformation.get("bboxWest");
      double dimLon=(Double) this.geoTiffInformation.get("bboxEast")-
                    firstValueLon;
      double resX=dimLon/width;
      int i;
      for(i=0; i<width; i++ )
         lonArray[i]=firstValueLon+resX*i;
      return lonArray;
    }

    /*Information Reference supported:
     *   - dimensions (geoTiff dimensions Number)
     *   - width (geoTiff witdh in pixel Number)
     *   - heigth (geoTiff heigth in pixel Number)
     *   - EPSG (geoTiff EPSG)
     *   - bboxWest (west envelope coordinate value)
     *   - bboxEast (east envelope coordinate value)
     *   - bboxNorth (north envelope coordinate value)
     *   - bboxSouth (south envelope coordinate value)
     *   - bboxEPSG (envelope EPSG)
     *   - [lowDim2...lowDim<n>] (lower envelope dimension value for each additional dimension (for dimensions>2))
     *   - [uppDim2...uppDim<n>] (upper envelope dimension value for each additional dimension (for dimensions>2))
     *   - lat
     *   - lon
     *   - nodata
     */
    public Object getInformation(String informationReference){
        if(this.geoTiffInformation == null)
            this.readInformation();
        return this.geoTiffInformation.get(informationReference);

    }

  /*  public void setSource(URL sourceURL) throws Exception {
        GeoTiffReader geoTiffReader=null;
        try{
         geoTiffReader= new GeoTiffReader(sourceURL.openStream());
        }catch (Throwable t){
            t.printStackTrace();
        }
        this.geoTiffCoverage = (GridCoverage2D) geoTiffReader.read(null);

    }*/

    public void setSource(URL sourceURL) throws Exception {
            File temp = File.createTempFile(""+sourceURL.getPath().hashCode(), ".tmp");
            GeoTiffReader geoTiffReader=null;
            try{
                InputStream is=sourceURL.openStream();
                copy(is, new FileOutputStream(temp));
                is.close();
            }catch (Throwable t){
                System.out.println("GeoTiff URL: " +sourceURL.toString());
                t.printStackTrace(System.out);
                throw new Exception();
            }
            
           // FileOuputStream tempOut=sourceURL.openStream();
        try{
         geoTiffReader= new GeoTiffReader(temp);
         this.geoTiffCoverage = (GridCoverage2D) geoTiffReader.read(null);
         this.metadata=geoTiffReader.getMetadata();
        }catch (Throwable t){
            System.out.println("GeoTiff URL: " +sourceURL.toString());
            t.printStackTrace(System.out);
            throw new Exception();
        }
        
        geoTiffReader.dispose();
        temp.delete();
        
    }


    

       /**
     * This method copy the data from an InputStream to an OtputStream
     * @param in InputStream
     * @param out OutputStream
     */
    private static void copy(InputStream in, OutputStream out) throws IOException {
        synchronized (in) {
            synchronized (out) {

                byte[] buffer = new byte[256];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1) {
                        break;
                    }
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    private String arrayDoubleToString(double[] array){
        int i;
        String toStringRes="";
        for(i=0;i<array.length; i++)
            toStringRes+=array[i]+",";

        return toStringRes.substring(0,toStringRes.length()-1);
    }

    public void setDimensionAttribute(String dimensionName, int length) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addLayer(String layerName, String layerType, ArrayList dimensions) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLayerValue(String layerName, String layerType, String value) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addLayerAttribute(String layerName, String attributeName, Object attributeValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addLayerAttribute(String variableName, String attributeName, String attributeType, String attributeValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void add2DLayerfromRaster(String layerName, ArrayList dimensions, String layerType, Raster rasterData) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addMultiDimLayerfromDataAndRasterArray(String layerName, String layerType, ArrayList dimensions, Raster[] rastersData, int[][] rasterDataPositions, String noData) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addGlobalInformation(String attributeName, String attributeType, String attributeValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addGlobalInformation(String attributeName, Object attributeValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ArrayList getDimensions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ArrayList getDimensionsByName(String dimensionListString) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    

   

}
