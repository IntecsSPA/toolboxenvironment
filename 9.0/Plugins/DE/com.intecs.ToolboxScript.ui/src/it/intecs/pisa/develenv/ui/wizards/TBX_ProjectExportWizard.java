package it.intecs.pisa.develenv.ui.wizards;
import java.io.File;
import java.util.Date;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.develenv.model.utils.FileSystemUtil;
import it.intecs.pisa.develenv.model.utils.SelectionParser;
import it.intecs.pisa.util.Zip;
import it.intecs.pisa.util.ServiceFoldersFilter;

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
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;


public class TBX_ProjectExportWizard extends Wizard implements IExportWizard {

	private TBX_ProjectExportWizardPage page=null;

	public TBX_ProjectExportWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		// creating the zip package
		String projectName=null;
		IProject project =null;
		String exportTo=null;
		String packageFullPath=null;
		File file=null;
		try
		{
			projectName=page.getSelectedProject();
			exportTo=page.getDestinationPath();
			
			project=ToolboxEclipseProjectUtil.getProject(projectName);			
			
			file=new File(exportTo,projectName+".zip");
			packageFullPath=file.getAbsolutePath();
			return ToolboxEclipseProjectUtil.createExportpackage(project, ToolboxEclipseProjectUtil.PackageType.FULL, packageFullPath);
		}
		catch(Exception e)
		{
			return false;
		}
		
		
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		//		 Trying to get the selected project
		IProject selPrj=null;
		
		page=new TBX_ProjectExportWizardPage("Select project to export");
		
		selPrj=SelectionParser.getOwnerProject(selection);
		page.setSelectedProject(selPrj.getName());
	}

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		this.addPage(page);
	}

}
