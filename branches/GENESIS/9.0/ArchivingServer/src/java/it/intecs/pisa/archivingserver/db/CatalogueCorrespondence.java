/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.db;

import java.sql.Statement;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CatalogueCorrespondence {
    public static void add(String itemId,String url,String rim_id) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("INSERT INTO T_CATALOGUE_ID_CORRESPONDENCE VALUES ('"+itemId+"','"+url+"','"+rim_id+"')");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static void delete(String itemId) throws Exception
    {
        InternalDatabase db;
        Statement stm=null;

        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("DELETE FROM T_CATALOGUE_ID_CORRESPONDENCE WHERE ID='"+itemId+"'");
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }
}
