
package it.intecs.pisa.pep.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.intecs.pisa.util.json.JsonUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
public class ServiceResource {
    
    private static final String WSDL_URL_SERVICE_INPUT_CONTENT="wdsl";

    private String name;

    /** Creates a new instance of ServiceResource */
    private ServiceResource(String name) {
        this.name = name;
    }

    /** Get instance of the ServiceResource */
    public static ServiceResource getInstance(String name) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of ServiceResource class.
        return new ServiceResource(name);
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.ServiceResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    
    
    /**
     * PUT method for updating or creating an instance of ServiceResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
        JsonObject contentJson=JsonUtil.getStringAsJSON(content);
        String wdslURL=contentJson.get(WSDL_URL_SERVICE_INPUT_CONTENT).getAsString();
        
    }

    /**
     * DELETE method for resource ServiceResource
     */
    @DELETE
    public void delete() {
    }
}
