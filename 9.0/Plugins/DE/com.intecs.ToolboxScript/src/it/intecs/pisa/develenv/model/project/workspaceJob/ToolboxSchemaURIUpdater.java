/**
 * 
 */
package it.intecs.pisa.develenv.model.project.workspaceJob;

import java.io.File;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.util.DOMUtil;

import org.eclipse.core.resources.IFile;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Massimiliano
 *
 */
public class ToolboxSchemaURIUpdater extends WorkspaceJob {
	private static final String ATTRIBUTE_SCHEMA_LOCATION = "schemaLocation";
	
	protected IProject project=null;
	protected String oldPath=null;
	protected String newPath=null;
	
	public ToolboxSchemaURIUpdater(String projectName,String oldPath,String newPath) {
		super("Updating schema URI");

		this.project=getProject(projectName);
		this.oldPath=oldPath;
		this.newPath=newPath;
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
		IFolder schemaDir=null;
			
		try {
			schemaDir=project.getFolder(ToolboxEclipseProject.FOLDER_SCHEMAS);
			
			updateFolder(schemaDir);
			
			project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
	}

	protected void updateFolder(IFolder folder)
	{
		IResource[] children=null;

		try
		{
			children=folder.members(false);
			for(IResource resource: children)
			{
				if(resource instanceof IFolder)
					updateFolder((IFolder) resource);
				else if(resource instanceof IFile)
					updateFile((IFile)resource);
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	
	protected void updateFile(IFile file)
	{
		Document document=null;
		DOMUtil util=null;
		String path=null;
		Element root=null;
		NodeList importNodes=null;
		NodeList includeNodes=null;
		Element importNode=null;
		Element includeNode=null;
		String schemaLocation=null;
		boolean modified=false;
		File oldFile=null;
		File schemaLocationFile=null;
		
		try
		{
			util=new DOMUtil();
			
			path=file.getLocationURI().getPath();
			
			document=util.fileToDocument(path);
			root=document.getDocumentElement();
			
			importNodes = root.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "import");	
			for(int i=0;i<importNodes.getLength();i++)
			{
				importNode=(Element) importNodes.item(i);
				
				schemaLocation=importNode.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);
				
				//getting canonical files in order to remove all . and .. paths
				oldFile=(new File(oldPath)).getCanonicalFile();
				
				if(schemaLocation.startsWith("file:"))
					schemaLocationFile=(new File(schemaLocation.substring(5))).getCanonicalFile();
				else schemaLocationFile=(new File(schemaLocation)).getCanonicalFile();
				
				if(oldFile.equals(schemaLocationFile))
				{
					importNode.setAttribute(ATTRIBUTE_SCHEMA_LOCATION, (new File(newPath)).toURI().toString());
					modified=true;
				}
			}
			
			includeNodes=root.getElementsByTagNameNS("http://www.w3.org/2001/XMLSchema", "include");
			//importNodes=root.getElementsByTagName("include");
			for(int i=0;i<includeNodes.getLength();i++)
			{
				includeNode=(Element) includeNodes.item(i);
				
				schemaLocation=includeNode.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);
				
				//getting canonical files in order to remove all . and .. paths
				oldFile=(new File(oldPath)).getCanonicalFile();
				
				if(schemaLocation.startsWith("file:"))
					schemaLocationFile=(new File(schemaLocation.substring(5))).getCanonicalFile();
				else schemaLocationFile=(new File(schemaLocation)).getCanonicalFile();
				
				
				if(oldFile.equals(schemaLocationFile))
				{
					includeNode.setAttribute(ATTRIBUTE_SCHEMA_LOCATION, (new File(newPath)).toURI().toString());
					modified=true;
				}
			}
			
			if(modified==true)
			{
				DOMUtil.dumpXML(document, new File(path));
			}
			
		}
		catch(Exception e)
		{
			
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
