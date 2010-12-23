/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TBX_NewProjectWizardPage.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.5 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/25 13:02:44 $
 * File ID: $Id: TBX_NewProjectWizardPage.java,v 1.5 2007/01/25 13:02:44 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.wizards;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.osgi.framework.Bundle;

/**
 * The Class implements one of the pages of the Service creation wizard 
 */
public class TBX_NewScriptFileWizardPage extends WizardNewFileCreationPage {

	public TBX_NewScriptFileWizardPage(String pageName, ISelection selection) {
		super(pageName, (IStructuredSelection)selection);
		// TODO Auto-generated constructor stub
	}

	protected void handleAdvancedButtonSelect()
	{
		
	}
	
	public void handleEvent(Event event)
	{
		
		try
		{
		if(event.type==SWT.Selection)
		{
			//Checking selection. The user shall select a TOOLBOX service
			
			if(validateSelection())
			{
				this.setErrorMessage(null);
				
			}
			else
				{
				this.setErrorMessage("You cannot add TOOLBOX Script here.");
				}	
			
		}
		}
		catch(Exception e)
		{
			
		}
		finally
		{
			super.handleEvent(event);
		}
	}

	private boolean validateSelection() {
		IPath fullPath;
		String[] segments;
		IWorkspaceRoot root;
		IProject project;
		fullPath=this.getContainerFullPath();
		
		if(fullPath !=null)
		{
			segments=fullPath.segments();
			
			root=ResourcesPlugin.getWorkspace().getRoot();
			project=root.getProject(segments[0]);
			
			return canAddFileHere(segments);
		}
		else return false;
	}
	
	protected boolean canAddFileHere(String[] segments)
	{
		if(segments.length>2)
		{
			if(segments[1].equals(ToolboxEclipseProject.FOLDER_OPERATIONS))
				return true;
			
			if(segments[1].equals(ToolboxEclipseProject.FOLDER_RESOURCES) && segments[2].equals(ToolboxEclipseProject.FOLDER_COMMON_SCRIPTS))
				return true;
		}
		
		return false;
	}
	
	protected boolean validatePage()
	{
		String fileName=null;
		
		if(validateSelection())
		{
			fileName=this.getFileName();
			
			if(fileName != null && fileName.endsWith(".tscript"))
				return true;
		}
		
		return false;
	}
	
	protected InputStream getInitialContents()
	{
		Bundle interfacesPlugin=null;
		URL entry=null;
		
		try {
			interfacesPlugin=Platform.getBundle("com.intecs.ToolboxScript.editorFiles");
			entry=interfacesPlugin.getEntry("scripts/templaForEmptyScript.tscript");
			return entry.openStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
