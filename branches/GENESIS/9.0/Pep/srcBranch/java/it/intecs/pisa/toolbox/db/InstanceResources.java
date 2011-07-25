/*
 *  Copyright 2009 Intecs Informatica e Tecnologia del Software.
 * 
 *  Licensed under the GNU GPL, version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.gnu.org/copyleft/gpl.html
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package it.intecs.pisa.toolbox.db;

import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class InstanceResources {
    public static final String TYPE_INPUT_MESSAGE="INPUT_MESSAGE";
    public static final String TYPE_OUTPUT_MESSAGE="OUTPUT_MESSAGE";
    public static final String TYPE_RESPONSE_BUILDER_MESSAGE="RESPONSE_BUILDER_MESSAGE";
    public static final String TYPE_INVALID_INPUT_MESSAGE="INVALID_INPUT_MESSAGE";
    public static final String TYPE_INVALID_OUTPUT_MESSAGE="INVALID_OUTPUT_MESSAGE";
    public static final String TYPE_INVALID_RESPONSE_BUILDER_MESSAGE="INVALID_RESPONSE_BUILDER_MESSAGE";
    public static final String TYPE_RESPONSE_BUILDER_EXECUTION="BUILDER_EXECUTION";
    public static final String TYPE_RESPONSE_ERROR_BUILDER_EXECUTION="ERROR_BUILDER_EXECUTION";
    public static final String TYPE_FIRST_SCRIPT_EXECUTION="FIRST_SCRIPT_EXECUTION";
    public static final String TYPE_SECOND_SCRIPT_EXECUTION="SECOND_SCRIPT_EXECUTION";
    public static final String TYPE_THIRD_SCRIPT_EXECUTION="THIRD_SCRIPT_EXECUTION";
    public static final String TYPE_GLOBAL_ERROR_SCRIPT_EXECUTION="GLOBAL_ERROR_SCRIPT_EXECUTION";
    public static final String TYPE_ERROR_EMAIL="ERROR_EMAIL";
    public static final String VARIABLE_SOAP_REQUEST="VARIABLE_SOAP_REQUEST";
    public static final String VARIABLE_BODY_REQUEST="VARIABLE_BODY_REQUEST";


    public static Document getXMLResource(long serviceInstanceId,String type) throws Exception
    {
        String resId;

        resId=getXMLResourceId(serviceInstanceId,type);

        return XMLResourcesPersistence.getInstance().retrieveXML(resId);

    }

    public static String getXMLResourceId(String serviceInstanceId,String type) throws Exception {
        return getXMLResourceId(Long.parseLong(serviceInstanceId),type);
    }
    
    

    public static String getXMLResourceId(long serviceInstanceId,String type) throws Exception {
        Statement stm=null;
        ResultSet rs=null;

        try
        {
            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT ID FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID=" + serviceInstanceId + " AND TYPE='" + type + "'");
            rs.next();

            return rs.getString("ID");
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }

    }
    
     public static long getResourceAssociatedInstanceId(long id) throws Exception {
        Statement stm=null;
        ResultSet rs=null;

        try
        {
            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT INSTANCE_ID FROM T_INSTANCES_RESOURCES WHERE ID='" + id+"'");
            rs.next();

            return rs.getLong("INSTANCE_ID");
        }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }

    }
    
     public static void storeXMLResource(Document doc,long seviceInstanceId, String type) throws SQLException, Exception {
        String id;
     
        id=XMLResourcesPersistence.getInstance().storeXML(doc);
        storeResourceEntry(id,seviceInstanceId,type);
     }

    public static void storeResourceEntry(String id, long seviceInstanceId, String type) throws SQLException, Exception {
        ToolboxInternalDatabase db;
        Statement stm=null;
        String sql;

        try
        {
            sql = "INSERT INTO T_INSTANCES_RESOURCES VALUES('" + id + "'," + seviceInstanceId + ",'" + type + "')";
            db = ToolboxInternalDatabase.getInstance();
            stm = db.getStatement();
            stm.executeUpdate(sql);
            stm.close();
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    public static String[] getInstanceAssociatedResourceTypes(long seviceInstanceId) throws Exception
    {
        Statement stm=null;
        ResultSet rs=null;
        Vector<String> array;
        String[] resourcesArray;

        try
        {
            array=new Vector<String>();
            stm = ToolboxInternalDatabase.getInstance().getStatement();

            rs = stm.executeQuery("SELECT TYPE FROM T_INSTANCES_RESOURCES WHERE INSTANCE_ID=" + seviceInstanceId );
            while(rs.next())
            {
                array.add(rs.getString("TYPE"));
            }

            resourcesArray=new String[array.size()];
            for(int i=0;i<array.size();i++)
            {
                resourcesArray[i]=array.get(i);
            }

       }
        finally
        {
            if(rs!=null)
                rs.close();

            if(stm!=null)
                stm.close();
        }

        return resourcesArray;
    }

    public static void storeResourceEntry(long id, long seviceInstanceId, String type) throws SQLException, Exception {
        storeResourceEntry(Long.toString(id),seviceInstanceId,type);
    }
}
