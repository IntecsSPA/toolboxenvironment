
package it.intecs.pisa.develenv.model.project;

import it.intecs.pisa.common.tbx.exceptions.CannotAuthenticateException;
import it.intecs.pisa.common.tbx.exceptions.CannotDeployException;
import it.intecs.pisa.common.tbx.exceptions.CannotUndeployException;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectDeployer;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.eclipse.core.resources.IProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This class shall be used to deploy a service to a Toolbox Runtime Environment. 
 * @author Massimiliano Fanciulli
 *
 */
public class ToolboxEclipseProjectDeployer {
    
	 public static boolean undeploy(String projectName, String deployURL, String username, String password) throws CannotAuthenticateException, CannotUndeployException
	    {
		IProject serviceProject=null;
		String token;
		try {
		    serviceProject=ToolboxEclipseProjectUtil.getProject(projectName);
		    
		    token = authenticate(deployURL, username,password);
			if (token == null) {
				throw new CannotAuthenticateException();
			}
			
			if(ToolboxEclipseProjectDeployer.undeploy(serviceProject, deployURL)==false)
		    	throw new CannotUndeployException();
		    
		    return invalidateToken(deployURL);
		} catch (IllegalArgumentException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    return false;
		}
	    }
	
	
    public static boolean undeploy(IProject serviceProject, String hostURL)
    {
    	HttpClient client;
		GetMethod method;
		int statusCode=200;
		String url;
    	try
    	{
    		if(checkIfServiceIsDeployed(hostURL, serviceProject.getName()))
    		{
    			url=hostURL+"/manager?cmd=delSrv&serviceName="+serviceProject.getName();
        		//performing undeploy	
				client = new HttpClient();
				    
				method = new GetMethod(url);
				statusCode=client.executeMethod(method);
    		}

    		return statusCode==200;
    	}
    	catch(Exception ecc)
    	{
    		return false;
    	}
    }
	public static boolean checkIfServiceIsDeployed(String hostURL, String serviceName) throws IOException, HttpException, SAXException {
		GetMethod get;
		String getServiceListUrl;
		MultiThreadedHttpConnectionManager connectionManager;
		DOMUtil util;
		Document serviceListDoc;
		InputStream in;
		util=new DOMUtil();
		Element rootEl;
		Element serviceEl;
		LinkedList serviceList;
				
		getServiceListUrl=hostURL+"/manager?cmd=GetServiceList";
		connectionManager = new MultiThreadedHttpConnectionManager();
		
		HttpClient client = new HttpClient(connectionManager);
		
		get = new GetMethod(getServiceListUrl);
		
		client.executeMethod(get);
		in = get.getResponseBodyAsStream();
		
		serviceListDoc=util.inputStreamToDocument(in);
		
		rootEl=serviceListDoc.getDocumentElement();
		serviceList=DOMUtil.getChildren(rootEl);
		
		for(int i=0;i<serviceList.size();i++)
		{
			serviceEl=(Element) serviceList.get(i);
			if(serviceEl.getAttribute("name").equals(serviceName))
				return true;
		}
		
		return false;
		
	}
    /**
     * This method shall be invoked in order to deploy the service described by serviceProject
     * to the Toolbox Runtime Environment whose URL is stored into the project preferences.
     * 
     * @param serviceProject Service project that shall be deployed
     * @param deployURL TODO
     * @param authToken TODO
     * @return true or false, depending on the deploy success
     */
    public static boolean deploy(IProject serviceProject, String deployURL, String authToken)
    {
	PostMethod post=null;
	File packageFile=null;
	FilePart part;
	StringPart token;
	int retValue=0;
	
	try {
		if(undeploy(serviceProject,deployURL)==false)
			return false;
		
	    packageFile=new File(System.getProperty("user.home"),serviceProject.getName()+".zip");
	    
	    ToolboxEclipseProjectUtil.createExportpackage(serviceProject, ToolboxEclipseProjectUtil.PackageType.FULL_DEPLOY, packageFile.getAbsolutePath());
	  
	    MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
	      	
	    HttpClient client = new HttpClient(connectionManager);
	    
	    post = new PostMethod(deployURL+"/deploy");   
	    
	    part=new FilePart(packageFile.getName(), packageFile);
	    part.setContentType("application/x-zip-compressed");
	    
	    token=new StringPart("authToken",authToken);
	    token.setContentType("text");
	    
	    Part[] parts = {part,token};
	    
	    post.setRequestEntity(
	        new MultipartRequestEntity(parts, post.getParams())
	        );
	  
	   
	    retValue=client.executeMethod(post);
	   
	    packageFile.delete();
	   
	    // handle response.
	    return retValue==200;
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}
	finally
	{
	    if(post!=null)
		post.releaseConnection();
	}
    }
    
    /**
     * This method shall be invoked in order to deploy the service described by serviceProject
     * to the Toolbox Runtime Environment whose URL is stored into the project preferences.
     * @param deployURL TODO
     * @param token TODO
     * @param serviceProject Service project that shall be deployed
     * @return true or false, depending on the deploy success
     */
    public static boolean deploy(String projectName, String deployURL, String username,String password) throws CannotAuthenticateException,CannotDeployException
    {
	IProject serviceProject=null;
	String token;
	try {
	    serviceProject=ToolboxEclipseProjectUtil.getProject(projectName);
	    
	    token = authenticate(deployURL, username,password);
		if (token == null) {
			throw new CannotAuthenticateException();
		}
	    
	    if(ToolboxEclipseProjectDeployer.deploy(serviceProject, deployURL, token)==false)
	    	throw new CannotDeployException();
	    
	    return invalidateToken(deployURL);
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return false;
	}
    }
    
    protected static boolean invalidateToken(String tbxUrl) {
		HttpClient client;
		GetMethod method;
		int statusCode = 200;
		String url;
	
		try {
			url = tbxUrl + "/manager?cmd=invalidateAuthentication";

			client = new HttpClient();

			method = new GetMethod(url);
			statusCode = client.executeMethod(method);

			return statusCode==200;
		} catch (Exception ecc) {
			return false;
		}
	}

	protected static String authenticate(String tbxUrl, String username, String password) {
		HttpClient client;
		GetMethod method;
		String url;
		DOMUtil util;
		Document respDoc;
		Element rootEl;
		try {
			util = new DOMUtil();
			url = tbxUrl + "/manager?cmd=authenticate&username=" + username
					+ "&password=" + password;

			client = new HttpClient();

			method = new GetMethod(url);
			int statusCode = client.executeMethod(method);

			respDoc = util.inputStreamToDocument(method
					.getResponseBodyAsStream());
			rootEl = respDoc.getDocumentElement();

			if (Boolean.parseBoolean(rootEl.getAttribute("success")) == true)
				return rootEl.getAttribute("token");
			else
				return null;
		} catch (Exception ecc) {
			return null;
		}
	}
    
}
