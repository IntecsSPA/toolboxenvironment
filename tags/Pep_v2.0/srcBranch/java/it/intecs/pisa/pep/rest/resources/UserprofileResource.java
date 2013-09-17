
package it.intecs.pisa.pep.rest.resources;

import it.intecs.pisa.pep.rest.AuthenticationManager;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.util.json.JsonUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Context;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
public class UserprofileResource {

    private String name;

    /** Creates a new instance of UserprofileResource */
    private UserprofileResource(String name) {
        this.name = name;
    }

    /** Get instance of the UserprofileResource */
    public static UserprofileResource getInstance(String name) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of UserprofileResource class.
        return new UserprofileResource(name);
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.UserprofileResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@Context HttpHeaders hh) {
        AuthenticationManager am=new AuthenticationManager();
        try {
            String authHeader=hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0); 
            String userHeader=am.getUser(authHeader);
            if(userHeader.equals(name) &&
               am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)){
               RestResponse jsonResponse= new RestResponse("getUserProfile");
               // load the user profile
               jsonResponse.setSuccess(true);
               return jsonResponse.getStringRestResponse();
            }else
               return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        } catch (Exception ex) {
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }
    }

    /**
     * PUT method for updating or creating an instance of UserprofileResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource UserprofileResource
     */
    @DELETE
    public void delete() {
    }
}
