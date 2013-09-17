
package it.intecs.pisa.pep.rest.manager;

import it.intecs.pisa.pep.rest.AuthenticationManager;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
@Path("manager/store")
public class ManagerStoreCommand {

    @Context
    private UriInfo context;

    /** Creates a new instance of ManagerStoreCommand */
    public ManagerStoreCommand() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.manager.ManagerStoreCommand
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * POST method  of ManagerStoreCommand
     * @param storeFile File to be stored
     * @return an HTTP response (JSON) with the path of the stored File.
     */
    @POST
    @Produces("text/html")
    
    public String store(File storeFile, @Context HttpHeaders hh)  {
        AuthenticationManager am=new AuthenticationManager();
        ManagerCommandResponse storeResponse= new ManagerCommandResponse("storeFile");
        try {
          /*  String authHeader=hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);
            
            if(am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)){*/
               try {
                    storeResponse.addJsonProperty("filePath", storeFile.getCanonicalPath());
                } catch (IOException ex) {
                   storeResponse.setSuccess(Boolean.FALSE);
                   storeResponse.setDetails(ex.getMessage());
                   return JsonUtil.getJsonAsString(storeResponse.getConfigurationResponse());
                }
           /* }else
              return JsonUtil.getJsonAsString(am.notAuthorizatedResponse()); */
            
        } catch (Exception ex) {
           ex.printStackTrace(); 
           return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }    
        storeResponse.setSuccess(Boolean.TRUE); 
        return JsonUtil.getJsonAsString(storeResponse.getConfigurationResponse());
    }
}
