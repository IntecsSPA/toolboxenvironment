/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.CatalogueCorrespondence;
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

/**
 *
 * @author massi
 */
public class PublishToEbRIMCatalogue implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        StoreItem storeItem;
        String id;
        File webappDir;
        Document doc;
        String itemId;

        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());
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
        if(itemId!=null)
            CatalogueCorrespondence.add(id, url, itemId);
        
        res=true;
        return res;
    }

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
        Element resourceTypeEl;
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

        resourceTypeEl=doc.createElement("csw:ResourceType");
        resourceTypeEl.setTextContent("urn:x-ogc:specification:csw-ebrim:ObjectType:EO:EOProduct");
        rootEl.appendChild(resourceTypeEl);
        return doc;
    }

    private String extractRIMIdFromHarvestResponse(Document resp) throws TransformerException {
        String[] supportedRIMTypes={"//csw:BriefRecord[dcelem:type = 'urn:oasis:names:tc:ebxml-regrep:ObjectType:RegistryObject:RegistryPackage']/dcelem:identifier"};
      
        for(String path:supportedRIMTypes)
        {
            XObject result = XPathAPI.eval(resp, path, new SOAPNamespacePrefixResolver());
            if(result!=null && result.toString()!=null && result.toString().equals("")==false)
                return result.toString();
        }
        
        return null;
    }
}
