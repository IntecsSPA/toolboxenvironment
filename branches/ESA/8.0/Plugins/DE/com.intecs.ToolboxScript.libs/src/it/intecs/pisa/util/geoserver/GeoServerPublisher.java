/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.util.geoserver;

import it.intecs.pisa.util.rest.RestPut;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GeoServerPublisher {

    public static void publishShape(URL geoserverURL, File fileToDeploy,  String workspaceName,String publishName, String username, String password) throws MalformedURLException, Exception {
        String fullUrl;
        Hashtable<String,String> headers;

        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/datastores/"+publishName+"/file.shp";

        headers=new Hashtable<String,String>();
        headers.put("Content-type", "application/zip");

        RestPut.put(new URL(fullUrl), headers, username, password, new FileInputStream(fileToDeploy));
    }

    public static void publishTiff(URL geoserverURL, File fileToDeploy,  String workspaceName,String publishName, String username, String password) throws Exception {
        String fullUrl;
        Hashtable<String,String> headers;

        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName+"/file.geotiff";

        headers=new Hashtable<String,String>();
        headers.put("Content-type", "image/tiff");

        RestPut.put(new URL(fullUrl), headers, username, password, new FileInputStream(fileToDeploy));
    }
}
