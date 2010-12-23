/**
 * 
 */
package it.intecs.pisa.develenv.model.project.workspaceJob;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Massimiliano
 *
 */
public class ToolboxOperationsFolderDeletion extends WorkspaceJob {

	private String operationName=null;
	private IProject project=null;
	
	public ToolboxOperationsFolderDeletion(String projectName,String operationName) {
		super("Removing "+operationName+" operation from descriptor");

		project=getProject(projectName);
	
		this.operationName=operationName;
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		IStatus status=Status.CANCEL_STATUS;
		
		status=doSomeWork(monitor);
		
		return status;
	}
	
	protected IStatus doSomeWork(IProgressMonitor monitor) {
		IFile serviceDescriptor=null;
		Service descriptor=null;
		Interface implementedInterface=null;
		String descriptorPath=null;
		
		try {	
			serviceDescriptor=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
			descriptorPath=serviceDescriptor.getLocationURI().getPath();
			
			descriptor=new Service();
			descriptor.loadFromFile(descriptorPath);
			
			implementedInterface=descriptor.getImplementedInterface();
			
			implementedInterface.removeOperation(operationName);
			
			descriptor.dumpToDisk(descriptorPath);
			
			return Status.OK_STATUS;
		} catch (Exception e) {
			return Status.CANCEL_STATUS;
		}
		
	}
	
	private IProject getProject(String projectName) {
		IProject project;
		IWorkspaceRoot root;
		IWorkspace workspace;
		
		workspace=ResourcesPlugin.getWorkspace();
		root = workspace.getRoot();
		project=root.getProject(projectName);
		
		return project;
	}

}
