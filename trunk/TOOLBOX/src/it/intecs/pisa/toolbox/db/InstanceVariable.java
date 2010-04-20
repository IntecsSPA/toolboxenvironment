/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.db;

import java.sql.Statement;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class InstanceVariable {
    public static final String BYTE = "byte";
    public static final String LONG = "long";
    public static final String SHORT = "short";
    public static final String FLOAT = "float";
    public static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";
    public static final String INT = "int";
    public static final String XML = "xml";
    public static final String STRING = "string";

     public static void deleteVarIntoDB(long instanceId) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;

        try {
            sql = "DELETE FROM T_INSTANCES_VARIABLES WHERE INSTANCE_ID=" + instanceId;
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

    public static void storeVarIntoDB(long instanceId, String name, String type, String value) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;

        try {
            sql = "INSERT INTO T_INSTANCES_VARIABLES VALUES (NULL," + instanceId + ",'" + name + "','" + type + "','" + value.replaceAll("'", "") + "')";

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
}
