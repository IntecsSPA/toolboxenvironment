/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptAsynchronousNewWizardPage.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.2 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/25 08:56:25 $
 * File ID: $Id: TscriptAsynchronousNewWizardPage.java,v 1.2 2007/01/25 08:56:25 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.wizards;

import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;

import org.eclipse.core.runtime.FileLocator;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class TscriptAsynchronousNewWizardPage.
 */

public class TBX_NewInterfaceNameWizardPage extends WizardPage {
	
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String ATTRIBUTE_VERSION = "version";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String TAG_INTERFACE = "interface";
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	private static final String PATH_INTERFACES_DEFINITION_XML = "interfacesDefinition.xml";
	
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";

	private static final String LABEL_INTERFACE_VERSION = "Interface version*:";
	private static final String LABEL_NAME = "Interface Name*:   ";
	private static final String LABEL_MANDATORY = "* Mandatory field";
	private static final String MAIN_TITLE = "Add an user-defined interface to the Interface Repository";
	private static final String WIZARD_DESCRIPTION = "This wizard creates a new interface into the repository. The new interface can  be used later to create a new Service";
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 * @throws Exception 
	 */
	public TBX_NewInterfaceNameWizardPage(final ISelection selection) throws Exception
	{
		super("wizardPage");
		this.setTitle(MAIN_TITLE);
		this.setDescription(WIZARD_DESCRIPTION);
		
		this.loadInterfaces();
	}
	
	private void loadInterfaces() throws Exception {
		String userHomeDir=null;
		File tbxConfigurationsDir=null;
		File userDefinedInterfacesFile=null;
		DOMUtil domutil=null;
		Document userDefinedInterfacesDoc=null;
		Document toolboxDefinedInterfacesDoc=null;
		LinkedList userDefinedChildren=null;
		LinkedList toolboxDefinedChildren=null;
		Element tag=null;
		Bundle interfacesPlugin=null;
		File toolboxDefinedInterfacesFile=null;
		URL entry=null;
		int interfacesCount=0;
		int childrenSize=0;
		
		InterfacesDescription tbxDefInterf=null;
		InterfacesDescription usrDefInterf=null;
		
		
		domutil=new DOMUtil();
		
		//		getting path for interface definition xml document
		interfacesPlugin=Platform.getBundle(BUNDLE_INTERFACES);
		entry=interfacesPlugin.getEntry(PATH_INTERFACES_DEFINITION_XML);
		
		toolboxDefinedInterfacesFile = new File(FileLocator.toFileURL(entry).getPath());
		
		tbxDefInterf=new InterfacesDescription();
		tbxDefInterf.loadFromFile(toolboxDefinedInterfacesFile, true);
		interfacesCount=tbxDefInterf.getInterfaces().size();
		
		
		toolboxDefinedInterfacesDoc=domutil.fileToDocument(toolboxDefinedInterfacesFile);
		toolboxDefinedChildren=DOMUtil.getChildrenByTagName(toolboxDefinedInterfacesDoc.getDocumentElement(), TAG_INTERFACE);
		
		
		//checking if interface name exists
		userHomeDir=System.getProperty("user.home");
		tbxConfigurationsDir=new File(userHomeDir,FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
		if(tbxConfigurationsDir.exists())
		{
			
			try {
				userDefinedInterfacesFile=new File(tbxConfigurationsDir,PATH_INTERFACES_DEFINITION_XML);
				userDefinedInterfacesDoc=domutil.fileToDocument(userDefinedInterfacesFile);
				
				userDefinedChildren=DOMUtil.getChildrenByTagName(userDefinedInterfacesDoc.getDocumentElement(), TAG_INTERFACE);
				
				childrenSize=userDefinedChildren.size();
				interfacesCount+=childrenSize;
				
				
			} catch (final Exception e) {
				
			} 

		}
		else 
			{
				this.interfacesName=new String[0];
				this.interfacesVersion=new String[0];
			}
		
		
		this.interfacesName=new String[interfacesCount];
		this.interfacesVersion=new String[interfacesCount];
				
		//cycling over user interfaces
		for(int i=0;i<childrenSize;i++)
		{
			tag=(Element)userDefinedChildren.get(i);
			this.interfacesName[i]=tag.getAttribute(ATTRIBUTE_NAME);
			this.interfacesVersion[i]=tag.getAttribute(ATTRIBUTE_VERSION);
		}
		
		//		cycling over toolbox interfaces
		for(int i=childrenSize;i<interfacesCount;i++)
		{
			tag=(Element)toolboxDefinedChildren.get(i-childrenSize);
			this.interfacesName[i]=tag.getAttribute(ATTRIBUTE_NAME);
			this.interfacesVersion[i]=tag.getAttribute(ATTRIBUTE_VERSION);
		}
		
		
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
		label.setText(LABEL_NAME);

		this.interfaceName = new Text(firstLineContainer, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		this.interfaceName.setLayoutData(gd);
		this.interfaceName.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewInterfaceNameWizardPage.this.interfaceChanged();
			}
		});
		
	
		final Composite secondLineContainer = new Composite(container, SWT.NULL);
		final GridLayout secondLineLayout = new GridLayout();
		
		secondLineContainer.setLayout(secondLineLayout);
		secondLineLayout.numColumns=2;
				
		gd = new GridData(GridData.FILL_HORIZONTAL);
		secondLineContainer.setLayoutData(gd);
		
		//Adding service Name label
		label = new Label(secondLineContainer, SWT.NULL);
		label.setText(LABEL_INTERFACE_VERSION);

		this.interfaceVersion = new Text(secondLineContainer, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		this.interfaceVersion.setLayoutData(gd);
		this.interfaceVersion.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				TBX_NewInterfaceNameWizardPage.this.interfaceChanged();
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
		this.interfaceChanged();
		this.setControl(container);
	}
	

	/**
	 * Initialize.
	 */

	private void initialize() {
		//initializing service name
		this.interfaceName.setText("new_interface");
		
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

	private void interfaceChanged() {
		String newInterfaceName = null;
		String newInterfaceVersion = null;

		//checking if the interface name has been provided
		newInterfaceName=this.getInterfaceName();
		newInterfaceVersion=this.getInterfaceVersion();
		
		if (newInterfaceName.length() == 0) {
			this.updateStatus("An interface name must be specified");
			return;
		}
		
		if (newInterfaceVersion.length() == 0) {
			this.updateStatus("An interface version must be specified");
			return;
		}
		
		//checking interfaceVersion
		if(newInterfaceVersion.matches("\\d+.\\d")==false)
		{
			this.updateStatus("The interface version is not valid.");
			return;
		}	
		
		//checking if interface name exists
		for(int i=0;i<this.interfacesName.length;i++)
		{
			if(this.interfacesName[i].equals(newInterfaceName) &&
			   this.interfacesVersion[i].equals(newInterfaceVersion))
			{
				this.updateStatus("An interface with the provided name and version already exists.");
				return;
			}
		}

	this.updateStatus(null);
	}
	
	@Override
	public boolean isPageComplete()
	{
		return this.isCompleted;
	}
	
	/**
	 * Update status.
	 * 
	 * @param message the message
	 */
	private void updateStatus(final String message) {
		
		if(message==null) {
			this.isCompleted=true;
		} else {
			this.isCompleted=false;
		}
		
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
	}


	/**
	 * Gets the project name.
	 * 
	 * @return the project name
	 */
	public String getInterfaceName() {
		return this.interfaceName.getText();
	}
	
	public String getInterfaceVersion() {
		return this.interfaceVersion.getText();
	}
	
	private String[] interfacesVersion=null;
	private String[] interfacesName=null;
	/**
	 * The interface name.
	 */
	private Text interfaceName;
	private Text interfaceVersion;
	
	private boolean isCompleted=false;
}