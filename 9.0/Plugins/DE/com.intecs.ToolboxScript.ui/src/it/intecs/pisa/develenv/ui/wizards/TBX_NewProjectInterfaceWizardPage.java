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

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.develenv.ui.exceptions.TDEException;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewProjectInterfaceWizardPage extends WizardPage {

	private static final String INTERFACE_DELIMITER = " ";
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	private static final String PATH_INTERFACES_DEFINITION_XML = "/interfacesDefinition.xml";
	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";
	private static final String TAG_INTERFACE = "interface";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Create a new TOOLBOX Service";
	private static final String WIZARD_DESCRIPTION = "This page will let you specify the interface that will be implemented by the Service.";
	protected boolean fileCreation=true;
	protected boolean requestsCreation=true;
	protected boolean outputCreation=true;
	
	private Button noInterface=null;
	private Button alreadyImplemented=null;
	
	private InterfacesDescription interfacesDescription=null;

	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewProjectInterfaceWizardPage(final ISelection selection)
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
		
		final Composite container = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns=1;
				
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
		
		//		Adding operations tree
		noInterface=new Button(container,SWT.RADIO);
		noInterface.setText("New interface.");
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		noInterface.setLayoutData(gd);
		noInterface.setSelection(true);
		noInterface.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectInterfaceWizardPage.this.checkRadioSelection(e);
			}

		});
		
		
		alreadyImplemented=new Button(container,SWT.RADIO);
		alreadyImplemented.setText("One of the following interfaces:");
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		alreadyImplemented.setLayoutData(gd);
		alreadyImplemented.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectInterfaceWizardPage.this.checkRadioSelection(e);
			}

		});
		
		//Adding service Name label
		this.interfacesList =new List (container,  SWT.BORDER |SWT.SINGLE);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.interfacesList.setLayoutData(gd);
		this.interfacesList.setEnabled(false);
		
		
		this.initialize();
		this.dialogChanged();
		this.setControl(container);
	}
	

	protected void checkRadioSelection(SelectionEvent e) {
		// TODO Auto-generated method stub
		if(e.widget == alreadyImplemented)
		{
			noInterface.setSelection(false);
			alreadyImplemented.setSelection(true);
			interfacesList.setEnabled(true);
		}
		else
		{
			this.noInterface.setSelection(true);
			this.alreadyImplemented.setSelection(false);
			interfacesList.setEnabled(false);
		}
	}

	/**
	 * Initialize.
	 */

	private void initialize() {	
		Bundle interfacesPlugin;
		URL entry=null;
		File file;
		String userHome=null;
		File tbxConfigurationsDir=null;
		File interfacesFile=null;
		
		//creating interfaces description
		interfacesDescription=new InterfacesDescription();
				
		//		getting path for interface definition xml document
		interfacesPlugin=Platform.getBundle(BUNDLE_INTERFACES);
		entry=interfacesPlugin.getEntry(PATH_INTERFACES_DEFINITION_XML);
		
		try {
			file = new File(FileLocator.toFileURL(entry).getPath());
			addInterfacesToList(file, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			userHome=System.getProperty("user.home");
			
			tbxConfigurationsDir=new File(userHome,FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
			interfacesFile=new File(tbxConfigurationsDir,PATH_INTERFACES_DEFINITION_XML);
	
			addInterfacesToList(interfacesFile, true);
		}
		catch(Exception usrExc)
		{
			
		}
		
		this.interfacesList.select(0);
				
		try {
			final Bundle bundle = Platform.getBundle(BUNDLE_NAME);
			
			final URL iconUrl=bundle.getEntry(TITLE_ICON_PATH);
			this.setImageDescriptor(ImageDescriptor.createFromURL(iconUrl));
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
		
	}

	private void addInterfacesToList(File file, boolean userDefined) throws TDEException {
		InterfacesDescription descr=null;
		Vector<Interface> interfaces=null;
		String itemName=null;
		
		descr=new InterfacesDescription();
		
		descr.loadFromFile(file, true);
		
		interfaces=descr.getInterfaces();
		for(Interface interf: interfaces)
		{
			itemName=interf.getName();
			itemName+=INTERFACE_DELIMITER+interf.getVersion();
			
			if(userDefined == true)
				itemName+=" (user defined)";
			
			this.interfacesList.add(itemName);
			this.interfacesDescription.addInterface(interf);
		}
		
	}

	/**
	 * Dialog changed.
	 */

	private void dialogChanged() {
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
	
	
	public String getSelectedInterfaceDisplayedName()
	{
		return this.interfacesList.getSelection()[0];	
	}
	
	public Interface getSelectedInterface()
	{
		String selectedItem=null;
		String interfaceName=null;
		String interfaceVersion=null;
		StringTokenizer tokenizer=null;
		
		selectedItem=this.interfacesList.getSelection()[0];
				
		tokenizer=new StringTokenizer(selectedItem,INTERFACE_DELIMITER);
		interfaceName=tokenizer.nextToken();
		interfaceVersion=tokenizer.nextToken();
		
		return interfacesDescription.getInterface(interfaceName,interfaceVersion);
		
	}
	
	public boolean needsRepositoryInterface()
	{
		return alreadyImplemented.getSelection();
	}
			
	private List interfacesList;
	
}
