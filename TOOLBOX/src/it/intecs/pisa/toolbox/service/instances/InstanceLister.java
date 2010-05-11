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

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.toolbox.util.ToolboxSimpleDateFormatter;
import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
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
        int instancesCount=0;

        try
        {
            util=new DOMUtil();
            instancesList=util.newDocument();

            service=ServiceManager.getInstance().getService(serviceName);

            instancesCount=getSynchronousInstancesNum(serviceName);
            try
            {
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
            insanceElement.setAttribute("pagesNumber", Integer.toString(numPages));
            insanceElement.setAttribute("instancesCount", Integer.toString(instancesCount));
            listRoot = (Element) instancesList.appendChild(insanceElement);

            stm= ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT * FROM T_SERVICE_INSTANCES WHERE MODE='S' AND SERVICE_NAME='"+serviceName+"' "+
                    " ORDER BY ID DESC LIMIT "+pageSize+" OFFSET "+(cursor-1)*pageSize);
            while(rs.next())
            {
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



    public static int getSynchronousInstancesNum(String serviceName) throws Exception {
        Statement stm;
        ResultSet rs;

        stm= ToolboxInternalDatabase.getInstance().getStatement();

        rs=stm.executeQuery("SELECT COUNT(ID) AS NUM FROM T_SERVICE_INSTANCES WHERE MODE='S' AND SERVICE_NAME='"+serviceName+"'");
        rs.next();
        return rs.getInt("NUM");
    }

    public static InputStream getAsynchronousInstances(String serviceName) throws Exception {
        return getAsynchronousInstances(serviceName,0, -1);
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
        int instancesCount=0;

        try
        {
            util=new DOMUtil();
            instancesList=util.newDocument();

            service=ServiceManager.getInstance().getService(serviceName);

            instancesCount=getAsynchronousInstancesNum(serviceName);
            try
            {
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
            insanceElement.setAttribute("pagesNumber", Integer.toString(numPages));
            insanceElement.setAttribute("instancesCount", Integer.toString(instancesCount));
            listRoot = (Element) instancesList.appendChild(insanceElement);

            stm= ToolboxInternalDatabase.getInstance().getStatement();
            rs=stm.executeQuery("SELECT * FROM T_SERVICE_INSTANCES WHERE MODE='A' AND SERVICE_NAME='"+serviceName+"' "+
                    " ORDER BY ID DESC LIMIT "+pageSize+" OFFSET "+((cursor-1)*pageSize));
            while(rs.next())
            {
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
        catch(Exception e)
        {
            throw new Exception("Cannot retrieve synchronous instances list",e);
        }

        return instancesList;
    }


    public static InputStream getAsynchronousInstances(String serviceName,int cursor, int pageSize) throws Exception {
        Document xslDoc;
        Document statusDoc;
        DOMUtil util;
        LinkedList instances;
        int instancesCount = 0;
        File stylesheetFile;
        File tmpFile;
        Source source;
        Source xsl;
        Result output;
        int startIndex = 1;
        int itemCount = 0;

        /*util = new DOMUtil();

        statusDoc = util.fileToDocument(statusFile);
        instances = DOMUtil.getChildren(statusDoc.getDocumentElement());
        instancesCount = instances.size();

        if (cursor <= 0) {
            startIndex = 1;
        } else {
            startIndex = cursor;
        }

        if (pageSize <= 0) {
            itemCount = instancesCount;
        } else {
            itemCount = pageSize;
        }

        //applying transformation
        stylesheetFile = new File(new File(new File(this.root, TBXService.WEB_INF), TBXService.XSL), "asynchInstancesPager.xsl");
        xslDoc = util.fileToDocument(stylesheetFile);

        XSLT.addParameter(xslDoc, "startIndex", String.valueOf(startIndex));
        XSLT.addParameter(xslDoc, "pageSize", String.valueOf(itemCount));

        xsl = new StreamSource(new FileInputStream(stylesheetFile));
        source = new StreamSource(new FileInputStream(statusFile));
        tmpFile = new File(System.getProperty("java.io.tmpdir"), "outputTempFile.xml");

        output = new StreamResult(new FileOutputStream(tmpFile));
        XSLT.transform(xsl, source, output);

        return new FileInputStream(tmpFile);*/
        return null;
    }



   /* public static int getAsynchronousInstancesNum(String orderId, String pushHost) throws Exception {
        Statement stm;
        ResultSet rs;

        stm= ToolboxInternalDatabase.getInstance().getStatement();

        rs=stm.executeQuery("SELECT COUNT(ID) AS NUM FROM T_SERVICE_INSTANCES WHERE MODE='S' AND SERVICE_NAME='"+serviceName+"'");
        rs.next();
        return rs.getInt("NUM");
    }*/

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
}
