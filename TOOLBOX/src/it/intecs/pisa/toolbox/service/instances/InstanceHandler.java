/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package it.intecs.pisa.toolbox.service.instances;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.constants.EngineConstants;
import it.intecs.pisa.toolbox.constants.OperationConstants;
import it.intecs.pisa.toolbox.constants.ToolboxFoldersFileConstants;
import it.intecs.pisa.toolbox.db.InstanceMarkers;
import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import org.w3c.dom.Document;
import it.intecs.pisa.toolbox.util.Util;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.InstanceVariable;
import it.intecs.pisa.toolbox.engine.EngineVariablesConstants;
import it.intecs.pisa.toolbox.engine.ToolboxEngineFactory;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperationCommonTasks;
import java.sql.SQLException;
import org.apache.log4j.Logger;


/**
 *
 * @author Massimiliano Fanciulli
 */
public class InstanceHandler {

    protected long serviceInstanceId;

    public InstanceHandler(long id) {
        this.serviceInstanceId = id;
    }

    public void deleteInstance() throws Exception
    {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;


        try {
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();

            deleteAllVariablesDumped();
            sql = "DELETE FROM T_INSTANCES_VARIABLES WHERE INSTANCE_ID=" + serviceInstanceId;
            stm.executeUpdate(sql);

            deleteXMLResources();
            sql = "DELETE FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID=" + serviceInstanceId;
            stm.executeUpdate(sql);

            sql = "DELETE FROM T_SERVICE_INSTANCES WHERE ID=" + serviceInstanceId;
            stm.executeUpdate(sql);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

     public void deleteAllVariablesDumped() throws Exception {
        Statement stm=null;
        ResultSet rs=null;
        long resId;
        XMLResourcesPersistence resPers;

        try
        {
            resPers=XMLResourcesPersistence.getInstance();

            stm=ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT ID FROM T_INSTANCES_VARIABLES WHERE INSTANCE_ID="+serviceInstanceId+" AND TYPE='xml'");

            while(rs.next())
            {
                resId=rs.getLong("ID");
                resPers.deleteXML(Long.toString(resId));
            }
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();

        }
    }

    public void cancelInstance() throws Exception
    {
        Document respMes;

        InstanceStatuses.updateInstanceStatus(serviceInstanceId, InstanceStatuses.STATUS_CANCELLED);

        InstanceVariable.storeVarIntoDB(serviceInstanceId, "errorMessage", InstanceVariable.STRING, "Instance cancelled");
        respMes=(Document) executeScript(TBXScript.SCRIPT_TYPE_GLOBAL_ERROR,false);
        TBXAsynchronousOperationCommonTasks.sendResponseToClient(serviceInstanceId, respMes);
        InstanceResources.storeXMLResource(respMes, serviceInstanceId, InstanceResources.TYPE_OUTPUT_MESSAGE);
    }

    public Object executeScript(String scriptType, boolean debugMode) throws Exception {
        ToolboxEngine engine;
        TBXService service;
        TBXOperation op;
        TBXScript script;
        String operationName;
        Object response;
        String id;
        File resultScriptFile;
        boolean executeCleanup=false;

        service = getService();

        operationName = InstanceInfo.getOperationNameFromInstanceId(serviceInstanceId);
        
        op = service.getOperation(operationName);
        script = (TBXScript) op.getScript(scriptType);

        File tmpDir=new File(new File(service.getToolbox().getRootDir(), ToolboxFoldersFileConstants.WEB_INF), ToolboxFoldersFileConstants.TMP);

        engine = ToolboxEngineFactory.create(service.getServiceName(), debugMode,tmpDir);

        try {
            if ((scriptType.equals(TBXScript.SCRIPT_TYPE_FIRST_SCRIPT) && op.isAsynchronous()==true)
             || scriptType.equals(TBXScript.SCRIPT_TYPE_SECOND_SCRIPT)
             || scriptType.equals(TBXScript.SCRIPT_TYPE_THIRD_SCRIPT)
             || scriptType.equals(TBXScript.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER)
             || scriptType.equals(TBXScript.SCRIPT_TYPE_GLOBAL_ERROR)) {
                engine.loadVariablesFromDB(serviceInstanceId);
                setConfigurationVariables(engine, service, op, scriptType);
            } else {
                initVariables(engine, op, scriptType);
                initConfigurationVariables(engine);
            }

            long respId;
            respId=XMLResourcesPersistence.getInstance().getNewResourceFile();
            resultScriptFile = XMLResourcesPersistence.getInstance().getXMLFile(respId);
            storeInstanceResourceFile(resultScriptFile, scriptType);

            response = engine.executeScript(script, resultScriptFile);

            InstanceResources.storeResourceEntry(respId, serviceInstanceId, scriptType);
        }
        catch(Exception e)
        {
            executeCleanup=true;
            throw e;
        }
        finally {
            if (scriptType.equals(TBXScript.SCRIPT_TYPE_FIRST_SCRIPT) || scriptType.equals(TBXScript.SCRIPT_TYPE_SECOND_SCRIPT) || scriptType.equals(TBXScript.SCRIPT_TYPE_THIRD_SCRIPT)|| scriptType.equals(TBXScript.SCRIPT_TYPE_RESPONSE_BUILDER)) {
                engine.storeVariableToDB(serviceInstanceId);
                 if(executeCleanup==true)
                    executeCleanupMarkers();
            }

            System.gc();
        }
        return response;
    }

    protected void setConfigurationVariables(ToolboxEngine toolboxEngine, TBXService service, TBXOperation op, String scriptType) throws SQLException {
        File serviceRoot = service.getServiceRoot();
        File serviceResourceDir = new File(serviceRoot, "Resources");

        String breakpoint;
        
        breakpoint="/"+service.getServiceName()+"/"+op.getScript(scriptType).getPath();

        IVariableStore confVarStore = toolboxEngine.getConfigurationVariablesStore();
        confVarStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_RESOURCE_DIR, serviceResourceDir.getAbsolutePath());
        confVarStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID, String.valueOf(serviceInstanceId));
        confVarStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SCRIPT_IN_EXECUTION, breakpoint);
    }

   


    private TBXService getService() throws Exception {
        TBXService serv;
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;


        try {
            sql = "SELECT SERVICE_NAME FROM T_SERVICE_INSTANCES WHERE ID=" + serviceInstanceId;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            serv = ServiceManager.getInstance().getService(rs.getString("SERVICE_NAME"));

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
        return serv;

    }

    private void storeInstanceResourceFile(File resourceFile, String scriptType) throws Exception {

        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        String id;
        String type;

        try {
            id = resourceFile.getName();
            id = id.substring(0, id.length() - 4);

            if (scriptType.equals(TBXScript.SCRIPT_TYPE_RESPONSE_BUILDER)) {
                type = InstanceResources.TYPE_RESPONSE_BUILDER_EXECUTION;
            } else if (scriptType.equals(TBXScript.SCRIPT_TYPE_FIRST_SCRIPT)) {
                type = InstanceResources.TYPE_FIRST_SCRIPT_EXECUTION;
            } else if (scriptType.equals(TBXScript.SCRIPT_TYPE_SECOND_SCRIPT)) {
                type = InstanceResources.TYPE_SECOND_SCRIPT_EXECUTION;
            } else if (scriptType.equals(TBXScript.SCRIPT_TYPE_THIRD_SCRIPT)) {
                type = InstanceResources.TYPE_THIRD_SCRIPT_EXECUTION;
            } else if(scriptType.equals(TBXScript.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER))
                type = InstanceResources.TYPE_RESPONSE_ERROR_BUILDER_EXECUTION;
            else if(scriptType.equals(TBXScript.SCRIPT_TYPE_GLOBAL_ERROR))
                type =InstanceResources.TYPE_GLOBAL_ERROR_SCRIPT_EXECUTION;
            else
            {
                throw new Exception("Script type not supported here");
            }

            sql = "INSERT INTO T_INSTANCES_RESOURCES VALUES('" + id + "'," + serviceInstanceId + ",'" + type + "')";

            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            stm.executeUpdate(sql);
            stm.close();
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    private void initVariables(ToolboxEngine toolboxEngine, TBXOperation op, String scriptType) throws Exception {
        TBXService service;
        Interface interf;
        String serviceMode;
        Document soapRequest, xmlRequest;
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql, id;
        ResultSet rs = null;
        Hashtable<String, Hashtable<String, String>> servVars;
        Enumeration<String> keys;
        String key;
        Hashtable<String, String> varBundle;
        String varType;
        XMLResourcesPersistence resPers;

        resPers=XMLResourcesPersistence.getInstance();
        try {
            sql = "SELECT SI.ORDER_ID AS ORDER_ID, SI.SERVICE_NAME AS SERVICER_NAME," +
                    "SI.OPERATION_NAME AS OPERATION_NAME, SI.INSTANCE_ID AS INSTANCE_ID FROM " +
                    "T_SERVICE_INSTANCES AS SI,T_INSTANCES_RESOURCES AS SR WHERE ID=" + serviceInstanceId +
                    " AND SI.ID=SR.INSTANCE_ID";

            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            toolboxEngine.put(EngineVariablesConstants.SERVICE_NAME, rs.getString("SERVICER_NAME"));
            toolboxEngine.put(EngineVariablesConstants.OPERATION_NAME, rs.getString("OPERATION_NAME"));
            toolboxEngine.put(EngineVariablesConstants.ORDER_ID, rs.getString("ORDER_ID"));
            toolboxEngine.put(EngineVariablesConstants.INSTANCE_KEY, rs.getString("INSTANCE_ID"));
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }


        try {
            sql = "SELECT ID FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID=" + serviceInstanceId + " AND TYPE='" + InstanceResources.TYPE_INPUT_MESSAGE + "'";

            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            id = rs.getString("ID");
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }

        service = (TBXService) op.getParent().getParent();
        serviceMode = op.isAsynchronous() ? OperationConstants.ASYNCHRONOUS : OperationConstants.SYNCHRONOUS;
        soapRequest = resPers.retrieveXML(id);
        xmlRequest = Util.removeSOAPElements(soapRequest);
        interf = service.getImplementedInterface();

        toolboxEngine.put(EngineConstants.SERVICE_MODE, serviceMode);
        toolboxEngine.put(EngineConstants.OPERATION_MODE, serviceMode);
        toolboxEngine.put(EngineConstants.XML_REQUEST, xmlRequest);
        toolboxEngine.put(EngineConstants.SOAP_REQUEST, soapRequest);

        InstanceResources.storeXMLResource(xmlRequest, serviceInstanceId, InstanceResources.VARIABLE_BODY_REQUEST);
        InstanceResources.storeXMLResource(soapRequest, serviceInstanceId, InstanceResources.VARIABLE_SOAP_REQUEST);

        if(interf.getTargetNameSpace()!= null)
            toolboxEngine.put(ToolboxEngineVariablesKeys.TARGET_NAMESPACE, interf.getTargetNameSpace());

        servVars = interf.getUserVariable();

        if (servVars != null) {
            keys = servVars.keys();

            while (keys.hasMoreElements()) {
                key = keys.nextElement();
                varBundle = servVars.get(key);

                varType = varBundle.get(Interface.VAR_TABLE_TYPE);
                if (varType.equals("string")) {
                    toolboxEngine.put(key, varBundle.get(Interface.VAR_TABLE_VALUE));
                } else if (varType.equals("boolean")) {
                    toolboxEngine.put(key, Boolean.valueOf(varBundle.get(Interface.VAR_TABLE_VALUE)));
                } else if (varType.equals("integer")) {
                    toolboxEngine.put(key, Integer.valueOf(varBundle.get(Interface.VAR_TABLE_VALUE)));
                }
            }
        }

        setConfigurationVariables(toolboxEngine,service,  op, scriptType);
    }

    private void deleteXMLResources() throws Exception {
        TBXService serv;
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;
        XMLResourcesPersistence resPers;

        try {
            resPers=XMLResourcesPersistence.getInstance();

            sql = "SELECT ID FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID=" + serviceInstanceId;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);

            while(rs.next())
                resPers.deleteXML(Long.toString(rs.getLong("ID")));
        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
    }
     private void initConfigurationVariables(ToolboxEngine engine) {
        IVariableStore confVarStore = engine.getConfigurationVariablesStore();

        confVarStore.setVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID, String.valueOf(serviceInstanceId));
 }

    public void executeCleanupMarkers() throws Exception {
        Logger logger=null;
        String serviceName;

        try {
            TBXService service;
            service=getService();
            serviceName=service.getServiceName();
            logger=service.getLogger();

            String[] markers;

            markers=InstanceMarkers.getDefinedMarkers(serviceInstanceId);
           for(String marker: markers)
            {
                Document script=InstanceMarkers.getMarkerScript(serviceInstanceId, marker);

                ToolboxEngine engine;
                engine=ToolboxEngineFactory.create(serviceName, false);
                engine.executeScript(script.getDocumentElement());

                logger.info("Marker "+marker+" executed");
            }

        } catch(Exception e)
        {
            logger.error("CANNOT EXECUTE CLEANUP MARKERS");
        }
    }

}