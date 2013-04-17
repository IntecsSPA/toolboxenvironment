package it.intecs.pisa.toolbox.security;

import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.constants.ToolboxFoldersFileConstants;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.security.service.ToolboxSecurityWrapper;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xerces.dom.DeferredTextImpl;
import org.apache.xml.security.signature.XMLSignature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class ToolboxSecurityConfigurator {

    public static String WSPOLICY_NAMESPACE = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    public static String WSU_NAMESPACE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static String SERVICE_KEYSTORE_FILENAME = "service.jks";//the default name of the keystore for a given protected service

     public static String SECURED_OPERATION = "execute";
     public static String NOT_SECURED_OPERATION = "pass";
     public static String ACTION_MAPPING = "actionMapping";
      
    /**
     * Return the absolute path where the XACML policy file for the given service is located
     * @param serviceName
     * @return an empty string if the given service has no root in the current Toolbox configuration
     */
    public static String getXACMLpolicyDir(String serviceName) {
        File file = Toolbox.getInstance().getServiceRoot(serviceName);
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath() + File.separator + "Resources" + File.separator + "Security" + File.separator + "Policy";
    }

    public static File[] getXACMLfiles(String serviceName) {
        File policyDir = new File(ToolboxSecurityConfigurator.getXACMLpolicyDir(serviceName));
        /*File policyDir = new File (new File (new File
        (new File (Toolbox.getInstance().getServiceRoot(service.getServiceName()), service.getServiceName()), "Resources")
        , "Security"), "Policy");*/
        return policyDir.listFiles();
    }

    public static void removeXACMLfiles(String serviceName) {
        File[] f = ToolboxSecurityConfigurator.getXACMLfiles(serviceName);
        if (f == null || f.length == 0) {
            return;
        }
        for (int i = 0; i < f.length; i++) {
            f[i].delete();
        }
    }

    /**
     * Return the XACML url for the given service
     * @param serviceName
     * @return an empty string if the given service does not exist or it has not XACML policy
     */
    public static String getXACMLpolicyURL(String serviceName) {
        try {
            TBXService service = ServiceManager.getInstance().getService(serviceName);
            if (service == null) {
                return "";
            }
            if (service.isWSSecurity()) {
                //TODO currently only one XACML file...
                File[] arr = ToolboxSecurityConfigurator.getXACMLfiles(service.getServiceName());
                if (arr == null || arr.length == 0) {
                    return "";
                }

                String s = ToolboxSecurityConfigurator.getXACMLfiles(service.getServiceName())[0].toURL().toString();
                //s = s.replaceAll("/", "%2F");
                s = URLEncoder.encode(s, Charset.defaultCharset().toString());
                return s;
            } else {
                return "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Returns the location of the keystore associated to the given service
     * @author Stefano
     * @param service
     * @return
     */
    public static String getJKSlocation(Service service) {
        String s = "";
        if (service.hasWSSecurity() == false) {
            return "";
        }

        try {
            Element serviceElem = ToolboxSecurityConfigurator.getAxis2ServiceConfigurationElement(service);
            NodeList propList = serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "property");
            Element temp = null;
            for (int i = 0; i < propList.getLength(); i++) {
                temp = (Element) propList.item(i);
                if (temp.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.file") == 0) {
                    DeferredTextImpl t = (DeferredTextImpl) temp.getFirstChild();
                    return t.getData();
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Set the location of the keystore associated to the given secure service
     * @author Stefano
     * @param service
     *
    public static void storeJKSlocation(Service service, String location) throws ToolboxException{
    String s = "";
    if (service.hasWSSecurity() == false){
    //TODO exception here!
    return;
    }
    try {
    Element serviceElem = Axis2ServicesConf.getAxis2ServiceConfigurationElement(service);
    NodeList propList = serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "property");
    Element temp = null;
    for (int i=0; i<propList.getLength(); i++){
    temp = (Element) propList.item(i);
    if (temp.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.file")==0){
    DeferredTextImpl t = (DeferredTextImpl) temp.getFirstChild();
    t.setData(location);
    
    //save services.xml
    try {
    DOMUtil.dumpXML(serviceElem.getOwnerDocument(), ToolboxSecurityConfigurator.getServicesConfigFile());
    } catch (Exception e) {
    Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
    throw new ToolboxException("Impossible to write the services.xml Axis2 file");
    }
    }
    }
    
    } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } catch (SAXException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    }
    }*/
    public static void removeJKSfile(String serviceName) {
        File f = null;
        try {
            f = new File(ToolboxSecurityConfigurator.getJKSlocation(ServiceManager.getInstance().getService(serviceName)));
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        if (f != null && f.exists()) {
            f.delete();
        }
    }

    /**
     * Returns the username of the keystore associated to the given service
     * @author Stefano
     * @param service
     * @return
     */
    public static String getJKSuser(Service service) {
        String s = "";
        if (service.hasWSSecurity() == false) {
            return "";
        }

        try {
            Element serviceElem = ToolboxSecurityConfigurator.getAxis2ServiceConfigurationElement(service);
            Element userElem = (Element) serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "user").item(0);
            DeferredTextImpl t = (DeferredTextImpl) userElem.getFirstChild();
            if (t != null) {
                return t.getData();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Set the username of the keystore associated to the given service
     * @author Stefano
     * @param service
     * @return
     *
    public static void storeJKSuser(Service service, String jksUser) throws ToolboxException {
    String s = "";
    if (service.hasWSSecurity() == false){
    //TODO exception here!!!
    return;
    }
    
    try {
    Element serviceElem = Axis2ServicesConf.getAxis2ServiceConfigurationElement(service);
    Element userElem = (Element) serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "user").item(0);
    DeferredTextImpl t = (DeferredTextImpl) userElem.getFirstChild();
    t.setData(jksUser);
    //save services.xml
    try {
    DOMUtil.dumpXML(serviceElem.getOwnerDocument(), ToolboxSecurityConfigurator.getServicesConfigFile());
    } catch (Exception e) {
    Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
    throw new ToolboxException("Impossible to write the services.xml Axis2 file");
    }
    
    
    } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } catch (SAXException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    }
    }*/
    
    /**
     * Returns the password of the keystore associated to the given service
     * @author Stefano
     * @param service
     * @return
     */
    public static String getJKSpassword(Service service) {
        String s = "";
        if (service.hasWSSecurity() == false) {
            return "";
        }

        try {
            Element serviceElem = ToolboxSecurityConfigurator.getAxis2ServiceConfigurationElement(service);
            NodeList propList = serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "property");
            Element temp = null;
            for (int i = 0; i < propList.getLength(); i++) {
                temp = (Element) propList.item(i);
                if (temp.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.keystore.password") == 0) {
                    DeferredTextImpl t = (DeferredTextImpl) temp.getFirstChild();
                    if (t != null) {
                        return t.getData();
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Returns the password of the private key associated to the given service
     * @author Stefano
     * @param service
     * @return
     */
    public static String getKeyPassword(Service service) {
        String s = "";
        if (service.hasWSSecurity() == false) {
            return "";
        }

        try {
            Element serviceElem = ToolboxSecurityConfigurator.getAxis2ServiceConfigurationElement(service);
            NodeList propList = serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "property");
            Element temp = null;
            for (int i = 0; i < propList.getLength(); i++) {
                temp = (Element) propList.item(i);
                if (temp.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.alias.password") == 0) {
                    DeferredTextImpl t = (DeferredTextImpl) temp.getFirstChild();
                    if (t != null) {
                        return t.getData();
                    }
                }
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }

    /**
     * Set the password of the keystore associated to the given service
     * @author Stefano
     * @param service
     * @return
     *
    public static void setJKSpassword(Service service, String passwd) throws ToolboxException{
    String s = "";
    if (service.hasWSSecurity() == false){
    //TODO exception here!!!
    return;
    } 	
    try {
    Element serviceElem = Axis2ServicesConf.getAxis2ServiceConfigurationElement(service);
    NodeList propList = serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "property");
    Element temp = null;
    for (int i=0; i<propList.getLength(); i++){
    temp = (Element) propList.item(i);
    if (temp.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.keystore.password")==0){
    DeferredTextImpl t = (DeferredTextImpl) temp.getFirstChild();
    t.setData(passwd);
    
    //save services.xml
    try {
    DOMUtil.dumpXML(serviceElem.getOwnerDocument(), ToolboxSecurityConfigurator.getServicesConfigFile());
    } catch (Exception e) {
    Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
    throw new ToolboxException("Impossible to write the services.xml Axis2 file");
    }
    }
    }
    
    } catch (IOException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } catch (SAXException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
    }
    }*/
    
    /**
     * Returns the WS-Security policy associated to the given service
     * @author Stefano
     * @param service
     * @param includeRampartConfig Rampart service configuration is stored inside the WS-Security policy. Set it to false to remove Rampart configuration from the returned policy. 
     * @return 
     */
    public static Element getWSSecurityPolicy(Service service, boolean includeRampartConfig) {
        if (!service.hasWSSecurity()) {
            return null;
        }
        Element policyElem = null;
        try {
            Element serviceElem = ToolboxSecurityConfigurator.getAxis2ServiceConfigurationElement(service);
            policyElem = (Element) serviceElem.getElementsByTagNameNS("http://schemas.xmlsoap.org/ws/2004/09/policy", "Policy").item(0);
            if (includeRampartConfig == false) {
                //remove rampart configuration from the policy
                Element rampartElem = (Element) serviceElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "RampartConfig").item(0);
                rampartElem.getParentNode().removeChild(rampartElem);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return policyElem;
    }

    /**
     * Returns the WS-Security policy associated to the given service used for the asynchronous response
     * @author Stefano
     * @param service
     * @param includeRampartConfig Rampart service configuration is stored inside the WS-Security policy. Set it to false to remove Rampart configuration from the returned policy. 
     * @return 
     */
    public static Element getWSSecurityPolicyForAsynchronousResponse(Service service, boolean includeRampartConfig) {
        if (!service.hasWSSecurity()) {
            return null;
        }
        Element policyElem = null;
        try {
            File file = ToolboxSecurityConfigurator.getAsynchResponsePolicyFile(service);
            DOMUtil util = new DOMUtil();
            Document doc = util.fileToDocument(file);
            policyElem = (Element) doc.getDocumentElement();
            if (includeRampartConfig == false) {
                //remove rampart configuration from the policy
                Element rampartElem = (Element) policyElem.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "RampartConfig").item(0);
                rampartElem.getParentNode().removeChild(rampartElem);
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return policyElem;
    }

    /**
     * Returns the services.xml configurator file related to the ToolboxSecurityWrapper Axis2 service
     * @author Stefano
     * @return
     */
    public static File getServicesConfigFile() {
        return Toolbox.getInstance().getAxis2ServicesRoot(ToolboxSecurityWrapper.SERVICE_NAME + File.separator + "META-INF" + File.separator + "services.xml");
    }

    /**
     * Returns the path where the default policy for the asynchronous response is located
     * @author Stefano
     * @return
     */
    public static String getDefaultAsynchResponsePolicyPath() {
        File serviceFile = new File(new File(new File(Toolbox.getInstance().getRootDir(), ToolboxFoldersFileConstants.WEB_INF), "xml"), "HMAT_WSSecurityPolicy_AsynchResponse.xml");
        return serviceFile.getAbsolutePath();
    }

    /**
     * Returns the path where the policy for the asynchronous response is located
     * @author Stefano
     * @param service
     * @return
     */
    public static String getAsynchResponsePolicyPath(Service service) {
        File file = Toolbox.getInstance().getServiceRoot(service.getServiceName());
        if (file == null) {
            return "";
        }
        return file.getAbsolutePath() + File.separator + "Resources"
                + File.separator + "Security" + File.separator + "HMAT_WSSecurityPolicy_AsynchResponse.xml";
    }

    /**
     * Returns the policy used for asynchronous response
     * @author Stefano
     * @param service
     * @return
     */
    public static File getAsynchResponsePolicyFile(Service service) {
        File file = Toolbox.getInstance().getServiceRoot(service.getServiceName());
        if (file == null) {
            return null;
        }
        return new File(ToolboxSecurityConfigurator.getAsynchResponsePolicyPath(service));
    }

    public static File getDefaultHMATpolicyFile() {
        return new File(new File(new File(Toolbox.getInstance().getRootDir(), ToolboxFoldersFileConstants.WEB_INF), "xml"), "HMAT_WSSecurityPolicy.xml");
    }

    /**
     * Configures the WS-security layer for the given service:
     * -adds a new <service> entry to the ToolboxSecurityWrapper Axis2 configuration file (services.xml)
     * -sets the JKS information
     * @param descriptor
     */
    public static void addWSSecurityLayerForService(Service service) throws ToolboxException {
        //Retrieve ToolboxSecurityWrapper service configuration file, i.e. services.xml
        File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
        Document services_xmlDoc = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
            services_xmlDoc = db.parse(serviceDesFile);

        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to parse the services.xml Axis2 file");
            throw new ToolboxException("Impossible to parse the services.xml Axis2 file");

        }

        //ASSERTION: there is not a service tag with value 'newServiceName'
        //load <service> tag with WS-Security policy (default value accordingly to HMA-T requirement
        File serviceFile = ToolboxSecurityConfigurator.getDefaultHMATpolicyFile();
        Document service_xmlDoc = null;
        DOMUtil util = new DOMUtil();
        try {
            //service_xmlDoc= db.parse(serviceFile);
            service_xmlDoc = util.fileToDocument(serviceFile);

        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to parse the HMA-T policy file");
            throw new ToolboxException("Impossible to parse the HMA-T policy file");
        }
        //set current value for service attribute
        Element service_xmlElem = service_xmlDoc.getDocumentElement();
        service_xmlElem.setAttribute("name", service.getServiceName());
        //TODO any other attribute to set here?

        //Add service to services.xml
        Element serviceGroup = (Element) services_xmlDoc.getElementsByTagName("serviceGroup").item(0);
        Element importedNode = (Element) services_xmlDoc.importNode(service_xmlElem, true);
        serviceGroup.appendChild(importedNode);

        Element policyElem = (Element) importedNode.getElementsByTagName("wsp:Policy").item(0);
        setJKSinfo(policyElem, service);

        //save services.xml
        try {
            DOMUtil.dumpXML(services_xmlDoc, serviceDesFile);
        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
            throw new ToolboxException("Impossible to write the services.xml Axis2 file");
        }

        //copy the wss policy for the asynchronous response getDefaultAsynchResponsePolicyPath
        try {
            File defPolicyFile = new File(ToolboxSecurityConfigurator.getDefaultAsynchResponsePolicyPath());
            Document defPolicy_xmlDoc = null;
            try {
                //db = documentBuilderFactory.newDocumentBuilder();
                //defPolicy_xmlDoc= db.parse(defPolicyFile);
                defPolicy_xmlDoc = util.fileToDocument(defPolicyFile);
            } catch (Exception e) {
                Toolbox.getInstance().getLogger().error("Impossible to parse the policy file for the aynchronous response");
                throw new ToolboxException("Impossible to parse the policy file for the aynchronous response");

            }
            Element policy_xmlElem = defPolicy_xmlDoc.getDocumentElement();
            setJKSinfo(policy_xmlElem, service);
            //write wss policy
            try {
                DOMUtil.dumpXML(defPolicy_xmlDoc, new File(ToolboxSecurityConfigurator.getAsynchResponsePolicyPath(service)));
            } catch (Exception e) {
                Toolbox.getInstance().getLogger().error("Impossible to write the policy file for the aynchronous response");
                throw new ToolboxException("Impossible the policy file for the aynchronous response");
            }
        } catch (ToolboxException ex) {
            //remove the ws-security information already stored
            try {
                removeWSSecurityLayerForService(service.getServiceName());
            } catch (Exception reallyStrangeHere) {
            }
            throw ex;
        }
    }
    
    public static void addUnprotectedOperationToService(String serviceName, String soapAction) throws ToolboxException {
        //Retrieve ToolboxSecurityWrapper service configuration file, i.e. services.xml
        File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
        Document services_xmlDoc = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
            services_xmlDoc = db.parse(serviceDesFile);

        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to parse the services.xml Axis2 file");
            throw new ToolboxException("Impossible to parse the services.xml Axis2 file");

        }
        try {
            NodeList serviceNodes = services_xmlDoc.getElementsByTagName("service");
            Element serviceNode = null;
            String serviceAttrValue = null;
            for (int index = 0; index < serviceNodes.getLength(); index++) {
                serviceAttrValue = ((Element) serviceNodes.item(index)).getAttribute("name");
                if (serviceAttrValue.equals(serviceName)) {
                    serviceNode = (Element) serviceNodes.item(index);
                    break;
                }
            }

            NodeList operationNodes = serviceNode.getElementsByTagName("operation");
            Element operationNode = null;
            String operationAttrValue = null;
            for (int index = 0; index < operationNodes.getLength(); index++) {
                operationAttrValue = ((Element) operationNodes.item(index)).getAttribute("name");
                if (operationAttrValue.equals(NOT_SECURED_OPERATION)) {
                    operationNode = (Element) operationNodes.item(index);
                    break;
                }
            }

            Element actionMapping = services_xmlDoc.createElement(ACTION_MAPPING);
            Text actionMappingValue = services_xmlDoc.createTextNode(soapAction);
            actionMapping.appendChild(actionMappingValue);

            operationNode.appendChild(actionMapping);


        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to add the new operation to Axis2 service");
            throw new ToolboxException("Impossible to add the new operation to Axis2 service");
        }

        //save services.xml
        try {
            DOMUtil.dumpXML(services_xmlDoc, serviceDesFile);
        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
            throw new ToolboxException("Impossible to write the services.xml Axis2 file");
        }
    }
    
    public static void makeOperationSecureToService(String serviceName, String soapAction) throws ToolboxException {
        //Retrieve ToolboxSecurityWrapper service configuration file, i.e. services.xml
        File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
        Document services_xmlDoc = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
            services_xmlDoc = db.parse(serviceDesFile);

        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to parse the services.xml Axis2 file");
            throw new ToolboxException("Impossible to parse the services.xml Axis2 file");

        }
        try {
            NodeList serviceNodes = services_xmlDoc.getElementsByTagName("service");
            Element serviceNode = null;
            String serviceAttrValue = null;
            for (int index = 0; index < serviceNodes.getLength(); index++) {
                serviceAttrValue = ((Element) serviceNodes.item(index)).getAttribute("name");
                if (serviceAttrValue.equals(serviceName)) {
                    serviceNode = (Element) serviceNodes.item(index);
                    break;
                }
            }

            NodeList operationNodes = serviceNode.getElementsByTagName("operation");
            Element unsecOperationNode = null;
            Element secOperationNode = null;
            String operationAttrValue = null;
            for (int index = 0; index < operationNodes.getLength(); index++) {
                operationAttrValue = ((Element) operationNodes.item(index)).getAttribute("name");
                if (operationAttrValue.equals(NOT_SECURED_OPERATION)) {
                    unsecOperationNode = (Element) operationNodes.item(index);
                }
                if (operationAttrValue.equals(SECURED_OPERATION)) {
                    secOperationNode = (Element) operationNodes.item(index);
                }
            }

            NodeList actionMappings = unsecOperationNode.getElementsByTagName(ACTION_MAPPING);
            Element actionMapping = null;
            for (int index = 0; index < actionMappings.getLength(); index++) {
                actionMapping = (Element) actionMappings.item(index);
                if (actionMapping.getTextContent().contains(soapAction)) {
                    break;
                }
            }
            unsecOperationNode.removeChild(actionMapping);
            
            secOperationNode.appendChild(actionMapping);


        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to make the operation secured for the Axis2 service");
            throw new ToolboxException("Impossible to make the operation secured for the Axis2 service");
        }

        //save services.xml
        try {
            DOMUtil.dumpXML(services_xmlDoc, serviceDesFile);
        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
            throw new ToolboxException("Impossible to write the services.xml Axis2 file");
        }
    }

    /**
     * Updates the WS-security layer for the given service:
     * -sets the JKS information
     * @param descriptor
     */
    public static void updateWSSecurityLayerForService(Service service) throws ToolboxException {
        //Retrieve ToolboxSecurityWrapper service configuration file, i.e. services.xml
        File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
        Document services_xmlDoc = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
            services_xmlDoc = db.parse(serviceDesFile);

        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to parse the services.xml Axis2 file");
            throw new ToolboxException("Impossible to parse the services.xml Axis2 file");

        }

        //Retrieve the service from services.xml  
        String serviceName = service.getServiceName();
        NodeList serviceNodes = services_xmlDoc.getElementsByTagName("service");
        Element serviceNode = null;
        String serviceAttrValue = null;
        for (int index = 0; index < serviceNodes.getLength(); index++) {
            serviceAttrValue = ((Element) serviceNodes.item(index)).getAttribute("name");
            if (serviceAttrValue.equals(serviceName)) {
                serviceNode = (Element) serviceNodes.item(index);
                break;
            }
        }


        Element policyElem = (Element) serviceNode.getElementsByTagName("wsp:Policy").item(0);

        setJKSinfo(policyElem, service);

        //save services.xml
        try {
            DOMUtil.dumpXML(services_xmlDoc, serviceDesFile);
        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
            throw new ToolboxException("Impossible to write the services.xml Axis2 file");
        }

        //copy the wss policy for the asynchronous response getDefaultAsynchResponsePolicyPath
        DOMUtil util = new DOMUtil();
        try {
            File defPolicyFile = new File(ToolboxSecurityConfigurator.getDefaultAsynchResponsePolicyPath());
            Document defPolicy_xmlDoc = null;
            try {
                //db = documentBuilderFactory.newDocumentBuilder();
                //defPolicy_xmlDoc= db.parse(defPolicyFile);
                defPolicy_xmlDoc = util.fileToDocument(defPolicyFile);
            } catch (Exception e) {
                Toolbox.getInstance().getLogger().error("Impossible to parse the policy file for the aynchronous response");
                throw new ToolboxException("Impossible to parse the policy file for the aynchronous response");

            }
            Element policy_xmlElem = defPolicy_xmlDoc.getDocumentElement();
            setJKSinfo(policy_xmlElem, service);
            //write wss policy
            try {
                DOMUtil.dumpXML(defPolicy_xmlDoc, new File(ToolboxSecurityConfigurator.getAsynchResponsePolicyPath(service)));
            } catch (Exception e) {
                Toolbox.getInstance().getLogger().error("Impossible to write the policy file for the aynchronous response");
                throw new ToolboxException("Impossible the policy file for the aynchronous response");
            }
        } catch (ToolboxException ex) {
            //remove the ws-security information already stored
            try {
                removeWSSecurityLayerForService(service.getServiceName());
            } catch (Exception reallyStrangeHere) {
            }
            throw ex;
        }
    }

    /**
     * Removes the given service from the Axis2 services configuration list;
     * no exception is thrown if the given service is not found in the list.
     * @param newServiceName
     * @author Stefano
     */
    public static void removeWSSecurityLayerForService(String serviceName) throws ToolboxException {
        File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
        Document services_xmlDoc = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = documentBuilderFactory.newDocumentBuilder();
            services_xmlDoc = db.parse(serviceDesFile);

        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to parse the services.xml Axis2 file");
            throw new ToolboxException("Impossible to parse the services.xml Axis2 file");
        }
        //search for the service which name is the given serviceName
        Element serviceGroup = (Element) services_xmlDoc.getElementsByTagName("serviceGroup").item(0);
        NodeList list = serviceGroup.getElementsByTagName("service");
        Element temp = null;
        boolean found = false;
        for (int i = 0; i < list.getLength(); i++) {
            temp = (Element) list.item(i);
            if (temp.getAttribute("name").compareTo(serviceName) == 0) {
                found = true;
                break;
            }
        }
        if (!found) {
            return;
        }
        serviceGroup.removeChild(temp);
        //now save the new document
        try {
            DOMUtil.dumpXML(services_xmlDoc, serviceDesFile);
        } catch (Exception e) {
            Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
            throw new ToolboxException("Impossible to write the services.xml Axis2 file");
        }
    }

    /**
     * Returns the <service> Element in the axis2 ToolboxSecurityWrapper configuration file:
     * this service has such Element only if it has wssecurity applied.
     * 
     * @throws IOException :
     * @throws SAXException :
     * 
     * @author Stefano
     */
    public static Element getAxis2ServiceConfigurationElement(Service service) throws IOException, SAXException {
        if (!service.hasWSSecurity()) {
            return null;
        }
        File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
        DOMUtil util = new DOMUtil();
        Document doc = util.fileToDocument(serviceDesFile);
        Element rootelem = doc.getDocumentElement();
        NodeList list = rootelem.getElementsByTagName("service");
        Element temp = null;
        Element serviceElem = null;
        for (int i = 0; i < list.getLength(); i++) {
            temp = (Element) list.item(i);
            if (temp.getAttribute("name").compareTo(service.getServiceName()) == 0) {
                serviceElem = temp;
                break;
            }
        }
        return serviceElem;
    }

    /**
     * Stores the WSS security information for the given services. The information are stored in the ToolboxSecurityWrapper axis2
     * configurator file (services.xml) and in the policy used for the asynchronous response.
     * @author Stefano
     * @param service
     */
    public static void storeJKSinfo(Service service) {
        String s = "";
        if (service.hasWSSecurity() == false) {
            Toolbox.getInstance().getLogger().error("Trying to set jks information for the unsecure service " + service.getServiceName());
            return;
        }
        try {
            //store the  information in the ToolboxSecurityWrapper services.xml configuration file
            Element policy = ToolboxSecurityConfigurator.getWSSecurityPolicy(service, true);
            ToolboxSecurityConfigurator.setJKSinfo(policy, service);
            //save services.xml
            File serviceDesFile = ToolboxSecurityConfigurator.getServicesConfigFile();
            try {
                DOMUtil.dumpXML(policy.getOwnerDocument(), serviceDesFile);
            } catch (Exception e) {
                Toolbox.getInstance().getLogger().error("Impossible to write the services.xml Axis2 file");
                throw new ToolboxException("Impossible to write the services.xml Axis2 file");
            }
            //store the information in the policy file for the asynchronous response
            Element policyForAsynch = ToolboxSecurityConfigurator.getWSSecurityPolicyForAsynchronousResponse(service, true);
            ToolboxSecurityConfigurator.setJKSinfo(policyForAsynch, service);
            //save policy file
            //write wss policy
            try {
                DOMUtil.dumpXML(policyForAsynch.getOwnerDocument(), new File(ToolboxSecurityConfigurator.getAsynchResponsePolicyPath(service)));
            } catch (Exception e) {
                Toolbox.getInstance().getLogger().error("Impossible to write the policy file for the aynchronous response");
                throw new ToolboxException("Impossible the policy file for the aynchronous response");
            }

        } catch (Exception ex) {
        }

    }

    /**
     * Takes in input a wsp:policy (xmlns:wsp="http://schemas.xmlsoap.org/ws/2004/09/policy")
     * Element and set the JKS information stored in the given Service.
     * 
     * @author Stefano
     * @param policy
     * @param service
     */
    public static void setJKSinfo(Element policy, Service service) {
        //set jks information
        Document doc = policy.getOwnerDocument();
        //Element policyElem = (Element) policy.getElementsByTagName("wsp:Policy").item(0);
        //TODO retrieve RampartConfig using xpath???
        //Element rampartConfig = (Element) ((Element) policy.getElementsByTagName("wsp:All").item(0)).getElementsByTagName("ramp:RampartConfig").item(0);

        //Element rampartConfig = (Element) policy.getElementsByTagNameNS("http://ws.apache.org/rampart/policy", "RampartConfig").item(0);
        
        NodeList rampartConfigList = policy.getElementsByTagName("ramp:RampartConfig");
        Element rampartConfig = (Element) rampartConfigList.item(0);

        Element userElem = (Element) rampartConfig.getElementsByTagName("ramp:user").item(0);

        DeferredTextImpl t = (DeferredTextImpl) userElem.getFirstChild();
        if (t != null) {
            t.setData(service.getJKSuser());
        } else {
            userElem.appendChild(doc.createTextNode(service.getJKSuser()));
        }

        NodeList rampConfnodeList = rampartConfig.getChildNodes();
        Element el = null;
        Object temp = null;

        for (int j = 0; j < rampConfnodeList.getLength(); j++) {
            temp = rampConfnodeList.item(j);
            if (!(temp instanceof Element)) {
                continue;
            }
            el = (Element) temp;
            if (el.getTagName().compareTo("ramp:signatureCrypto") != 0
                    && el.getTagName().compareTo("ramp:encryptionCrypto") != 0) {
                continue;
            }
            Element crypto = (Element) el.getElementsByTagName("ramp:crypto").item(0);
            NodeList list = crypto.getChildNodes();
            Element el2 = null;
            for (int i = 0; i < list.getLength(); i++) {
                temp = list.item(i);
                if (!(temp instanceof Element)) {
                    continue;
                }
                el2 = (Element) temp;
                if (el2.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.file") == 0) {
                    t = (DeferredTextImpl) el2.getFirstChild();
                    if (t != null) {
                        t.setData(service.getJKSlocation());
                    } else {
                        el2.appendChild(doc.createTextNode(service.getJKSlocation()));
                    }
                } else if (el2.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.keystore.password") == 0) {

                    t = (DeferredTextImpl) el2.getFirstChild();
                    if (t != null) {
                        t.setData(service.getJKSpasswd());
                    } else {
                        el2.appendChild(doc.createTextNode(service.getJKSpasswd()));
                    }
                } else if (el2.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.keystore.alias") == 0) {

                    t = (DeferredTextImpl) el2.getFirstChild();
                    if (t != null) {
                        t.setData(service.getJKSuser());
                    } else {
                        el2.appendChild(doc.createTextNode(service.getJKSuser()));
                    }
                } else if (el2.getAttribute("name").compareTo("org.apache.ws.security.crypto.merlin.alias.password") == 0) {

                    t = (DeferredTextImpl) el2.getFirstChild();
                    if (t != null) {
                        t.setData(service.getKeyPasswd());
                    } else {
                        el2.appendChild(doc.createTextNode(service.getKeyPasswd()));
                    }
                }
            }
        }
    }

    /**
     * Returns the default security resource (i.e. folder) for the given service
     * @author Stefano
     * @param service
     * @return
     */
    public static File getSecurityResource(Service service) {
        File serviceRoot = Toolbox.getInstance().getServiceRoot(service.getServiceName());
        File securityFolder = new File(serviceRoot, "Resources" + File.separator + "Security");
        return securityFolder;
    }

    /**
     * Return the Key from the given KeyStore associated to the given user and password.
     * @author Stefano
     * @param jks
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    public static Key getKeyFromCertificate(InputStream jks, String username, String password) throws Exception {
        KeyStore caKs = KeyStore.getInstance("JKS");

        caKs.load(jks, password.toCharArray());

        X509Certificate cert = (X509Certificate) caKs.getCertificate(username);
        X509Certificate[] issuerCerts = {cert};

        String sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_RSA;
        String pubKeyAlgo = issuerCerts[0].getPublicKey().getAlgorithm();
        if (pubKeyAlgo.equalsIgnoreCase("DSA")) {
            sigAlgo = XMLSignature.ALGO_ID_SIGNATURE_DSA;
        }

        java.security.Key issuerPK = caKs.getKey(username, password.toCharArray());
        return issuerPK;
    }
}
