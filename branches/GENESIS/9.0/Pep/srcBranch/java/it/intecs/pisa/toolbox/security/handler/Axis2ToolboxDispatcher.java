package it.intecs.pisa.toolbox.security.handler;

import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;

import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.AxisOperation;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.HandlerDescription;
import org.apache.axis2.engine.AbstractDispatcher;

/***
 * This class allows to add a custom dispatcher for Axis2 framework, in particular it allows to retrieve the 
 * Toolbox Axis service operation to be invoked, despite of the actual SOAP action value/existence.
 * 
 * Also it allows to map each unprotected service request to the default ToolboxDefaultUnprotectedService Axis2 service
 * It has to be referred in the axis2.xml configuration file.
 * @author Stefano
 *
 */
public class Axis2ToolboxDispatcher extends AbstractDispatcher {
	
	public static final String NAME = "ToolboxBasedDispatcher";
	
	public Axis2ToolboxDispatcher()
	{
	}

	public AxisOperation findOperation(AxisService service, MessageContext messageContext)
	throws AxisFault {
		HttpServletRequest req =(HttpServletRequest)messageContext.getProperty("transport.http.servletRequest");
                
            ToolboxConfiguration tbxConf=ToolboxConfiguration.getInstance();    
	    if (!(req.getRequestURI().indexOf(tbxConf.getApplicationName())>0 ||
                    req.getRequestURI().indexOf(tbxConf.getApplicationName())>0))
	    	return null;
		
		AxisOperation operation = messageContext.getAxisService().getOperation(new QName("execute"));
		return operation;
	}

	
	public void initDispatcher() {
        init(new HandlerDescription(NAME));
    }
	
	/**
	 * Unprotected Toolbox services have not dedicated entries in services.xml.
	 * Each protected Toolbox services has a dedicated entry in the services.xml and this entry allows
	 * Axis2 to retrieve the dedicated Axis2 service; so, according to the axis2 configuration and handlers priority,
	 * in case of protected service request this method is not invoked by the Axis engine. 
	 * */
	public AxisService findService(MessageContext messageContext) throws AxisFault {
				
		return messageContext.getConfigurationContext().getAxisConfiguration().getService("ToolboxDefaultUnprotectedService");
	}
}
