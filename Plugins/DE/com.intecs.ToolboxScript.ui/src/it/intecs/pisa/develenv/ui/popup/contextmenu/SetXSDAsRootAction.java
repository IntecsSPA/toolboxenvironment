package it.intecs.pisa.develenv.ui.popup.contextmenu;

import java.io.File;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.util.XSD;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SetXSDAsRootAction implements IObjectActionDelegate {

	private TreeSelection selection=null;
	/**
	 * Constructor for Action1.
	 */
	public SetXSDAsRootAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		TreePath[] paths=null;
		IProject project=null;
		Object schemasFolder=null;
		IFile file=null;
		
		paths=selection.getPaths();
		if(paths.length>1)
			showMessage("Error","You have selected more than one schema");
		
		project=(IProject) paths[0].getFirstSegment();
		if(ToolboxEclipseProjectUtil.isToolboxProject(project))
		{
		
			schemasFolder=paths[0].getSegment(1);
			if((schemasFolder instanceof IFolder)==false || ((IFolder)schemasFolder).getName().equals(ToolboxEclipseProject.FOLDER_SCHEMAS)==false)
				showMessage("Error","The schema files must be under the Schemas directory");
			else
			{
				file= (IFile) paths[0].getLastSegment();
				setSchemaRootForProject(project,file);
			}
		}
		else showMessage("Error","The project is not a Toolbox Service");
	}

	private void setSchemaRootForProject(IProject project, IFile file) {
		//going to set the schema root
		//must be placed under the Schemas directory
		IFile descriptorFile=null;
		IFile schemaFile=null;
		Service serviceDescriptor=null;
		IPath schemaRoot=null;
		String path=null;
		String targetNameSpace=null;
		Interface implementedInterface=null;
		
		descriptorFile=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
		
		serviceDescriptor=new Service();
		path = descriptorFile.getLocationURI().getPath();
		serviceDescriptor.loadFromFile(path);
		
		schemaRoot=file.getFullPath().removeFirstSegments(2);
		
		implementedInterface=serviceDescriptor.getImplementedInterface();
		implementedInterface.setSchemaRoot(schemaRoot.toString());
		
		schemaFile=project.getFile(file.getFullPath().removeFirstSegments(1));
		targetNameSpace=getSchemaTargetNamespace(schemaFile);
		
		implementedInterface.setTargetNameSpace(targetNameSpace);
		serviceDescriptor.dumpToDisk(path);
	}

	private String getSchemaTargetNamespace(IFile schemaRoot) {
		
		return XSD.getTargetNamespace(new File(schemaRoot.getLocationURI().getPath()));
	}

	private void showMessage(String title,String message) {
		Shell shell = new Shell();
		MessageDialog.openInformation(
			shell,
			title,
			message);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof TreeSelection)
			this.selection=(TreeSelection) selection;
	
	}

}
