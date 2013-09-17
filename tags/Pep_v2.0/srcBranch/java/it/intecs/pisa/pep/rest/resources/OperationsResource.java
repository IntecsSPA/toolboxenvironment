/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author Andrea Marongiu services/pepservices/{servicename}/Operations
 */
@Path("/Operations")
public class OperationsResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of OperationsResource */
    public OperationsResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.OperationsResource
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
    public OperationResource getOperationResource(@PathParam("name")
    String name, @PathParam("servicename")
    String servicename) {
//        System.out.println("TEST");
        return OperationResource.getInstance(name, servicename);
    }
}
