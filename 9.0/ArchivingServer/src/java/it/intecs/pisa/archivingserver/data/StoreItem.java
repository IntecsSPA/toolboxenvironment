/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.data;

/**
 * This class stores all information available in the store input message.
 *
 * <storeItem>
 *   <downloadUrl>http://....</downloadUrl>
 *   <metadataUrl>http://....</metadataUrl>
 *   <publish>
 *      <publishFtp>ftp://..</publishFtp>
 *      <publishGeoServer></publishGeoServer>
 *      <publishCatalogue>http://...</publishCatalogue>
 *   </publish>
 * </storeItem>
 * @author Massimiliano Fanciulli
 */
public class StoreItem {
    public long deleteAfter=0;
    public String type;
    public String idString;
    public boolean hidden;
    public String downloadUrl;
    public String metadataUrl;
    public String[] publishFtp;
    public String[] publishGeoserver;
    public String geoserverType;
    public String geoserverWorkspace;
    public String[] publishCatalogue;
    public String[] publishSOS;
    public boolean publishHttp;

    public String[] notifyURL;
    public String[] notifyTopic;
    public String[] notifyEventType;


    /*
     this.idString="";
    this.watchFolder= null;  
    this.deleteAfter= 50000000000000;
    this.hidden= false;
    this.type= null;
    this.downloadUrl= "";
    this.metadataUrl= "";
    this.publishFtp= new Array();
    this.publishGeoserver= new Array();
    this.geoserverType= "";
    this.geoserverUser= "";
    this.geoserverPassword= "";
    this.publishCatalogue= new Array();
    this.publishSOS= new Array();
    this.publishHttp= null;
     */
}
