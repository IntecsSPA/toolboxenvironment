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
package it.intecs.pisa.toolbox.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ServiceStatuses {
    public static final byte STATUS_RUNNING = 1;
    public static final byte STATUS_STOPPED = 2;
    public static final byte STATUS_SUSPENDED = 3;

    public static String toString(byte status) throws Exception
    {
        switch(status)
        {
            case STATUS_RUNNING:
                return "Running";

            case STATUS_STOPPED:
                return "Stopped";

            case STATUS_SUSPENDED:
                return "Suspended";

            default:
                throw new Exception("status value not supported");

        }
    }

    public static void addServiceStatus(String serviceName) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String sql;
        try {
            sql = "INSERT INTO T_SERVICE_STATUS VALUES('" + serviceName + "',1)";

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Cannot update service status", e);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static void removeServiceStatus(String serviceName) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String sql;
        try {
            sql = "DELETE FROM T_SERVICE_STATUS WHERE NAME='" + serviceName + "'";

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Cannot delete service status", e);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static void updateStatus(String serviceName, byte status) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String sql;
        try {
            sql = "UPDATE T_SERVICE_STATUS SET STATUS=" + Byte.toString(status) + " WHERE NAME='" + serviceName + "'";

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Cannot update service status", e);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static byte getStatus(String serviceName) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        ResultSet rs = null;
        String sql;
        try {
            sql = "SELECT STATUS FROM T_SERVICE_STATUS WHERE NAME='" + serviceName + "'";

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            rs = stm.executeQuery(sql);
            rs.next();
            
            return rs.getByte("STATUS");
        } catch (Exception e) {
            throw new Exception("Cannot update service status", e);
        } finally {
            if(rs!=null)
                rs.close();

            if (stm != null) {
                stm.close();
            }
        }
    }
}
