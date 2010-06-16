/**
 * This class is used to hold all information specific to a Service
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.util.DOMUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class Service {

    protected static final String DESCRIPTOR_VERSION = "2.2";
    private static final String ATTRIBUTE_VERSION = "version";
    private static final String TAG_INTERFACE = "interface";
    private static final String TAG_SCHEMA_SET_LOCATION = "schemaSetLocation";
    public static final String DESCRIPTOR_NAMESPACE = "http://pisa.intecs.it/mass/toolbox/serviceDescriptor";
    private static final String TAG_SERVICE = "service";
    private static final String ATTRIBUTE_SSL_CERTIFICATE = "sslCertificate";
    private static final String ATTRIBUTE_SUSPEND_MODE = "suspendMode";
    private static final String ATTRIBUTE_QUEUING = "queuing";
    private static final String ATTRIBUTE_SERVICE_NAME = "serviceName";
    public static final String SUSPEND_MODE_HARD = "hard";
    public static final String SUSPEND_MODE_SOFT = "soft";
    public static final String ATTRIBUTE_WSSECURITY = "wssecurity";
    protected InputStream serviceAbstract = null;
    protected InputStream serviceDescription = null;
    protected String serviceName = new String();
    protected boolean queuing = false;
    protected String suspendMode = null;
    protected String SSLcertificate = null;
    protected Interface implementedInterface = null;
    protected DOMUtil domutil = null;
    protected String version = null;
    protected boolean wssecurity = false;
    /**
     * The following attributes regarding JKS are not stored in the serviceDescriptor file, in fact they appear in the Axis2
     * service configuration file.
     */
    //the location of the key store
    private String jksLocation = "";
    //the user-name of the key store
    private String jksUser = "";
    //the password of the keystore
    private String jksPasswd ="";
    //the password for the private key
    private String keyPasswd ="";

    public Service() {
        domutil = new DOMUtil();

        serviceAbstract=new ByteArrayInputStream("".getBytes());
        serviceDescription=new ByteArrayInputStream("".getBytes());
    }

     public void copyFrom(Service s)
    {
        serviceName=new String(s.getServiceName());
        queuing=s.isQueuing();
        suspendMode=new String(s.getSuspendMode());
        SSLcertificate=new String(s.getSSLcertificate());
        version=new String(s.getVersion());

        this.implementedInterface=(Interface) s.getImplementedInterface().clone();
        adjustReferences();
    }


    public boolean isQueuing() {
        return queuing;
    }

    public void setQueuing(boolean queuing) {
        this.queuing = queuing;
    }

    public String getSuspendMode() {
        return suspendMode;
    }

    public void setSuspendMode(String suspendMode) {
        this.suspendMode = suspendMode;
    }

    public Document createServiceDescriptor() {
        Document descriptor = null;
        Element root = null;
        Element schemaRootEl = null;
        LinkedList interfacesChildren = null;

        descriptor = domutil.newDocument();
        root = descriptor.createElementNS(DESCRIPTOR_NAMESPACE, TAG_SERVICE);

        descriptor.appendChild(root);
        root.setAttribute(ATTRIBUTE_VERSION, DESCRIPTOR_VERSION);
        root.setAttribute(ATTRIBUTE_QUEUING, Boolean.toString(queuing));
        root.setAttribute(ATTRIBUTE_SERVICE_NAME, serviceName);
        
        root.setAttribute(ATTRIBUTE_SUSPEND_MODE, suspendMode);
       
        if (SSLcertificate != null) {
            root.setAttribute(ATTRIBUTE_SSL_CERTIFICATE, SSLcertificate);
        }

        root.setAttribute(ATTRIBUTE_WSSECURITY, ""+wssecurity);
  
        try {
            implementedInterface.appendToDoc(root);

            //updating schemaDir
            interfacesChildren = DOMUtil.getChildrenByTagName(root, TAG_INTERFACE);
            for (int i = 0; i < interfacesChildren.size(); i++) {
                Element child = (Element) interfacesChildren.get(i);
                schemaRootEl = DOMUtil.getChildByTagName(child, TAG_SCHEMA_SET_LOCATION);
                schemaRootEl.setTextContent("Schemas");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return descriptor;
    }

    public void initializeFromXMLDescriptor(Element serviceEl) throws Exception {
        String value = null;
        LinkedList children = null;
        Element interfaceEl = null;

        try {
            this.version = serviceEl.getAttribute(ATTRIBUTE_VERSION);

            value = serviceEl.getAttribute(ATTRIBUTE_QUEUING);
            this.setQueuing(Boolean.parseBoolean(value));

            value = serviceEl.getAttribute(ATTRIBUTE_SERVICE_NAME);
            this.setServiceName(value);

            value = serviceEl.getAttribute(ATTRIBUTE_SUSPEND_MODE);
            this.setSuspendMode(value);

            value = serviceEl.getAttribute(ATTRIBUTE_SSL_CERTIFICATE);
            this.setSSLcertificate(value);
            
            children = DOMUtil.getChildrenByTagName(serviceEl, TAG_INTERFACE);
            interfaceEl = (Element) children.get(0);
            
            value = serviceEl.getAttribute(ATTRIBUTE_WSSECURITY);
            this.setWSSsecurity(Boolean.valueOf(value));

            initInterfaceFromXML(interfaceEl);

            adjustReferences();
        } catch (Exception ecc) {
            ecc.printStackTrace();
        }
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public InputStream getServiceAbstract() {
       return serviceAbstract;
    }

    public void setServiceAbstract(InputStream serviceAbstract) {
        this.serviceAbstract = serviceAbstract;
    }

    public InputStream getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(InputStream serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getSSLcertificate() {
        return SSLcertificate;
    }

    public void setSSLcertificate(String lcertificate) {
        SSLcertificate = lcertificate;
    }
    
    public boolean hasWSSecurity() {
        return wssecurity;
    }

    public void setWSSsecurity(boolean security) {
    	wssecurity = security;
    }

    public Interface getImplementedInterface() {
        return implementedInterface;
    }

    public void setImplementedInterface(Interface implementedInterface) {
        this.implementedInterface = implementedInterface;
    }

    public String getSchemaPath() {
        if (this.implementedInterface != null) {
            return this.implementedInterface.getSchemaDir();
        } else {
            return null;
        }
    }

    public String getFullSchemaPath() {
        if (this.implementedInterface != null) {
            return this.implementedInterface.getSchemaDir() + "/" + implementedInterface.getSchemaRoot();
        } else {
            return null;
        }
    }

    public boolean loadFromStream(InputStream descriptorFile) {
        DOMUtil util = null;
        Document doc = null;

        try {
            util = new DOMUtil();
            doc = util.inputStreamToDocument(descriptorFile);

            initializeFromXMLDescriptor(doc.getDocumentElement());

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean loadFromFile(File descriptorFile) {
        DOMUtil util = null;
        Document doc = null;

        try {
            util = new DOMUtil();
            doc = util.fileToDocument(descriptorFile);

            initializeFromXMLDescriptor(doc.getDocumentElement());

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean loadFromFile(String filePath) {
        File descriptorFile = null;

        descriptorFile = new File(filePath);
        if (descriptorFile != null && descriptorFile.exists()) {
            return loadFromFile(descriptorFile);
        } else {
            return false;
        }
    }

    public void dumpToDisk(File file) {
        Document doc = null;

        try {
            doc = this.createServiceDescriptor();

            DOMUtil.dumpXML(doc, file);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    public void dumpToDisk(String filePath) {
        File file = null;

        try {
            file = new File(filePath);

            dumpToDisk(file);
        } catch (final Exception e) {
            e.printStackTrace();
        }

    }

    public String getVersion() {
        return version;
    }

    protected void initInterfaceFromXML(Element interfaceEl) {
        implementedInterface = new Interface();
        implementedInterface.initFromXML(interfaceEl);
    }
    
    /**
     * 
     * @return
     * @author Stefano
     */
    public boolean isWSSecurity(){
    	return wssecurity;
    }
    
    /**
     * 
     * @param sec
     * @author Stefano
     */
    public void setWSSecurity(boolean sec){
    	wssecurity = sec;
    }
    
    /**
     * 
     * @param location
     * @author Stefano
     */
    public void setJKSlocation(String location){
    	jksLocation = location;
    }
    
    /**
     * 
     * @author Stefano
     * @return
     */
    public String getJKSlocation(){
    	return jksLocation;
    }
    
    /**
     * 
     * @author Stefano
     * @param user
     */
    public void setJKSuser(String user){
    	jksUser = user;
    }
    
    /**
     * 
     * @author Stefano
     * @return
     */
    public String getJKSuser(){
    	return jksUser;
    }
    
    /**
     * 
     * @author Stefano
     * @param passwd
     */
    public void setJKSpasswd(String passwd){
    	jksPasswd = passwd;
    }
    
    /**
     * 
     * @author Stefano
     * @return
     */
    public String getJKSpasswd(){
    	return jksPasswd;
    }
    
    /**
     * 
     * @author Stefano
     * @param passwd
     */
    public void setKeyPasswd(String passwd){
    	keyPasswd = passwd;
    }
    
    /**
     * 
     * @author Stefano
     * @return
     */
    public String getKeyPasswd(){
    	return keyPasswd;
    }


    public void adjustReferences()
    {
        if(implementedInterface!=null)
            implementedInterface.setParent(this);
    }

    /**
     * Returns the WS-Security associated policy, if any.
     * child classes can override this method
     * @author Stefano
     * @return The DOM Element corresponding to the associated policy.
     */
    public Element getWSSPolicy(){
    	return null;
    }
}
