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

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class InstanceInfo {
    public static String getServiceNameFromInstanceId(long id) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;
        String opName;


        try {
            sql = "SELECT SERVICE_NAME FROM T_SERVICE_INSTANCES WHERE ID=" + id;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            opName = rs.getString("SERVICE_NAME");

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
        return opName;
    }

    public static String getOperationNameFromInstanceId(long id) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;
        String opName;


        try {
            sql = "SELECT OPERATION_NAME FROM T_SERVICE_INSTANCES WHERE ID=" + id;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            opName = rs.getString("OPERATION_NAME");

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
        return opName;
    }

     public static String getSOAPActionFromInstanceId(long id) throws Exception {
         TBXService service;
         String serviceName;
         ServiceManager servMan;

         servMan=ServiceManager.getInstance();
         serviceName=getServiceNameFromInstanceId(id);

         service=servMan.getService(serviceName);
         Operation operation = service.getImplementedInterface().getOperationByName(getOperationNameFromInstanceId(id));

         return operation.getSoapAction();
     }

    public static int getRemainingAttempts(long id) throws Exception
    {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;

        try {
            sql = "SELECT AVAILABLE_PUSH_RETRIES FROM T_SERVICE_INSTANCES WHERE ID=" + id;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            return rs.getInt("AVAILABLE_PUSH_RETRIES");

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
    }

    public static String getInstanceKeyFromInstanceId(long instanceId) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;
        String id;


        try {
            sql = "SELECT INSTANCE_ID FROM T_SERVICE_INSTANCES WHERE ID=" + instanceId;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            id = rs.getString("INSTANCE_ID");

        } finally {
            if (rs != null) {
                rs.close();
}

            if (stm != null) {
                stm.close();
            }
        }
        return id;
    }

    public static String getOrderIdFromInstanceId(long instanceId) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;
        ResultSet rs = null;
        String id;


        try {
            sql = "SELECT ORDER_ID FROM T_SERVICE_INSTANCES WHERE ID=" + instanceId;
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();

            id = rs.getString("ORDER_ID");

        } finally {
            if (rs != null) {
                rs.close();
            }

            if (stm != null) {
                stm.close();
            }
        }
        return id;
    }
}
