/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Massimiliano Faniculli
 */
public class OperationInfo {
    public static String getOperationName(long serviceInstanceId) throws Exception {
        Statement stm;
        ResultSet rs;

        stm = ToolboxInternalDatabase.getInstance().getStatement();

        rs = stm.executeQuery("SELECT OPERATION_NAME FROM T_SERVICE_INSTANCES WHERE ID=" + serviceInstanceId);
        rs.next();

        return rs.getString("OPERATION_NAME");
    }
}
