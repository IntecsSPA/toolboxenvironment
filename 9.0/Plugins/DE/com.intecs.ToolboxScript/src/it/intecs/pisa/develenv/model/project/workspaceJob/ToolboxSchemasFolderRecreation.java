/**
 * 
 */
package it.intecs.pisa.develenv.model.project.workspaceJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;

/**
 * @author Massimiliano
 *
 */
public class ToolboxSchemasFolderRecreation extends ToolboxMainFolderRecreation {
	
	public ToolboxSchemasFolderRecreation(String projectName) {
		super(projectName,ToolboxEclipseProject.FOLDER_SCHEMAS);

	}

	protected void doSomeWork(IProgressMonitor monitor) {
		Service descriptor=null;
		IFile descriptorFile=null;
		String descriptorPath=null;
		
		super.doSomeWork(monitor);
		
		//removing root schema from descriptor
		descriptorFile=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
		
		descriptorPath=descriptorFile.getLocationURI().getPath();
		
		descriptor=new Service();
		descriptor.loadFromFile(descriptorPath);
		
		descriptor.getImplementedInterface().setSchemaRoot("");
		
		descriptor.dumpToDisk(descriptorPath);
	}
}
