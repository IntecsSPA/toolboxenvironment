/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gisPlugin.tags;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.plugins.nativeTagPlugin.NativeTagExecutor;
import org.w3c.dom.Element;

/**
 *
 * @author Andrea Marongiu
 */

/*
 * <geoserverDataDelete>
 *      <geoserverURL></geoserverURL>
 *      <geoserverVersion></geoserverVersion>
 *      <geoserverUsername></geoserverUsername>
 *      <geoserverPassword></geoserverPassword>
 *      <deployName></deployName>
 *      <data type=""/> // types supported geotiff,shp,style,
 * </geoserverDataDelete>
 *
 */


/*CURL EXAMPLE

 VECTOR
 * curl -u admin:geoserver -XDELETE http://192.168.31.5:8023/geoserver/rest/layers/admin98
 * curl -u admin:geoserver -XDELETE http://192.168.31.5:8023/geoserver/rest/workspaces/acme/datastores/admin98/featuretypes/admin98
 * curl -u admin:geoserver -XDELETE http://192.168.31.5:8023/geoserver/rest/workspaces/acme/datastores/admin98


 RASTER
 * curl -u admin:geoserver -XDELETE http://192.168.31.5:8023/geoserver/rest/layers/pippo
 * curl -u admin:geoserver -XDELETE http://192.168.31.5:8023/geoserver/rest/workspaces/toolbox/coveragestores/pippo/coverages/pippo
 * curl -u admin:geoserver -XDELETE http://192.168.31.5:8023/geoserver/rest/workspaces/toolbox/coveragestores/pippo


 */

public class GeoserverDataDeleteTag extends NativeTagExecutor{

    protected static final String GEOSERVER_URL = "geoserverURL";
    protected static final String GEOSERVER_VERSION = "geoserverVersion";
    protected static final String GEOSERVER_USERNAME = "geoserverUsername";
    protected static final String GEOSERVER_PASSWORD = "geoserverPassword";
    protected static final String WORKSPACE="workspace";
    protected static final String DATA = "data";
    protected static final String DATA_TYPE = "type";
    protected static final String DEPLOY_NAME="deployName";


    @Override
    public Object executeTag(org.w3c.dom.Element geoserverDataDeploy) throws Exception{
      String deployName, deployType, geoserverURL, geoserverVersion, 
              geoserverUsername, geoserverPassword,geoserverWorkspace;

      org.w3c.dom.Element geoserverURLElement= (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_URL).item(0);
      org.w3c.dom.Element geoserverVersionElement= (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_VERSION).item(0);
      org.w3c.dom.Element geoserverUsernameElement= (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_USERNAME).item(0);
      org.w3c.dom.Element geoserverPasswordElement= (Element) geoserverDataDeploy.getElementsByTagName(GEOSERVER_PASSWORD).item(0);
      org.w3c.dom.Element workspaceElement= (Element) geoserverDataDeploy.getElementsByTagName(WORKSPACE).item(0);
      org.w3c.dom.Element dataElement= (Element) geoserverDataDeploy.getElementsByTagName(DATA).item(0);
      deployType=dataElement.getAttribute(DATA_TYPE).toLowerCase();
      org.w3c.dom.Element deployNameElement= (Element) geoserverDataDeploy.getElementsByTagName(DEPLOY_NAME).item(0);

       ToolboxConfiguration td = ToolboxConfiguration.getInstance();
        geoserverURL = geoserverURLElement != null ? geoserverURLElement.getTextContent() :
                                        td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_URL);
        geoserverVersion = geoserverVersionElement != null ? geoserverVersionElement.getTextContent() :
                                                td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_VERSION);
        geoserverWorkspace = workspaceElement != null ? workspaceElement.getTextContent() :
                                                td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_TOOLBOX_WORKSPACE);
        geoserverUsername = geoserverUsernameElement != null ? geoserverUsernameElement.getTextContent() :
                                                td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_USERNAME);
        geoserverPassword = geoserverPasswordElement != null ? geoserverPasswordElement.getTextContent() :
                                                    td.getConfigurationValue(ToolboxConfiguration.GEOSERVER_PASSWORD);


      deployName=deployNameElement!=null ? deployNameElement.getTextContent() : null;
      

    /*  String restRoot=geoserverURL+"/rest/";
      String restRequest=restRoot;
      boolean opResult=false;
      int version=Integer.parseInt(""+geoserverVersion.charAt(0));
      if(version >= 2){
          restRequest+="workspaces/"+geoserverWorkspace;
          if(deployType.equalsIgnoreCase("shp")){
             opResult=RESTService.deleteURL(
                                      new URL(restRoot+"layers/"+deployName),
                                      geoserverUsername, geoserverPassword);
             restRequest+="/datastores/"+deployName;
             opResult=opResult && RESTService.deleteURL(
                               new URL(restRequest+"/featuretypes/"+deployName),
                                      geoserverUsername, geoserverPassword);
             opResult=opResult && RESTService.deleteURL(
                               new URL(restRequest),
                                      geoserverUsername, geoserverPassword);
          }else
            if(deployType.equalsIgnoreCase("geotiff")){
              opResult=RESTService.deleteURL(
                                     new URL(restRoot+"layers/"+deployName),
                                     geoserverUsername, geoserverPassword);
              restRequest+="/coveragestores/"+deployName;
              opResult=opResult && RESTService.deleteURL(
                               new URL(restRequest+"/coverages/"+deployName),
                                      geoserverUsername, geoserverPassword);
              opResult=opResult && RESTService.deleteURL(
                               new URL(restRequest),
                                      geoserverUsername, geoserverPassword);
            }else
              if(deployType.equalsIgnoreCase("style")){
                opResult=RESTService.deleteURL(
                                     new URL(restRoot+"/styles/"+deployName),
                                     geoserverUsername, geoserverPassword);
              }  
      }else{
         restRoot+="folders/"+geoserverWorkspace+"/layers/"+deployName;
         opResult=RESTService.deleteURL(new URL(restRoot),
                                          geoserverUsername, geoserverPassword);
      }*/
      
      return false;
    }

}