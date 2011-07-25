
package it.intecs.pisa.pep.rest.resources;

import it.intecs.pisa.pep.rest.AuthenticationManager;
import com.google.gson.JsonObject;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.util.json.JsonUtil;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
@Path("configuration")
public class ConfigurationResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of ConfigurationResource */
    public ConfigurationResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.ConfigurationResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@Context HttpHeaders hh) {
        AuthenticationManager am=new AuthenticationManager();
        try {
            String authHeader=hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);
            
            if(am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)){
                RestResponse jsonResponse= new RestResponse("getConfiguration");
                ToolboxConfiguration configuration;
                configuration=ToolboxConfiguration.getInstance();
                try{
                    jsonResponse.addJsonElement("configuration", configuration.getConfiguration());
                } catch (Exception ex) {
                    jsonResponse.setSuccess(false);
                    jsonResponse.setDetails(ex.getMessage());
                    return jsonResponse.getStringRestResponse();
                }  
                jsonResponse.setSuccess(true);
                return jsonResponse.getStringRestResponse();
            }else
               return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        } catch (Exception ex) {
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }
    }

    /**
     * PUT method for updating or creating an instance of ConfigurationResource
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
                RestResponse jsonResponse= new RestResponse("saveConfiguration");
                JsonObject contentJson=JsonUtil.getStringAsJSON(content);
                ToolboxConfiguration configuration;
                configuration=ToolboxConfiguration.getInstance();
                try{
                    configuration.setAndSaveConfiguration(contentJson);  
                }catch (Exception ex) {
                   ex.printStackTrace(); 
                   jsonResponse.setSuccess(false);
                   jsonResponse.setDetails(ex.getMessage());
                   return jsonResponse.getStringRestResponse();
                }    
                jsonResponse.setSuccess(true);
                return jsonResponse.getStringRestResponse();
            }else
               return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        } catch (Exception ex) {
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }
        
    }
    
   
}
