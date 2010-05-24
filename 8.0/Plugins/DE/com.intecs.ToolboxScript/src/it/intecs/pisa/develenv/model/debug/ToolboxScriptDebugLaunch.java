package it.intecs.pisa.develenv.model.debug;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.communication.ClientDebugConsole;
import it.intecs.pisa.develenv.model.asynchJobs.AsynchInfoDialogs;
import it.intecs.pisa.develenv.model.launch.ToolboxScriptLaunchConfiguration;
import it.intecs.pisa.develenv.model.launch.ToolboxScriptRunLaunch;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectPreferences;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.util.DOMUtil;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IBreakpoint;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ToolboxScriptDebugLaunch extends ToolboxScriptRunLaunch {

	public static boolean execute(ILaunchConfiguration configuration,
			String mode, ILaunch launch, IProgressMonitor monitor) {
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
		boolean isAsynchronous = false;
		Operation operation;
		InputStream inputStream;
		InputStream responseStream;
		InputStream pushedMessageStream;
		ClientDebugConsole console=null;
		String messageId;
		final int monitorSteps = 8;
		String monitorTaskText;
		IWorkspace wrkSpace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = wrkSpace.getRoot();
		long checkEach = 1000;

		try {
			// retrieving configuration parameters
			serviceName = configuration.getAttribute(
					ToolboxScriptLaunchConfiguration.CONFIGURATION_SERVICE,
					(String) null);
			operationName = configuration.getAttribute(
					ToolboxScriptLaunchConfiguration.CONFIGURATION_OPERATION,
					(String) null);
			testFilePath = configuration.getAttribute(
					ToolboxScriptLaunchConfiguration.CONFIGURATION_TESTFILE,
					(String) null);
			pollingRate = configuration
					.getAttribute(
							ToolboxScriptLaunchConfiguration.CONFIGURATION_POLLING_RATE,
							(String) null);

			monitorTaskText = "Debugging operation " + operationName
					+ " exposed by the service " + serviceName;
			monitor.beginTask(monitorTaskText, monitorSteps);

			monitor.setTaskName("Retrieving TOOLBOX Url");
			// preparing host url
			project = ToolboxEclipseProjectUtil.getProject(serviceName);
			monitor.worked(1);

			// checking if the service is deployed
			monitor.setTaskName("Checking if the service is deployed");
			if (checkIfDeployed(project, serviceName, operationName) == false) {
				notifyServiceOrOperationNotDeployed(serviceName, operationName);

				return false;
			}
			monitor.worked(1);

			monitor.setTaskName("Updating polling rate");
			if (pollingRate != null) {
				if (updatePollingRate(project, serviceName, operationName,
						pollingRate) == false) {
					return false;
				}
			}
			monitor.worked(1);

			monitor.setTaskName("Creating Push Url");
			pushUrl = getPushUrl(project);
			if (pushUrl == null) {
				notifyToolboxUrlError(serviceName);
				return false;
			}
			monitor.worked(1);

			monitor.setTaskName("Creating directory for storing messages");
			// getting Service descriptor
			serviceDescriptorFile = project
					.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
			serviceDescriptorPath = root.getLocation().append(
					serviceDescriptorFile.getFullPath());

			descriptor = new Service();
			descriptor.loadFromFile(serviceDescriptorPath.toFile());

			operation = descriptor.getImplementedInterface().getOperationByName(
					operationName);
			soapAction = operation.getSoapAction();
			checkEach = getPollingRateInMilliseconds(operation, configuration);

			isAsynchronous = operation.getType().equals(
					Operation.OPERATION_TYPE_ASYNCHRONOUS);

			// creating test directory
			testFolder = createTestFolder(project);
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);

			testFile = testFolder.getFile("Input.xml");

			// creating test file
			format = new SimpleDateFormat("yyyyMMddHHmmss");
			messageId = serviceName + "_" + operationName + "_"
					+ format.format(new Date());
			inputStream = createInputTestFile(testFilePath, pushUrl, messageId);

			// saving input test file
			saveFile(inputStream, testFile);
			// AsynchEditorOpener.openFileOnEditorWhenSizeGreaterThanZero(testFile);

			inputStream.close();
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			monitor.worked(1);

			inputStream = createInputTestFile(testFilePath, pushUrl, messageId);

			console=new ClientDebugConsole(-1);
			
			TscriptDebugTarget target = new TscriptDebugTarget(launch, console, testFolder);
			
						
			launch.addDebugTarget(target);
			
			console.setListener(target);

			if (connectToDebugConsole(getManagerUrl(project),console,serviceName,operationName)) {
				
				
				url = getDebugUrl(project,console);
				if (url == null) {
					notifyToolboxUrlError(serviceName);

					return false;
				}
				
				monitor.setTaskName("Invoking operation");
				responseStream = sendFile(url, soapAction, inputStream);
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
					AsynchInfoDialogs.showErrorDialog("Error while debugging operation","Toolbox returned a SOAP fault");
					
					return false;
				}
				
				if(isAsynchronous)
				{
					monitor.setTaskName("Checking for pushed message");
					//performing wait until pushed message arrives
					pushedMessageStream=performCheckForPushedMessage(project,messageId,checkEach,monitor);
					
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
		
				if(checkIfSOAPFault(outputFile))
					AsynchInfoDialogs.showErrorDialog("Error while debugging operation","Toolbox returned a SOAP fault");
				else AsynchInfoDialogs.showInfoDialog("Debugging operation","Test successfully performed.");
				
				
			} else
				AsynchInfoDialogs.showErrorDialog("Debugging operation",
						"Cannot connect to debug console");
		} catch (Exception e) {
			AsynchInfoDialogs.showErrorDialog("Running operation",
					"The following error has occurred while testing the operation: "
							+ e.getMessage());

			return false;
		} finally {
			monitor.done();
		}
		return true;

	}

	private static String getDebugUrl(IProject project, ClientDebugConsole console) {
		String url;
	
		url=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		if(url!=null && url.equals("")==false)
			url+="/debug/"+console.getToken();
		else url=null;
		
		return url;
	}

	private static boolean connectToDebugConsole(String urlString,ClientDebugConsole console,String serviceName,String operationName) {
		URL url;
		String host;
		int port;
		String[][] breakpoints=null;
		IBreakpoint[] breakpointList;
		int i=0;
		TscriptLineBreakpoint lineBrkpt;
		Element rootEl=null;
		String boundedAddress="";
		
		try {
			rootEl=initConsole(urlString);
			
			if(rootEl!=null && rootEl.getAttribute("inited").equals("true"))
			{
				port= Integer.parseInt(rootEl.getAttribute("listeningOnPort"));
				boundedAddress=rootEl.getAttribute("listeningAtAddress");
				
				/*url = new URL(urlString);
				
				host=url.getHost();*/
				
				breakpointList=(IBreakpoint[]) DebugPlugin.getDefault().getBreakpointManager().getBreakpoints();
				breakpoints=new String[breakpointList.length][3];
				
				for(IBreakpoint breakpoint: breakpointList)
				{
					lineBrkpt=(TscriptLineBreakpoint)breakpoint;
					
					breakpoints[i][0]=lineBrkpt.getFilePath();
					breakpoints[i][1]=lineBrkpt.getXPath();
					breakpoints[i][2]=Integer.toString(lineBrkpt.getLineNumber());
					i++;
				}
				
				return console.startDebugSession(boundedAddress, port,serviceName,operationName,breakpoints);
			}
			else return false;
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	protected static Element initConsole(String url) {
		HttpClient client;
		GetMethod method;
		Document doc;
		Element rootEl;
		DOMUtil util;
		
		try {
			util=new DOMUtil();
			url+="?cmd=initDebugConsole";
			
			client= new HttpClient();
			method = new GetMethod(url);
		
			client.executeMethod(method);
						
			doc=util.inputStreamToDocument(method.getResponseBodyAsStream());
			rootEl=doc.getDocumentElement();
			
			return rootEl;
			
		} catch (Exception e) {
			return null;
		} 
	}

}
