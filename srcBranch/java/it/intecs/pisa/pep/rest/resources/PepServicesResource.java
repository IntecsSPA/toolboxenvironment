
package it.intecs.pisa.pep.rest.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
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
@Path("/services/pepservices")
public class PepServicesResource {

    @Context
    private UriInfo context;

    /** Creates a new instance of PepServicesResource */
    public PepServicesResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.PepServicesResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        JsonArray servicesJSA= new JsonArray();
        JsonObject serviceJS=null;
        RestResponse getPEPServiceListResponse = new RestResponse("getPEPServiceList");
        ServiceManager serviceManager=ServiceManager.getInstance();
        TBXService[] services = serviceManager.getServicesAsArray();
        List serviceList = new ArrayList();
        for(TBXService service:services) 
            serviceList.add(service.getServiceName());
        Collections.sort(serviceList, String.CASE_INSENSITIVE_ORDER);
        ListIterator itr = serviceList.listIterator();

            while(itr.hasNext()){
                serviceJS=new JsonObject();
                serviceJS.addProperty("serviceName", (String)itr.next());
                servicesJSA.add(serviceJS);
            }
                
        getPEPServiceListResponse.setSuccess(true);
        getPEPServiceListResponse.addJsonElement("pepServices", servicesJSA);
        return getPEPServiceListResponse.getStringRestResponse();
    }

    /**
     * Sub-resource locator method for {name}
     */
    @Path("{name}")
    public PepServiceResource getPepServiceResource(@PathParam("name")
    String name) {
        return PepServiceResource.getInstance(name);
    }
}
