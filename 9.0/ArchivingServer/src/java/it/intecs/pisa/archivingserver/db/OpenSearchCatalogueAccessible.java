package it.intecs.pisa.archivingserver.db;

import static it.intecs.pisa.archivingserver.db.Accessibles.add;
import static it.intecs.pisa.archivingserver.db.Accessibles.delete;
import static it.intecs.pisa.archivingserver.db.Accessibles.getUrls;

/**
 *
 * @author Andrea Marongiu
 */
public class OpenSearchCatalogueAccessible extends Accessibles {

    public static void add(String itemId, String url) throws Exception {
        add(itemId, url, "OPENSEARCHCAT");
    }

    public static void addDelUrl(String itemId, String deleteUrl) throws Exception {
        add(itemId, deleteUrl, "OPENSEARCHCATDELETE");
    }

    public static String[] getUrls(String itemId) throws Exception {
        return getUrls(itemId, "OPENSEARCHCAT");
    }

    public static String[] getDelUrls(String itemId) throws Exception {
        return getUrls(itemId, "OPENSEARCHCATDELETE");
    }

    public static void delete(String itemId) throws Exception {
        delete(itemId, "OPENSEARCHCAT");
    }

    public static void deleteDelUrl(String itemId) throws Exception {
        delete(itemId, "OPENSEARCHCATDELETE");
    }

    public static String[] getStatus(String itemId, String type) throws Exception {
        return getStatus(itemId, "OPENSEARCHCAT");
    }

}
