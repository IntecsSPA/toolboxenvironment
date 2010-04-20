/**
 * 
 */
package it.intecs.pisa.common.tbx;

import it.intecs.pisa.common.tbx.lifecycle.LifeCycle;
import it.intecs.pisa.util.DOMUtil;
import java.util.Iterator;
import java.util.LinkedList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class Operation {
    public static final String TAG_LIFE_CYCLE_SCRIPTS = "lifeCycleScripts";
    protected static final String ATTRIBUTE_OVERRIDE_BY_USER = "overrideByUser";
	protected static final String ATTRIBUTE_RETRY_RATE = "retryRate";
	protected static final String ATTRIBUTE_RETRY_ATTEMPTS = "retryAttempts";
	protected static final String ATTRIBUTE_REQUEST_TIMEOUT = "requestTimeout";
	protected static final String ATTRIBUTE_POLLING_RATE = "pollingRate";
	protected static final String TAG_ADMITTED_HOSTS = "admittedHosts";
	protected static final String TAG_TEST_FILES = "testFiles";
	protected static final String ATTRIBUTE_CALLBACK_SOAPACTION = "callbackSOAPAction";
	protected static final String ATTRIBUTE_CALLBACK_SOAP_ACTION = ATTRIBUTE_CALLBACK_SOAPACTION;
	protected static final String ATTRIBUTE_NAME = "name";
	protected static final String ATTRIBUTE_SOAPACTION = "SOAPAction";
	protected static final String ATTRIBUTE_NAMESPACE = "namespace";
	protected static final String ATTRIBUTE_TYPE = "type";
	protected static final String ATTRIBUTE_PATH = "path";
	
	protected static final String TAG_CALLBACK_OUTPUT_TYPE = "callbackOutputType";
	protected static final String TAG_CALLBACK_INPUT_TYPE = "callbackInputType";
	protected static final String TAG_OUTPUT_TYPE = "outputType";
	protected static final String TAG_INPUT_TYPE = "inputType";
	protected static final String TAG_OPERATION = "operation";
	protected static final String TAG_SCRIPT = "scriptFile";
	
	public static final String OPERATION_TYPE_ASYNCHRONOUS =  "asynchronous";
	public static final String OPERATION_TYPE_SYNCHRONOUS = "synchronous";
	
	
	

	protected String type="";
	protected String name="";
	protected String inputType="";
	protected String outputType="";
	protected String callbackInputType="";
	protected String callbackOutputType="";
	protected String inputTypeNameSpace="";
	protected String outputTypeNameSpace="";
	protected String callbackInputTypeNameSpace="";
	protected String callbackOutputTypeNameSpace="";
	protected String testFile="";
	protected String soapAction="";
	protected String callbackSoapAction="";
	protected Script[] scripts;
	protected String requestTimeout="1h";
	protected String pollingRate="5s";
	protected String retryAttempts="";
	protected String retryRate="";
    protected LifeCycle serviceLifeCycle=null;
    protected Interface parentInterf;
    protected String admittedHosts="";



	public Operation()
	{
	    scripts=new Script[0];
	}

    public Operation(Operation op)
    {
        this.copyFrom(op);
    }

    public void copyFrom(Operation op)
    {
        LifeCycle cycle;

        name=new String(op.getName());
        type=new String(op.getType());
        inputType=new String(op.getInputType());
        outputType=new String(op.getOutputType());
        callbackInputType=new String(op.getCallbackInputType());
        callbackOutputType=new String(op.getCallbackOutputType());
        inputTypeNameSpace=new String(op.getInputTypeNameSpace());
        outputTypeNameSpace=new String(op.getOutputTypeNameSpace());
        callbackInputTypeNameSpace=new String(op.getCallbackInputTypeNameSpace());
        callbackOutputTypeNameSpace=new String(op.getCallbackOutputTypeNameSpace());
        testFile=new String(op.getTestFile());
        soapAction=new String(op.getSoapAction());
        callbackSoapAction=new String(op.getCallbackSoapAction());
        pollingRate=new String(op.getPollingRate());
        this.admittedHosts=new String(op.getAdmittedHosts());

        if(op.getRetryAttempts()!=null)
            retryAttempts=new String(op.getRetryAttempts());
        else retryAttempts=null;

        if(op.getRetryRate()!=null)
            retryRate=new String(op.getRetryRate());
        else retryRate=null;

        if(op.getRequestTimeout()!=null)
            requestTimeout=new String(op.getRequestTimeout());
        else requestTimeout=null;

        if(op.getServiceLifeCycle()!=null)
        {
            cycle=new LifeCycle();
            this.serviceLifeCycle=(LifeCycle) op.getServiceLifeCycle().clone();
        }
        copyScriptsFrom(op.getScripts());
    }

    public Interface getParent() {
        return this.parentInterf;
    }

    protected void copyScriptsFrom(Script[] scripts)
    {
        Script[] newScripts;
        int i=0;

        newScripts= new Script[scripts.length];
        for(Script s:scripts)
        {
            newScripts[i]=(Script) s.clone();
            i++;
        }

        this.setScripts(newScripts);
    }
  
    @Override
    public synchronized Object clone() {
        Operation newOp;
                
        newOp=new Operation();
        newOp.copyFrom(this);
        
        return newOp;
    }

    public String[] getNeededScriptsType()
    {
        String[] types;
        int i=0;

        types=new String[getScripts().length];
        for(Script s:getScripts())
        {
            types[i]=s.getType();
            i++;
        }
        
        return types;
    }
	
	public void addScript(Script s)
	{
        Script[] newScripts;
        int i=0;

	    removeScriptForType(s.getType());

        newScripts=new Script[getScripts().length+1];
        for(Script sc:getScripts())
        {
            newScripts[i]=sc;
            i++;
        }

        newScripts[scripts.length]=s;
        setScripts(newScripts);
	}

    public void removeScriptForType(String type)
    {
        boolean isTypeSet=false;
        Script[] newScripts;
        int i=0;

        for(Script s:getScripts())
        {
            if(s.getType().equals(type))
                isTypeSet=true;
        }

        if(isTypeSet==true)
        {
            newScripts=new Script[getScripts().length-1];
            for(Script s:getScripts())
            {
                if(s.getType().equals(type)==false)
                {
                    newScripts[i]=s;
                    i++;
                }
            }

        }
    }
	
	public Script getScript(String type)
	{
	    for(Script s:getScripts())
        {
            if(s.getType().equals(type)==true)
                return s;
        }

        return null;
	}

    public Script[] getScripts()
	{
	    return scripts;
	}

    public boolean scriptMustBeOverridden(String type)
    {
        Script s;

        s=getScript(type);

        return s.isOverrideByUser();
    }

  
	
	public void initFromXMLDocument(Element operationEl) {
		Element inputEl=null;
		Element outputEl=null;
		Element callbackInputEl=null;
		Element callbackOutputEl=null;
		Element scriptEl=null;
		Element testFileEl=null;
        Element admittedHostsEl=null;
		LinkedList children=null;
		Iterator iterator=null;
		Script script;
        Element serviceLifeCycleEl;

		try
		{
            soapAction=operationEl.getAttribute(ATTRIBUTE_SOAPACTION);
			name=operationEl.getAttribute(ATTRIBUTE_NAME);
			type=operationEl.getAttribute(ATTRIBUTE_TYPE);
			
			inputEl=DOMUtil.getChildByTagName(operationEl, TAG_INPUT_TYPE);
			outputEl=DOMUtil.getChildByTagName(operationEl, TAG_OUTPUT_TYPE);
			
			inputType=inputEl.getAttribute(ATTRIBUTE_TYPE);
			inputTypeNameSpace=inputEl.getAttribute(ATTRIBUTE_NAMESPACE);
			outputType=outputEl.getAttribute(ATTRIBUTE_TYPE);
			outputTypeNameSpace=outputEl.getAttribute(ATTRIBUTE_NAMESPACE);

            admittedHostsEl=DOMUtil.getChildByTagName(operationEl, TAG_ADMITTED_HOSTS);
			if(admittedHostsEl!=null)
                admittedHosts=admittedHostsEl.getTextContent();

			if(type.equals(OPERATION_TYPE_ASYNCHRONOUS))
			{
				callbackInputEl=DOMUtil.getChildByTagName(operationEl, TAG_CALLBACK_INPUT_TYPE);
				callbackOutputEl=DOMUtil.getChildByTagName(operationEl, TAG_CALLBACK_OUTPUT_TYPE);
			
				callbackInputType=callbackInputEl.getAttribute(ATTRIBUTE_TYPE);
				callbackInputTypeNameSpace=callbackInputEl.getAttribute(ATTRIBUTE_NAMESPACE);
				callbackOutputType=callbackOutputEl.getAttribute(ATTRIBUTE_TYPE);
				callbackOutputTypeNameSpace=callbackOutputEl.getAttribute(ATTRIBUTE_NAMESPACE);
			
				callbackSoapAction=operationEl.getAttribute(ATTRIBUTE_CALLBACK_SOAPACTION);	
				pollingRate=operationEl.getAttribute(ATTRIBUTE_POLLING_RATE);
				
				requestTimeout=operationEl.getAttribute(ATTRIBUTE_REQUEST_TIMEOUT);
				if(requestTimeout!=null && requestTimeout.equals(""))
					requestTimeout=null;
				
				retryAttempts=operationEl.getAttribute(ATTRIBUTE_RETRY_ATTEMPTS);
				if(retryAttempts!=null && retryAttempts.equals(""))
					retryAttempts=null;
				
				retryRate=operationEl.getAttribute(ATTRIBUTE_RETRY_RATE);
				if(retryRate!=null && retryRate.equals(""))
					retryRate=null;
				
			}
			
			children=DOMUtil.getChildrenByTagName(operationEl, Operation.TAG_SCRIPT);
			iterator=children.iterator();
			
			while(iterator.hasNext())
			{
			    scriptEl=(Element)iterator.next();
                script = initScript(scriptEl);
                addScript(script);
            }

            testFileEl=DOMUtil.getChildByTagName(operationEl, TAG_TEST_FILES);
			if(testFileEl!=null)
				testFile=DOMUtil.getStringFromElement(testFileEl);

            serviceLifeCycleEl=DOMUtil.getChildByTagName(operationEl, TAG_LIFE_CYCLE_SCRIPTS);
            if(serviceLifeCycleEl!=null)
            {
                this.serviceLifeCycle=new LifeCycle();
                this.serviceLifeCycle.initFromXML(serviceLifeCycleEl);
            }
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
		
	}

	public void appendToDoc(Element parent)  {
		Element operationEl=null;
		Element typeEl=null;
		Element scriptEl=null;
		Element admittedHostEl=null;
		Document doc=null;
		boolean isAsynchronous=false;
		String path=null;
        Script script;
		
		try
		{
            isAsynchronous=type.equals(Operation.OPERATION_TYPE_ASYNCHRONOUS);
            doc=parent.getOwnerDocument();
			
			operationEl=doc.createElement(TAG_OPERATION);
			operationEl.setAttribute(ATTRIBUTE_NAME, name);
			operationEl.setAttribute(ATTRIBUTE_TYPE, type);
			operationEl.setAttribute(ATTRIBUTE_SOAPACTION, soapAction);
			operationEl.setAttribute(ATTRIBUTE_REQUEST_TIMEOUT, requestTimeout);
			
			//addimng admitted hosts
			admittedHostEl=doc.createElement(TAG_ADMITTED_HOSTS);
            admittedHostEl.setTextContent(this.admittedHosts);
			operationEl.appendChild(admittedHostEl);
			
			//	adding input and output type and namespace
			typeEl=doc.createElement(TAG_INPUT_TYPE);
			typeEl.setAttribute(ATTRIBUTE_TYPE, inputType);
			typeEl.setAttribute(ATTRIBUTE_NAMESPACE, inputTypeNameSpace);
			operationEl.appendChild(typeEl);
			
			typeEl=doc.createElement(TAG_OUTPUT_TYPE);
			typeEl.setAttribute(ATTRIBUTE_TYPE, outputType);
			typeEl.setAttribute(ATTRIBUTE_NAMESPACE, outputTypeNameSpace);
			operationEl.appendChild(typeEl);
			
			if(isAsynchronous)
			{
				//must add asynch informations
				operationEl.setAttribute(ATTRIBUTE_CALLBACK_SOAP_ACTION, callbackSoapAction);
				operationEl.setAttribute(ATTRIBUTE_POLLING_RATE, pollingRate);
				
				if(retryAttempts!=null)
					operationEl.setAttribute(ATTRIBUTE_RETRY_ATTEMPTS, retryAttempts);
				if(retryRate!=null)
					operationEl.setAttribute(ATTRIBUTE_RETRY_RATE, retryRate);
				
				//	adding input and output type and namespace for callback message
				typeEl=doc.createElement(TAG_CALLBACK_INPUT_TYPE);
				typeEl.setAttribute(ATTRIBUTE_TYPE, callbackInputType);
				typeEl.setAttribute(ATTRIBUTE_NAMESPACE, callbackInputTypeNameSpace);
				operationEl.appendChild(typeEl);
				
				typeEl=doc.createElement(TAG_CALLBACK_OUTPUT_TYPE);
				typeEl.setAttribute(ATTRIBUTE_TYPE, callbackOutputType);
				typeEl.setAttribute(ATTRIBUTE_NAMESPACE, callbackOutputTypeNameSpace);
				operationEl.appendChild(typeEl);
			}
			
			//appending first script
			for(Script s:scripts)
			{
				s.appendToDoc(operationEl);
			}
			
            /*script=getScript(Script.SCRIPT_TYPE_FIRST_SCRIPT);
			script.appendToDoc(operationEl);

            script=getScript(Script.SCRIPT_TYPE_GLOBAL_ERROR);
			script.appendToDoc(operationEl);
			
			if(isAsynchronous)
			{
                script=getScript(Script.SCRIPT_TYPE_RESPONSE_BUILDER);
				script.appendToDoc(operationEl);

                script=getScript(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER);
				script.appendToDoc(operationEl);
				
        		script=getScript(Script.SCRIPT_TYPE_SECOND_SCRIPT);
				script.appendToDoc(operationEl);
				
				script=getScript(Script.SCRIPT_TYPE_THIRD_SCRIPT);
				script.appendToDoc(operationEl);
			}*/
			
			parent.appendChild(operationEl);
		}catch(Exception e)
		{
		    //  handle error here
		}
	}

	public String getTestFile() {
		return testFile;
	}

	public void setTestFile(String testFile) {
		this.testFile = testFile;
	}

	public void setPollingRate(String pollingRate) {
		this.pollingRate = pollingRate;
	}

	public String getPollingRate() {
		return pollingRate;
	}

	public long getPollingRateInSeconds() {
		char granularity;
		long multiplier=0;
		String value;
		
		granularity=pollingRate.charAt(this.pollingRate.length()-1);
		
		switch(granularity)
		{
			case 's':
				multiplier=1;
			break;
			
			case 'm':
				multiplier=60;
			break;
				
			case 'h':
				multiplier=3600;
			break;
				
			case 'd':
				multiplier=86400;
			break;
				
			case 'w':
				multiplier=604800;
			break;
		}
		
		value=pollingRate.substring(0,pollingRate.length()-1);
		return Long.parseLong(value)*multiplier;
	}

        public long getRequestTimeoutInSeconds() {
		char granularity;
		long multiplier=0;
		String value;

		granularity=requestTimeout.charAt(requestTimeout.length()-1);

		switch(granularity)
		{
			case 's':
				multiplier=1;
			break;

			case 'm':
				multiplier=60;
			break;

			case 'h':
				multiplier=3600;
			break;

			case 'd':
				multiplier=86400;
			break;

			case 'w':
				multiplier=604800;
			break;
		}

		value=requestTimeout.substring(0,requestTimeout.length()-1);
		return Long.parseLong(value)*multiplier;
	}

         public long getRetryRateInSeconds() {
		char granularity;
		long multiplier=0;
		String value;

		granularity=retryRate.charAt(retryRate.length()-1);

		switch(granularity)
		{
			case 's':
				multiplier=1;
			break;

			case 'm':
				multiplier=60;
			break;

			case 'h':
				multiplier=3600;
			break;

			case 'd':
				multiplier=86400;
			break;

			case 'w':
				multiplier=604800;
			break;
		}

		value=retryRate.substring(0,retryRate.length()-1);
		return Long.parseLong(value)*multiplier;
	}

	public String getRequestTimeout() {
		return requestTimeout;
	}

	public void setRequestTimeout(String operationTimeout) {
		this.requestTimeout = operationTimeout;
	}

	public String getRetryAttempts() {
		return retryAttempts;
	}

	public void setRetryAttempts(String retryAttempts) {
		this.retryAttempts = retryAttempts;
	}

	public String getRetryRate() {
		return retryRate;
	}

	public void setRetryRate(String retryRate) {
		this.retryRate = retryRate;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCallbackSoapAction() {
		return callbackSoapAction;
	}

	public void setCallbackSoapAction(String callbackSoapAction) {
		this.callbackSoapAction = callbackSoapAction;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

	public String getCallbackInputType() {
		return callbackInputType;
	}

	public void setCallbackInputType(String callbackInputType) {
		this.callbackInputType = callbackInputType;
	}

	public String getCallbackInputTypeNameSpace() {
		return callbackInputTypeNameSpace;
	}

	public void setCallbackInputTypeNameSpace(String callbackInputTypeNameSpace) {
		this.callbackInputTypeNameSpace = callbackInputTypeNameSpace;
	}

	public String getCallbackOutputType() {
		return callbackOutputType;
	}

	public void setCallbackOutputType(String callbackOutputType) {
		this.callbackOutputType = callbackOutputType;
	}

	public String getCallbackOutputTypeNameSpace() {
		return callbackOutputTypeNameSpace;
	}

	public void setCallbackOutputTypeNameSpace(String callbackOutputTypeNameSpace) {
		this.callbackOutputTypeNameSpace = callbackOutputTypeNameSpace;
	}

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	public String getInputTypeNameSpace() {
		return inputTypeNameSpace;
	}

	public void setInputTypeNameSpace(String inputTypeNameSpace) {
		this.inputTypeNameSpace = inputTypeNameSpace;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getOutputTypeNameSpace() {
		return outputTypeNameSpace;
	}

	public void setOutputTypeNameSpace(String outputTypeNameSpace) {
		this.outputTypeNameSpace = outputTypeNameSpace;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    /**
     * @param scripts the scripts to set
     */
    public void setScripts(Script[] scripts) {
        this.scripts = scripts;
    }

   
    protected Script initScript(Element scriptEl) {
        Script script;

        script = new Script();
        script.initFromXMLDocument(scriptEl);
        return script;
    }

    protected void init() {

    }

    public LifeCycle getServiceLifeCycle()
    {
        return this.serviceLifeCycle;
    }

    void dumpOperation() {
        for(Script s:this.scripts)
            s.dumpScript();
    }

    void setParent(Interface aThis) {
        parentInterf=aThis;

        for(Script s:this.scripts)
            s.setParent(this);
    }

    public String getAdmittedHosts() {
        return admittedHosts;
    }

    public void setAdmittedHosts(String admittedHosts) {
        this.admittedHosts = admittedHosts;
    }

    public boolean isAsynchronous()
    {
        return this.type.equals(it.intecs.pisa.common.tbx.Operation.OPERATION_TYPE_ASYNCHRONOUS);
    }
}
