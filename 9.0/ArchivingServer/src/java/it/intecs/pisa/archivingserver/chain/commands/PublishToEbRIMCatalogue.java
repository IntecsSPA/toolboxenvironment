/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.CatalogueCorrespondence;
import it.intecs.pisa.archivingserver.db.FTPAccessible;
import it.intecs.pisa.archivingserver.db.GeoServerAccessible;
import it.intecs.pisa.archivingserver.db.HttpAccessible;
import it.intecs.pisa.archivingserver.db.SOAPCatalogueAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.soap.SOAPNamespacePrefixResolver;
import it.intecs.pisa.archivingserver.soap.SimpleSOAPClient;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author massi
 */
public class PublishToEbRIMCatalogue implements Command {
    protected static final String NAMESPACE_GMD="http://www.isotc211.org/2005/gmd";
    protected static final String NAMESPACE_EOP="http://earth.esa.int/eop";
    protected static final String NAMESPACE_OPT="http://earth.esa.int/opt";
    protected static final String NAMESPACE_SAR="http://earth.esa.int/sar";
    protected static final String NAMESPACE_ATM="http://earth.esa.int/atm";
    protected static final String NAMESPACE_GML="http://www.opengis.net/gml";

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        StoreItem storeItem;
        String id;
        File webappDir;
        Document doc;

        try {
            storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            id = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            webappDir = (File) cc.getAttribute(CommandsConstants.APP_DIR);
            doc=(Document) cc.getAttribute(CommandsConstants.ITEM_METADATA);

            if(doc!=null)
            {
                if(checkIfRIM(doc))
                {
                    //performing transaction
                }
                else
                {
                    putDataLinkIntoMetadata(cc);

                    File toDir;
                    File toFile;
                    String harvestDocId;

                    harvestDocId=DateUtil.getCurrentDateAsUniqueId();

                    toDir=new File(webappDir,"storagearea/"+id);
                    toDir.mkdirs();

                    toFile=new File(toDir,harvestDocId+".xml");

                    IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), new FileOutputStream(toFile));

                    for(String url:storeItem.publishCatalogue)
                    {
                        Document soapMsg;
                        soapMsg=createHarvestMessage(webappDir,id,harvestDocId);

                        boolean res=harvestData(id,url, soapMsg);

                        if(res==true)
                            SOAPCatalogueAccessible.add(id, url);
                    }

                    toFile.delete();
                }
            }
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    private boolean harvestData(String id,String url, Document doc) throws Exception, MalformedURLException {
        boolean res=false;

        SimpleSOAPClient client;
        client = new SimpleSOAPClient();
        client.setTo(new URL(url));
        client.setSoapAction("http://www.opengis.net/cat/csw/2.0.2/requests#Harvest");
        Document resp=client.sendReceive(doc);

        //Storing reference into db for reverse lookup
        String itemId=extractRIMIdFromHarvestResponse(resp);
        if(itemId!=null){
            CatalogueCorrespondence.add(id, url, itemId);
            Log.log("Harvest metadata to catalogue "+url+" completed. Metadata id: "+itemId);
        }else
            Log.log("Harvest metadata to catalogue "+url+" failed! ");
        res=true;
        return res;
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private boolean checkIfRIM(Document doc) {
        return false;
    }

    private Document createHarvestMessage(File appDir,String id, String harvestDocId) throws FileNotFoundException, IOException {
        Document doc;
        DOMUtil util;
        Element rootEl;
        Element sourceEl;
        Properties prop;

        util=new DOMUtil();
        doc=util.newDocument();

        prop=Prefs.load(appDir);
        String fullUrl = prop.getProperty("http.public.url")+"/storagearea/"+id + "/" + harvestDocId+".xml";

        rootEl=doc.createElement("csw:Harvest");
        rootEl.setAttribute("service", "CSW");
        rootEl.setAttribute("version", "2.0.2");
        rootEl.setAttribute("xmlns:csw", "http://www.opengis.net/cat/csw/2.0.2");

        doc.appendChild(rootEl);

        sourceEl=doc.createElement("csw:Source");
        sourceEl.setTextContent(fullUrl);
        rootEl.appendChild(sourceEl);

        return doc;
    }

    private String extractRIMIdFromHarvestResponse(Document resp) throws TransformerException, Exception {
        String[] supportedRIMTypes={"//csw:BriefRecord[dcelem:type = 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage']/dcelem:identifier"};
      
       // Log.debug(DOMUtil.getDocumentAsString(resp));
        for(String path:supportedRIMTypes)
        {
            XObject result = XPathAPI.eval(resp, path, new SOAPNamespacePrefixResolver());
            if(result!=null && result.toString()!=null && result.toString().equals("")==false)
                return result.toString();
        }
        
        return null;
    }

    private void putDataLinkIntoMetadata(ChainContext cc) throws Exception {
        StoreItem storeItem;
        Document doc;
        
        storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
        doc=(Document) cc.getAttribute(CommandsConstants.ITEM_METADATA);
        
       String  id = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
       String url=getMetadataUrl(storeItem,id);

        Element root=doc.getDocumentElement();
        String namespaceURI=root.getNamespaceURI();

        if(namespaceURI.equals(NAMESPACE_GMD))
            putLinkIntoCIMMetadata(doc,url);
        else if(namespaceURI.equals(NAMESPACE_OPT)||
                namespaceURI.equals(NAMESPACE_ATM)||
                namespaceURI.equals(NAMESPACE_SAR)||
                namespaceURI.equals(NAMESPACE_EOP))
            putLinkIntoEOMetadata(doc,url);

    }

    private void putLinkIntoEOMetadata(Document doc,String url)
    {
        try
        {
            Element root=doc.getDocumentElement();
            Element resultOf=getChildren(root,NAMESPACE_GML,"resultOf");
            Element eoResult=DOMUtil.getChildByLocalName(resultOf,"EarthObservationResult");
            Element product=getChildren(eoResult,NAMESPACE_EOP,"product");
            Element pi=getChildren(product,NAMESPACE_EOP,"ProductInformation");
            Element filename=getChildren(pi,NAMESPACE_EOP,"fileName");

            filename.setTextContent(url);
            
        }
        catch(Exception e)
        {
            Log.logException(e);
        }
    }

    private void putLinkIntoCIMMetadata(Document doc,String url) {
        try
        {
            Element root=doc.getDocumentElement();
            Element distribInfoEl=getChildren(root,NAMESPACE_GMD,"distributionInfo");
            Element distributionEl=getChildren(distribInfoEl,NAMESPACE_GMD,"MD_Distribution");
            Element tranferOptionsEl=getChildren(distributionEl,NAMESPACE_GMD,"transferOptions");
            Element digitalTransferOptions=getChildren(tranferOptionsEl,NAMESPACE_GMD,"MD_DigitalTransferOptions");
            Element onLineEl=getChildren(digitalTransferOptions,NAMESPACE_GMD,"onLine");
            Element onlineResourceEl=getChildren(onLineEl,NAMESPACE_GMD,"CI_OnlineResource");
            Element linkageEl=getChildren(onlineResourceEl,NAMESPACE_GMD,"linkage");
            Element urlEl=getChildren(linkageEl,NAMESPACE_GMD,"URL");

            urlEl.setTextContent(url);

        }
        catch(Exception e)
        {
            Log.logException(e);
        }
    }

    private Element getChildren(Element parentEl,String namespace,String localname)
    {
        NodeList children = parentEl.getChildNodes();

        for(int i=0;i<children.getLength();i++)
        {
            Node node=children.item(i);
            if(node instanceof Element &&
               node.getNamespaceURI().equals(namespace) &&
               node.getLocalName().equals(localname))
                return (Element) node;
        }

        return null;
    }

    private String getMetadataUrl(StoreItem item,String itemId) throws Exception {
        if(item.publishHttp==true)
        {
            String[] urls = HttpAccessible.getUrls(itemId);
            return urls[0];
        }
        else if (item.publishFtp.length>0)
        {
            String[] urls = FTPAccessible.getUrls(itemId);
            return urls[0];
        }
        else if(item.publishGeoserver.length>0)
        {
            String[] urls = GeoServerAccessible.getUrls(itemId);
            return urls[0];
        }

        return "http://www.google.it";
    }
}
