/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.db;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ReverseCatalogueId {
    public static String getItem(String url,String rim_id) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            ResultSet res=stm.executeQuery("SELECT ID FROM T_CATALOGUE_ID_CORRESPONDENCE WHERE RIM_ID='"+rim_id+"' AND URL='"+url+"'");
            res.next();

            return res.getString("ID");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static String getCatalogueId(String url,String itemId) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            ResultSet res=stm.executeQuery("SELECT RIM_ID FROM T_CATALOGUE_ID_CORRESPONDENCE WHERE ID='"+itemId+"' AND URL='"+url+"'");
            res.next();

            return res.getString("RIM_ID");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }
}
