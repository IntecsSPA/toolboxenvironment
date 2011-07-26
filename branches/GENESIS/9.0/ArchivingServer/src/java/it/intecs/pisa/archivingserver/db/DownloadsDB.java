/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author massi
 */
public class DownloadsDB {
    public static void addDownload(String id,String url) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("INSERT INTO T_DOWNLOADS VALUES ('"+id+"','"+url+"','ADDED')");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static void updateStatus(String id, String status) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("UPDATE T_DOWNLOADS SET STATUS='"+status+"' WHERE ID='"+id+"'");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static String getStatus(String id) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();
            ResultSet rs = stm.executeQuery("SELECT STATUS FROM T_DOWNLOADS WHERE ID='" + id + "'");
            rs.next();

            return rs.getString("STATUS");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }
    
    
    public static void delete(String id) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("DELETE FROM T_DOWNLOADS WHERE ID='"+id+"'");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

        public static Boolean hasBeenHandled(String url) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();
            ResultSet rs = stm.executeQuery("SELECT COUNT(*) FROM T_DOWNLOADS WHERE URL='" + url + "'");
            rs.next();

            return rs.getInt(1) != 0;
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }


}
