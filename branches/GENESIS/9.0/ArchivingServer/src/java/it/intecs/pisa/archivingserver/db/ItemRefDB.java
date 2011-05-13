/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author massi
 */
public class ItemRefDB {
    public static void register(String itemId,long arrivalDate,long expireDate) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("INSERT INTO T_ITEMS VALUES ('"+itemId+"',"+arrivalDate+","+expireDate+")");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static void unregister(String itemId) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("DELETE FROM T_ITEMS WHERE ID='"+itemId+"'");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static boolean exists(String itemId) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;
        ResultSet rs;
        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            rs=stm.executeQuery("SELECT ID FROM T_ITEMS WHERE ID='"+itemId+"'");
            return rs.next();
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static boolean getExpired(String itemId) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;
        ResultSet rs;
        long currentTime;
        try
        {
            currentTime=(new Date()).getTime();

            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            rs=stm.executeQuery("SELECT ID FROM T_ITEMS WHERE EXPIRES<"+currentTime);
            return rs.next();
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }
    
    public static String [][] getList() throws Exception
    {
        InternalDatabase db;
        Statement stm=null;
        ResultSet rs;
        long currentTime;
        try
        {
            ArrayList<String[]> vector;
            vector=new ArrayList<String[]>();
            
            currentTime=(new Date()).getTime();

            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            rs=stm.executeQuery("SELECT ID,ARRIVAL_DATE,EXPIRES FROM T_ITEMS");
            while(rs.next())
            {
                String[] value=new String[3];
                value[0]=rs.getString("ID");
                value[1]=String.valueOf(rs.getLong("ARRIVAL_DATE"));
                value[2]=String.valueOf(rs.getLong("EXPIRES"));
                
                vector.add(value);
            }
                
            return vector.toArray(new String[0][0]);
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }
}
