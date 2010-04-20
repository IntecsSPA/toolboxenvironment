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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewProjectDescriptionWizardPage extends WizardPage {

	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String LABEL_DESCRIPTION = "Description: ";
	private static final String LABEL_DESCRIPTION_FILE = "Description File:";
	private static final String LABEL_ABSTRACT_TEXT = "Abstract Text:";
	private static final String LABEL_BROWSE = "Browse..";
	private static final String LABEL_ABSTRACT_FILE = "Abstract file: ";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String MAIN_TITLE = "Create a new TOOLBOX Service";
	private static final String WIZARD_DESCRIPTION = "This page will let you specify the abstract and the description of the service.";
	protected boolean fileCreation=true;
	protected boolean requestsCreation=true;
	protected boolean outputCreation=true;
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewProjectDescriptionWizardPage(final ISelection selection)
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
		
		//This is the page container
		this.container = new Composite(parent, SWT.NULL);
		final GridLayout mainLayout = new GridLayout();
		this.container.setLayout(mainLayout);
		
		mainLayout.numColumns=3;
		
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		this.container.setLayoutData(gd);
			
		this.addAbstractFileComponents(this.container);
		
		this.addAbstractTextComponents(this.container);
		
		this.addDescriptionFileComponents(this.container);
		
		this.addDescriptionTextComponents(this.container);
				
		this.initialize();
		this.dialogChanged();
		this.setControl(this.container);
	}
	
	private void addAbstractTextComponents(final Composite lineContainer) {
		GridData gd;

		this.abstractTextRadio=new Button(lineContainer,SWT.RADIO);
		this.abstractTextRadio.setText(LABEL_ABSTRACT_TEXT);
		
		this.abstractTextRadio.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectDescriptionWizardPage.this.selectAbstractRadioButtons(e);
			}
		});
		
				
		this.abstractText=new Text(lineContainer,SWT.MULTI|SWT.BORDER);
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.horizontalSpan=2;
		this.abstractText.setLayoutData(gd);

	}

	private void addAbstractFileComponents(final Composite lineContainer) {
		GridData gd;
			
		//Adding buttons to Abstract radio
		this.abstractFileRadio=new Button(lineContainer,SWT.RADIO);
		this.abstractFileRadio.setText(LABEL_ABSTRACT_FILE);
		
		this.abstractFileRadio.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectDescriptionWizardPage.this.selectAbstractRadioButtons(e);
			}
		});
		
		
		this.abstractFilePath=new Text(lineContainer,SWT.BORDER);
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		this.abstractFilePath.setLayoutData(gd);
		this.abstractFilePath.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewProjectDescriptionWizardPage.this.checkAbstractPath();
			}

		});
		
		this.abstractFileBrowseRadio=new Button(lineContainer,SWT.NULL);
		this.abstractFileBrowseRadio.setText(LABEL_BROWSE);
		
		this.abstractFileBrowseRadio.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectDescriptionWizardPage.this.browseForAbstractFile();
			}
		});
	}
		
	protected void checkAbstractPath() {
		String newPath=null;
		
		newPath=this.abstractFilePath.getText();
		final File pathFile=new File(newPath);
		
		if(pathFile.exists()==false) {
			this.updateStatus("The abstract file path is not valid.");
		} else {
			this.updateStatus(null);
		}
	}

	/**
	 * @param lineContainer
	 */
	private void addDescriptionTextComponents(final Composite lineContainer) {
		GridData gd;
			
		this.descriptionTextRadio=new Button(lineContainer,SWT.RADIO);
		this.descriptionTextRadio.setText(LABEL_DESCRIPTION);
		
		this.descriptionTextRadio.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectDescriptionWizardPage.this.selectDescriptionRadioButtons(e);
			}
		});
		
		this.descriptionText=new Text(lineContainer,SWT.MULTI|SWT.BORDER);
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		gd.horizontalSpan=2;
		this.descriptionText.setLayoutData(gd);
	}

	/**
	 * @param lineContainer
	 */
	private void addDescriptionFileComponents(final Composite lineContainer) {
		GridData gd;

		this.descriptionFileRadio=new Button(lineContainer,SWT.RADIO);
		this.descriptionFileRadio.setText(LABEL_DESCRIPTION_FILE);
		
		this.descriptionFileRadio.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectDescriptionWizardPage.this.selectDescriptionRadioButtons(e);
			}
		});
		
		this.descriptionFilePath=new Text(lineContainer,SWT.BORDER);
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		this.descriptionFilePath.setLayoutData(gd);
		this.descriptionFilePath.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewProjectDescriptionWizardPage.this.checkDescriptionPath();
			}

		});
		
		this.descriptionFileBrowseRadio=new Button(lineContainer,SWT.NULL);
		this.descriptionFileBrowseRadio.setText(LABEL_BROWSE);
		
		this.descriptionFileBrowseRadio.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectDescriptionWizardPage.this.browseForDescriptionFile();
			}
		});
		
	}

	
	protected void checkDescriptionPath() {
		String newPath=null;
		
		newPath=this.descriptionFilePath.getText();
		final File pathFile=new File(newPath);
		
		if(pathFile.exists()==false) {
			this.updateStatus("The description file path is not valid.");
		} else {
			this.updateStatus(null);
		}
		
	}

	public void browseForAbstractFile()
	{
		if(this.abstractFileBrowseRadio.isEnabled())
			{
				final Shell s = new Shell();
		    
			    final FileDialog fd = new FileDialog(s, SWT.OPEN);
			    fd.setText("Open");
			    final String[] filterExt = {"*.txt","*.doc", ".rtf"};
			    fd.setFilterExtensions(filterExt);
			    final String selected = fd.open(); 
			    
				this.abstractFilePath.setText(selected);
			}
	}
	
	
	public void browseForDescriptionFile()
	{
		if(this.descriptionFileBrowseRadio.isEnabled())
			{
				final Shell s = new Shell();
					
			    
			    final FileDialog fd = new FileDialog(s, SWT.OPEN);
			    fd.setText("Open");
			    final String[] filterExt = {"*.txt","*.doc", ".rtf"};
			    fd.setFilterExtensions(filterExt);
			    final String selected = fd.open(); 
			    
				this.descriptionFilePath.setText(selected);
			}
	}
	
	
	public void selectAbstractRadioButtons(final SelectionEvent s)
	{
		if(s.getSource()==this.abstractFileRadio)
		{
			this.abstractTextRadio.setSelection(false);
			this.abstractText.setEnabled(false);
			
			this.abstractFileRadio.setSelection(true);
			this.abstractFilePath.setEnabled(true);
			this.abstractFileBrowseRadio.setEnabled(true);
		}
		else 
		{
			this.abstractFileRadio.setSelection(false);
			this.abstractFilePath.setEnabled(false);
			this.abstractFileBrowseRadio.setEnabled(false);
			
			this.abstractTextRadio.setSelection(true);
			this.abstractText.setEnabled(true);
		}
	}
	
	
	public void selectDescriptionRadioButtons(final SelectionEvent s)
	{
		if(s.getSource()==this.descriptionFileRadio)
		{
			this.descriptionTextRadio.setSelection(false);
			this.descriptionText.setEnabled(false);
			
			this.descriptionFileRadio.setSelection(true);
			this.descriptionFilePath.setEnabled(true);
			this.descriptionFileBrowseRadio.setEnabled(true);
		}
		else 
		{
			this.descriptionFileRadio.setSelection(false);
			this.descriptionFilePath.setEnabled(false);
			this.descriptionFileBrowseRadio.setEnabled(false);
			
			this.descriptionTextRadio.setSelection(true);
			this.descriptionText.setEnabled(true);
		}
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
		
		this.abstractFileRadio.setSelection(true);
		this.abstractText.setEnabled(false);
		
		this.descriptionFileRadio.setSelection(true);
		this.descriptionText.setEnabled(false);
	}


	/**
	 * Dialog changed.
	 */

	private void dialogChanged() {
	/*	IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));*/
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
	
	public InputStream getServiceAbstract()
	{
		if(this.abstractFileRadio.getSelection())
		{
			final File abstractFile=new File(this.abstractFilePath.getText());
			FileInputStream stream=null;
			try {
				stream = new FileInputStream(abstractFile);
			} catch (final FileNotFoundException e) {		}
			return stream;
		}
		else
		{
			final StringBufferInputStream stream=new StringBufferInputStream(this.abstractText.getText());
			return stream;
		}
	}

	public InputStream getServiceDescription()
	{
		if(this.descriptionFileRadio.getSelection())
		{
			final File descriptionFile=new File(this.descriptionFilePath.getText());
			FileInputStream stream=null;
			try {
				stream = new FileInputStream(descriptionFile);
			} catch (final FileNotFoundException e) { }
			return stream;
		}
		else
		{
			final StringBufferInputStream stream=new StringBufferInputStream(this.descriptionText.getText());
			return stream;
		}
	}

	
	private Button abstractFileRadio;
	private Text abstractFilePath;
	private Button abstractFileBrowseRadio;
	
	private Button abstractTextRadio;
	private Text abstractText;
	
	
	
	private Button descriptionFileRadio;
	private Text descriptionFilePath;
	private Button descriptionFileBrowseRadio;
	
	private Button descriptionTextRadio;
	private Text descriptionText;
	
	private Composite container;
}


