package it.intecs.pisa.archivingserver.chain.commands;

import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.db.OpenSearchCatalogueAccessible;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.util.http.HttpUtils;
import it.intecs.pisa.util.json.JsonUtil;
import java.net.URL;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.Command;
import javawebparts.misc.chain.Result;

/**
 *
 * @author Andrea Marongiu
 */
public class DeleteFromOpenSearchCatalogue implements Command {

    private static final String DELETE_RESPONSE_STATUS_PROPERTY = "status";

    @Override
    public Result init(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

    public Result execute(ChainContext chainContext) {
        String itemId;
        JsonObject deleteResp;
        try {
            itemId = (String) chainContext.getAttribute(CommandsConstants.ITEM_ID);
            
           String deleteURLString = OpenSearchCatalogueAccessible.getDelUrls(itemId)[0];
           String catalogueURLString = OpenSearchCatalogueAccessible.getUrls(itemId)[0];

            deleteResp = deleteObjectsFromOpenSearchCatalogues(itemId, deleteURLString, catalogueURLString);

            if (deleteResp != null) {

                String status = deleteResp.get(DELETE_RESPONSE_STATUS_PROPERTY).getAsString();
                if (status.equalsIgnoreCase("success")) {
                    OpenSearchCatalogueAccessible.delete(itemId);
                    OpenSearchCatalogueAccessible.deleteDelUrl(itemId);
                    Log.log("Delete \"" + itemId + "\" metadata to OpenSearch Catalogue " + catalogueURLString + " completed.");
                } else {
                    Log.log("Delete \"" + itemId + "\" metadata to OpenSearch Catalogue " + catalogueURLString + " failed!");
                }

            }
        } catch (Exception e) {
            Log.logException(e);
            return new Result(Result.FAIL);
        }
        return new Result(Result.SUCCESS);
    }

    private JsonObject deleteObjectsFromOpenSearchCatalogues(String itemID, 
            String deleteURLString, String catalogueURLString) throws Exception {

        JsonObject deleteResponse = null;
        
        try {
            deleteResponse = JsonUtil.getInputAsJson(
                    HttpUtils.delete(new URL(deleteURLString), null, null, null));

        } catch (Exception ex) {
            Log.log("Delete \"" + itemID + "\"  metadata to openSearch Catalogue " + catalogueURLString + " failed! ");
            Log.logException(ex);
            return null;
        }

        return deleteResponse;
    }

    @Override
    public Result cleanup(ChainContext cc) {
        return new Result(Result.SUCCESS);
    }

}
