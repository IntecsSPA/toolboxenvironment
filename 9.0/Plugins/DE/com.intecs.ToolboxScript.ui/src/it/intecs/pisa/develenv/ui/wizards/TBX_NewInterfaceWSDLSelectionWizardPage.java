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

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewInterfaceWSDLSelectionWizardPage extends WizardPage {

	private static final String LABEL_WSDL_FILE = "WSDL file:";
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
;
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String LABEL_BROWSE = "Browse..";
	private static final String MAIN_TITLE = "Add an user-defined interface to the Interface Repository";
	private static final String WIZARD_DESCRIPTION = "This page lets you select a WSDL file that describes your interface";
	private boolean isComplete=false;
	

	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewInterfaceWSDLSelectionWizardPage(final ISelection selection)
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
		Composite newContainer;
		
		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns=2;
				
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
		
		this.label=new Label(container,SWT.NULL);
		this.label.setText(LABEL_WSDL_FILE);
		//Adding buttons to Abstract radio	
		
		newContainer= new Composite(container, SWT.NULL);
		final GridLayout mainLayout = new GridLayout();
		newContainer.setLayout(mainLayout);
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		newContainer.setLayoutData(gd);
		
		mainLayout.numColumns=2;
				
		this.wsdlFilePath=new Text(newContainer,SWT.BORDER);
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		this.wsdlFilePath.setLayoutData(gd);
		this.wsdlFilePath.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewInterfaceWSDLSelectionWizardPage.this.checkPath();
			}

		});
		
		this.wsdlFileBrowseButton=new Button(newContainer,SWT.NULL);
		this.wsdlFileBrowseButton.setText(LABEL_BROWSE);
		
		this.wsdlFileBrowseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewInterfaceWSDLSelectionWizardPage.this.browseForWSDLFile();
			}
		});
		
		this.initialize();
		this.dialogChanged();
		this.setControl(container);
	}
	


	private void checkPath() {
		String newPath=null;
		
		newPath=this.wsdlFilePath.getText();
		final File pathFile=new File(newPath);
		
		if(pathFile.exists()==false) {
			this.updateStatus("The file doesn't exists.");
		} else {
			this.updateStatus(null);
		}
	}
	
	
	public void browseForWSDLFile()
	{
		final Shell s = new Shell();
    
		final FileDialog fd = new FileDialog(s, SWT.OPEN);
	    fd.setText("Select a WSDL");
	    final String[] filterExt = {"*.wsdl"};
	    fd.setFilterExtensions(filterExt);
	    final String selected = fd.open(); 
	
	    if(selected==null) {
			this.isComplete=false;
		} else
	    {
	    	this.isComplete=true;
	    	this.wsdlFilePath.setText(selected);
	    }
	    
	    this.updateStatus(null);
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
	}


	/**
	 * Dialog changed.
	 */

	private void dialogChanged() {

		this.updateStatus(null);
	}
	
	@Override
	public boolean isPageComplete()
	{
		return this.isComplete;
	}

	/**
	 * Update status.
	 * 
	 * @param message the message
	 */
	private void updateStatus(final String message) {
		if(message==null) {
			this.isComplete=true;
		} else {
			this.isComplete=false;
		}
		
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
	}
			
	public String getSelectedWSDL()
	{
		return this.wsdlFilePath.getText();
	}
	
	private Text wsdlFilePath=null;
	private Button wsdlFileBrowseButton=null;
	private Label label=null;
}
