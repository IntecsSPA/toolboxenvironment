/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.sun.org.apache.xpath.internal.XPathAPI;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.FTPAccessible;
import it.intecs.pisa.archivingserver.db.GeoServerAccessible;
import it.intecs.pisa.archivingserver.db.HttpAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.soap.SimpleSOAPClient;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author massi
 */
public class NotifyPublishing implements Command {

    protected static final String NAMESPACE_WFS = "http://www.opengis.net/wfs";
    protected static final String NAMESPACE_GEN = "http://www.genesis-fp7.eu/xmlschema/Global";
    protected static final String NAMESPACE_GML = "http://www.opengis.net/gml";
    protected static final String NAMESPACE_XLINK = "http://www.w3.org/1999/xlink";
    protected static final String NAMESPACE_OGC = "http://www.opengis.net/ogc";

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        StoreItem storeItem;
        String id;
        File webappDir;
        Document doc = null;

        try {
            storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);

            if (storeItem.notifyURL.length > 0) {
                for (int index = 0; index < storeItem.notifyURL.length; index ++) {
                    try {
                        doc = createTransactionMessage(cc,storeItem.notifyTopic[index],storeItem.notifyEventType[index]);
                        notify(storeItem.notifyURL[index], doc);
                    } catch (Exception e) {
                        Log.logException(e);
                    }
                }
            }
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    private boolean notify(String url, Document doc) throws Exception, MalformedURLException {
        boolean res = false;

        SimpleSOAPClient client;
        client = new SimpleSOAPClient();
        client.setTo(new URL(url));
        client.setSoapAction("http://www.opengis.net/cat/csw/2.0.2/requests#Harvest");
        Document resp = client.sendReceive(doc);

        // do something with the response

        res = true;
        return res;
    }

    private Document createTransactionMessage(ChainContext cc, String topicHref, String eventTypeHref) throws FileNotFoundException, IOException, SAXException, Exception {
        Document doc;
        DOMUtil util;
        File webappDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
        StoreItem storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
        String id = (String) cc.getAttribute(CommandsConstants.ITEM_ID);

        File template = new File(webappDir, "WEB-INF/classes/wfs_transaction_alert_template.xml");
        util = new DOMUtil();
        doc = util.fileToDocument(template);

        String url = getMetadataUrl(storeItem, id);

        Element root = doc.getDocumentElement();

        // set topic
        Element topic = (Element) XPathAPI.selectSingleNode(root, "//gen:topic");
        topic.setAttributeNS(NAMESPACE_XLINK, "href", topicHref);

        // set event type
        Element eventType = (Element) XPathAPI.selectSingleNode(root, "//gen:eventType");
        eventType.setAttributeNS(NAMESPACE_XLINK, "href", eventTypeHref);

        // set date
//        Node timestamp = XPathAPI.selectSingleNode(root, "//gen:timestamp");
//        String dateString = DateUtil.getCurrentDateAsString("YYYY-MM-DDThh:mm:ss");
//        timestamp.setTextContent(dateString);

        // set url
        Node uri = XPathAPI.selectSingleNode(root, "//gen:anyPayload");
        uri.setTextContent(url);

        return doc;
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private String getMetadataUrl(StoreItem item, String itemId) throws Exception {
        if (item.publishHttp == true) {
            String[] urls = HttpAccessible.getUrls(itemId);
            return urls[0];
        } else if (item.publishFtp.length > 0) {
            String[] urls = FTPAccessible.getUrls(itemId);
            return urls[0];
        } else if (item.publishGeoserver.length > 0) {
            String[] urls = GeoServerAccessible.getUrls(itemId);
            return urls[0];
        }

        return "http://www.google.it";
    }
}
