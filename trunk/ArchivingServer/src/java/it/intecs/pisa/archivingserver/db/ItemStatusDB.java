/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.db;

import it.intecs.pisa.archivingserver.data.ItemStatus;


/**
 *
 * @author Massimiliano Fanciulli
 */
public class ItemStatusDB {
    public static ItemStatus getStatus(String id) throws Exception
    {
       ItemStatus status;

       status=new ItemStatus();
       status.id=id;
       status.downloadStatus=DownloadsDB.getStatus(id);
       status.catalogues=SOAPCatalogueAccessible.getUrls(id);
       status.http=HttpAccessible.getUrls(id);
       status.ftp=FTPAccessible.getUrls(id);
       status.geoserver=GeoServerAccessible.getUrls(id);
       return status;
    }
}
