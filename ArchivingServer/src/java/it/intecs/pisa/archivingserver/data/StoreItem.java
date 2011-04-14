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
 *      <publishSOS>http://...</publishSOS>
 *   </publish>
 * </storeItem>
 * @author Massimiliano Fanciulli
 */
public class StoreItem {
    public long deleteAfter=0;
    public String type;
    public String downloadUrl;
    public String metadataUrl;
    public String[] publishFtp;
    public String[] publishGeoserver;
    public String[] publishCatalogue;
    public String[] publishSOS;
    public boolean publishHttp;
}
