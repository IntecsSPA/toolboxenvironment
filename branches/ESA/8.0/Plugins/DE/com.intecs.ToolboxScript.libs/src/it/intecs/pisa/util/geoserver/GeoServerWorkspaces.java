/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.util.geoserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.intecs.pisa.util.rest.RestGet;
import it.intecs.pisa.util.rest.RestPost;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GeoServerWorkspaces {
    public static String[] list(URL rest_URL, String user, String password) throws Exception
    {
        Vector<String> workspacesArray;
        String fullUrl;

        fullUrl=rest_URL.toString()+"/rest/workspaces.json";

        Hashtable<String,String> headers;
        headers=new Hashtable<String,String>();
        headers.put("Accept", "text/json");
        headers.put("Content-type", "application/xml");

        JsonObject listing = RestGet.getAsJSON(new URL(fullUrl), headers,user, password);
        JsonObject workspaces = listing.get("workspaces").getAsJsonObject();
        JsonArray array = workspaces.getAsJsonArray("workspace");

        workspacesArray=new Vector<String>();
        Iterator<JsonElement> iter=array.iterator();
        while(iter.hasNext())
        {
            JsonElement el = iter.next();
            workspacesArray.add(el.getAsJsonObject().get("name").getAsString());
        }

        return workspacesArray.toArray(new String[0]);
    }

    /**
     * This method creates a new workspace if not already available
     * @param rest_URL
     * @param workspacename
     * @param user
     * @param password
     * @return
     * @throws Exception
     */
    public static boolean create(URL rest_URL,String workspacename, String user, String password) throws Exception
    {
        boolean isWorkspaceCreated=false;

        String[] listing = GeoServerWorkspaces.list(rest_URL, user, password);
        for(String workspace:listing)
        {
            if(workspace.equals(workspacename))
                isWorkspaceCreated=true;
        }

        if(isWorkspaceCreated==false)
        {
            String fullUrl;

            fullUrl=rest_URL.toString()+"/rest/workspaces";

            Hashtable<String,String> headers;
            headers=new Hashtable<String,String>();
            headers.put("Accept", "text/json");
            headers.put("Content-type", "text/json");

            JsonObject workspacejson=new JsonObject();
            JsonObject workjson=new JsonObject();
            workspacejson.add("workspace", workjson);
            workjson.addProperty("name", workspacename);

            JsonObject response=RestPost.postAsJSON(new URL(fullUrl), headers, user, password, workspacejson);
        }
        return true;
    }
}
