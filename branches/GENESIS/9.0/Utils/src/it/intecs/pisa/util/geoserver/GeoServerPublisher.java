/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util.geoserver;

import com.google.gson.JsonObject;
import it.intecs.pisa.gis.util.VectorUtil;
import it.intecs.pisa.util.rest.RestPost;
import it.intecs.pisa.util.rest.RestPut;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;


/**
 *
 * @author Massimiliano Fanciulli
 */
public class GeoServerPublisher {
    

    public static void publishShape(URL geoserverURL, File fileToDeploy,  String workspaceName,String publishName, String username, String password) throws MalformedURLException, Exception {
        String fullUrl;
        HashMap<String,String> headers;
        InputStream reponseStream;
        String zipFilePath;

        zipFilePath = VectorUtil.createSHPZipDeployName(fileToDeploy.getAbsolutePath(), publishName);

        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/datastores/"+publishName+"/file.shp";

        headers=new HashMap<String,String>();
        headers.put("Content-type", "application/zip");
        headers.put("Accept", "text/json");

        reponseStream=RestPut.put(new URL(fullUrl), headers, username, password, new FileInputStream(new File(zipFilePath)));
    }

    public static void publishTiff(URL geoserverURL, File fileToDeploy,  String workspaceName,String publishName, String username, String password) throws Exception {
        String fullUrl;
        HashMap<String,String> headers;
        InputStream reponseStream;

        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName+"/file.geotiff";

        headers=new HashMap<String,String>();
        headers.put("Content-type", "image/tiff");
        headers.put("Accept", "text/json");
        headers.put("configure","none");
        headers.put("coverageName",publishName);

        reponseStream=RestPut.put(new URL(fullUrl), headers, username, password, new FileInputStream(fileToDeploy));
    }
    
    /**
    *
    * @author Andrea Marongiu
    */
    
    public static void publishImageMosaic(URL geoserverURL, File fileToDeploy,  
            String workspaceName,String publishName, String username, 
            String password, String[] dimension, String styleName) throws Exception {
        String fullUrl;
        HashMap<String,String> headers;
        Hashtable<String,String> headersHT;
        InputStream reponseStream;

        // 1. Create Layer
        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName+"_store/file.imagemosaic?coverageName="+publishName;
        headers=new HashMap<String,String>();
        headers.put("Content-type", "application/zip");
        headers.put("Accept", "text/json");
        headers.put("configure","none");
        headers.put("coverageName",publishName);
        reponseStream=RestPut.put(new URL(fullUrl), headers, username, password, new FileInputStream(fileToDeploy));
        
        // 2. Enable dimension
        if(dimension.length > 0){
            fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName+"_store/coverages/"+publishName+".xml";
            headers=new HashMap<String,String>();
            headers.put("Content-type", "text/xml");
            headers.put("Accept", "text/json");
            headers.put("configure","none");
            reponseStream=RestPut.put(new URL(fullUrl), headers, username, 
                password, new ByteArrayInputStream(getStringDimensionDocument(dimension).getBytes()));
        }
        
        //3. Enable the coverage 
        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName+"_store/coverages/"+publishName+".xml";
        headers=new HashMap<String,String>();
        headers.put("Content-type", "text/xml");
        headers.put("Accept", "text/json");
        headers.put("configure","none");
        reponseStream=RestPut.put(new URL(fullUrl), headers, username, 
                password, new ByteArrayInputStream(getStringEnableCoverageDocument().getBytes()));
        
        //4. Make default style 
        fullUrl=geoserverURL.toString()+"/rest/layers/"+workspaceName+":"+publishName; 
        headers=new HashMap<String,String>();
        headers.put("Content-type", "text/xml");
        headers.put("Accept", "text/json");
        headers.put("configure","none");
        reponseStream=RestPut.put(new URL(fullUrl), headers, username, 
                password, new ByteArrayInputStream(getStringDefaultStyleDocument(styleName).getBytes()));
        
        
        //5. Enable the layer
        fullUrl=geoserverURL.toString()+"/rest/layers/"+workspaceName+":"+publishName; 
        headers=new HashMap<String,String>();
        headers.put("Content-type", "text/xml");
        headers.put("Accept", "text/json");
        headers.put("configure","none");
        reponseStream=RestPut.put(new URL(fullUrl), headers, username, 
                password, new ByteArrayInputStream(getStringEnableLayerDocument().getBytes()));
        
        //6. Reload the GeoServer configuration
        fullUrl=geoserverURL.toString()+"/rest/reload"; 
        headersHT=new Hashtable<String,String>();
        headers.put("Content-type", "text/xml");
        headers.put("Accept", "text/json");
        headers.put("configure","none");
        JsonObject postMess=null;
        RestPost.postAsJSON(new URL(fullUrl), headersHT, username, 
                password, postMess);
        
        //TODO: Aggiungere controlli 
    }
    
    
    
    //TODO: Usare DOM
    private static String getStringDimensionDocument(String dimString[]){
        String docDim="";
        docDim="<coverage><metadata>";
        
        for(int i=0; i<dimString.length; i++){
            docDim+="<entry key=\""+dimString[i]+"\"><dimensionInfo><enabled>true</enabled><presentation>LIST</presentation></dimensionInfo></entry>";
        }
        docDim+="</metadata></coverage>";
    
        return docDim;
    }
    
    private static String getStringEnableCoverageDocument(){
        String docEnCov="<coverage><enabled>true</enabled></coverage>";
        return docEnCov;
    }
    
    private static String getStringEnableLayerDocument(){
        String docEnLayer="<layer><enabled>true</enabled></layer>";
        return docEnLayer;
    }
    
    
    private static String getStringDefaultStyleDocument(String style){
        String docDefStyle="<layer><defaultStyle><name>"+style+"</name></defaultStyle></layer>";
        return docDefStyle;
    }
}
