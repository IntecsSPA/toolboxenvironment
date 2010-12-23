/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.db;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class FTPAccessible extends Accessibles{
    public static void add(String itemId,String url) throws Exception
    {
        add(itemId,url,"FTP");
    }

    public static String[] getUrls(String itemId) throws Exception
    {
        return getUrls(itemId,"FTP");
    }

    public static void delete(String itemId) throws Exception
    {
        delete(itemId,"FTP");
    }

    public static String[] getStatus(String itemId,String type) throws Exception
    {
        return getStatus(itemId,"FTP");
    }
}
