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

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewTestFileNameInsertionWizardPage extends WizardPage
 {
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Create a new test file";
	private static final String WIZARD_DESCRIPTION = "Provide a file name for the test file.";
	
	private String projectName=null;
	private String operationName=null;
	private Text nameText=null;
	private Button soapCheckBox=null;
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewTestFileNameInsertionWizardPage(final ISelection selection)
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
		addNameInsertionControls(container);
		addOptionsControls(container);
		
		this.initialize();
		this.setControl(container);
	}
	

	

	private void addNameInsertionControls(Composite parent) {
		GridData gd=null;
		GridLayout layout=null;
		Composite container = null;
		
				
		container=new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		container.setLayoutData(gd);
		
		nameText=new Text(container,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		nameText.setLayoutData(gd);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewTestFileNameInsertionWizardPage.this.checkFileName();
			}
		});
		
	}
	

	protected void checkFileName() {
		// TODO Auto-generated method stub
		String name=null;
		IFolder testFolder=null;
		IFolder testOperationFolder=null;
		IFile file=null;
		boolean exists=false;
		
		name=nameText.getText();
		if(name==null || name.equals(""))
			updateStatus("File name is not valid. Provide a valid one.");
		else 
		{
			//if(name.matches("\\d+.xml")==true)
			{
				IWorkspace wrkSpace = ResourcesPlugin.getWorkspace();
				IWorkspaceRoot root = wrkSpace.getRoot();
				IProject project = root.getProject(this.projectName);
				testFolder=project.getFolder(ToolboxEclipseProject.FOLDER_TEST_DIR);
				testOperationFolder=testFolder.getFolder(this.operationName);
				file=testOperationFolder.getFile(name);
				
				exists=file.exists();
						
				updateStatus(exists?"A file with the same name already exists.":null);
				
			}
			//else updateStatus("Filename must end with .xml");
			
		}
	}
	
	private void updateStatus(String errorMessage)
	{
		this.setErrorMessage(errorMessage);
		this.setPageComplete(errorMessage==null);
	}

	private void addOptionsControls(Composite parent) {
		GridData gd=null;
		GridLayout layout=null;
		Composite container = null;
				
		container=new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		container.setLayout(layout);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
		
		soapCheckBox=new Button(container,SWT.CHECK);
		soapCheckBox.setText("Add SOAP Envelope");
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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	@Override
	public void setVisible(boolean visible) {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter=null;
		TBX_NewTestFileWizard ownerWizard=null;
				
		if(visible==true)
		{
			ownerWizard=(TBX_NewTestFileWizard) this.getWizard();
			operationName=ownerWizard.getSelectedOperation();
			projectName=ownerWizard.getSelectedProject();
			
			formatter=new SimpleDateFormat("yyyyMMddHHmmss");
			this.nameText.setText(operationName+"_"+formatter.format(new Date())+".xml");
			
			
			checkFileName();
		}
		
		super.setVisible(visible);
	}

	public String getFileName() {
		// TODO Auto-generated method stub
		return this.nameText.getText();
	}

	public boolean isSOAPEnvelopeNeeded()
	{
		return this.soapCheckBox.getSelection();
	}

}
