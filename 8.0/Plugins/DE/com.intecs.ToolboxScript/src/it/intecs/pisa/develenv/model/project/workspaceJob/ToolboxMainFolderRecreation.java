/**
 * 
 */
package it.intecs.pisa.develenv.model.project.workspaceJob;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
public class ToolboxMainFolderRecreation extends WorkspaceJob {

	protected IProject project=null;
	protected IFolder folder=null;
	
	public ToolboxMainFolderRecreation(String projectName,String folderName) {
		super("Recreating folder "+folderName+"after deletion");

		this.project=getProject(projectName);
		this.folder=project.getFolder(folderName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor)
			throws CoreException {
		
		doSomeWork(monitor);
		
		return Status.OK_STATUS;
	}
	
	protected void doSomeWork(IProgressMonitor monitor) {
	
		
		try {
			folder.create(true, true, monitor);
			
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
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
