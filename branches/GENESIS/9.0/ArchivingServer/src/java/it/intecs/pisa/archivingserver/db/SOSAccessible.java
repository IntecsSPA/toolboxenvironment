
package it.intecs.pisa.archivingserver.db;

/**
 *
 * @author maro
 */
public class SOSAccessible extends Accessibles{
    
    public static void add(String itemId,String url) throws Exception
    {
        add(itemId,url,"SOS");
    }

    public static String[] getUrls(String itemId) throws Exception
    {
        return getUrls(itemId,"SOS");
    }

    public static void delete(String itemId) throws Exception
    {
        delete(itemId,"SOS");
    }

    public static String[] getStatus(String itemId,String type) throws Exception
    {
        return getStatus(itemId,"SOS");
    }
    
}
