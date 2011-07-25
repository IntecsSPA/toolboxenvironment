/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util.geoserver;

import it.intecs.pisa.gis.util.VectorUtil;
import it.intecs.pisa.util.rest.RestPut;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


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
}
