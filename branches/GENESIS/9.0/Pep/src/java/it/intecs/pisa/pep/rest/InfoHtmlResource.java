
package it.intecs.pisa.pep.rest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
@Path("info.{format}")
public class InfoHtmlResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of InfoResource */
    public InfoHtmlResource() {
        
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.InfoResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("text/html")
      public String getResource(@PathParam("format") String format) {
        return "<b>Info "+format+"</b><p>"+context.getPath()+"</p>";
    }
    

   
    /**
     * PUT method for updating or creating an instance of InfoResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("text/html")
    public void putHtml(String content) {
    }
}

