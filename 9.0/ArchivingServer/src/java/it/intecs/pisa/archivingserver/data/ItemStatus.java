/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.data;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ItemStatus {
    public String id;
    public String downloadStatus;
    public String[] http;
    public String[] catalogues;
    public String[] openSearchCatalogues;
    public String[] sos;
    public String[] ftp;
    public String[] geoserver;
}
