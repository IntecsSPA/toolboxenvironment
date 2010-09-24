/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptAsynchronousNewWizard.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.5 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/02/19 13:08:52 $
 * File ID: $Id: TscriptAsynchronousNewWizard.java,v 1.5 2007/02/19 13:08:52 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.wizards;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.utils.InterfaceDescriptionLoader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * The Class TscriptAsynchronousNewWizard.
 */

public class TBX_AddRemoveOperationsWizard extends Wizard implements INewWizard {
	
	private static final String WIZARD_PAGE_TITLE = "This wizard adds or removes operations to a service.";
	private static final String FILE_SERVICE_DESCRIPTOR_XML = "serviceDescriptor.xml";
	private static final String FILENAME_SERVICE_DESCRIPTOR = "serviceDescriptor.xml";
	
	/**
	 * The page.
	 */
	private TBX_AddRemoveOperationsWizardPage page=null;
	private TBX_AddRemoveOperationsSelectionWizardPage selectionPage=null;
	private TBX_AddRemoveOperationsNoRepositoryInterfaceWizardPage noRepoPage=null;
	
	/**
	 * The selection.
	 */
	private ISelection selection;

	/**
	 * The Constructor.
	 */
	public TBX_AddRemoveOperationsWizard() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		try {
			page=new TBX_AddRemoveOperationsWizardPage(selection);
			page.setMessage(WIZARD_PAGE_TITLE);
			this.addPage(page);
			
			selectionPage=new TBX_AddRemoveOperationsSelectionWizardPage(selection);
			this.addPage(selectionPage);
			
			noRepoPage=new TBX_AddRemoveOperationsNoRepositoryInterfaceWizardPage(selection);
			this.addPage(noRepoPage);
		} catch (final Exception e) {
			
		}
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String projectName=this.page.getSelectedProjectName();
		final String[] operations=this.selectionPage.getCheckedOperations();
		
		
		try {
			doFinish(projectName,operations,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	/*	final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					TBX_AddRemoveOperationsWizard.this.doFinish(projectName,operations,monitor);
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
		}*/
		return true;
	}

	/**
	 * Do finish.
	 * 
	 * @param operationName the operation name
	 * @param containerName the container name
	 * @param monitor the monitor
	 * 
	 * @throws CoreException the core exception
	 */
	private void doFinish(final String projectName, final String[] operations,final IProgressMonitor monitor) throws Exception {
		Service descr=null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project = null;
		IFile descriptorFile=null;
		Interface implementedInterface=null;
		File descrFile;
			
		wrkSpace = ResourcesPlugin.getWorkspace();
		root = wrkSpace.getRoot();
		project = root.getProject(projectName);
		
		descriptorFile=project.getFile(FILENAME_SERVICE_DESCRIPTOR);
		
		descrFile=descriptorFile.getLocation().toFile();
		//loading service descriptor
		descr=new Service();
		descr.loadFromFile(descrFile);
				
		implementedInterface=descr.getImplementedInterface();
		
		//updating service depending on its implemented interface
		if(implementedInterface.implementsOutOfRepositoryInterface())
			addOutOfRepoOperation(descr);
		else updateRepositoryInterface(operations, descr);
		
}

	private void addOutOfRepoOperation(Service descr) throws Exception {
		Operation newOperation;
		
		newOperation=noRepoPage.getOperation();
		
		ToolboxEclipseProject eclipseProject = new 	ToolboxEclipseProject(descr);
		eclipseProject.addOperation(newOperation);
	}

	private void updateRepositoryInterface(String[] operations, Service descr) {
		Service newDescriptor;
		Interface repoInterf;
		InterfacesDescription repository;
		String interfName;
		String interfVersion;
		Operation[] newOperations;
		Operation[] repoOperations;
		Operation[] implementedOperations;
		ToolboxEclipseProject eclipseProject;
		Interface implementedInterface=null;
		int i=0;
		
		implementedInterface=descr.getImplementedInterface();
		
		interfName=implementedInterface.getName();
		interfVersion=implementedInterface.getVersion();
		
		//loading interfacesDescription
		repository=InterfaceDescriptionLoader.load();
		repoInterf=repository.getInterface(interfName, interfVersion);
		
		repoOperations=repoInterf.getOperations();
		implementedOperations=descr.getImplementedInterface().getOperations();
		
		newOperations=new Operation[operations.length+implementedOperations.length];
		
		for(Operation oper: repoOperations)
		{
			//checking if shall be added
			for(String selectedOper: operations)
			{
				if(oper.getName().equals(selectedOper))
				{
					newOperations[i]=oper;
					i++;
				}
			}
		}
		
		for(Operation op:implementedOperations)
		{
			newOperations[i]=op;
			i++;
		}
		
		repoInterf.setOperations(newOperations);
		
		//		creating a virtual service Descriptor
		newDescriptor=new Service();
		newDescriptor.setQueuing(descr.isQueuing());
		newDescriptor.setServiceAbstract(descr.getServiceAbstract());
		newDescriptor.setServiceDescription(descr.getServiceDescription());
		newDescriptor.setServiceName(descr.getServiceName());
		newDescriptor.setSSLcertificate(descr.getSSLcertificate());
		newDescriptor.setSuspendMode(descr.getSuspendMode());
		newDescriptor.setImplementedInterface(repoInterf);
		
		eclipseProject=new 	ToolboxEclipseProject(descr);
		eclipseProject.updateProject(newDescriptor);
	}

	


	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}
	
	
	private Service getServiceDescriptor(String projectName) {
		Service descriptor;
		IWorkspaceRoot root;
		IProject project;
		IFile file;
		File descriptorFile;
		root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(projectName);
		file = project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
		
		descriptorFile = new File(root.getLocation().toFile(),file.getFullPath().toOSString());
		
		descriptor = new Service();
		descriptor.loadFromFile(descriptorFile);
		return descriptor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage prevPage) {
		// TODO Auto-generated method stub
		Service descriptor =null;
		
		descriptor=getSelectedServiceDescriptor();
		
		if(hasServiceNoInterface(descriptor))
			if(prevPage==page)
				return this.noRepoPage;
			else return this.selectionPage;
		else return super.getNextPage(page);
	}

	private Service getSelectedServiceDescriptor() {
		Service descriptor=null;
		String projectName=null;
		
		
		projectName=this.page.getSelectedProjectName();
		descriptor = getServiceDescriptor(projectName);
		return descriptor;
	}
	
	private boolean hasServiceNoInterface(Service descriptor)
	{
		String interfaceName=null;
		String interfaceVersion=null;
		
		interfaceName=descriptor.getImplementedInterface().getName();
		interfaceVersion=descriptor.getImplementedInterface().getVersion();
		
		if(interfaceName.equals("") && interfaceVersion.equals(""))
			return true;
		else return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		Service descriptor =null;
		
		descriptor=getSelectedServiceDescriptor();
		
		if(hasServiceNoInterface(descriptor))
			return page.isPageComplete() && noRepoPage.isPageComplete();
		else return page.isPageComplete() && selectionPage.isPageComplete();
	}
	
}