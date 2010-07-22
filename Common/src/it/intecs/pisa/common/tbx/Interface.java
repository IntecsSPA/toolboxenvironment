/**
 * 
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.common.tbx.lifecycle.LifeCycle;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Massimiliano
 *
 */
public class Interface {
    public static final String VAR_TABLE_TYPE="type";
    public static final String VAR_TABLE_VALUE="value";
    public static final String VAR_TABLE_DISPLAY_TEXT="displayedText";

    public static final String TAG_LIFE_CYCLE_SCRIPTS = "lifeCycleScripts";
    public static final String TAG_ADMITTED_HOSTS = "admittedHosts";
    public static final String INTERFACE_TYPE_STANDARD = "Standard";
    public static final String TAG_VARIABLES = "variables";
    public static final String TAG_VARIABLE = "variable";
    protected static final String TAG_MATCH = "match";
    protected static final String TAG_OPERATIONS_REDEFINITION = "operationsRedefinition";
    protected static final String TAG_TARGET_NAME_SPACE = "targetNameSpace";
    protected static final String TAG_OPERATION = Operation.TAG_OPERATION;
    protected static final String TAG_OPERATIONS = "operations";
    protected static final String TAG_WSDL_FILE = "wsdlFile";
    protected static final String TAG_SCHEMA_SET_LOCATION = "schemaSetLocation";
    protected static final String TAG_ROOT_SCHEMA_FILE = "rootSchemaFile";
    public static final String TAG_INTERFACE = "interface";
    protected static final String TAG_PROPERTY_NAME = "propertyName";
    protected static final String TAG_PROPERTY_VALUE = "propertyValue";
    protected static final String ATTRIBUTE_VERSION = "version";
    protected static final String ATTRIBUTE_NAME = "name";
    protected static final String ATTRIBUTE_TYPE = "type";
    protected static final String ATTRIBUTE_MODE = "mode";
    protected static final String ATTRIBUTE_VALIDATION="schemaValidation";
    protected String name = null;
    protected String version = null;
    protected String type = null;
    protected String mode = null;
    protected String schemaRoot = null;
    protected String schemaDir = null;
    protected String wsdlpath = null;
    protected String targetNameSpace = null;
    protected Operation[] operations = null;
    protected Hashtable<String, Hashtable<String, String>> userVariables;
    protected String admittedHosts = "";
    protected LifeCycle serviceLifeCycle;
    protected Service parentService;
    private boolean validationActive=true;

    public Interface() {
        operations = new Operation[0];
        userVariables = new Hashtable<String, Hashtable<String, String>>();
    }

    public void dumpInterface() {
        for (Operation op : this.operations) {
            op.dumpOperation();
        }
    }

    public Service getParent() {
        return this.parentService;
    }

    public boolean isForServiceType(String serviceType) {
        return type.equals(serviceType);
    }

    protected Hashtable<String, Hashtable<String, String>> cloneUserVariablesTable(Hashtable<String, Hashtable<String, String>> cloneFrom) {
        Hashtable<String, Hashtable<String, String>> cloneTo;
        String key, fieldkey;
        Enumeration<String> keys, fieldkeys;
        Hashtable<String, String> clonedUserVariable = null, userVar;

        cloneTo = new Hashtable<String, Hashtable<String, String>>();

        keys = cloneFrom.keys();
        while (keys.hasMoreElements()) {
            key = keys.nextElement();

            userVar = cloneFrom.get(key);
            clonedUserVariable = new Hashtable<String, String>();

            fieldkeys = userVar.keys();
            while (fieldkeys.hasMoreElements()) {
                fieldkey = fieldkeys.nextElement();

                clonedUserVariable.put(fieldkey, new String(userVar.get(fieldkey)));
            }

            cloneTo.put(new String(key), clonedUserVariable);
        }

        return cloneTo;
    }

    @Override
    public synchronized Object clone() {
        Interface interf;
        Operation[] copyOperation;
        int i = 0;
        LifeCycle lifeCycle;

        interf = new Interface();
        interf.setName(new String(name));
        interf.setVersion(new String(version));
        interf.setMode(new String(mode));
        interf.setType(new String(type));
        if (schemaRoot != null) {
            interf.setSchemaRoot(new String(schemaRoot));
        }
        interf.setSchemaDir(new String(schemaDir));
        if (wsdlpath != null) {
            interf.setWsdlPath(new String(wsdlpath));
        }

        if(targetNameSpace!=null)
         interf.setTargetNameSpace(new String(targetNameSpace));
        
        interf.setAdmittedHosts(new String(admittedHosts));
        copyOperation = new Operation[operations.length];

        for (Operation op : operations) {
            copyOperation[i] = (Operation) op.clone();
            i++;
        }

        interf.setOperations(copyOperation);

        interf.setUserVariables(cloneUserVariablesTable(userVariables));

        if (this.serviceLifeCycle != null) {
            lifeCycle = (LifeCycle) this.serviceLifeCycle.clone();
            interf.setServiceLifeCycle(lifeCycle);
        }
        return interf;
    }

    public void addOperations(Operation op) {
        Operation[] newarray = null;

        newarray = new Operation[operations.length + 1];

        for (int i = 0; i < operations.length; i++) {
            newarray[i] = operations[i];
        }

        newarray[operations.length] = op;
        op.parentInterf=this;
        operations = newarray;

    }

    public boolean isSoapActionImplemented(String soapAction) {
        for (Operation op : this.operations) {
            if (op.getSoapAction().equals(soapAction)) {
                return true;
            }
        }

        return false;
    }

    public boolean isOperationImplemented(String operationName) {
        for (Operation op : this.operations) {
            if (op.getName().equals(operationName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method assumes that the root node is a <interface> node
     * @param f
     */
    public void initFromFile(File f) {
        Document interfDoc;
        DOMUtil util;

        try {
            util = new DOMUtil();
            interfDoc = util.fileToDocument(f);

            initFromXML(interfDoc.getDocumentElement());

        } catch (IOException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void initFromXML(Element interfNode) {
        Element schemaRootEl = null;
        Element schemaSetEl = null;
        Element WSDLFileEl = null;
        Element targetNameSpaceEl = null;
        Element operationsEl = null;
        Element operationEl = null;
        Element userVariablesEl;
        Element userVariableEl;
        Element admittedHostsEl;
        Element serviceLifeCycleEl;
        LinkedList childrenEl;
        Iterator iter;
        String userVarKey, userVarValueStr, userVarType;
        Object userVarValue;
        String userVarDisplayedText;
        Hashtable<String, String> varTable;

        try {

            //getting nodesinitFromXML
            schemaRootEl = DOMUtil.getChildByTagName(interfNode, TAG_ROOT_SCHEMA_FILE);
            schemaSetEl = DOMUtil.getChildByTagName(interfNode, TAG_SCHEMA_SET_LOCATION);
            WSDLFileEl = DOMUtil.getChildByTagName(interfNode, TAG_WSDL_FILE);
            targetNameSpaceEl = DOMUtil.getChildByTagName(interfNode, TAG_TARGET_NAME_SPACE);

            //setting fields
            name = interfNode.getAttribute(ATTRIBUTE_NAME);
            version = interfNode.getAttribute(ATTRIBUTE_VERSION);
            mode = interfNode.getAttribute(ATTRIBUTE_MODE);
            type = interfNode.getAttribute(ATTRIBUTE_TYPE);

            if (type == null || type.equals("")) {
                type = INTERFACE_TYPE_STANDARD;
            }

            if (mode == null) {
                mode = "";
            }

            if(interfNode.hasAttribute(ATTRIBUTE_VALIDATION)==true)
               validationActive=Boolean.parseBoolean(interfNode.getAttribute(ATTRIBUTE_VALIDATION));


            schemaRoot = DOMUtil.getTextFromNode(schemaRootEl);
            schemaDir = DOMUtil.getTextFromNode(schemaSetEl);
            wsdlpath = DOMUtil.getTextFromNode(WSDLFileEl);
            targetNameSpace = DOMUtil.getTextFromNode(targetNameSpaceEl);

            operationsEl = DOMUtil.getChildByTagName(interfNode, TAG_OPERATIONS);

            operations = getOperationsDefinition(operationsEl);

            userVariablesEl = DOMUtil.getChildByTagName(interfNode, TAG_VARIABLES);
            if (userVariablesEl != null) {
                childrenEl = DOMUtil.getChildren(userVariablesEl);
                this.userVariables = new Hashtable<String, Hashtable<String, String>>();

                iter = childrenEl.iterator();
                while (iter.hasNext()) {
                    userVariableEl = (Element) iter.next();
                    userVarKey = userVariableEl.getAttribute("name");
                    userVarValueStr = userVariableEl.getAttribute("value");
                    userVarType = userVariableEl.getAttribute("type");
                    userVarDisplayedText= userVariableEl.getAttribute("displayedText");

                    varTable=new Hashtable<String,String>();
                    varTable.put(VAR_TABLE_TYPE, userVarType);
                    varTable.put(VAR_TABLE_DISPLAY_TEXT, userVarDisplayedText);
                    varTable.put(VAR_TABLE_VALUE, userVarValueStr);
                    
                    userVariables.put(userVarKey, varTable);
                }
            }

            admittedHostsEl = DOMUtil.getChildByTagName(interfNode, TAG_ADMITTED_HOSTS);
            if (admittedHostsEl != null) {
                admittedHosts = admittedHostsEl.getTextContent();
            } else {
                admittedHosts = "";
            }


            serviceLifeCycleEl = DOMUtil.getChildByTagName(interfNode, TAG_LIFE_CYCLE_SCRIPTS);
            if (serviceLifeCycleEl != null) {
                serviceLifeCycle = new LifeCycle();
                serviceLifeCycle.initFromXML(serviceLifeCycleEl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void appendToDoc(Element parent) {
        Element newInterface = null;
        Element rootSchemaFileEl = null;
        Element schemaSetLocationEl = null;
        Element wsdlFileEl = null;
        Element operationsEl = null;
        Element targetNameSpaceEL = null;
        Element userVarsEl;
        Element userVarEl;
        Element admittedHostsEl = null;
        Document doc = null;
        Enumeration<String> userVarsKeys,fieldKeys;
        String userVarKey;
        String key,value;
        Hashtable<String,String> userVarValue;

        try {
            doc = parent.getOwnerDocument();

            //		creating interface node
            newInterface = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_INTERFACE);
            newInterface.setAttribute(ATTRIBUTE_NAME, name);
            newInterface.setAttribute(ATTRIBUTE_VERSION, version);
            newInterface.setAttribute(ATTRIBUTE_TYPE, type);
            newInterface.setAttribute(ATTRIBUTE_MODE, mode);
            newInterface.setAttribute(ATTRIBUTE_VALIDATION, Boolean.toString(validationActive));

            parent.appendChild(newInterface);

            rootSchemaFileEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_ROOT_SCHEMA_FILE);
            rootSchemaFileEl.setTextContent(schemaRoot);
            newInterface.appendChild(rootSchemaFileEl);

            schemaSetLocationEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_SCHEMA_SET_LOCATION);
            schemaSetLocationEl.setTextContent(schemaDir);
            newInterface.appendChild(schemaSetLocationEl);

            wsdlFileEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_WSDL_FILE);
            wsdlFileEl.setTextContent(wsdlpath);
            newInterface.appendChild(wsdlFileEl);

            targetNameSpaceEL = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_TARGET_NAME_SPACE);
            targetNameSpaceEL.setTextContent(targetNameSpace);
            newInterface.appendChild(targetNameSpaceEL);

            operationsEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_OPERATIONS);
            newInterface.appendChild(operationsEl);

            for (Operation op : operations) {
                op.appendToDoc(operationsEl);
            }

            userVarsEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_VARIABLES);
            newInterface.appendChild(userVarsEl);

            userVarsKeys = userVariables.keys();
            while (userVarsKeys.hasMoreElements()) {
                userVarEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_VARIABLE);

                userVarKey = userVarsKeys.nextElement();
                userVarEl.setAttribute("name", userVarKey);

                userVarValue = userVariables.get(userVarKey);

                userVarEl.setAttribute(VAR_TABLE_TYPE,userVarValue.get(VAR_TABLE_TYPE));
                userVarEl.setAttribute(VAR_TABLE_DISPLAY_TEXT,userVarValue.get(VAR_TABLE_DISPLAY_TEXT));
                userVarEl.setAttribute(VAR_TABLE_VALUE,userVarValue.get(VAR_TABLE_VALUE));

                userVarsEl.appendChild(userVarEl);
            }

            admittedHostsEl = doc.createElementNS(Service.DESCRIPTOR_NAMESPACE,TAG_ADMITTED_HOSTS);
            newInterface.appendChild(admittedHostsEl);
            admittedHostsEl.setTextContent(admittedHosts);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Operation getOperationBySOAPAction(String soapAction) {
        Operation[] operationsArray = null;

        operationsArray = this.getOperations();
        for (Operation op : operationsArray) {
            if (op.getSoapAction().equals(soapAction)) {
                return op;
            }
        }
        return null;
    }

    public Operation getOperationByName(String selectedOperationName) {
        Operation[] operationsArray = null;

        operationsArray = this.getOperations();
        for (Operation op : operationsArray) {
            if (op.getName().equals(selectedOperationName)) {
                return op;
            }
        }
        return null;
    }

    public boolean implementsOutOfRepositoryInterface() {
        return name == null || version == null || name.equals("") || version.equals("");
    }

    public void removeOperation(String operationName) {
        Operation[] newOperationArray = null;
        int i = 0;

        newOperationArray = new Operation[operations.length - 1];
        for (Operation op : operations) {
            if (op.getName().equals(operationName) == false) {
                newOperationArray[i] = op;
                i++;
            }
        }

        this.operations = newOperationArray;
    }

    protected Properties getMatchesList(Element redefinitionRootEl) {
        int matchesCount;
        Element matchEl;
        Element el;
        String propertyName, propertyValue;
        Properties table;
        LinkedList matchesList;

        table = new Properties();

        matchesList = DOMUtil.getChildrenByTagName(redefinitionRootEl, TAG_MATCH);
        matchesCount = matchesList.size();

        for (int i = 0; i < matchesCount; i++) {
            matchEl = (Element) matchesList.get(i);

            el = DOMUtil.getChildByTagName(matchEl, TAG_PROPERTY_NAME);
            propertyName = el.getTextContent();

            el = DOMUtil.getChildByTagName(matchEl, TAG_PROPERTY_VALUE);
            propertyValue = el.getTextContent();

            table.put(propertyName, propertyValue);
        }

        return table;
    }

    protected Operation[] getOperationsDefinition(Element parentEl) {
        LinkedList operationList;
        Element operationEl;
        int count = 0;
        Operation[] operationsDef;

        operationList = DOMUtil.getChildrenByTagName(parentEl, TAG_OPERATION);
        count = operationList.size();

        operationsDef = new Operation[count];
        for (int j = 0; j < count; j++) {
            operationEl = (Element) operationList.get(j);
            operationsDef[j] = new Operation();
            operationsDef[j].initFromXMLDocument(operationEl);
        }

        return operationsDef;
    }

    void setParent(Service aThis) {
        this.parentService = aThis;

        for (Operation op : this.operations) {
            op.setParent(this);
        }
    }

    private boolean checkMatch(Element parentEl, Properties match) {
        Element matchEl;
        Properties nodeMatch;
        boolean doMatch = false;
        Enumeration keys;
        String key;

        try {
            nodeMatch = getMatchesList(parentEl);
            if (nodeMatch.size() == match.size()) {
                keys = match.keys();
                while (keys.hasMoreElements()) {
                    key = (String) keys.nextElement();

                    if (match.get(key).equals(nodeMatch.get(key))) {
                        doMatch = true;
                    } else {
                        doMatch = false;
                        break;
                    }
                }

            }
        } catch (Exception e) {
            doMatch = false;
        }

        return doMatch;
    }

    public String getTargetNameSpace() {
        return targetNameSpace;
    }

    public void setTargetNameSpace(String targetNameSpace) {
        this.targetNameSpace = targetNameSpace;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the mode
     */
    public String getMode() {
        return mode;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setUserVariables(Hashtable<String, Hashtable<String, String>> userVariables) {
        this.userVariables = userVariables;
    }

    public Hashtable<String, Hashtable<String, String>> getUserVariable() {
        return this.userVariables;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSchemaRoot() {
        // TODO Auto-generated method stub
        return schemaRoot;
    }

    public String getSchemaDir() {
        return schemaDir;
    }

    public void setSchemaDir(String schemaDir) {
        this.schemaDir = schemaDir;
    }

    public String getWsdlPath() {
        return wsdlpath;
    }

    public void setWsdlPath(String wsdlpath) {
        this.wsdlpath = wsdlpath;
    }

    public void setSchemaRoot(String schemaRoot) {
        this.schemaRoot = schemaRoot;
    }

    public Operation[] getOperations() {
        return operations;
    }

    public void setOperations(Operation[] operations) {
        this.operations = operations;
    }

    public void setAdmittedHosts(String hosts) {
        this.admittedHosts = hosts;
    }

    public String getAdmittedHosts() {
        return this.admittedHosts;
    }

    public LifeCycle getServiceLifeCycle() {
        return serviceLifeCycle;
    }

    private void setServiceLifeCycle(LifeCycle lifeCycle) {
        this.serviceLifeCycle = lifeCycle;
    }

    /**
     * @return the validationActive
     */
    public boolean isValidationActive() {
        return validationActive;
    }

    /**
     * @param validationActive the validationActive to set
     */
    public void setValidationActive(boolean validationActive) {
        this.validationActive = validationActive;
    }

    public void removeAllOperations() {
        operations = new Operation[0];
    }
}
