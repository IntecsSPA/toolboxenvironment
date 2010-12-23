/**
 * 
 */
package it.intecs.pisa.develenv.model.project.workspaceJob;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;

/**
 * @author Massimiliano
 *
 */
public class ToolboxResourcesFolderRecreation extends ToolboxMainFolderRecreation {
	
	public ToolboxResourcesFolderRecreation(String projectName) {
		super(projectName,ToolboxEclipseProject.FOLDER_RESOURCES);

	}

	protected void doSomeWork(IProgressMonitor monitor) {
		IFolder commonScritsFolder=null;
		IFolder jarsFolder=null;
		try {
			super.doSomeWork(monitor);
			
			commonScritsFolder=folder.getFolder(ToolboxEclipseProject.FOLDER_COMMON_SCRIPTS);
			commonScritsFolder.create(true, true, monitor);
			
			jarsFolder=folder.getFolder(ToolboxEclipseProject.FOLDER_JARS);
			jarsFolder.create(true, true, monitor);
			
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}
}
