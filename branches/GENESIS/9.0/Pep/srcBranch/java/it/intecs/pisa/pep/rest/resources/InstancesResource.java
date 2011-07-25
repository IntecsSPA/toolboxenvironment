
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
@Path("/instances")
public class InstancesResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of InstancesResource */
    public InstancesResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.resources.InstancesResource
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
    public InstanceResource getInstanceResource(@PathParam("name")
    String name) {
        return InstanceResource.getInstance(name);
    }
}
