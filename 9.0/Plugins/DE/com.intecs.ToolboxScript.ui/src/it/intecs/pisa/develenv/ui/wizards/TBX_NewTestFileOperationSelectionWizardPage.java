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

import it.intecs.pisa.develenv.ui.widgets.ServiceOperationsTree;
import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewTestFileOperationSelectionWizardPage extends WizardPage implements Listener
 {
		
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Create a new test file";
	private static final String WIZARD_DESCRIPTION = "Select the operation for which the test file shall be created";
	
	private ServiceOperationsTree operationsTree;

	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewTestFileOperationSelectionWizardPage(final ISelection selection)
	{
		super("wizardPage");
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
		this.operationsTree =new ServiceOperationsTree(container);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.operationsTree.setLayoutData(gd);
		this.operationsTree.addListener(SWT.Modify, this);
		
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
		
		this.setPageComplete(false);
	}


	public String getSelectedOperationName()
	{
		return this.operationsTree.getSelectedProjectName();
	}
	
	public boolean canFlipToNextPage()
	{
		TreeItem[] selection=null;
		
		if(operationsTree.getItemCount()>0)
		{
			selection=operationsTree.getSelection();
			if(selection.length>0)
			{
				return true;
			}
			
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		TBX_NewTestFileProjectSelectionWizardPage prevPage=null;
		String selectedProject=null;
		
		if(visible==true)
		{
			prevPage=(TBX_NewTestFileProjectSelectionWizardPage) this.getPreviousPage();
			selectedProject=prevPage.getSelectedProjectName();
			
			this.operationsTree.setSelectedProject(selectedProject);
		}
		
		super.setVisible(visible);
	}

	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		this.setPageComplete(true);
	}

}
