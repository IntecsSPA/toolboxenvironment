/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.security.chain;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.apache.log4j.Logger;

/**
 *
 * @author mariarosaria
 */
public class ConfigurationSecurityCommands {

    private static String COMMANDS_CONFIGURATION_FILE = "WEB-INF/xml/configuredCommands.xml";
    private static String AUTHENTICATION_COMMANDS = "authenticationCommands";
    private static String AUTHORIZATION_COMMANDS = "authorizationCommands";
    static Logger logger = Logger.getLogger(ConfigurationSecurityCommands.class);

    public Document createChainForService(String serviceName, JsonObject serviceInformationJson) {
        Document serviceChain = null;

        try {
            File stepsFile = new File(Toolbox.getInstance().getRootDir(), COMMANDS_CONFIGURATION_FILE);

            DOMUtil domUtil = new DOMUtil();
            Document doc = domUtil.fileToDocument(stepsFile);
            serviceChain = (Document) doc.cloneNode(true);

            // Set the name of the service catalog commands
            Element catalogEl = (Element) serviceChain.getElementsByTagName("catalog").item(0);
            Attr attr = catalogEl.getAttributeNode("id");
            attr.setValue(serviceName);

            logger.info("Creating service command chain for service " + serviceName);

            NodeList chains = serviceChain.getElementsByTagName("chain");
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

            JsonObject chosenCommandsJson = serviceInformationJson.getAsJsonObject("chosenCommands");

            JsonObject authenticationJson = chosenCommandsJson.getAsJsonObject("authentication");
            JsonObject authorizationJson = chosenCommandsJson.getAsJsonObject("authorization");

            configureServiceCommands(authenticationChain, authenticationJson);
            configureServiceCommands(authorizationChain, authorizationJson);

        } catch (Exception ex) {
            logger.error("Error when creating command chain for service " + serviceName);
            ex.printStackTrace();
            return null;
        }

        return serviceChain;
    }

    public String saveServiceChainToFile(String serviceName, Document serviceChain) {
        String filePath = null;
        try {
            if (serviceChain == null) {
                logger.error("The service command chain for service " + serviceName + " is null");
                return filePath;
            }

            logger.info("Saving service command chain for service " + serviceName + " to temporary folder");

            File outputFile = File.createTempFile("serviceChain", ".xml");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(serviceChain);
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);

            filePath = outputFile.getCanonicalPath();

            // filePath = filePath.replaceAll("\\\\", "\\\\\\\\");

        } catch (TransformerException tfe) {
            logger.error("Error when saving service command chain for service " + serviceName);
            tfe.printStackTrace();
        } catch (IOException ioe) {
            logger.error("Error when saving service command chain for service " + serviceName);
            ioe.printStackTrace();
        }
        return filePath;

    }

    public String createAndSaveServiceChainToTempFolder(String serviceName, JsonObject serviceInformationJson) {
        String pathFile = null;
        try {
            Document serviceChain = createChainForService(serviceName, serviceInformationJson);
            pathFile = saveServiceChainToFile(serviceName, serviceChain);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return pathFile;
    }

    public void createAndSaveServiceChainToServiceFolder(String serviceName, JsonObject serviceInformationJson) throws Exception {

        Document serviceChain = createChainForService(serviceName, serviceInformationJson);
        File serviceRoot = Toolbox.getInstance().getServiceRoot(serviceName);
        File serviceChainFile = new File(serviceRoot + File.separator + "serviceChain.xml");
        DOMUtil.dumpXML(serviceChain, serviceChainFile);
    }

    private void configureServiceCommands(Element chainDOM, JsonObject chainJson) {
        JsonArray commandsJson = chainJson.getAsJsonArray("commands");

        for (int i = 0; i < commandsJson.size(); i++) {
            JsonObject commandJson = commandsJson.get(i).getAsJsonObject();
            String commandId = commandJson.get("id").getAsString();
            Element commandDOM = getElementByAttributeId(chainDOM, "command", commandId);
            if (!commandJson.get("selected").getAsBoolean()) {
                if (commandDOM != null) {
                    chainDOM.removeChild(commandDOM);
                    continue;
                }
            }
            JsonArray propertiesJson = commandJson.getAsJsonArray("properties");

            for (int j = 0; j < propertiesJson.size(); j++) {
                JsonObject propertyJson = propertiesJson.get(j).getAsJsonObject();
                String propertyId = propertyJson.get("id").getAsString();
                String propertyType = propertyJson.get("type").getAsString();
                String propertyValue = null;
                if (propertyType.compareTo("text") == 0) {
                    propertyValue = propertyJson.get("value").getAsString();
                } else if (propertyType.compareTo("file") == 0) {

                    JsonObject propertyValueObject = propertyJson.get("value").getAsJsonObject();
                    propertyValue = propertyValueObject.get("fileName").getAsString();
                }

                Element propertyDOM = getElementByAttributeId(commandDOM, "property", propertyId);
                Attr propertyValueDOM = propertyDOM.getAttributeNode("value");
                propertyValueDOM.setValue(propertyValue);
                propertyDOM.removeAttribute("selected");

            }
        }
    }

    private Element getElementByAttributeId(Element parent, String elementSearched, String elementIdValue) {
        Element el = null;

        NodeList els = parent.getElementsByTagName(elementSearched);
        for (int i = 0; i < els.getLength(); i++) {
            el = (Element) els.item(i);
            if (el.getAttribute("id").compareTo(elementIdValue) == 0) {
                break;
            }
        }
        return el;
    }
}
