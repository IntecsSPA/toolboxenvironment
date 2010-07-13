package it.intecs.pisa.develenv.ui.popup.contextmenu;

import it.intecs.pisa.common.tbx.exceptions.CannotAuthenticateException;
import it.intecs.pisa.common.tbx.exceptions.CannotDeployException;
import it.intecs.pisa.common.tbx.exceptions.ServiceValidationException;
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

public class deployServiceAction implements IObjectActionDelegate {

	private TreeSelection selection = null;

	/**
	 * Constructor for Action1.
	 */
	public deployServiceAction() {
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
		TreePath[] paths = null;
		IProject project = null;
		String tbxUrl = null;
		String username;
		String password;

		try {
			paths = selection.getPaths();
			if (paths == null || paths.length == 0) {
				displayError("Project not selected");
				return;
			}

			project = (IProject) paths[0].getFirstSegment();

			if (ToolboxEclipseProjectUtil.isToolboxProject(project)) {
				tbxUrl = ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
				username = ToolboxEclipseProjectPreferences.getToolboxHostUsername(project);
				password = ToolboxEclipseProjectPreferences.getToolboxHostPassword(project);
				
				if (tbxUrl != null && tbxUrl.equals("") == false) {
					// checking that url ends with /TOOLBOX
					if ((tbxUrl.endsWith("/TOOLBOX") || tbxUrl.endsWith("/TOOLBOX/")) == false)
						MessageDialog.openError(null, "Error","The following URL is not correct: " + tbxUrl);
					else {
						try
						{
							ToolboxEclipseProjectDeployer.deploy(project.getName(), tbxUrl, username,password);

							MessageDialog.openInformation(null, "Deploy","Successfully deployed to host " + tbxUrl);
						}
						catch(CannotAuthenticateException e)
						{
							displayError("Cannot deploy. unable to authenticate user");
						}
						catch(CannotDeployException e)
						{
							displayError("Cannot deploy to host "+tbxUrl);
						}
						catch(ServiceValidationException e)
						{
							String message=e.getDetail();
							String additionInfo=message!=null && message.equals("")==false?" The validation process returned this exception: "+message:"";
							displayError("The service is not valid. Please check operation scripts for validity."+additionInfo);
						}
						catch(Exception e)
						{
							displayError("An unexpected error occurred while deploying the service to host "+tbxUrl);
						}
					}
				} else {
					displayError("The Toolbox host set in the preferences is not valid");
				}

			} else
				displayError("The project is not a Toolbox Service");
		} catch (Exception ecc) {
			displayError("An error occurred when trying to deploy the service. Details: "
					+ ecc.getMessage());
		}
	}

	

	private void displayError(String message) {
		MessageDialog.openError(null, "Error", message);
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof TreeSelection)
			this.selection = (TreeSelection) selection;

	}

}
