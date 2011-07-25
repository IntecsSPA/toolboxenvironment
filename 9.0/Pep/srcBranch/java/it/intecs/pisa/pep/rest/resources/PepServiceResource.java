package it.intecs.pisa.pep.rest.resources;

import com.google.gson.JsonArray;
import it.intecs.pisa.pep.rest.AuthenticationManager;
import javax.ws.rs.core.Context;
import com.google.gson.JsonObject;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.plugins.gatewayPlugin.manager.GatewayCommands;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.util.wsil.WSILBuilder;
import it.intecs.pisa.util.json.JsonUtil;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.HttpHeaders;

/**
 * REST Web Service
 *
 * @author Andrea Marongiu
 */
public class PepServiceResource {

    private String name;

    /** Creates a new instance of PepServiceResource */
    private PepServiceResource(String name) {
        this.name = name;
    }

    /** Get instance of the PepServiceResource */
    public static PepServiceResource getInstance(String name) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of PepServiceResource class.
        return new PepServiceResource(name);
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.PepServiceResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@Context HttpHeaders hh) {
        AuthenticationManager am = new AuthenticationManager();
        JsonObject getResult = new JsonObject();
        JsonObject jsonOp=null;
        ServiceManager sm = null;
        TBXService service;
        JsonArray jsonOps= new JsonArray();
        Operation [] operations;
        try {
            String authHeader = hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);

            if (am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)) {
                getResult = new GatewayCommands().getGatewayServiceConfiguration(this.name);
                sm = ServiceManager.getInstance();
                service=sm.getService(this.name);
                operations = service.getImplementedInterface().getOperations();
                
                for (Operation op : operations) {
                    jsonOp=new JsonObject();
                    jsonOp.addProperty("name", op.getName());
                    jsonOp.addProperty("soapAction", op.getSoapAction());
                    jsonOp.addProperty("inputType", op.getInputType());
                    jsonOp.addProperty("inputTypeNamespace", op.getInputTypeNameSpace());
                    jsonOp.addProperty("outputType", op.getOutputType());
                    jsonOp.addProperty("outputTypeNamespace", op.getOutputTypeNameSpace());
                    jsonOps.add(jsonOp);
                }
                
                getResult.add("operations", jsonOps);
                getResult.addProperty("wsdl", service.getWSDLUrl());
            } else {
                return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }
        return JsonUtil.getJsonAsString(getResult);
    }

    /**
     * PUT method for updating or creating an instance of PepServiceResource
     * @param content representation for the resource
     * @return an HTTP response (JSON) with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    @Produces("application/json")
    public String putJson(String content, @Context HttpHeaders hh) {
        AuthenticationManager am = new AuthenticationManager();
        JsonObject putResult = new JsonObject();
        try {
            String authHeader = hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);
            if (am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)) {
                JsonObject contentJson = JsonUtil.getStringAsJSON(content);
                putResult = new GatewayCommands().createorUpdateGatewayService(this.name, contentJson);
            } else {
                return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }
        return JsonUtil.getJsonAsString(putResult);
    }

    /**
     * DELETE method for resource PepServiceResource
     */
    @DELETE
    @Produces("application/json")
    public String delete(@Context HttpHeaders hh) {
        AuthenticationManager am = new AuthenticationManager();
        JsonObject putResult = new JsonObject();
        try {
            String authHeader = hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);
            RestResponse deleteGatewayResponse = new RestResponse("deleteGatewayService");
            if (am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)) {
                ServiceManager serviceManager = ServiceManager.getInstance();
                try {
                    serviceManager.deleteService(this.name);
                    // WSILBuilder.createWSIL();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    deleteGatewayResponse.setSuccess(false);
                    deleteGatewayResponse.setDetails(ex.getMessage());
                    return deleteGatewayResponse.getStringRestResponse();
                }
                deleteGatewayResponse.setSuccess(true);
                return deleteGatewayResponse.getStringRestResponse();
            } else {
                return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }

    }
}
