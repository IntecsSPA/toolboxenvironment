package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.db.InstanceStatuses;
import it.intecs.pisa.toolbox.db.ToolboxInternalDatabase;
import it.intecs.pisa.toolbox.service.instances.InstanceHandler;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.util.SOAPUtil;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import it.intecs.pisa.util.Util;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.db.InstanceVariable;
import it.intecs.pisa.toolbox.log.ErrorMailer;

public abstract class TBXOperation extends Operation{
   protected Logger logger;
   protected String orderId="";
   protected String messageId="";

    public TBXOperation()
    {
        super();
        
    }

    public TBXOperation(Operation op) {
        super(op);

     }

    
    public void start() {}

    public void stop() {}

    public void suspend() {}

    public void resume() {}

    @Override
     protected void copyScriptsFrom(Script[] scripts)
    {
        TBXScript[] newScripts;
        int i=0;

        newScripts= new TBXScript[scripts.length];
        for(Script s:scripts)
        {
            newScripts[i]=new TBXScript(s);
            i++;
        }

        this.scripts=newScripts;
    }



    /**
     *  This method is used to execute the Toolbox scripts. 
     * @param soapAction
     * @param soapRequest
     * @return
     * @throws java.lang.Exception
     */
 public Document processRequest(Document soapRequest,boolean debugMode)  throws ToolboxException, Exception{
     long serviceInstanceId=0;
     Document validInputMessage;
     Document validOutputMessage;
     Document response=null;
     String errorScriptToExecute=null;
     String outputMessageType;
     boolean isAsynch=false;
     InstanceHandler handler;

     try
     {
        isAsynch=this.isAsynchronous();
        errorScriptToExecute=isAsynch==true?Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER:Script.SCRIPT_TYPE_GLOBAL_ERROR;

        serviceInstanceId=storeRequestInstanceIntoDB(isAsynch);

        if(debugMode)
            Toolbox.getInstance().setInstanceKeyUnderDebug(serviceInstanceId);
     }
     catch(Exception e)
     {
        processErrorOnInstanceCreation();
        ErrorMailer.send(getServiceName(), soapAction, messageId, orderId,"Error while creating instance for incoming message" );
        throw new Exception("Error while creating instance for incoming message");
     }

     try
     {
        storeInstanceKeys(serviceInstanceId,soapRequest, debugMode);
     }
     catch(Exception e)
     {
         processInvalidSOAP(serviceInstanceId);
         ErrorMailer.send(getServiceName(), soapAction, messageId, orderId,"Error while extracting information from the incomming SOAP message" );
         throw e;
     }

     try
     {
        validInputMessage=checkMessage(soapRequest);
     }
     catch(Exception e)
     {
         updateInstanceStatus(serviceInstanceId,InstanceStatuses.STATUS_INVALID_INPUT_MESSAGE);
         InstanceResources.storeXMLResource(soapRequest,serviceInstanceId,InstanceResources.TYPE_INVALID_INPUT_MESSAGE);
         ErrorMailer.send(getServiceName(), soapAction, messageId, orderId,"Error while checking input message:"+e.getMessage() );
         return processErrorRequest(serviceInstanceId,errorScriptToExecute,"Error while checking input message:"+e.getMessage());
     }
     
     try
     {
        InstanceResources.storeXMLResource(soapRequest,serviceInstanceId,InstanceResources.TYPE_INPUT_MESSAGE);
        updateInstanceStatus(serviceInstanceId,InstanceStatuses.STATUS_EXECUTING);

        response=executeRequestScripts(serviceInstanceId,soapRequest,debugMode);
     }
     catch(Exception e)
     {
         logger.error(e.getMessage());

         if(isAsynch)
             updateInstanceStatus(serviceInstanceId,InstanceStatuses.STATUS_ERROR_ON_RESP_BUILDER);
         else updateInstanceStatus(serviceInstanceId,InstanceStatuses.STATUS_ERROR);
         ErrorMailer.send(getServiceName(), soapAction, messageId, orderId,"Error while executing script. Cause"+e.getMessage() );
         return processErrorRequest(serviceInstanceId,errorScriptToExecute,"Error while executing script");
     }

     try
     {
        if(SOAPUtil.isSOAPFault(response)==false && parentInterf.isValidationActive())
            validOutputMessage=validateMessage(response);
        else validOutputMessage=response;

         outputMessageType=isAsynch==true?InstanceResources.TYPE_RESPONSE_BUILDER_MESSAGE:InstanceResources.TYPE_OUTPUT_MESSAGE;
         InstanceResources.storeXMLResource(validOutputMessage,serviceInstanceId,outputMessageType);

         if(isAsynch==false)
         {
            updateInstanceStatus(serviceInstanceId,InstanceStatuses.STATUS_COMPLETED);

            handler=new InstanceHandler(serviceInstanceId);
            handler.deleteAllVariablesDumped();
         }
         return validOutputMessage;
     }  
     catch(Exception ecc2)
     {
         byte errorInstanceStatus;
         String errorOutputMessageType;
         
         errorInstanceStatus=isAsynch==true?InstanceStatuses.STATUS_INVALID_RESPONSE_BUILDER_MESSAGE:InstanceStatuses.STATUS_INVALID_OUTPUT_MESSAGE;
         errorOutputMessageType=isAsynch==true?InstanceResources.TYPE_INVALID_RESPONSE_BUILDER_MESSAGE:InstanceResources.TYPE_INVALID_OUTPUT_MESSAGE;
             
         updateInstanceStatus(serviceInstanceId,errorInstanceStatus);
         InstanceResources.storeXMLResource(soapRequest,serviceInstanceId,errorOutputMessageType);
         ErrorMailer.send(getServiceName(), soapAction, messageId, orderId,"Error while validating output message:"+ecc2.getMessage());
         return processErrorRequest(serviceInstanceId,errorScriptToExecute,"Error while validating output message:"+ecc2.getMessage());
     }
     
 }

    protected void processErrorOnInstanceCreation() {
        Toolbox tbx;
        Logger tbxLogger;
        tbx = Toolbox.getInstance();
        tbxLogger = tbx.getLogger();
        logger.error("An error occurred when trying to create a new instance for the incoming message");
    }

    protected void processInvalidSOAP(long serviceInstanceId) throws Exception {
        InstanceHandler inHandler;

        inHandler = new InstanceHandler(serviceInstanceId);
        inHandler.deleteInstance();

        logger.error("Incoming SOAP message not valid. The instance cannot be started with such input message.");
    }

 protected Document validateMessage(Document doc) throws Exception
 {
             return ((TBXSOAPInterface)this.parentInterf).validateDocument(doc);
 }
 
 public abstract Document executeRequestScripts(long instanceId,Document soapRequest,boolean debugMode) throws Exception;
 
 protected TBXService getParentService()
 {
     TBXService ser;

     ser=(TBXService) this.parentInterf.getParent();

     return ser;
 }

    protected Document processErrorRequest(long seviceInstanceId,String scriptToExecute, String errorMsg) throws ToolboxException, Exception {
        Logger serviceLogger;
        File errorResultFile;       
        long id;
        String type;
        String outputMesType;
        Document doc;
        try
        {
            serviceLogger = getParentService().getLogger();
            serviceLogger.error(errorMsg);

            id=XMLResourcesPersistence.getInstance().getNewResourceFile();
            errorResultFile=XMLResourcesPersistence.getInstance().getXMLFile(id);

            type = this.isAsynchronous() == true ? InstanceResources.TYPE_RESPONSE_ERROR_BUILDER_EXECUTION : InstanceResources.TYPE_GLOBAL_ERROR_SCRIPT_EXECUTION;
            outputMesType=isAsynchronous()==true? InstanceResources.TYPE_RESPONSE_BUILDER_MESSAGE : InstanceResources.TYPE_OUTPUT_MESSAGE;

            InstanceResources.storeResourceEntry(id, seviceInstanceId,type);
            InstanceVariable.storeVarIntoDB(seviceInstanceId, "errorMessage", InstanceVariable.STRING, errorMsg);
            InstanceVariable.storeVarIntoDB(seviceInstanceId, "targetNamespace", InstanceVariable.STRING, parentInterf.getTargetNameSpace());

            InstanceHandler handler;

            handler=new InstanceHandler(seviceInstanceId);
            doc= (Document) handler.executeScript(scriptToExecute, false);

            handler.deleteAllVariablesDumped();

            InstanceResources.storeXMLResource(doc, seviceInstanceId, outputMesType);
            return doc;
        }
        catch(Exception e)
        {
            throw new Exception("Cannot execute error script",e);
        }
    }

    public String getServiceName()
    {
        return getParentService().getServiceName();
    }

    @Override
     protected Script initScript(Element scriptEl) {
        TBXScript script;

        script = new TBXScript();
        script.initFromXMLDocument(scriptEl);
        return script;
    }

    protected void updateInstanceStatus(long seviceInstanceId,byte status) throws Exception
    {
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        String sql;
        try
        {
            sql="UPDATE T_SERVICE_INSTANCES SET STATUS="+new Integer(status)
                  +" WHERE ID="+seviceInstanceId;

            db=ToolboxInternalDatabase.getInstance();

            stm=db.getStatement();
            stm.executeUpdate(sql);
        }
        catch(Exception e)
        {
            throw new Exception("Cannot update status for instance",e);
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }

    protected void updateInstanceInformation(long seviceInstanceId,String orderId,String instanceKey, String pushHost) throws Exception
    {
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        String sql;
        try
        {
            sql="UPDATE T_SERVICE_INSTANCES SET INSTANCE_ID='"+instanceKey
                  +"',ORDER_ID='"+orderId+"',PUSH_HOST='"+pushHost+"' WHERE ID="+seviceInstanceId;

            db=ToolboxInternalDatabase.getInstance();

            stm=db.getStatement();
            stm.executeUpdate(sql);
        }
        catch(Exception e)
        {
            throw new Exception("Cannot store new instance into DB",e);
        }
        finally
        {
            if(stm!=null)
                stm.close();
        }
    }


    private long storeRequestInstanceIntoDB(boolean isAsynch) throws Exception {
        long serviceInstanceId=0;
        ToolboxInternalDatabase db=null;
        Statement stm=null;
        ResultSet rs=null;
        Date arrivalDate;
        String sql,selectsql;
        long arrivalDateAsLong=0;
        long expirationDateAsLong=0;
        int remainingPushRetries=0;
        try
        {
            arrivalDate=new Date();
            arrivalDateAsLong=arrivalDate.getTime();
            expirationDateAsLong=arrivalDateAsLong+(1000*getRequestTimeoutInSeconds());
            if(isAsynch==true && retryAttempts!=null)
                remainingPushRetries=Integer.parseInt(retryAttempts);

            sql="INSERT INTO T_SERVICE_INSTANCES VALUES(NULL,'','','"+
                 this.getParentService().getServiceName()+"','"+name+"',"+(isAsynch?"'A'":"'S'")+",1,"+
                 arrivalDateAsLong+","+expirationDateAsLong+","+remainingPushRetries+",'')";
            
            db=ToolboxInternalDatabase.getInstance();

            stm=db.getStatement();
            stm.executeUpdate(sql);

            selectsql="SELECT ID FROM T_SERVICE_INSTANCES WHERE SERVICE_NAME='"+this.getServiceName()+
                    "' AND OPERATION_NAME='"+name+"' AND ARRIVAL_DATE="+arrivalDateAsLong;

            rs=stm.executeQuery(selectsql);
            rs.next();

            serviceInstanceId=rs.getInt("ID");
            
            return serviceInstanceId;
        }
        catch(Exception e)
        {
            throw new Exception("Cannot store new instance into DB",e);
        }
        finally
        {
            if(rs!=null)
                rs.close();
            
            if(stm!=null)
                stm.close();
        }
    }

    protected void storeInstanceKeys(long seviceInstanceId,Document soapRequest, boolean debugEnabled) throws Exception{}

    void setLogger(Logger log) {
        this.logger=log;
    }

    protected Document checkMessage(Document soapRequest) throws Exception {
        Document inputMessageBody;
        Document validInputMessage;

        inputMessageBody = Util.removeSOAPElements(soapRequest);
        if(parentInterf.isValidationActive())
        {
            logger.info("Validating input message");
            validInputMessage = validateMessage(inputMessageBody);
        }
        else validInputMessage=inputMessageBody;

        return validInputMessage;
    }
}
