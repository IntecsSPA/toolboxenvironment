package it.intecs.pisa.develenv.ui.popup.contextmenu;

import it.intecs.pisa.common.tbx.exceptions.CannotAuthenticateException;
import it.intecs.pisa.common.tbx.exceptions.CannotUndeployException;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectDeployer;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectPreferences;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class undeployServiceAction implements IObjectActionDelegate {

	private TreeSelection selection=null;
	/**
	 * Constructor for Action1.
	 */
	public undeployServiceAction() {
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
		String tbxUrl=null;
		boolean unDeployResult=false;
		String serviceName;
		String username;
		String password;
		try
		{
		
        		paths=selection.getPaths();
        		if(paths==null || paths.length==0)
        		{
        			displayError("Project not selected");
        			return;
        		}
        		
        		project=(IProject) paths[0].getFirstSegment();
        		serviceName=project.getName();
        		
        		if(ToolboxEclipseProjectUtil.isToolboxProject(project))
        		{
        			tbxUrl=ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
        			username = ToolboxEclipseProjectPreferences.getToolboxHostUsername(project);
    				password = ToolboxEclipseProjectPreferences.getToolboxHostPassword(project);
    				
        			if(tbxUrl != null && tbxUrl.equals("")==false)
        			{
        				//checking if url ends with /deploy
        				if((tbxUrl.endsWith("/TOOLBOX") || tbxUrl.endsWith("/TOOLBOX/"))==false)
            				MessageDialog.openError(null, "Error", "The following URL is not correct: "+tbxUrl);
            			else
            			{
            				try
            				{
	            				unDeployResult=ToolboxEclipseProjectDeployer.undeploy(serviceName, tbxUrl, username, password);
	            				MessageDialog.openInformation(null, "Undeploy", "Service "+serviceName+" successfully undeployed");
	                			
            				}
							catch(CannotAuthenticateException e)
							{
								displayError("Cannot Undeploy. Unable to authenticate user");
							}
							catch(CannotUndeployException e)
							{
								displayError("Cannot Undeploy to host "+tbxUrl);
							}
                			
            			}
        			}
        			else 
        			{
        			    displayError("The Toolbox host set in the preferences is not valid");
        			}
        		}
        		else displayError("The project is not a Toolbox Service");
		}
		catch(Exception ecc)
		{
		    displayError("An error occurred when trying to deploy the service. Details: "+ecc.getMessage());
		}
	}

	private void displayError(String message) {		
		MessageDialog.openError(null, "Error", message);
	}
	

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof TreeSelection)
			this.selection=(TreeSelection) selection;
	
	}

}
