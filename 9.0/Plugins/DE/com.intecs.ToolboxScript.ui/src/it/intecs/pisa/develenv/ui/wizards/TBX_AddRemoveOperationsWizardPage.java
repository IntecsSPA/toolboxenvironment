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

import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_AddRemoveOperationsWizardPage extends WizardPage
 {
		
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Add a service operation";
	private static final String WIZARD_DESCRIPTION = "Select operations to add.";
	
	private TBXProjectTree projectsTree;
	private ISelection selection=null;
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_AddRemoveOperationsWizardPage(final ISelection selection)
	{
		super("wizardPage");
				
		this.selection=selection;
		
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(final Composite parent) {
		GridData gd;
			
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns=1;
				
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
		
		//Adding operations tree
		this.projectsTree =new TBXProjectTree(container);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.projectsTree.setLayoutData(gd);
		
		this.initialize();
		this.setControl(container);
	}
	

	

	/**
	 * Initialize.
	 */

	private void initialize() {	
		try {
			final Bundle bundle = Platform.getBundle(BUNDLE_NAME);
			
			final URL iconUrl=bundle.getEntry(TITLE_ICON_PATH);
			this.setImageDescriptor(ImageDescriptor.createFromURL(iconUrl));
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
		
		try
		{
			ITreeSelection tselection = (ITreeSelection) selection;
			
			TreePath[] paths = tselection.getPaths();
			
			//gettting first selected project, if any
			String selProject=null;
			for(TreePath pathItem : paths)
			{
				Object segment = pathItem.getFirstSegment();
				if(segment instanceof IProject)
				{
					IProject selPrj = (IProject)segment;
					selProject=selPrj.getName();
					
				}
			}
			
			this.projectsTree.setSelectedProject(selProject);

		}
		catch(Exception e)
		{
			
		}
	}


	public String getSelectedProjectName()
	{
		return this.projectsTree.getSelectedProjectName();
	}
	
	public boolean canFlipToNextPage()
	{
		TreeItem[] selection=null;
		
		if(projectsTree.getItemCount()>0)
		{
			selection=projectsTree.getSelection();
			if(selection.length>0)
				return true;
			else return false;
		}
		else return false;
	}

}
