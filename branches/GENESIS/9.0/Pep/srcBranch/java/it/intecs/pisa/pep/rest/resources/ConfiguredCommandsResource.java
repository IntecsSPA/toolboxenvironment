/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.pep.rest.resources;

import com.google.gson.JsonArray;
import it.intecs.pisa.pep.rest.AuthenticationManager;
import it.intecs.pisa.pep.rest.RestResponse;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.json.JsonUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileReader;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * REST Web Service
 *
 * @author mariarosaria
 */
@Path("ConfiguredCommands")
public class ConfiguredCommandsResource {

    @Context
    private UriInfo context;
    private String COMMANDS_CONFIGURATION_FILE = "WEB-INF/xml/configuredCommands.xml";
    private String AUTHENTICATION_COMMANDS = "authenticationCommands";
    private String AUTHORIZATION_COMMANDS = "authorizationCommands";

    /** Creates a new instance of ConfiguredCommandsResource */
    public ConfiguredCommandsResource() {
    }

    /**
     * Retrieves representation of an instance of it.intecs.pisa.pep.rest.resources.ConfiguredCommandsResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson(@Context HttpHeaders hh) {

        AuthenticationManager am = new AuthenticationManager();
        try {
            String authHeader = hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);

            if (am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)) {
                RestResponse jsonResponse = new RestResponse("getConfiguredCommands");
                File stepsFile = new File(Toolbox.getInstance().getRootDir(), COMMANDS_CONFIGURATION_FILE);
                try {
                    DOMUtil domUtil = new DOMUtil();
                    Document doc = domUtil.fileToDocument(stepsFile);
                    NodeList chains = doc.getElementsByTagName("chain");
                    Element authenticationChain = null;
                    Element authorizationChain = null;
                    for (int index = 0; index < chains.getLength(); index++) {
                        Element currentChain = (Element) chains.item(index);
                        if (currentChain.getAttribute("id").compareTo(AUTHENTICATION_COMMANDS) == 0) {
                            authenticationChain = currentChain;
                        } else if (currentChain.getAttribute("id").compareTo(AUTHORIZATION_COMMANDS) == 0) {
                            authorizationChain = currentChain;
                        }
                    }

                    JsonObject configuredCommands = new JsonObject();
                    
                    JsonObject authenticationChainJson = new JsonObject();
                    createJsonCommands(authenticationChain, authenticationChainJson);
                    configuredCommands.add("authentication", authenticationChainJson);

                    JsonObject authorizationChainJson = new JsonObject();
                    createJsonCommands(authorizationChain, authorizationChainJson);
                    configuredCommands.add("authorization", authorizationChainJson);
                    
                    jsonResponse.addJsonElement("configuredCommands", configuredCommands);

                } catch (Exception ex) {
                    jsonResponse.setSuccess(false);
                    jsonResponse.setDetails(ex.getMessage());
                    return jsonResponse.getStringRestResponse();
                }
                jsonResponse.setSuccess(true);
                return jsonResponse.getStringRestResponse();
            } else {
                return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
            }
        } catch (Exception ex) {
            return JsonUtil.getJsonAsString(am.notAuthorizatedResponse());
        }

    }

    /**
     * PUT method for updating or creating an instance of ConfiguredCommandsResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content, @Context HttpHeaders hh) {
        AuthenticationManager am = new AuthenticationManager();
        try {
            String authHeader = hh.getRequestHeader(AuthenticationManager.AUTHORIZATION_HEADER).get(0);

            if (am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)) {
                // TODO invoke GatewayCommands to save resource
            } else {
                throw new WebApplicationException((Status.FORBIDDEN));
            }
        } catch (Exception ex) {
            throw new WebApplicationException((Status.INTERNAL_SERVER_ERROR));
        }

    }

    private void createJsonCommands(Element chainDOM, JsonObject chainJson) {
        // these are the attributes of the chain
        NamedNodeMap chainAttributes = chainDOM.getAttributes();
        for (int index = 0; index < chainAttributes.getLength(); index++) {
            Node attr = chainAttributes.item(index);
            chainJson.addProperty(attr.getNodeName(), attr.getNodeValue());
        }
        // these are the commands of the chain
        NodeList commandsDOM = chainDOM.getElementsByTagName("command");
        JsonArray commandsJson = new JsonArray();
        for (int index = 0; index < commandsDOM.getLength(); index++) {       
            Element commandDOM = (Element) commandsDOM.item(index);
            // be sure to select only the leaf commands
            if (commandDOM.getAttribute("className").isEmpty())
                continue;
            
            JsonObject commandJson = new JsonObject();
            NamedNodeMap commandAttributes = commandDOM.getAttributes();
            for (int i = 0; i < commandAttributes.getLength(); i++) {
                Node attr = commandAttributes.item(i);
                commandJson.addProperty(attr.getNodeName(), attr.getNodeValue());
            }
            NodeList propertiesDOM = commandDOM.getElementsByTagName("property");
            JsonArray propertiesJson = new JsonArray();
            for (int i = 0; i < propertiesDOM.getLength(); i++) {
                Element propertyDOM = (Element) propertiesDOM.item(i);
                JsonObject propertyJson = new JsonObject();
                NamedNodeMap propertyAttributes = propertyDOM.getAttributes();
                for (int j = 0; j < propertyAttributes.getLength(); j++) {
                    Node attr = propertyAttributes.item(j);
                    propertyJson.addProperty(attr.getNodeName(), attr.getNodeValue());
                }
                propertiesJson.add(propertyJson);
            }
            commandJson.add("properties", propertiesJson);
            commandsJson.add(commandJson);
        }
        chainJson.add("commands", commandsJson);
    }
}
