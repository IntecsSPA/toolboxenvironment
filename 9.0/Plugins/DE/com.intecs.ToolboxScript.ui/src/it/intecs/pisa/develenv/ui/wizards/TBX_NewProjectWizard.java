/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TBX_NewProjectWizard.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.10 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/02/19 13:08:52 $
 * File ID: $Id: TBX_NewProjectWizard.java,v 1.10 2007/02/19 13:08:52 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.wizards;


import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectPreferences;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The Class TBX_NewProjectWizard.
 */
public class TBX_NewProjectWizard extends Wizard implements INewWizard {



	private static final String LABEL_CREATING_SERVICE = "Creating service";
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		this.page = new TBX_NewProjectWizardPage(this.selection);
		this.addPage(this.page);
		
		this.descpage = new TBX_NewProjectDescriptionWizardPage(this.selection);
		this.addPage(this.descpage);
		
		this.interfacepage=new TBX_NewProjectInterfaceWizardPage(this.selection);
		this.addPage(this.interfacepage);

		this.operationspage=new TBX_NewProjectOperationsWizardPage(this.selection);
		this.addPage(this.operationspage);
		
		this.requestpage=new TBX_NewProjectRequestManagementWizardPage(this.selection);
		this.addPage(this.requestpage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Interface interf=null;
		Interface copyInterface=null;
		final String toolboxUrl;
		final String username;
		final String password;
		final String debugPort;
		final Service serviceDescr;
		
		//retrieving selected interface
		interf=interfacepage.getSelectedInterface();
		
		copyInterface=new Interface();
		
		if(this.interfacepage.needsRepositoryInterface())
		{
			
			
			copyInterface.setName(interf.getName());
			copyInterface.setVersion(interf.getVersion());
			copyInterface.setMode(interf.getMode());
			copyInterface.setType(interf.getType());
			copyInterface.setSchemaDir(interf.getSchemaDir());
			copyInterface.setSchemaRoot(interf.getSchemaRoot());
			copyInterface.setTargetNameSpace(interf.getTargetNameSpace());
			copyInterface.setWsdlPath(interf.getWsdlPath());
			copyInterface.setOperations(operationspage.getSelectedOperations());
		}
		else
		{
			/*copyInterface.setName(interf.getName());
			copyInterface.setVersion(interf.getVersion());
			copyInterface.setSchemaDir(interf.getSchemaDir());
			copyInterface.setSchemaRoot(interf.getSchemaRoot());
			copyInterface.setTargetNameSpace(interf.getTargetNameSpace());
			copyInterface.setWsdlPath(interf.getWsdlPath());*/
		}
		//		filling serviceDescriptor
		serviceDescr=new Service();
		
		serviceDescr.setServiceName(page.getServiceName());
		serviceDescr.setServiceAbstract(descpage.getServiceAbstract());
		serviceDescr.setServiceDescription(descpage.getServiceDescription());
		serviceDescr.setQueuing(requestpage.getQueuing());
		serviceDescr.setSuspendMode(requestpage.getSuspendMode());
		serviceDescr.setImplementedInterface(copyInterface);
		
		if(this.requestpage.getSSLCertificate()!=null) {
			serviceDescr.setSSLcertificate(this.requestpage.getSSLCertificate());
		}
		
		
		toolboxUrl=requestpage.getToolboxUrl();
		username=requestpage.getToolboxUsername();
		password=requestpage.getToolboxPassword();

		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					TBX_NewProjectWizard.this.doFinish(serviceDescr,toolboxUrl,username, password, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			this.getContainer().run(true, false, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(this.getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}
	
	

	/**
	 * Do finish.
	 * @param toolboxUsername TODO
	 * @param toolboxPassword TODO
	 * @param monitor
	 *            the monitor
	 * @param ProjectName
	 *            the Project name
	 * @throws CoreException
	 *             the core exception
	 */

	private void doFinish(final Service descr, final String toolboxHost,String toolboxUsername, String toolboxPassword, final IProgressMonitor monitor)
			throws CoreException {
		String serviceName;
		IProject project=null;
		ToolboxEclipseProject tbxProject=null;
		
		serviceName=descr.getServiceName();
		
		tbxProject=new ToolboxEclipseProject(descr);
		tbxProject.createProject();
		
		project=getProject(serviceName);
		ToolboxEclipseProjectPreferences.setToolboxHostURL(project,toolboxHost);
		ToolboxEclipseProjectPreferences.setToolboxHostUsername(project,toolboxUsername);
		ToolboxEclipseProjectPreferences.setToolboxHostPassword(project,toolboxPassword);
		
		monitor.beginTask(LABEL_CREATING_SERVICE + serviceName, 1);
	}
	
	
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}
	
	@Override
	public boolean canFinish()
	{
		if(this.requestpage.isPageDisplayed()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The selection.
	 */
	private ISelection selection;

	private TBX_NewProjectWizardPage page;
	private TBX_NewProjectDescriptionWizardPage descpage;
	private TBX_NewProjectInterfaceWizardPage interfacepage;
	private TBX_NewProjectOperationsWizardPage operationspage;
	private TBX_NewProjectRequestManagementWizardPage requestpage;


	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		// TODO Auto-generated method stub
		
		if((page==this.interfacepage) && (interfacepage.needsRepositoryInterface()==false))
			return requestpage;
		else return super.getNextPage(page);
	}
	
	private IProject getProject(String projectName) {
		IWorkspaceRoot root;
		IProject project;

		root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(projectName);
		
		return project;
	}
}
