/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

/**
 *
 * @author massi
 */
public class Accessibles {
    public static void add(String itemId,String url,String type) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("INSERT INTO T_ACCESSIBLES_HTTP VALUES ('"+itemId+"','"+url+"','"+type+"')");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static void delete(String itemId,String type) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("DELETE FROM T_ACCESSIBLES_HTTP WHERE ID='"+itemId+"' AND TYPE='"+type+"'");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static String[] getUrls(String itemId,String type) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();
            ResultSet rs = stm.executeQuery("SELECT URL FROM T_ACCESSIBLES_HTTP WHERE ID='" + itemId + "' AND  TYPE='" + type + "'");

            Vector<String> results;
            results=new Vector<String>();

            while(rs.next())
            {
                results.add(rs.getString("URL"));
            }

            /*String[] resultsArray;
            resultsArray=new String[results.size()];

            for(int i=0;i<results.size();i++)
                resultsArray[i]=results.get(i);

            return resultsArray;*/

            return results.toArray(new String[0]);
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }
}
