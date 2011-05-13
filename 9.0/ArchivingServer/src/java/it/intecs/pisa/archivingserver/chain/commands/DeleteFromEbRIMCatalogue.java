package it.intecs.pisa.archivingserver.chain.commands;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import it.intecs.pisa.archivingserver.db.CatalogueCorrespondence;
import it.intecs.pisa.archivingserver.db.ReverseCatalogueId;
import it.intecs.pisa.archivingserver.db.SOAPCatalogueAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.soap.SOAPNamespacePrefixResolver;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.http.HttpUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli, Andrea Marongiu
 */
public class DeleteFromEbRIMCatalogue implements Command {

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        String itemId;
        try {
            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);

            deleteObjectsFromebRIMCatalogues(itemId);
            //Add deletion of item from catalogue
            SOAPCatalogueAccessible.delete(itemId);
            CatalogueCorrespondence.delete(itemId);
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    private void deleteObjectsFromebRIMCatalogues(String itemId) throws Exception {
        String[] urls = SOAPCatalogueAccessible.getUrls(itemId);
        for(String url:urls)
        {
            deleteObject(url,itemId);
        }
    }

    /**
     * This method should be replaced with a better way of creating a SOAP message.
     * @param urlStr
     * @param itemId
     * @throws Exception
     */
    private void deleteObject(String urlStr, String itemId) throws Exception {
        String catId = ReverseCatalogueId.getCatalogueId(urlStr, itemId);
        String soapRequest="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
          "xmlns:ns=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:ogc=\"http://www.opengis.net/ogc\" "+
          "xmlns:rim=\"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0\">"+
           "<soapenv:Header/>"+
           "<soapenv:Body>"+
              "<ns:Transaction service=\"CSW\" version=\"2.0.2\" verboseResponse=\"true\">"+
                 "<ns:Delete typeName=\"rim:RegistryPackage\">"+
                    "<ns:Constraint version=\"1.1.0\">"+
                       "<ogc:Filter>"+
                          "<ogc:And>"+
                             "<ogc:PropertyIsEqualTo>"+
                                "<ogc:PropertyName>/rim:RegistryPackage/@id</ogc:PropertyName>"+
                                "<ogc:Literal>"+catId+"</ogc:Literal>"+
                             "</ogc:PropertyIsEqualTo>"+
                          "</ogc:And>"+
                       "</ogc:Filter>"+
                    "</ns:Constraint>"+
                 "</ns:Delete>"+
              "</ns:Transaction>"+
           "</soapenv:Body>"+
        "</soapenv:Envelope>";


       Hashtable<String,String> headers;
       headers=new Hashtable<String,String>();
       headers.put("soapaction", "http://www.opengis.net/cat/csw/2.0.2/requests#Transaction");
       headers.put("content-type","text/xml;charset=UTF-8");
       InputStream responseStream=HttpUtils.post(new URL(urlStr), headers, null, null, new ByteArrayInputStream(soapRequest.getBytes()));
            
       String responseStr=IOUtil.inputToString(responseStream);
      // Log.debug("Catalogue response: "+responseStr);
       DOMUtil domutil= new DOMUtil();
       String totalDeleted=extractRIMIdFromDeleteResponse(domutil.stringToDocument(responseStr));
       if(totalDeleted!=null){
            Log.log("Delete "+catId+" metadata to catalogue "+urlStr+" completed. Total Deleted: "+totalDeleted);
        }else
            Log.log("Delete "+catId+" metadata to catalogue "+urlStr+" failed! ");
    }
    
    
   private String extractRIMIdFromDeleteResponse(Document resp) throws TransformerException {
        String[] totalDeleted={"//csw:TransactionResponse/csw:TransactionSummary/csw:totalDeleted"};
      
        for(String path:totalDeleted)
        {
            XObject result = XPathAPI.eval(resp, path, new SOAPNamespacePrefixResolver());
            if(result!=null && result.toString()!=null && result.toString().equals("")==false)
                return result.toString();
        }
        
        return null;
    } 
}
