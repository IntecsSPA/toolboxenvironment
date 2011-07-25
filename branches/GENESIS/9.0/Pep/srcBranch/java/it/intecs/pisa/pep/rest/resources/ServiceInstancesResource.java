
package it.intecs.pisa.pep.rest.resources;

import com.google.gson.JsonObject;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.service.instances.InstanceLister;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
public class ServiceInstancesResource {

    private String name;

    /** Creates a new instance of ServiceInstancesResource */
    private ServiceInstancesResource(String name) {
        this.name = name;
    }

    /** Get instance of the ServiceInstancesResource */
    public static ServiceInstancesResource getInstance(String name) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of ServiceInstancesResource class.
        return new ServiceInstancesResource(name);
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.resources.ServiceInstancesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@Context HttpServletRequest request) {
        RestResponse getServiceInstances = new RestResponse("getServiceInstances");
        JsonObject instancesJson=null;
        try {
            String start=request.getParameter("start");
            String limit=request.getParameter("limit");
            instancesJson=InstanceLister.getSynchronousInstancesAsJson(
                    this.name,Integer.parseInt(start)+1,Integer.parseInt(limit));

        } catch (Exception ex) {
            ex.printStackTrace();
            getServiceInstances.setSuccess(false);
            getServiceInstances.setDetails(ex.getMessage());
            return getServiceInstances.getStringRestResponse();
        }
        
        getServiceInstances.setSuccess(true);
        getServiceInstances.addJsonElement("instancesPage", instancesJson);
        return getServiceInstances.getStringRestResponse();
    }

    /**
     * PUT method for updating or creating an instance of ServiceInstancesResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
        throw new UnsupportedOperationException();
    }

    /**
     * DELETE method for resource ServiceInstancesResource
     */
    @DELETE
    public void delete() {
    }
}
