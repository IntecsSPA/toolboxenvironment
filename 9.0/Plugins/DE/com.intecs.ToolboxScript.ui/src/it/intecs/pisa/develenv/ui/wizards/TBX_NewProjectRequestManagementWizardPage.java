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

import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.ui.widgets.ServicePropertyComposite;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewProjectRequestManagementWizardPage extends WizardPage implements Listener{

	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String MAIN_TITLE = "Create a new TOOLBOX Service";
	private static final String WIZARD_DESCRIPTION = "This page will let you specify the Request Management settings.";

	
	private ServicePropertyComposite properties=null;
	
	Service initializationDescr=null;
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewProjectRequestManagementWizardPage(final ISelection selection) 
	{
		super("wizardPage");
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewProjectRequestManagementWizardPage(final ISelection selection,Service descr)
	{
		super("wizardPage");
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
		initializationDescr=descr;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		properties=new ServicePropertyComposite(parent,null);
		
		this.initialize();
		this.updateStatus(null);
		this.setControl(properties);
		properties.addListener(SWT.Modify, this);
	}
	
	
	/*protected void checkSSLPath() {
			String newPath=null;
		
		newPath=properties.getSSLCertificate();
		final File pathFile=new File(newPath);
		
		if(pathFile.exists()==false) {
			this.updateStatus("The SSL file path is not valid.");
		} else {
			this.updateStatus(null);
		}
		
	}*/

	public boolean isPageDisplayed()
	{
		return this.isCurrentPage();
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
			if(this.initializationDescr!=null)
			{
				properties.setSuspendMode(initializationDescr.getSuspendMode());				
				properties.setSSLCertificate(initializationDescr.getSSLcertificate());
				properties.setQueuing(initializationDescr.isQueuing());
			}
			
		}
		catch(Exception ex1)
		{
			
		}
		
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

	public boolean getQueuing()
	{
		return properties.getQueuing();
	}
		
	public String getSuspendMode()
	{
		return properties.getSuspendMode();
	}

	public String getSSLCertificate()
	{
		return properties.getSSLCertificate();
	}

	public void handleEvent(Event event) {
		if(event!=null && event.text !=null && event.text.equals("")==false)
			this.updateStatus(event.text);
		else this.updateStatus(null);
	}

	public String getToolboxUrl() {
		return properties.getToolboxHost();
	}
	
	public String getToolboxUsername()
	{
		return properties.getToolboxAuthUsername();
	}

	public String getToolboxPassword() {
		return properties.getToolboxAuthPassword();
	}

}
