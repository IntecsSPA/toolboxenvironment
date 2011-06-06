/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pep.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author maro
 */
@Path("/services")
public class ServicesResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of ServicesResource */
    public ServicesResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.ServicesResource
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
    public ServiceResource getServiceResource(@PathParam("name")
    String name) {
        return ServiceResource.getInstance(name);
    }
}
