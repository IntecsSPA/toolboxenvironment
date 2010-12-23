/**
 * 
 */
package it.intecs.pisa.develenv.ui.wizards;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.utils.FileSystemUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Massimiliano
 *
 */
public class TBX_JarsImportWizard extends Wizard implements IImportWizard {

	private TBX_JarsImportSelectionWizardPage selectionPage=null;
	
	/**
	 * 
	 */
	public TBX_JarsImportWizard() {
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		//Copying resource to the directory
		String projectName=null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project =null;
		IFolder resourceFolder=null;
		String resourceToImport=null;
		IFileStore sourceStore=null;
		IFolder destFolder=null;
		IFolder jarsFolder=null;
		IFileInfo sourceInfo=null;
		IFile destFile=null;
		
		try
		{
			//retrieving the resource directory
			wrkSpace=ResourcesPlugin.getWorkspace();
			root=wrkSpace.getRoot();
			
			projectName=selectionPage.getSelectedProject();
			project= root.getProject(projectName);
			resourceFolder=project.getFolder(ToolboxEclipseProject.FOLDER_RESOURCES);
			jarsFolder=resourceFolder.getFolder(ToolboxEclipseProject.FOLDER_JARS);
			
			resourceToImport=selectionPage.getResourceToImport();
			
			sourceStore=EFS.getStore(URIUtil.toURI(resourceToImport));
			//Checking if directory
			sourceInfo=sourceStore.fetchInfo();
			
			if(sourceInfo.isDirectory())
			{
				//must copy a complete directory
				destFolder=jarsFolder.getFolder(sourceStore.getName());
								
				FileSystemUtil.copyDirectory(URIUtil.toURI(resourceToImport).getPath(),destFolder.getLocationURI().getPath(),new String[]{".jar"});
			}
			else
			{
				destFile=jarsFolder.getFile(sourceStore.getName());
				FileSystemUtil.copyFile(URIUtil.toURI(resourceToImport),destFile.getLocationURI());
			}
			
			resourceFolder.refreshLocal(IResource.DEPTH_INFINITE, null);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// Trying to get the selected project
		TreePath[] paths=null;
		ITreeSelection tselection=null;
		String selProject=null;
		IProject selPrj=null;
		Object segment=null;
		
		selectionPage=new TBX_JarsImportSelectionWizardPage("Select resource to import");
		
		try
		{
			tselection=(ITreeSelection) selection;
			
			paths=tselection.getPaths();
			
			//gettting first selected project, if any
			for(TreePath pathItem : paths)
			{
				segment=pathItem.getFirstSegment();
				if(segment instanceof IProject)
				{
					selPrj=(IProject)segment;
					selProject=selPrj.getName();
					
				}
			}
			
			selectionPage.setSelectedProject(selProject);

		}
		catch(Exception e)
		{
			
		}
		
	}
	
	 public void addPages() {
	        super.addPages(); 
	       
			addPage(selectionPage);
			selectionPage.setPreviousPage(null);
	    }

	@Override
	public boolean canFinish() {
		// TODO Auto-generated method stub
				
		return selectionPage.isPageComplete() && super.canFinish();
	}

}
