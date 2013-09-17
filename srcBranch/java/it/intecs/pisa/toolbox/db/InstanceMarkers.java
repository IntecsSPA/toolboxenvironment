/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.db;

import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class InstanceMarkers {

    public static void delete(long instanceId) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;

        try {
            sql = "DELETE FROM T_INSTANCES_MARKERS WHERE INSTANCE_ID=" + instanceId;
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

    public static void store(long instanceId, String name, String value) throws Exception {
        ToolboxInternalDatabase db;
        Statement stm = null;
        String sql;

        try {
            sql = "INSERT INTO T_INSTANCES_MARKERS VALUES (" + instanceId + ",'" + name + "','" + value.replaceAll("'", "") + "')";

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

    public static String[] getDefinedMarkers(long instanceId) {
        Statement stm = null;
        ResultSet rs = null;
        Vector<String> defined;
        try {
            defined = new Vector<String>();

            String sql = "SELECT NAME FROM T_INSTANCES_MARKERS WHERE INSTANCE_ID=" + instanceId + " AND VALUE!=''";
            ToolboxInternalDatabase db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);

            while (rs.next()) {
                defined.add(rs.getString("NAME"));
            }

            return defined.toArray(new String[0]);
        } catch (Exception e) {
            return new String[0];
        } finally {
            try
            {
                if (rs != null) {
                    rs.close();
                }

                if (stm != null) {
                    stm.close();
                }
            }
            catch(Exception e)
            {

            }
        }
    }
    
    public static String getMarkerValue(long instanceId,String name) {
        Statement stm = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT VALUE FROM T_INSTANCES_MARKERS WHERE INSTANCE_ID=" + instanceId + " AND NAME='"+name+"'";
            ToolboxInternalDatabase db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            rs = stm.executeQuery(sql);

            boolean hasNext=rs.next();

            if(hasNext)
                return rs.getString("VALUE");
            else return "";
        } catch (Exception e) {
            return "";
        } finally {
            try
            {
                if (rs != null) {
                    rs.close();
                }

                if (stm != null) {
                    stm.close();
                }
            }
            catch(Exception e)
            {

            }
        }
    }

    public static Document getMarkerScript(long instanceId,String markername) throws Exception
    {
        String scriptId;

        scriptId=getMarkerValue(instanceId,markername);

        XMLResourcesPersistence resPers = XMLResourcesPersistence.getInstance();
        return resPers.retrieveXML(scriptId);
    }

    public static void update(long serviceInstanceId, String name, String documentRef) throws Exception {
        ToolboxInternalDatabase db = null;
        Statement stm = null;
        String sql;
        try {
            sql = "UPDATE T_INSTANCES_MARKERS SET VALUE='" + documentRef + "' WHERE INSTANCE_ID=" + serviceInstanceId+" AND NAME='"+name+"'";

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
}
