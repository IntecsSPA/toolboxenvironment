/*
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package it.intecs.pisa.develenv.model.launch;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.asynchJobs.AsynchInfoDialogs;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectPreferences;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.SOAPMessageBuilder;
import it.intecs.pisa.util.SOAPUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * This class is used to execute a run
 * @author Massimiliano
 *
 */
public class ToolboxScriptRunLaunch {
	
	public static boolean execute(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		String url;
		String pushUrl;
		String serviceName;
		String operationName;
		String testFilePath;
		String fileName;
		String pollingRate;
		IProject project;
		IFile testFile;
		IFile outputFile;
		IFile pushedMessageFile;
		IFile serviceDescriptorFile;
		IPath serviceDescriptorPath;
		IFolder testFolder;
		String soapAction;
		Service descriptor;
		SimpleDateFormat format;
		boolean isAsynchronous=false;
		Operation operation;
		InputStream inputStream;
		InputStream responseStream;
		InputStream pushedMessageStream;
		String messageId;
		final int monitorSteps=8; 
		String monitorTaskText;
		IWorkspace wrkSpace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wrkSpace.getRoot();
		long checkEach=1000;
		
		try
		{
			//retrieving configuration parameters
			serviceName= configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_SERVICE, (String)null);
			operationName=configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_OPERATION, (String)null);
			testFilePath=configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_TESTFILE, (String)null);
			pollingRate=configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_POLLING_RATE, (String)null);
			
			monitorTaskText="Testing operation "+operationName+" exposed by the service "+serviceName;
			monitor.beginTask(monitorTaskText, monitorSteps);
			
			monitor.setTaskName("Retrieving TOOLBOX Url");
			//	preparing host url
			project=ToolboxEclipseProjectUtil.getProject(serviceName);
			url=getTestUrl(project);
			if(url==null)
			{
				notifyToolboxUrlError(serviceName);

				return false;
			}
			monitor.worked(1);
			
			//checking if the service is deployed
			monitor.setTaskName("Checking if the service is deployed");
			if(checkIfDeployed(project,serviceName,operationName)==false)
			{
				notifyServiceOrOperationNotDeployed(serviceName,operationName);
 
				return false;
			}
			monitor.worked(1);
			
			monitor.setTaskName("Updating polling rate");
			if(pollingRate!=null)
			{
				if(updatePollingRate(project,serviceName,operationName,pollingRate)==false)
				{
					return false;
				}
			}
			monitor.worked(1);
			
			monitor.setTaskName("Creating Push Url");
			pushUrl=getPushUrl(project);
			if(pushUrl==null)
			{
				notifyToolboxUrlError(serviceName);
				return false;
			}
			monitor.worked(1);
			
			
			monitor.setTaskName("Creating directory for storing messages");
			//getting Service descriptor
			serviceDescriptorFile=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
			serviceDescriptorPath=root.getLocation().append(serviceDescriptorFile.getFullPath());
			
			descriptor=new Service();
			descriptor.loadFromFile(serviceDescriptorPath.toFile());
			
			operation=descriptor.getImplementedInterface().getOperationByName(operationName);
			soapAction=operation.getSoapAction();
			checkEach=getPollingRateInMilliseconds(operation, configuration);
			
			isAsynchronous=operation.getType().equals(Operation.OPERATION_TYPE_ASYNCHRONOUS);
			

			//creating test directory
			testFolder=createTestFolder(project);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			
			testFile=testFolder.getFile("Input.xml");
			
			//creating test file
			format=new SimpleDateFormat("yyyyMMddHHmmss");
			messageId=serviceName+"_"+operationName+"_"+format.format(new Date());
			inputStream = createInputTestFile(testFilePath,pushUrl,messageId);
			
			//saving input test file
			saveFile(inputStream,testFile);
			//AsynchEditorOpener.openFileOnEditorWhenSizeGreaterThanZero(testFile);
			
			inputStream.close();
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			monitor.worked(1);
			
			
			inputStream = createInputTestFile(testFilePath,pushUrl,messageId);
			monitor.setTaskName("Invoking operation");
			responseStream = sendFile(url, soapAction,inputStream);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			monitor.worked(1);
			
			monitor.setTaskName("Saving operation response");
			
			if(isAsynchronous)
				fileName="InputAck.xml";
			else fileName="Output.xml";
				
			outputFile=testFolder.getFile(fileName);
			saveFile(responseStream,outputFile);
			
			responseStream.close();
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			monitor.worked(1);
			
			if(checkIfSOAPFault(outputFile))
			{
				AsynchInfoDialogs.showErrorDialog("Error while running operation","Toolbox returned a SOAP fault");
				return false;
			}
			
			if(isAsynchronous)
				retrieveExecutionTree(project,serviceName,"A", messageId,"executionResult_responseBuilder.xml", testFolder);
			else retrieveExecutionTree(project,serviceName,"S", messageId,"executionResult.xml", testFolder);
			
			if(isAsynchronous && monitor.isCanceled()==false)
			{
				monitor.setTaskName("Checking for pushed message");
				//performing wait until pushed message arrives
				pushedMessageStream=performCheckForPushedMessage(project,messageId,checkEach,monitor,launch);
				
				if(pushedMessageStream!=null)
				{
					pushedMessageFile=testFolder.getFile("Output.xml");
					saveFile(pushedMessageStream,pushedMessageFile);
					pushedMessageStream.close();
										
					project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					monitor.worked(1);
					
					outputFile=pushedMessageFile;
				}
			}
			
			retrieveExecutionTree(project,serviceName,"A", messageId,"executionResult_firstScript.xml", testFolder);
			retrieveExecutionTree(project,serviceName,"A", messageId,"executionResult_secondScript.xml", testFolder);
			retrieveExecutionTree(project,serviceName,"A", messageId,"executionResult_thirdScript.xml", testFolder);
			
			if(monitor.isCanceled())
				AsynchInfoDialogs.showInfoDialog("Running operation","Execution terminated by the user.");
			else if(checkIfSOAPFault(outputFile))
				AsynchInfoDialogs.showErrorDialog("Error while running operation","Toolbox returned a SOAP fault");
			else AsynchInfoDialogs.showInfoDialog("Running operation","Test completed.");
			
		}
		catch(Exception e)
		{
			AsynchInfoDialogs.showErrorDialog("Running operation","The following error has occurred while testing the operation: "+e.getMessage());
			
			return false;
		}
		finally
		{
			monitor.done();
		}
		return true;
	}

	private static void retrieveExecutionTree(IProject prj, String serviceName, String instanceType, String messageId, String resourceKey, IFolder folder) {
		String url;
		HttpClient client;
		GetMethod method;
		int statusCode;
		InputStream response;
		Document doc;
		DOMUtil util;
		IFile file;
		try {
			util=new DOMUtil();
			
			url=ToolboxEclipseProjectPreferences.getToolboxHostURL(prj);
			url+="/manager?cmd=getResource&serviceName="+serviceName;
			url+="&instanceId="+messageId;
			url+="&resourceKey="+resourceKey;
			url+="&instanceType="+instanceType;
			
			client= new HttpClient();
			method = new GetMethod(url);
		
			statusCode = client.executeMethod(method);
			if(statusCode!=200)
				return;
			
			response=method.getResponseBodyAsStream();
			doc=util.inputStreamToDocument(response);
					
			
			file=folder.getFile(resourceKey);
			
			DOMUtil.dumpXML(doc, new File(file.getLocationURI()));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}

	protected static long getPollingRateInMilliseconds(Operation operation, ILaunchConfiguration configuration) {
		String pollingRateStr=null;
		
		try
		{
			if(operation.isAsynchronous()==false)
				return 10000;
							
			pollingRateStr=configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_POLLING_RATE, "");
			
			if(pollingRateStr.equals(""))
			{
				pollingRateStr=operation.getPollingRate();
			}
			
			return getPollingRateInSeconds(pollingRateStr)*1000;
		}
		catch(Exception e)
		{
			return 10000;
		}
	}
	
	protected static long getPollingRateInSeconds(String pr)
	{
		char granularity;
		long multiplier=0;
		String value;
		
		granularity=pr.charAt(pr.length()-1);
		
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
		
		value=pr.substring(0,pr.length()-1);
		return Long.parseLong(value)*multiplier;
	}

	protected static boolean updatePollingRate(IProject project, String serviceName, String operationName, String pollingRate) {
		String url;
		HttpClient client;
		GetMethod method;
		int statusCode;
		
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		url+="/manager?cmd=configureOperation&&serviceName=";
		url+=serviceName;
		url+="&&operationName=";
		url+=operationName;
		url+="&&pollingRate=";
		url+=pollingRate;
		
		try {
			client= new HttpClient();
			method = new GetMethod(url);
		
			statusCode = client.executeMethod(method);
			
			if(statusCode!=200)
				return false;
			else return true;
		} catch (Exception e) {
			return false;
		} 
	}

	protected static void notifyServiceOrOperationNotDeployed(String serviceName, String operationName) throws CoreException {
		String message=null;
		
		message="The service "+serviceName+" and/or the operation "+operationName+" are not deployed";
	
		AsynchInfoDialogs.showErrorDialog("Error while running operation",message);
	}

	protected static void notifyToolboxUrlError(String serviceName) throws CoreException {
		String message=null;
		
		message="Toolbox host has not properly set to the service "+serviceName;
		
		AsynchInfoDialogs.showErrorDialog("Error while running operation",message);
	}

	protected static boolean checkIfSOAPFault(IFile testFile) {
		try {
			InputStream fileContent=testFile.getContents();
			
			DOMUtil util;
			
			util=new DOMUtil();
			Document doc;
		
			doc = util.inputStreamToDocument(fileContent);
			
			return SOAPUtil.isSOAPFault(doc);
		} catch (Exception e) {
			return true;
		} 
	}

	protected static boolean checkIfDeployed(IProject project,String serviceName, String operationName) {
		HttpClient client;
		GetMethod method;
		int statusCode;
		DOMUtil util;
		Document doc;
		Element root;
		Element ithServiceTag;
		Element ithOperationTag;
		Iterator serviceIterator;
		Iterator operationIterator;
		LinkedList serviceTags;
		LinkedList operationTags;
		String url;
		
		util=new DOMUtil();
		
		client= new HttpClient();
	    
		
		
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		url+="/manager?cmd=GetServiceList";
		
		method = new GetMethod(url);
		
		try {
			statusCode = client.executeMethod(method);
			if(statusCode!=200)
				return false;
			
			InputStream serviceList;
			
			serviceList=method.getResponseBodyAsStream();
			
			doc=util.inputStreamToDocument(serviceList);
			
			root=doc.getDocumentElement();
			serviceTags=util.getChildren(root);
			
			serviceIterator=serviceTags.iterator();
			while(serviceIterator.hasNext())
			{
				ithServiceTag=(Element)serviceIterator.next();
				
				if(ithServiceTag.getAttribute("name").equals(serviceName))
				{
					operationTags=util.getChildren(ithServiceTag);
					
					operationIterator=operationTags.iterator();
					while(operationIterator.hasNext())
					{
						ithOperationTag=(Element) operationIterator.next();
						if(ithOperationTag.getAttribute("name").equals(operationName))
							return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		} 
	}

	protected static InputStream performCheckForPushedMessage(IProject project,String messageId, long checkEach, IProgressMonitor monitor, ILaunch launch) {
		String pushedMessageUrl;
		int loopCount=0;
		int maxLoops=10; //should be configurable
		int waitInterval=60000; //should be configurable
		HttpClient client;
		GetMethod method;
		int statusCode=404;
		
		try {
			pushedMessageUrl=GetPushedMessageUrl(project,messageId);
			if(pushedMessageUrl!=null)
			{
				client = new HttpClient();
			    
				method = new GetMethod(pushedMessageUrl);
				
				while(monitor.isCanceled()==false && statusCode==404 && loopCount<maxLoops)
				{
					statusCode=client.executeMethod(method);
					if(statusCode==404)
						Thread.sleep(checkEach);
					
					if(monitor.isCanceled())
						return null;
				}
				
				return method.getResponseBodyAsStream();
			}
			
			return null;
		} catch (Exception e) {
			MessageDialog.openError(null, "Retrieving pushed message", "An error has occurred while trying to retrieve the pushed message");
			return null;
		} 
	}

	protected static String GetPushedMessageUrl(IProject project, String messageId) {
		String url;
		
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		if(url!=null && url.equals("")==false)
			url+="/Push?cmd=GetPushedMessage&&MessageId="+messageId;
		else url=null;
		
		return url;
	}

	protected static String getPushUrl(IProject project) {
		String url;
		
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		if(url!=null && url.equals("")==false)
			url+="/Push";
		else url=null;
		
		return url;
	}
	
	protected static String getManagerUrl(IProject project) {
		String url;
		
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		if(url!=null && url.equals("")==false)
			url+="/manager";
		else url=null;
		
		return url;
	}

	protected static void saveFile(InputStream inputStream,IFile file) throws Exception {
		DOMUtil util;
		Document doc;
	
		
		util=new DOMUtil();
		
		doc=util.inputStreamToDocument(inputStream);
		DOMUtil.dumpXML(doc, file.getLocation().toFile(),true);

	}

	protected static InputStream createInputTestFile(String testFilePath,String pushUrl,String messageId) throws IOException, SAXException, Exception {
		Document testFileDoc;
		Document soapTestFile;
		IPath path;
		File tempFile;
		DOMUtil util;
		IWorkspace wrkSpace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wrkSpace.getRoot();
		InputStream inputStream;
		
		util=new DOMUtil(true);
		path=new Path(testFilePath);
		
		path=root.getLocation().append(path);
		
		testFileDoc=util.fileToDocument(path.toFile());
		soapTestFile=SOAPMessageBuilder.buildSOAPMessage(testFileDoc, messageId, pushUrl);

		inputStream=util.getDocumentAsInputStream(soapTestFile);
		return inputStream;
	}

	protected static IFolder createTestFolder(IProject project) throws CoreException {
		SimpleDateFormat format;
		
		IFolder execResults=project.getFolder("Test Results");
		if(!execResults.exists())
			execResults.create(true, true, null);
		
		format=new SimpleDateFormat("yyyyMMddHHmmss");
		IFolder currentExecutionResultFolder=execResults.getFolder("Test "+format.format(new Date()));
		if(!currentExecutionResultFolder.exists())
			currentExecutionResultFolder.create(true, true, null);
		
		return currentExecutionResultFolder;
		
	}

	protected static InputStream sendFile(String url,String soapAction, InputStream stream) throws FileNotFoundException, IOException, HttpException {
		PostMethod post;
		int result;
		
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		  	
	    HttpClient client = new HttpClient(connectionManager);
	    
	    post = new PostMethod(url);
	    
	    post.setRequestEntity(new InputStreamRequestEntity(stream,"text/xml"));
	    post.setRequestHeader("soapaction",soapAction);
	   
	    result=client.executeMethod(post);
		   
		InputStream in = post.getResponseBodyAsStream();
		return in;
	}

	protected static String getTestUrl(IProject project) {
		String url;
	
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		if(url!=null && url.equals("")==false)
			url+="/services/"+project.getName();
		else url=null;
		
		return url;
	}
	
}
