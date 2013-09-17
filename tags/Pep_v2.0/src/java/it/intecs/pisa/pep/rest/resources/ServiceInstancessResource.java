
package it.intecs.pisa.pep.rest.resources;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
@Path("/servicesInstances")
public class ServiceInstancessResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of ServiceInstancessResource */
    public ServiceInstancessResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.resources.ServiceInstancessResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * Sub-resource locator method for {name}
     */
    @Path("{name}")
    public ServiceInstancesResource getServiceInstancesResource(@PathParam("name")
    String name) {
        return ServiceInstancesResource.getInstance(name);
    }
}
