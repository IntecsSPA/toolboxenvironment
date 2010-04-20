/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano
 */
public class DeleteAllInstancesCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest request, HttpServletResponse resp) throws Exception {
        String serviceName = "";
        String opType = "A";
        Statement stm=null;
        ResultSet rs=null;
        try {
            opType=request.getParameter("opType");
            serviceName=request.getParameter("serviceName");

            stm=ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='"+serviceName+"' AND MODE='"+opType+"'");

            while(rs.next())
            {
                try
                {
                    deleteInstance(rs.getLong("ID"));
                }
                catch(Exception e)
                {

                }
            }

            stm.execute("DELETE FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='"+serviceName+"' AND MODE='"+opType+"'");

            resp.sendRedirect("viewServiceInstances.jsp?instanceType=" + opType + "&serviceName=" + serviceName);
        } catch (Exception ex) {
            String errorMsg = "Error deleting service (" + serviceName + ") : " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }
    }

    private void deleteInstance(long id) throws Exception {
        Statement stm=null;
        ResultSet rs=null;
        String resId;
        XMLResourcesPersistence resPers=null;
        try{
            resPers=XMLResourcesPersistence.getInstance();

            stm= ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT ID FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID="+id);

            while(rs.next())
            {
                resId=rs.getString("ID");
                resPers.deleteXML(resId);
            }

            stm.execute("DELETE FROM T_INSTANCES_RESOURCESS WHERE INSTANCE_ID="+id);

        }
        finally{
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }


    }

}
