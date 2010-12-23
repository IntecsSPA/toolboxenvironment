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

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * The Class implements one of the pages of the Service creation wizard 
 */
public class TBX_NewProjectWizardPage extends WizardPage {

	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String LABEL_SERVICE_NAME = "Service Name*:";
	private static final String LABEL_MANDATORY = "* Mandatory field";
	private static final String MAIN_TITLE = "Create a new TOOLBOX Service";
	private static final String WIZARD_DESCRIPTION = "This wizard creates a new Service project.";
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewProjectWizardPage(final ISelection selection)
	{
		super("wizardPage");
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(final Composite parent) {
		Label label;
		GridData gd;
		
		final Composite container = new Composite(parent, SWT.NULL);
		final Composite firstLineContainer = new Composite(container, SWT.NULL);
		
		final GridLayout layout = new GridLayout();
		final GridLayout firstLineLayout = new GridLayout();
		
		container.setLayout(layout);
		firstLineContainer.setLayout(firstLineLayout);
		
		firstLineLayout.numColumns=2;
		
		layout.numColumns = 1;
		
		gd = new GridData(GridData.FILL_HORIZONTAL);
		firstLineContainer.setLayoutData(gd);
		
		//Adding service Name label
		label = new Label(firstLineContainer, SWT.NULL);
		label.setText(LABEL_SERVICE_NAME);

		this.serviceName = new Text(firstLineContainer, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		this.serviceName.setLayoutData(gd);
		this.serviceName.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewProjectWizardPage.this.dialogChanged();
			}
		});
			
		//		Adding service Name label
		label = new Label(container, SWT.NULL);
		label.setText(LABEL_MANDATORY);
		gd = new GridData(SWT.NULL,SWT.FILL,false,true);
		gd.horizontalAlignment=SWT.END;
		gd.verticalAlignment=SWT.END;
		label.setLayoutData(gd);

		this.initialize();
		this.dialogChanged();
		this.setControl(container);
	}
	

	/**
	 * Initialize.
	 */

	private void initialize() {
		//initializing service name
		this.serviceName.setText("new_service");
		
		try {
			final Bundle bundle = Platform.getBundle(BUNDLE_NAME);
			
			final URL iconUrl=bundle.getEntry(TITLE_ICON_PATH);
			this.setImageDescriptor(ImageDescriptor.createFromURL(iconUrl));
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
	}


	/**
	 * Dialog changed.
	 */

	private void dialogChanged() {
		String newServiceName = null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project =null;
		
		newServiceName=this.getServiceName();
		
		if (newServiceName.length() == 0) {
			this.updateStatus("Service name must be specified");
			return;
		}
		
		//checking if service name already exists
		wrkSpace = ResourcesPlugin.getWorkspace();
		root = wrkSpace.getRoot();
		project = root.getProject(newServiceName);
		if(project.exists())
		{
			this.updateStatus("A Service with the provided name already exists.");
			return;
		}
		
		
		this.updateStatus(null);
	}
	
	/**
	 * Update status.
	 * 
	 * @param message the message
	 */
	private void updateStatus(final String message) {
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
	}


	/**
	 * Gets the project name.
	 * 
	 * @return the project name
	 */
	public String getServiceName() {
		return this.serviceName.getText();
	}
	


	/**
	 * The project name.
	 */
	private Text serviceName;
	
}
