/**
 * 
 */
package it.intecs.pisa.develenv.model.project;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxInfoFolderRecreation;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxOperationsFolderDeletion;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxOperationsMainFolderRecreation;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxResourcesFolderRecreation;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxSchemaURIUpdater;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxSchemasFolderRecreation;
import it.intecs.pisa.develenv.model.project.workspaceJob.ToolboxTestFolderRecreation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author Massimiliano
 *
 */
public class ToolboxResourceDeltaVisitor implements IResourceDeltaVisitor {
 	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
			
		
		switch(delta.getKind())
		{
		 case IResourceDelta.ADDED:
			 return processAdd(delta);
                   
		 case IResourceDelta.REMOVED:
        	 return processRemove(delta);
         
         case IResourceDelta.CHANGED:
         break;
		}
		
		return true;
	}

	private boolean processAdd(IResourceDelta delta)
	{
		String removed=null;
		
		if(delta.getMovedFromPath()!=null)
			removed=delta.getMovedFromPath().toString();
		
		if(removed!=null)
		{
			//moved file
			//check if moved file is a schema file
			if(removed.toUpperCase().endsWith(".XSD"))
				return processMovedSchema(delta);
			else
			{
				System.out.print("Moving "+removed+" to "+delta.getResource().getFullPath());
				return true;
			}
		}
		
		return true;
	}
	
	
	private boolean processMovedSchema(IResourceDelta delta) {
		//checking if moved file is the root schema file
		IProject project=null; 
		IFile descriptorFile=null;
		String sourceProjectName=null;
		String destProjectName=null;
		String schemaRoot=null;
		IPath destPath=null;
		IPath sourcePath=null;
		IResource destination=null;
		Service serviceDescriptor=null;
		Interface implementedInterface=null;
		String oldPath=null;
		String newPath=null;
		
		destination=delta.getResource();
		destPath=destination.getFullPath();
		sourcePath=new Path(delta.getMovedFromPath().toString());
		
		
		sourceProjectName=sourcePath.segment(0);
		destProjectName=destPath.segment(0);
		
		
		if(ToolboxEclipseProjectUtil.isToolboxProject(sourceProjectName))
		{
			//should check if schema is still under the Schemas directory
			if(sourceProjectName.equals(destProjectName))
			{
				//we have moved a file into the project
				
				//checking if the schema is still under the Schemas directory
				if(destPath.segmentCount()>2 && destPath.segment(1).equals(ToolboxEclipseProject.FOLDER_SCHEMAS))
				{
					//Checking if the moved file is the schema root
					project=getProject(sourceProjectName);
					descriptorFile=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
					
					
					serviceDescriptor=new Service();
					serviceDescriptor.loadFromFile(descriptorFile.getLocationURI().getPath());
					
					implementedInterface=serviceDescriptor.getImplementedInterface();
					
					schemaRoot=implementedInterface.getSchemaRoot();
					
					//checking if the moved schema is the schema root
					if(schemaRoot.equals(sourcePath.removeFirstSegments(2).toString()))
					{
						implementedInterface.setSchemaRoot(destPath.removeFirstSegments(2).toString());
						serviceDescriptor.dumpToDisk(descriptorFile.getLocationURI().getPath());
					}
					
					//updating the schema references
					
					oldPath=project.getWorkspace().getRoot().getLocation().append(delta.getMovedFromPath()).toString();
					
					newPath=project.getWorkspace().getRoot().getLocation().append(destPath).toString();
										
					updateSchemaURI(sourceProjectName,oldPath,newPath);
					
				}
				else
				{
					//out of the schemas directory
				}
				
				
				
			}
		}
		
		
		return true;
	}

	private boolean processRemove(IResourceDelta delta) {
		IResource res = null;
		IPath path=null;
		
		res=delta.getResource();
			
		if(res instanceof IFolder)
			return processRemoveFolder(res);
		else if(res instanceof IFile)
		{
			path=res.getFullPath();
			if(delta.getMovedToPath()==null && path.lastSegment().toUpperCase().endsWith(".XSD"))
			{
				return processRemoveXSDFile(res);
			}
			
		}
		return true;
	}

	private boolean processRemoveXSDFile(IResource res) {
		IPath path=null;
		IProject project=null;
		IFile descriptor=null;
		String projectName=null;
		Service serviceDescriptor=null;
		String descriptorPath=null;
		Interface implementedInterface=null;
				
		path=res.getFullPath();
		projectName=path.segment(0);
		
		project=getProject(projectName);
		descriptor=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
		
		serviceDescriptor=new Service();
		descriptorPath = descriptor.getLocationURI().getPath();
		serviceDescriptor.loadFromFile(descriptorPath);
		
		implementedInterface=serviceDescriptor.getImplementedInterface();
		
		if(implementedInterface!=null && implementedInterface.getSchemaRoot().equals(path.removeFirstSegments(2).toString()))
		{
			//setting root schema
			implementedInterface.setSchemaRoot("");
			
			serviceDescriptor.dumpToDisk(descriptorPath);
		}
		return true;
	}

	private boolean processRemoveFolder(IResource res) {
		IPath path;
		String projectName;
		String firstLevelFolderName;
		String secondLevelName;
		
		
		path=res.getFullPath();
		
		projectName=path.segment(0);
		firstLevelFolderName=path.segment(1);
		secondLevelName=path.segment(2);
		
		switch(path.segmentCount())
		{
			case 0:
			break;
			
			case 1:
			break;
			
			case 2:
				if(shallProcess(projectName))
				{
					if(firstLevelFolderName.equals(ToolboxEclipseProject.FOLDER_INFO) )
					{
						recreateInfoFolder(projectName);
					}
					else if(firstLevelFolderName.equals(ToolboxEclipseProject.FOLDER_OPERATIONS) )
					{
						recreateOperationFolder(projectName);
					}
					else if(firstLevelFolderName.equals(ToolboxEclipseProject.FOLDER_SCHEMAS) )
					{
						recreateSchemasFolder(projectName);
					}
					else if(firstLevelFolderName.equals(ToolboxEclipseProject.FOLDER_RESOURCES) )
					{
						recreateResourcesFolder(projectName);
					}
					else if(firstLevelFolderName.equals(ToolboxEclipseProject.FOLDER_TEST_DIR) )
					{
						recreateTestFolder(projectName);
					}
					else
					{
						
					}
					
				}
				
			return true;
			
			
			case 3:
				if(shallProcess(projectName))
				{
					
					if(firstLevelFolderName.equals(ToolboxEclipseProject.FOLDER_OPERATIONS))
					{
						removeOperation(projectName,secondLevelName);
					}
					else
					{
						
					}
					
					
				}
				
				
			return true;
			
			default:
				
			return true;
		}
		
		return true;
	}
	
	private void queueJob(WorkspaceJob job,String projectName)
	{
		IProject project=null;
		try {
			
			project=getProject(projectName);
			
			job.setRule(project);
			job.schedule();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateSchemaURI(String projectName,String oldPath,String newPath) {
		ToolboxSchemaURIUpdater job =null;
		
		job= new ToolboxSchemaURIUpdater(projectName,oldPath,newPath);
		queueJob(job,projectName);
	}
	
	private void recreateTestFolder(String projectName) {
		ToolboxTestFolderRecreation job =null;
		
		job= new ToolboxTestFolderRecreation(projectName);
		queueJob(job,projectName);
	}

	private void recreateResourcesFolder(String projectName) {
		ToolboxResourcesFolderRecreation job =null;
		
		job= new ToolboxResourcesFolderRecreation(projectName);
		queueJob(job,projectName);
	}

	private void recreateSchemasFolder(String projectName) {
		ToolboxSchemasFolderRecreation job =null;
		
		job=new ToolboxSchemasFolderRecreation(projectName);
		queueJob(job,projectName);
	}

	private void recreateInfoFolder(String projectName) {
		ToolboxInfoFolderRecreation job = null;
		
		job=new ToolboxInfoFolderRecreation(projectName);
		queueJob(job,projectName);
	}

	private void removeOperation(String projectName,String operationName) {
		ToolboxOperationsFolderDeletion job = null;
		
		job=new ToolboxOperationsFolderDeletion(projectName,operationName);
		queueJob(job,projectName);
	}
	
	
	
	private void recreateOperationFolder(String projectName) {
		ToolboxOperationsMainFolderRecreation job = null;
		
		job=new ToolboxOperationsMainFolderRecreation(projectName);
		queueJob(job,projectName);
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

	private boolean shallProcess(String projectname)
	{
		return ToolboxEclipseProjectUtil.isToolboxProject(projectname);
	}

}
