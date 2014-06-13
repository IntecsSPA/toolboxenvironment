package it.intecs.pisa.archivingserver.chain.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.db.OpenSearchCatalogueAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.http.HttpUtils;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.net.URL;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;
import org.w3c.dom.Document;

/**
 *
 * @author Andrea Marongiu
 */
public class PublishToOpenSearchCatalogue implements Command {
    
    private static final String PUT_RESPONSE_SUCCESS_PROPERTY="success";
    private static final String PUT_RESPONSE_TOTAL_PROPERTY="total";
    private static final String PUT_RESPONSE_REPORT_PROPERTY="report";
    private static final String PUT_RESPONSE_REPORT_ID_PROPERTY="id";
    //private static final String PUT_RESPONSE_REPORT_STATUS_PROPERTY="status";

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    @Override
    public Result execute(ChainContext cc) {
        StoreItem storeItem;
        String id;
        Document doc;
        JsonObject putResult=null;

        try {
            storeItem = (StoreItem) cc.getAttribute(CommandsConstants.STORE_ITEM);
            id = (String) cc.getAttribute(CommandsConstants.ITEM_ID);
            doc = (Document) cc.getAttribute(CommandsConstants.ITEM_METADATA);

            if (doc != null) {

                for (int i=0; i< storeItem.ingestionOpenSearchCatalogue.length; i++) {

                    String ingestionURL=storeItem.ingestionOpenSearchCatalogue[i];
                    String catalogueURL=storeItem.publishOpenSearchCatalogue[i];
                    
                    putResult = putMetadata(ingestionURL, doc);

                    if (putResult != null) {

                        String itemId=null;
                        int success, total;
                        success= putResult.get(PUT_RESPONSE_SUCCESS_PROPERTY).getAsInt();
                        total= putResult.get(PUT_RESPONSE_TOTAL_PROPERTY).getAsInt();
                        if(success== total){
                            JsonArray report= putResult.getAsJsonArray(PUT_RESPONSE_REPORT_PROPERTY);
                            itemId = ((JsonObject) report.get(0)).get(PUT_RESPONSE_REPORT_ID_PROPERTY).getAsString();
                            OpenSearchCatalogueAccessible.add(id, catalogueURL+"/opensearch/eoproduct?id="+itemId);
                            OpenSearchCatalogueAccessible.add(id, catalogueURL+"/metadata?id="+itemId);
                            Log.log("Ingestion metadata to OpenSearch Catalogue "+ingestionURL+" completed. Metadata id: "+itemId);
                        }else
                            Log.log("Ingestion metadata to OpenSearch Catalogue "+ingestionURL+" failed.");
                    }
                }

            }
        } catch (Exception e) {
            Log.log(e.getMessage());
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    private JsonObject putMetadata(String openSearchIngestionURL, Document doc) {
        JsonObject ingestionResponse = null;
        try {
             ingestionResponse = JsonUtil.getInputAsJson(HttpUtils.put(
                    new URL(openSearchIngestionURL), null, null, null,
                    DOMUtil.getDocumentAsInputStream(doc)));

        } catch (Exception ex) {
            Log.log("Ingestion metadata to openSearch Catalogue " + openSearchIngestionURL + " failed! ");
            Log.logException(ex);
            return null;
        }

        /* if(itemId!=null){
         CatalogueCorrespondence.add(id, url, itemId);
         Log.log("Harvest metadata to catalogue "+url+" completed. Metadata id: "+itemId);
         }else
         Log.log("Harvest metadata to catalogue "+url+" failed! ");
         res=true;*/
        return ingestionResponse;
    }

    public Result cleanup(ChainContext chainContext) {
        return new Result(Result.SUCCESS);
    }

}
