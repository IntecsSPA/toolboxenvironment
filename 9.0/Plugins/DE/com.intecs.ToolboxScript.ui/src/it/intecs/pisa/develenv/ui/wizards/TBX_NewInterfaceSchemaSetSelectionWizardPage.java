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


import it.intecs.pisa.develenv.ui.widgets.DirectoryInsertion;

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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewInterfaceSchemaSetSelectionWizardPage extends WizardPage implements Listener{

	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String MAIN_TITLE = "Add an user-defined interface to the Interface Repository";
	private static final String WIZARD_DESCRIPTION = "This page lets you specify the schema set that described your inteface's data";
	
	private static final String LABEL_SCHEMA_DIR="Schema directory:";
	
	private DirectoryInsertion dirIns=null;
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewInterfaceSchemaSetSelectionWizardPage(final ISelection selection)
	{
		super("wizardPage");
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(final Composite parent) {
		GridData gd=null;
		GridLayout layout=null;
		Composite container=null;
		
		//This is the page container
		container = new Composite(parent, SWT.NULL);
		layout = new GridLayout();
		container.setLayout(layout);
		
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
			
		this.addAbstractFileComponents(container);
				
		this.initialize();
		this.setControl(container);
	}
	
	private void addAbstractFileComponents(final Composite lineContainer) {
		
		GridData gd;
		GridLayout layout = new GridLayout();
		
		lineContainer.setLayout(layout);
		dirIns=new DirectoryInsertion(lineContainer,SWT.NULL,LABEL_SCHEMA_DIR);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		dirIns.setLayoutData(gd);
		
		dirIns.addListener(SWT.Modify, this);
	
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
		
		setPageComplete(false);
	}

	/*@Override
	public boolean isPageComplete()
	{
		return this.isComplete;
	}*/
	
	
	/**
	 * Update status.
	 * 
	 * @param message the message
	 */
	private void updateStatus(final String message) {
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
	}
	
	public String getSchemaRootDir()
	{
		return this.dirIns.getSelectedDirectory();
	}

	private boolean isComplete=false;
	
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
	    	if(event!=null &&(event.text!=null && event.text.equals("")==false))
	    	    updateStatus(event.text);
	    	else updateStatus(null);
	}
}


