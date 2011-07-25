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
 * @author Messimiliano Fanciulli
 */
public class InstanceStatuses {

    public static final byte STATUS_QUEUED = 1;
    public static final byte STATUS_INVALID_INPUT_MESSAGE = 2;
    public static final byte STATUS_EXECUTING = 3;
    public static final byte STATUS_INVALID_OUTPUT_MESSAGE = 4;
    public static final byte STATUS_COMPLETED = 5;
    public static final byte STATUS_ERROR = 6;
    public static final byte STATUS_INVALID_RESPONSE_BUILDER_MESSAGE = 7;
    public static final byte STATUS_PENDING=8;
    public static final byte STATUS_CANCELLED=9;
    public static final byte STATUS_UNPUSHED=10;
    public static final byte STATUS_EXPIRED=11;
    public static final byte STATUS_PUSH_RETRY=12;
    public static final byte STATUS_ABORTED=13;
    public static final byte STATUS_READY=14;
    public static final byte STATUS_RESPONSE_LEAVING=15;
    public static final byte STATUS_CHECKING=16;
    public static final byte STATUS_ERROR_ON_RESP_BUILDER=17;
    public static final byte STATUS_ACCESS_DENIED=18;

    public static String getStatusAsString(byte status) throws Exception {
        switch (status) {
            case STATUS_QUEUED:
                return "Queued";

            case STATUS_INVALID_INPUT_MESSAGE:
                return "Invalid input message";

            case STATUS_EXECUTING:
                return "Executing";

            case STATUS_INVALID_OUTPUT_MESSAGE:
                return "Invalid output message";

            case STATUS_COMPLETED:
                return "Completed";

            case STATUS_ERROR:
                return "Error";

            case STATUS_INVALID_RESPONSE_BUILDER_MESSAGE:
                return "Invalid response builder";

            case STATUS_PENDING:
                return "Pending";

            case STATUS_CANCELLED:
                return "Cancelled";

            case STATUS_UNPUSHED:
                return "Unpushed";
                
            case STATUS_EXPIRED:
                return "Expired";

            case STATUS_PUSH_RETRY:
                return "Push retry";

           case STATUS_ABORTED:
                return "Aborted";

           case STATUS_READY:
                return "Ready";

           case STATUS_RESPONSE_LEAVING:
                return "Response leaving";

            case STATUS_CHECKING:
                return "Checking";

            case STATUS_ERROR_ON_RESP_BUILDER:
                return "Error on Response builder";
                
            case STATUS_ACCESS_DENIED:
                return "AccessDenied";    

            default:
                throw new Exception("Cannot translate state to String");
        }
    }

    public static void updateInstanceStatus(long serviceInstanceId,byte status) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String sql;
        try {
            sql = "UPDATE T_SERVICE_INSTANCES SET STATUS=" + new Integer(status) + " WHERE ID=" + serviceInstanceId;

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            stm.executeUpdate(sql);
        } catch (Exception e) {
            throw new Exception("Cannot update status for instance", e);
        } finally {
            if (stm != null) {
                stm.close();
            }
        }
    }

    public static byte getInstanceStatus(long serviceInstanceId) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        ResultSet rs=null;
        String sql;
        try {
            sql = "SELECT STATUS FROM T_SERVICE_INSTANCES WHERE ID=" + serviceInstanceId;

            db = ToolboxInternalDatabase.getInstance();

            stm = db.getStatement();
            rs=stm.executeQuery(sql);
            rs.next();

            return rs.getByte("STATUS");
        } catch (Exception e) {
            throw new Exception("Cannot get status for instance", e);
        } finally {
            if(rs!=null)
                rs.close();

            if (stm != null) {
                stm.close();
            }
        }
    }
}
