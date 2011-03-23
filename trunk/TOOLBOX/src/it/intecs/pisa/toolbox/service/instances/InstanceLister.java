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

package it.intecs.pisa.toolbox.service.instances;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.toolbox.util.ToolboxSimpleDateFormatter;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli, Andrea Marongiu
 */
public class InstanceLister {
    public static final String CLIENTEXPIRATIONDATETIME = "clientExpirationDateTime";
    public static final String HOSTNAME = "hostName";
    public static final String ID = "id";
    public static final String OPERATION = "operation";
    public static final String OPERATIONNAME = "operationName";
    public static final String SERVICEEXPIRATIONDATETIME = "serverExpirationDateTime";
    public static final String INSTANCES = "instances";
    public static final String INSTANCE = "instance";
    public static final String KEY = "key";
    public static final String ORDER_ID = "orderId";
    public static final String SOAP_ACTION = "soapAction";
    public static final String DATE = "date";
    public static final String STATUS = "status";
    public static final String STATUS_AS_BYTE = "statusAsByte";
    public static final String TYPE = "type";

    public static InputStream getSynchronousInstances(String serviceName) throws Exception {
        return getSynchronousInstances(serviceName,0, 20);
    }

     public static Document getSynchronousInstancesAsDocument(String serviceName,int cursor, int pageSize) throws Exception {
        Document instancesList;
        TBXService service;
        DOMUtil util;
        Statement stm;
        ResultSet rs=null;
        int numPages;
        Element listRoot = null;
        Element newInstance;
        Operation op;
        String sqlQuery="";
        int instancesCount=0;

        try{
            util=new DOMUtil();
            instancesList=util.newDocument();
            service=ServiceManager.getInstance().getService(serviceName);
            instancesCount=getSynchronousInstancesNum(serviceName);
            try{
              numPages= (instancesCount / pageSize) + 1;
            }
            catch(Exception ecc)
            {
                numPages= 1;
            }
            finally
            {
              if(rs!=null)
                 rs.close();
            }


            Element insanceElement = instancesList.createElement(INSTANCES);
            insanceElement.setAttribute("instancesCount", Integer.toString(instancesCount));
            stm= ToolboxInternalDatabase.getInstance().getStatement();
            sqlQuery="SELECT * FROM T_SERVICE_INSTANCES WHERE MODE='S' AND SERVICE_NAME='"+serviceName+"' "+
                    " ORDER BY ID DESC ";
            if(cursor != -1 && pageSize != -1){
               sqlQuery+="LIMIT "+pageSize+" OFFSET "+(cursor-1)*pageSize;
               insanceElement.setAttribute("pagesNumber", Integer.toString(numPages));
            }else
               insanceElement.setAttribute("pagesNumber", Integer.toString(1)); 
            
            listRoot = (Element) instancesList.appendChild(insanceElement);
            rs=stm.executeQuery(sqlQuery);
            while(rs.next()){
                op=service.getOperation(rs.getString("OPERATION_NAME"));
                newInstance = (Element) listRoot.appendChild(instancesList.createElement(INSTANCE));
                newInstance.setAttribute(ID, rs.getString("ID"));
                newInstance.setAttribute(KEY, rs.getString("INSTANCE_ID"));
                newInstance.setAttribute(ORDER_ID, rs.getString("ORDER_ID"));
                newInstance.setAttribute(SOAP_ACTION,op.getSoapAction());
                newInstance.setAttribute(DATE, ToolboxSimpleDateFormatter.format(rs.getLong("ARRIVAL_DATE")));
                newInstance.setAttribute(STATUS, InstanceStatuses.getStatusAsString(rs.getByte("STATUS")));
                newInstance.setAttribute(STATUS_AS_BYTE, Byte.toString(rs.getByte("STATUS")));
                newInstance.setAttribute(TYPE, "S");
                newInstance.setAttribute(OPERATION, rs.getString("OPERATION_NAME"));
            }
        }
        catch(Exception e)
        {
            throw new Exception("Cannot retrieve synchronous instances list",e);
        }
        return instancesList;
     }
     
     
   /**
     * 
     * @param serviceName
     * @param cursor zero-based index
     * @param pageSize
     * @return Json 
     * @throws java.lang.Exception
     * @author Andrea Marongiu
     */
     public static JsonObject getSynchronousInstancesAsJSON(String serviceName,int cursor, int pageSize) throws Exception {
         JsonObject syncInstances=null;
         JsonObject instance=null;
         TBXService service;
         Statement stm;
         ResultSet rs=null;
         int numPages;
         Operation op;
         String sqlQuery="";
         int instancesCount=0;

        try{
           syncInstances=new JsonObject();
           service=ServiceManager.getInstance().getService(serviceName);
           instancesCount=getSynchronousInstancesNum(serviceName);
           
           try{
                 numPages= (instancesCount / pageSize) + 1;
           }catch(Exception ecc){
            numPages= 1;
           }finally{
             if(rs!=null)
              rs.close();
          }
           
          JsonArray instancesArray = new JsonArray();
          
           
          syncInstances.add("instancesCount", new JsonPrimitive(Integer.toString(instancesCount)));

          stm= ToolboxInternalDatabase.getInstance().getStatement();
          sqlQuery="SELECT * FROM T_SERVICE_INSTANCES WHERE MODE='S' AND SERVICE_NAME='"+serviceName+"' "+
                    " ORDER BY ID DESC ";
          if(cursor != -1 && pageSize!= -1){
             sqlQuery+="LIMIT "+pageSize+" OFFSET "+((cursor-1)*pageSize);
             syncInstances.add("pagesNumber", new JsonPrimitive(Integer.toString(numPages)));
          }else
             syncInstances.add("pagesNumber", new JsonPrimitive(Integer.toString(1))); 
          
          rs=stm.executeQuery(sqlQuery);
            while(rs.next()){
                op=service.getOperation(rs.getString("OPERATION_NAME"));
                instance=new JsonObject();
                instance.add(ID, new JsonPrimitive(rs.getString("ID")));
                instance.add(KEY, new JsonPrimitive(rs.getString("INSTANCE_ID")));
                instance.add(ORDER_ID, new JsonPrimitive(rs.getString("ORDER_ID")));
                instance.add(SOAP_ACTION, new JsonPrimitive(op.getSoapAction()));
                instance.add(DATE, new JsonPrimitive(ToolboxSimpleDateFormatter.format(rs.getLong("ARRIVAL_DATE"))));
                instance.add(STATUS, new JsonPrimitive(InstanceStatuses.getStatusAsString(rs.getByte("STATUS"))));
                instance.add(STATUS_AS_BYTE, new JsonPrimitive(Byte.toString(rs.getByte("STATUS"))));
                instance.add(TYPE, new JsonPrimitive("S"));
                instance.add(OPERATION, new JsonPrimitive(rs.getString("OPERATION_NAME")));
                instancesArray.add(instance);
            }

            syncInstances.add(INSTANCES, instancesArray);

        }
        catch(Exception e){
           throw new Exception("Cannot retrieve synchronous instances list",e);
        }
         
         return syncInstances;
     }
     
     
     /**
     * 
     * @param serviceName
     * @param cursor zero-based index
     * @param pageSize
     * @return Json 
     * @throws java.lang.Exception
     * @author Andrea Marongiu
     */
     public static JsonObject getAsynchronousInstancesAsJSON(String serviceName,int cursor, int pageSize) throws Exception {
         JsonObject syncInstances=null;
         JsonObject instance=null;
         TBXService service;
         Statement stm;
         ResultSet rs=null;
         int numPages;
         Operation op;
         String sqlQuery="";
         int instancesCount=0;

        try{
           syncInstances=new JsonObject();
           service=ServiceManager.getInstance().getService(serviceName);
           instancesCount=getSynchronousInstancesNum(serviceName);
           
           try{
                 numPages= (instancesCount / pageSize) + 1;
           }catch(Exception ecc){
            numPages= 1;
           }finally{
             if(rs!=null)
              rs.close();
          }
          JsonArray instancesArray = new JsonArray();
          
          
          syncInstances.add("instancesCount", new JsonPrimitive(Integer.toString(instancesCount)));

          stm= ToolboxInternalDatabase.getInstance().getStatement();
          sqlQuery="SELECT * FROM T_SERVICE_INSTANCES WHERE MODE='A' AND SERVICE_NAME='"+serviceName+"' "+
                    " ORDER BY ID DESC ";
          if(cursor != -1 && pageSize!= -1){
             sqlQuery+="LIMIT "+pageSize+" OFFSET "+((cursor-1)*pageSize);
             syncInstances.add("pagesNumber", new JsonPrimitive(Integer.toString(numPages)));
          }else   
             syncInstances.add("pagesNumber", new JsonPrimitive(Integer.toString(1))); 
          
          rs=stm.executeQuery(sqlQuery);
          
          while(rs.next()){
                op=service.getOperation(rs.getString("OPERATION_NAME"));
                instance=new JsonObject();
                instance.add(ID, new JsonPrimitive(rs.getString("ID")));
                instance.add(KEY, new JsonPrimitive(rs.getString("INSTANCE_ID")));
                instance.add(ORDER_ID, new JsonPrimitive(rs.getString("ORDER_ID")));
                instance.add(SOAP_ACTION, new JsonPrimitive(op.getSoapAction()));
                instance.add(DATE, new JsonPrimitive(ToolboxSimpleDateFormatter.format(rs.getLong("ARRIVAL_DATE"))));
                instance.add(STATUS, new JsonPrimitive(InstanceStatuses.getStatusAsString(rs.getByte("STATUS"))));
                instance.add(STATUS_AS_BYTE, new JsonPrimitive(Byte.toString(rs.getByte("STATUS"))));
                instance.add(TYPE, new JsonPrimitive("A"));
                instance.add(SERVICEEXPIRATIONDATETIME,new JsonPrimitive(ToolboxSimpleDateFormatter.format(rs.getLong("EXPIRATION_DATE"))));
                instance.add(CLIENTEXPIRATIONDATETIME, new JsonPrimitive(ToolboxSimpleDateFormatter.format(rs.getLong("EXPIRATION_DATE"))));
                instance.add(OPERATION, new JsonPrimitive(rs.getString("OPERATION_NAME")));
                instance.add(HOSTNAME,new JsonPrimitive(rs.getString("PUSH_HOST")));
                instancesArray.add(instance);
            }

            syncInstances.add(INSTANCES, instancesArray);
        }
        catch(Exception e){
           throw new Exception("Cannot retrieve synchronous instances list",e);
        }
         
         return syncInstances;
     }
     
     
     
    /**
     *
     * @param cursor zero-based index
     * @param pageSize
     * @return
     * @throws java.lang.Exception
     */
    public static InputStream getSynchronousInstances(String serviceName,int cursor, int pageSize) throws Exception {
        Document instancesList;

        instancesList=getSynchronousInstancesAsDocument(serviceName,cursor,pageSize);
        return DOMUtil.getDocumentAsInputStream(instancesList);
    }

    public static Long[] getInstances(String serviceName, String instanceType) throws Exception {
        ArrayList<Long> instancesArray = null;
        Statement stm=null;
        ResultSet rs=null;
        
        String sqlCom="SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='"+serviceName+"' AND MODE='"+instanceType+"'";
         rs=stm.executeQuery(sqlCom);

         while(rs.next()){
           instancesArray.add(new Long(rs.getLong("ID")));
         }
    
      return instancesArray.toArray(new Long[0]);
    }


    public static int getSynchronousInstancesNum(String serviceName) throws Exception {
        Statement stm;
        ResultSet rs;

        stm= ToolboxInternalDatabase.getInstance().getStatement();

        rs=stm.executeQuery("SELECT COUNT(ID) AS NUM FROM T_SERVICE_INSTANCES WHERE MODE='S' AND SERVICE_NAME='"+serviceName+"'");
        rs.next();
        return rs.getInt("NUM");
    }

    public static Document getAsynchronousInstancesAsDocument(String serviceName,int cursor, int pageSize) throws Exception {
        Document instancesList;
        TBXService service;
        DOMUtil util;
        Statement stm;
        ResultSet rs=null;
        int numPages;
        Element listRoot = null;
        Element newInstance;
        Operation op;
        String sqlQuery="";
        int instancesCount=0;

        try
        {
         util=new DOMUtil();
         instancesList=util.newDocument();
         service=ServiceManager.getInstance().getService(serviceName);
         instancesCount=getAsynchronousInstancesNum(serviceName);
            try{
                 numPages= (instancesCount / pageSize) + 1;
            }
            catch(Exception ecc){
                numPages= 1;
            }
            finally{
                if(rs!=null)
                    rs.close();
            }

            Element insanceElement = instancesList.createElement(INSTANCES);
            insanceElement.setAttribute("instancesCount", Integer.toString(instancesCount));
           
            stm= ToolboxInternalDatabase.getInstance().getStatement();
            sqlQuery="SELECT * FROM T_SERVICE_INSTANCES WHERE MODE='A' AND SERVICE_NAME='"+serviceName+"' "+
                    " ORDER BY ID DESC ";
            if(cursor!=-1 && pageSize!=-1){
                sqlQuery+="LIMIT "+pageSize+" OFFSET "+((cursor-1)*pageSize);
                insanceElement.setAttribute("pagesNumber", Integer.toString(numPages));
            }else
               insanceElement.setAttribute("pagesNumber", Integer.toString(1)); 
            listRoot = (Element) instancesList.appendChild(insanceElement);
            rs=stm.executeQuery(sqlQuery);
            while(rs.next()){
                op=service.getOperation(rs.getString("OPERATION_NAME"));
                newInstance = (Element) listRoot.appendChild(instancesList.createElement(INSTANCE));
                newInstance.setAttribute(ID, rs.getString("ID"));
                newInstance.setAttribute(KEY, rs.getString("INSTANCE_ID"));
                newInstance.setAttribute(ORDER_ID, rs.getString("ORDER_ID"));
                newInstance.setAttribute(HOSTNAME,rs.getString("PUSH_HOST"));
                newInstance.setAttribute(SOAP_ACTION,op.getSoapAction());
                newInstance.setAttribute(DATE, ToolboxSimpleDateFormatter.format(rs.getLong("ARRIVAL_DATE")));
                newInstance.setAttribute(STATUS, InstanceStatuses.getStatusAsString(rs.getByte("STATUS")));
                newInstance.setAttribute(STATUS_AS_BYTE, Byte.toString(rs.getByte("STATUS")));
                newInstance.setAttribute(OPERATION, rs.getString("OPERATION_NAME"));
                newInstance.setAttribute(SERVICEEXPIRATIONDATETIME,ToolboxSimpleDateFormatter.format(rs.getLong("EXPIRATION_DATE")));
                newInstance.setAttribute(CLIENTEXPIRATIONDATETIME, ToolboxSimpleDateFormatter.format(rs.getLong("EXPIRATION_DATE")));
                newInstance.setAttribute(TYPE, "A");
            }
        }
        catch(Exception e){
            throw new Exception("Cannot retrieve synchronous instances list",e);
        }

        return instancesList;
    }

    public static int getAsynchronousInstancesNum(String serviceName) throws Exception {
    Statement stm;
        ResultSet rs;

        stm= ToolboxInternalDatabase.getInstance().getStatement();

        rs=stm.executeQuery("SELECT COUNT(ID) AS NUM FROM T_SERVICE_INSTANCES WHERE MODE='A' AND SERVICE_NAME='"+serviceName+"'");
        rs.next();
        return rs.getInt("NUM");
        
    }

    public static Document getInstancesByOrderId(String orderId) throws Exception {
        Document instancesList;
        TBXService service;
        DOMUtil util;
        Statement stm;
        ResultSet rs=null;
        int numPages;
        Element listRoot = null;
        Element newInstance;
        Operation op;
        int instancesCount=0;

        try
        {
            util=new DOMUtil();
            instancesList=util.newDocument();

            Element insanceElement = instancesList.createElement(INSTANCES);

            listRoot = (Element) instancesList.appendChild(insanceElement);

            stm= ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT * FROM T_SERVICE_INSTANCES WHERE ORDER_ID='"+orderId+"'  AND PUSH_HOST=''");
            while(rs.next())
            {
                newInstance = (Element) listRoot.appendChild(instancesList.createElement(INSTANCE));

                newInstance.setAttribute(ID, rs.getString("ID"));
                newInstance.setAttribute(KEY, rs.getString("INSTANCE_ID"));
                newInstance.setAttribute(ORDER_ID, rs.getString("ORDER_ID"));
                newInstance.setAttribute(SOAP_ACTION,InstanceInfo.getSOAPActionFromInstanceId(new Long(rs.getString("ID"))));
                newInstance.setAttribute(DATE, ToolboxSimpleDateFormatter.format(rs.getLong("ARRIVAL_DATE")));
                newInstance.setAttribute(STATUS, InstanceStatuses.getStatusAsString(rs.getByte("STATUS")));
                newInstance.setAttribute(STATUS_AS_BYTE, Byte.toString(rs.getByte("STATUS")));
                newInstance.setAttribute(TYPE, "S");
                newInstance.setAttribute(OPERATION, rs.getString("OPERATION_NAME"));
}



        }
        catch(Exception e)
        {
            throw new Exception("Cannot retrieve synchronous instances list",e);
        }

        return instancesList;
    }

    public static Document getInstancesByOrderIdAndPushHost(String orderId, String pushHost) throws Exception {
        Document instancesList;
        TBXService service;
        DOMUtil util;
        Statement stm;
        ResultSet rs=null;
        int numPages;
        Element listRoot = null;
        Element newInstance;
        Operation op;
        int instancesCount=0;

        try
        {
            util=new DOMUtil();
            instancesList=util.newDocument();

            Element insanceElement = instancesList.createElement(INSTANCES);

            listRoot = (Element) instancesList.appendChild(insanceElement);

            stm= ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT * FROM T_SERVICE_INSTANCES WHERE ORDER_ID='"+orderId+"' AND PUSH_HOST='"+pushHost+"'");
            while(rs.next())
            {
                newInstance = (Element) listRoot.appendChild(instancesList.createElement(INSTANCE));

                newInstance.setAttribute(ID, rs.getString("ID"));
                newInstance.setAttribute(KEY, rs.getString("INSTANCE_ID"));
                newInstance.setAttribute(ORDER_ID, rs.getString("ORDER_ID"));
                newInstance.setAttribute(SOAP_ACTION,InstanceInfo.getSOAPActionFromInstanceId(new Long(rs.getString("ID"))));
                newInstance.setAttribute(DATE, ToolboxSimpleDateFormatter.format(rs.getLong("ARRIVAL_DATE")));
                newInstance.setAttribute(STATUS, InstanceStatuses.getStatusAsString(rs.getByte("STATUS")));
                newInstance.setAttribute(STATUS_AS_BYTE, Byte.toString(rs.getByte("STATUS")));
                newInstance.setAttribute(TYPE, "A");
                newInstance.setAttribute(OPERATION, rs.getString("OPERATION_NAME"));
            }



        }
        catch(Exception e)
        {
            throw new Exception("Cannot retrieve synchronous instances list",e);
        }

        return instancesList;
    }

    public static Long[] getInstancesByMessageID(String serviceName,String operationName,String messageID) throws Exception
    {
        String query;
        ArrayList<Long> vector;

        vector=new ArrayList<Long>();
        query="SELECT ID FROM T_SERVICE_INSTANCES WHERE INSTANCE_ID='"+messageID+"' AND SERVICE_NAME='"+serviceName+"' AND OPERATION_NAME='"+operationName+"'";

        Statement stm = ToolboxInternalDatabase.getInstance().getStatement();
        ResultSet rs = stm.executeQuery(query);
        while(rs.next())
        {
          vector.add(new Long(rs.getLong("ID")));
        }

        return vector.toArray(new Long[0]);
    }

    public static Long[] getInstancesOlderThan(Date date) throws Exception
    {
        String query;
        ArrayList<Long> vector;

        vector=new ArrayList<Long>();
        query="SELECT ID FROM T_SERVICE_INSTANCES WHERE ARRIVAL_DATE<"+date.getTime();

        Statement stm = ToolboxInternalDatabase.getInstance().getStatement();
        ResultSet rs = stm.executeQuery(query);
        while(rs.next())
        {
          vector.add(new Long(rs.getLong("ID")));
        }

        return vector.toArray(new Long[0]);
    }

    public static Long[] getInstancesWithStatus(byte status) throws Exception
    {
        String query;
        ArrayList<Long> list;

        list=new ArrayList<Long>();
        query="SELECT ID FROM T_SERVICE_INSTANCES WHERE STATUS="+ new Integer(status);

        Statement stm = ToolboxInternalDatabase.getInstance().getStatement();
        ResultSet rs = stm.executeQuery(query);
        while(rs.next())
        {
          list.add(new Long(rs.getLong("ID")));
        }

        return list.toArray(new Long[0]);
    }
    
    public static Long[] getInstancesWithStatusAndNotExpired(byte status) throws Exception
    {
        String query;
        ArrayList<Long> list;

        list=new ArrayList<Long>();
        Date nowDate = new Date();
        long now = nowDate.getTime();
        query="SELECT ID FROM T_SERVICE_INSTANCES WHERE STATUS="+ new Integer(status)+" AND EXPIRATION_DATE>"+now;

        Statement stm = ToolboxInternalDatabase.getInstance().getStatement();
        ResultSet rs = stm.executeQuery(query);
        while(rs.next())
        {
          list.add(new Long(rs.getLong("ID")));
        }

        return list.toArray(new Long[0]);
    }

    public static Long[] getInstancesToRetryPushing() throws Exception {
        String query;
        ArrayList<Long> list;

        list=new ArrayList<Long>();
        Date nowDate = new Date();
        long now = nowDate.getTime();
        query="SELECT ID FROM T_SERVICE_INSTANCES WHERE STATUS=12 AND AVAILABLE_PUSH_RETRIES>0";

        Statement stm = ToolboxInternalDatabase.getInstance().getStatement();
        ResultSet rs = stm.executeQuery(query);
        while(rs.next())
        {
          list.add(new Long(rs.getLong("ID")));
        }

        return list.toArray(new Long[0]);
    }
}
