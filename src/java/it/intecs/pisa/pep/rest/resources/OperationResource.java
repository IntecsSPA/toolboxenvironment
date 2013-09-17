
package it.intecs.pisa.pep.rest.resources;

import it.intecs.pisa.pep.rest.AuthenticationManager;
import com.google.gson.JsonObject;
import it.intecs.pisa.toolbox.plugins.gatewayPlugin.manager.GatewayCommands;
import it.intecs.pisa.util.json.JsonUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
public class OperationResource {

    private String name;
    
    
    
    @Context
    private UriInfo context;
    
    /** Creates a new instance of OperationResource */
    private OperationResource(String name) {
        this.name = name;
    }

    /** Get instance of the OperationResource */
    public static OperationResource getInstance(String name, String serviceName) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of OperationResource class.
        return new OperationResource(name);
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.OperationResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of OperationResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String putJson(String content, @Context HttpHeaders hh) {
        AuthenticationManager am=new AuthenticationManager();
        try {
            String authHeader=hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);
            
            if(am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)){
                JsonObject putResult=new JsonObject();
                JsonObject contentJson=JsonUtil.getStringAsJSON(content);
                putResult = new GatewayCommands().createGatewayOperation(this.name, contentJson);
                return JsonUtil.getJsonAsString(putResult);
            }else
               return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        } catch (Exception ex) {
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }
    }

    /**
     * DELETE method for resource OperationResource
     */
    @DELETE
    public void delete() {
        
        
    }
}
