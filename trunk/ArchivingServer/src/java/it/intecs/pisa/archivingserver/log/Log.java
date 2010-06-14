/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.log;

import it.intecs.pisa.archivingserver.db.InternalDatabase;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class Log {
    public static void log(String text)
    {
        System.out.println("[ArchivingServer] "+text);
       /* InternalDatabase db;
        Statement stm=null;

        System.out.println("[ArchivingServer]"+text);
        try
        {
            db=InternalDatabase.getInstance();
            stm=db.getStatement();

            stm.execute("INSERT INTO T_LOG VALUES ('"+text+"',"+(new Date()).getTime()+")");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(stm!=null)
                try {
                stm.close();
            } catch (SQLException ex) {
               
            }
        }*/
    }
}
