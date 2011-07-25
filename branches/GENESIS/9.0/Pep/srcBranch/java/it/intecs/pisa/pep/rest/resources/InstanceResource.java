
package it.intecs.pisa.pep.rest.resources;

import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
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
public class InstanceResource {

    private String name;

    /** Creates a new instance of InstanceResource */
    private InstanceResource(String name) {
        this.name = name;
    }

    /** Get instance of the InstanceResource */
    public static InstanceResource getInstance(String name) {
        return new InstanceResource(name);
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.resources.InstanceResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {
        RestResponse getInstanceInfoResponse = new RestResponse("getInstanceInfo");
        try {
            String instanceRequestId=InstanceResources.getXMLResourceId(name, 
                    InstanceResources.TYPE_INPUT_MESSAGE);
            getInstanceInfoResponse.addJsonProperty("requestResourceID", instanceRequestId);
     
            getInstanceInfoResponse.addJsonProperty("instanceID", name);
            
        } catch (Exception ex) {
            getInstanceInfoResponse.setSuccess(false);
            getInstanceInfoResponse.setDetails(ex.getMessage());
            return getInstanceInfoResponse.getStringRestResponse();
        }    
        getInstanceInfoResponse.setSuccess(true);
        try {    
            String instanceResponseId=InstanceResources.getXMLResourceId(name, 
                    InstanceResources.TYPE_OUTPUT_MESSAGE);

            getInstanceInfoResponse.addJsonProperty("responseResourceID", instanceResponseId);

 
        } catch (Exception ex) {
            return getInstanceInfoResponse.getStringRestResponse();
        }
        return getInstanceInfoResponse.getStringRestResponse();
    }

    /**
     * PUT method for updating or creating an instance of InstanceResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource InstanceResource
     */
    @DELETE
    public String  delete() {
        RestResponse deleteInstanceInfoResponse = new RestResponse("deleteInstance");
        try {
            InstanceHandler handler=new InstanceHandler(Long.parseLong(this.name));
                        handler.deleteInstance();
        } catch (Exception ex) {
            deleteInstanceInfoResponse.setSuccess(false);
            deleteInstanceInfoResponse.setDetails(ex.getMessage());
            return deleteInstanceInfoResponse.getStringRestResponse();
        }
        deleteInstanceInfoResponse.setSuccess(true);
        return deleteInstanceInfoResponse.getStringRestResponse();
    }
}
