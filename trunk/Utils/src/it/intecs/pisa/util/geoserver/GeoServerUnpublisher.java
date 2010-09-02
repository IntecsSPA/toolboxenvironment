/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.geoserver;

import it.intecs.pisa.util.rest.RestDelete;
import java.net.URL;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GeoServerUnpublisher {
    public static void unpublishCoverage(URL geoserverURL, String workspaceName,String publishName, String username, String password) throws Exception {
        String fullUrl;

        fullUrl=geoserverURL.toString()+"/rest/layers/"+publishName;

        int result = RestDelete.del(new URL(fullUrl), null, username, password);
        System.out.println(result);

        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName+"/coverages/"+publishName;

        result = RestDelete.del(new URL(fullUrl), null, username, password);
        System.out.println(result);

        fullUrl=geoserverURL.toString()+"/rest/workspaces/"+workspaceName+"/coveragestores/"+publishName; 

        result = RestDelete.del(new URL(fullUrl), null, username, password);
        System.out.println(result);

    }
}
