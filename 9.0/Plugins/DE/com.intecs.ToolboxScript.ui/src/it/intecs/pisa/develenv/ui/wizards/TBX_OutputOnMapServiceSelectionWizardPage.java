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

import it.intecs.pisa.develenv.model.utils.SelectionParser;
import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;
import java.net.URL;
import org.eclipse.core.resources.IProject;
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
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_OutputOnMapServiceSelectionWizardPage extends WizardPage implements Listener
 {
		
	private static final String FILE_ICON = "/icons/wizards/new_service_page_3.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Service selection";
	private static final String WIZARD_DESCRIPTION = "Select the service to which the \"Output on Map\" stylesheet shall be associated";
	
	private TBXProjectTree projectsTree;
	private ISelection selection=null;

	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_OutputOnMapServiceSelectionWizardPage(final ISelection selection)
	{
		super("wizardPage");
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
		
		this.selection=selection;
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
		projectsTree.addListener(SWT.SELECTED, this);
		
		this.initialize();
		this.setControl(container);
	}
	
	/**
	 * Initialize.
	 */

	private void initialize() {	
		IProject selPrj=null;
		String selProject=null;
		
		try {
			final Bundle bundle = Platform.getBundle(BUNDLE_NAME);
			
			final URL iconUrl=bundle.getEntry(FILE_ICON);
			this.setImageDescriptor(ImageDescriptor.createFromURL(iconUrl));
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
		
		if(selection!=null)
		{
			selPrj=SelectionParser.getOwnerProject(selection);
			selProject=selPrj.getName();
			
			this.projectsTree.setSelectedProject(selProject);
		}

	}


	public String getSelectedProjectName()
	{
		return this.projectsTree.getSelectedProjectName();
	}
	
	public boolean canFlipToNextPage()
	{
		return this.isPageComplete();
	}

	public void handleEvent(Event event) {
		
	}

	

}
