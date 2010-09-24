/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: ToolboxDebugEngine.java,v $
 * Version: $Name:  $
 * File Revision: $Revision: 1.10 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/15 14:24:13 $
 * File ID: $Id: ToolboxDebugEngine.java,v 1.10 2007/01/15 14:24:13 fanciulli Exp $
------------------------------------------------------------------------------------------*/
package it.intecs.pisa.toolbox.engine;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.common.communication.ServerDebugConsole;
import it.intecs.pisa.common.communication.messages.ExecutionTreeMessage;
import it.intecs.pisa.toolbox.db.InstanceVariable;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.pluginscore.exceptions.ReturnTagException;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import it.intecs.pisa.pluginscore.ITagExecutor;
import it.intecs.pisa.pluginscore.ITagPlugin;
import it.intecs.pisa.pluginscore.TagPluginManager;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import org.apache.log4j.*;
import it.intecs.pisa.util.*;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The Class ToolboxEngine.
 */
public class ToolboxEngine implements IEngine {

    protected ToolboxEngineVariableStore variablesStore = null;
    protected ToolboxEngineVariableStore configurationVariableStore = null;
    

    /**
     * The Constructor.
     *
     * @param timerManager the timer manager
     * @param logger the logger
     */
    public ToolboxEngine(Logger logger) {
        this(logger, false);
    }

    /**
     * The Constructor.
     *
     * @param timerManager the timer manager
     * @param ftpServerManager the ftp server manager
     * @param logger the logger
     */
    public ToolboxEngine(Logger logger, boolean debugMode) {
        Document resultScript;
        Element currentNode;

        variablesStore = new ToolboxEngineVariableStore();
        configurationVariableStore = new ToolboxEngineVariableStore();

        resultScript = new DOMUtil().newDocument();

        currentNode = resultScript.createElement("executionResults");
        resultScript.appendChild(currentNode);

        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_LOGGER, logger);
        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_RESULT_SCRIPT, resultScript);
        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_UNDER_DEBUG, Boolean.valueOf(debugMode));
        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_DEBUG_CONSOLE, Toolbox.getInstance().getDbgConsole());
        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_LOG_LEVEL, Toolbox.getInstance().getLogLevel());
    }

    /**
     * The Constructor.
     *
     * @param timerManager the timer manager
     * @param ftpServerManager the ftp server manager
     * @param tempDir the temp dir
     * @param logger the logger
     */
    public ToolboxEngine(Logger logger, boolean debugMode, File tempDir) {
        this(logger, debugMode);

        configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEMP_DIR, tempDir);
    }

    /**
     * Put.
     *
     * @param key the key
     * @param value the value
     */
    public void put(Object key, Object value) {
        variablesStore.setVariable(key, value);
    }

    /**
     * Get.
     *
     * @param key the key
     *
     * @return the object
     */
    public Object get(Object key) {
        return variablesStore.getVariable(key);
    }

    @Override
    public Object clone() {
        ToolboxEngine newEngine;
        Hashtable variables;
        IVariableStore newEngineConfVarStore;

        newEngine = new ToolboxEngine((Logger) configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_LOGGER),              
                 false,
                (File) configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEMP_DIR));


        variables = this.variablesStore.getVariables();
        Enumeration keys = variables.keys();

        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            newEngine.put(key, get(key));
        }

        newEngineConfVarStore = newEngine.getConfigurationVariablesStore();
        newEngineConfVarStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_RESOURCE_DIR, configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_RESOURCE_DIR));

        return newEngine;
    }

    private String processStringForEmbeddedVariables(String inputStr) {
        String outputStr, tempStr,
                variableName;

        if (inputStr.contains("${") == false) {
            return inputStr;
        }
        int index = 0;
        int startIndex = 0, endIndex = 0;


        outputStr = inputStr;

        while ((startIndex = outputStr.indexOf("${")) != -1) {
            //getting string before variable

            tempStr = outputStr.substring(0, startIndex);

            endIndex = outputStr.indexOf("}", startIndex);

            //getting variable name
            variableName = outputStr.substring(startIndex + 2, endIndex);

            tempStr += (String) String.valueOf(this.variablesStore.getVariable(variableName));

            //adding end of string
            tempStr += outputStr.substring(endIndex + 1);

            outputStr = tempStr;
        }

        return outputStr;
    }

    /**
     * Execute script.
     *
     * @param expression the expression
     *
     * @return the object
     *
     * @throws Exception the exception
     */
    public Object executeScript(Element expression) throws Exception {
        Object returnObject;
        try {
            returnObject = executeTag(expression);
        }/*catch (DebugTerminatedException dbgEcc) {
        returnObject = null;
        System.out.println("Debug successfully terminated");
        }  */ catch (ReturnTagException rtEcc) {
            //catching the return statement
            returnObject = rtEcc.getReturnedObject();
        } catch (Exception e) {
            throw e;
        }
        return returnObject;
    }

    /**
     * Execute script.
     *
     * @param expression the expression
     *
     * @return the object
     *
     * @throws Exception the exception
     */
    public Object executeScript(TBXScript script) throws Exception {
        Object returnObject;
        try {
            this.configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SCRIPT_IN_EXECUTION, script.getFullPath());

            returnObject = executeTag(script.getScriptDoc().getDocumentElement());
        } catch (ReturnTagException rtEcc) {
            //catching the return statement
            returnObject = rtEcc.getReturnedObject();
        } catch (Exception e) {
            throw e;
        }
        return returnObject;
    }

    /**
     * Execute script.
     *
     * @param resultScriptFile the result script file
     * @param expression the expression
     *
     * @return the object
     *
     * @throws Exception the exception
     */
    public Object executeScript(Element expression, File resultScriptFile) throws
            Exception {
        Object returnObject;
        String instancePath;
        Document resultScript = null;
        ServerDebugConsole console = null;
        ExecutionTreeMessage execMsg = null;

        try {
            resultScript = (Document) configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_RESULT_SCRIPT);

            instancePath = resultScriptFile.getParent();

            this.configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_RESULT_SCRIPT_FILE, resultScriptFile);
            this.configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.INSTANCE_DIRECTORY, instancePath);

            returnObject = executeScript(expression);
        } finally {
            if (resultScript != null) {
                DOMUtil.dumpXML(resultScript, resultScriptFile);

                console = (ServerDebugConsole) this.configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_DEBUG_CONSOLE);
                if (console != null) {
                    execMsg = new ExecutionTreeMessage(resultScriptFile.getName(), resultScript);
                    console.sendCommand(execMsg);
                    System.out.println("TREE SENT!!");
                }
            }
        }

        return returnObject;
    }

    //NSI
    /**
     * Execute script.
     *
     * @param resultScriptFile the result script file
     * @param expression the expression
     *
     * @return the object
     *
     * @throws Exception the exception
     */
    public Object executeScript(TBXScript script, File resultScriptFile) throws
            Exception {
        Object returnObject;
        String instancePath;
        Document resultScript = null;
        ServerDebugConsole console = null;
        ExecutionTreeMessage execMsg = null;

        try {
            resultScript = (Document) configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_RESULT_SCRIPT);

            this.configurationVariableStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SCRIPT_IN_EXECUTION, script.getPath());

            returnObject = executeScript(script.getScriptDoc().getDocumentElement(), resultScriptFile);
        } finally {
            if (resultScript != null) {
                DOMUtil.dumpXML(resultScript, resultScriptFile);


                console = (ServerDebugConsole) this.configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_DEBUG_CONSOLE);
                if (console != null) {
                    execMsg = new ExecutionTreeMessage(resultScriptFile.getName(), resultScript);
                    console.sendCommand(execMsg);
                }
            }
        }

        return returnObject;
    }

    private Object executeTag(Element expression) throws ReturnTagException, Exception {
        TagPluginManager manager;
        ITagPlugin plugin = null;
        ITagExecutor executor = null;
        String tagNamespace = null;
        String tagName = null;
        Document resultDocument;
        Element resultEl;
        Boolean debugMode;

        debugMode = (Boolean) this.configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_UNDER_DEBUG);

        tagNamespace = expression.getNamespaceURI();
        tagName = expression.getNodeName();

        manager = TagPluginManager.getInstance();
        plugin = manager.getTagPlugin(tagNamespace);
        executor = plugin.getTagExecutorClass(tagName, this);


        resultDocument = (Document) this.configurationVariableStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_RESULT_SCRIPT);
        resultEl = resultDocument.getDocumentElement();
        return executor.executeTag(expression, resultEl, debugMode);
    }

    public String evaluateString(String strToBeEvaluated, EngineStringType type) {
        switch (type) {
            case ATTRIBUTE:
                return this.evaluateAttribute(strToBeEvaluated);

            case TEXT:
                return this.processStringForEmbeddedVariables(strToBeEvaluated);

            default:
                return null;
        }
    }

    protected String evaluateAttribute(String attribute) {

        if (attribute.startsWith("$") && attribute.startsWith("${") == false) {
            return get(attribute.substring(1, attribute.length())).toString();
        } else {

            return this.processStringForEmbeddedVariables(attribute);
        }
    }

    public IVariableStore getConfigurationVariablesStore() {
        return this.configurationVariableStore;
    }

    public IVariableStore getVariablesStore() {
        return this.variablesStore;
    }

    public void loadVariablesFromDB(long serviceInstanceId) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        ResultSet rs = null;
        String type, name;
        Object value;
        String sql;
        Document document;
        XMLResourcesPersistence resPers;

        try {
            resPers=XMLResourcesPersistence.getInstance();

            sql = "SELECT * FROM T_INSTANCES_VARIABLES WHERE INSTANCE_ID=" + serviceInstanceId;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);

            while (rs.next()) {
                type = rs.getString("TYPE");
                name = rs.getString("NAME");
                 
                if (type.equals(InstanceVariable.BYTE)) {
                    put(name, Byte.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.SHORT)) {
                    put(name, Short.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.INT)) {
                    put(name, Integer.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.LONG)) {
                    put(name, Long.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.FLOAT)) {
                    put(name, Float.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.DOUBLE)) {
                    put(name, Double.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.BOOLEAN)) {
                    put(name, Boolean.valueOf(rs.getString("VALUE")));
                } else if (type.equals(InstanceVariable.CHAR)) {
                    put(name, new Character(rs.getString("VALUE").charAt(0)));
                } else if (type.equals(InstanceVariable.STRING)) {
                    put(name, rs.getString("VALUE"));
                } else if (type.equals(InstanceVariable.XML)) {
                    document = resPers.retrieveXML(rs.getString("VALUE"));

                    resPers.deleteXML(rs.getString("VALUE"));

                    put(name, document);
                }
            }

            InstanceVariable.deleteVarIntoDB(serviceInstanceId);

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
    }

    public void storeVariableToDB(long instanceId) throws Exception {
        Hashtable table;
        Enumeration keys;
        String type, name;
        Object value;
        String documentId;

        table = this.variablesStore.variables;

        keys = table.keys();
        while (keys.hasMoreElements()) {
            name = (String) keys.nextElement();
            value = table.get(name);


            if (value instanceof Byte) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.BYTE, Byte.toString((Byte) value));
            } else if (value instanceof Short) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.SHORT, Short.toString((Short) value));
            } else if (value instanceof Integer) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.INT, Integer.toString((Integer) value));
            } else if (value instanceof Long) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.LONG, Long.toString((Long) value));
            } else if (value instanceof Float) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.FLOAT, Float.toString((Float) value));
            } else if (value instanceof Double) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.DOUBLE, Double.toString((Double) value));
            } else if (value instanceof Boolean) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.BOOLEAN, Boolean.toString((Boolean) value));
            } else if (value instanceof Character) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.CHAR, Character.toString((Character) value));
            } else if (value instanceof String) {
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.STRING, (String) value);
            } else if (value instanceof Document) {
                documentId = XMLResourcesPersistence.getInstance().storeXML((Document) value);
                InstanceVariable.storeVarIntoDB(instanceId, name, InstanceVariable.XML, documentId);

            }

        }

    }

   

   
}
