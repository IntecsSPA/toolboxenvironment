/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.archivingserver.chain.commands;

import it.intecs.pisa.archivingserver.db.CatalogueCorrespondence;
import it.intecs.pisa.archivingserver.db.ReverseCatalogueId;
import it.intecs.pisa.archivingserver.db.SOAPCatalogueAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.http.HttpUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class DeleteFromEbRIMCatalogue implements Command {

    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext cc) {
        String itemId;
        try {
            Log.log("Executing class "+this.getClass().getCanonicalName());

            itemId=(String) cc.getAttribute(CommandsConstants.ITEM_ID);

            deleteObjectsFromebRIMCatalogues(itemId);
            //Add deletion of item from catalogue
            SOAPCatalogueAccessible.delete(itemId);
            CatalogueCorrespondence.delete(itemId);
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

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

    private void deleteObject(String urlStr, String itemId) throws Exception {
        String catId = ReverseCatalogueId.getCatalogueId(urlStr, itemId);

        String soapRequest="<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:ogc=\"http://www.opengis.net/ogc\">"+
                           "<soapenv:Header/>"+
                           "<soapenv:Body>"+
                              "<ns:Transaction service=\"CSW\" version=\"2.0.2\" verboseResponse=\"false\" requestId=\"1\">"+
                                 "<ns:Delete typeName=\"rim:ExtrinsicObject\">"+
                                    "<ns:Constraint version=\"\">"+
                                       "<ogc:Filter>"+
                                          //"<ogc:And>"+
                                             "<ogc:PropertyIsEqualTo>"+
                                                "<ogc:PropertyName>/rim:ExtrinsicObject/@id</ogc:PropertyName>"+
                                                "<ogc:Literal>"+catId+"</ogc:Literal>"+
                                             "</ogc:PropertyIsEqualTo>"+
                                          //"</ogc:And>"+
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

            System.out.println("Catalogue response: "+responseStr);

    }
}
