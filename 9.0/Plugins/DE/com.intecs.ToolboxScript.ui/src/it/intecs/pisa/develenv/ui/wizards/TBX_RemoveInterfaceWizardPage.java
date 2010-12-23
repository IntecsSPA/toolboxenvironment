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

import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.LinkedList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_RemoveInterfaceWizardPage extends WizardPage {

	private static final String ATTRIBUTE_VERSION = "version";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";

	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Remove an interface from the Interface Repository";
	private static final String WIZARD_DESCRIPTION = "This page will let you select one interface. The selected interface will be removed from the repository";
	private boolean isComplete=false;
	
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	private static final String PATH_INTERFACES_DEFINITION_XML = "interfacesDefinition.xml";
	

	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_RemoveInterfaceWizardPage(final ISelection selection)
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
		
		//Adding operations tree
		this.interfaceTree =new Tree (container, SWT.VIRTUAL| SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.interfaceTree.setLayoutData(gd);
		this.interfaceTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_RemoveInterfaceWizardPage.this.checkSelection(e);
			}
		});
		
		this.loaduserInterfaces();
		this.initialize();
		this.dialogChanged();
		this.setControl(container);
	}
	

	private void loaduserInterfaces() {
		DOMUtil util=null;
		File userInterfacesFile=null;
		File dir=null;
		Document interfacesDoc=null;
		LinkedList children=null;
		TreeItem item=null;
			
		util=new DOMUtil();
		
		dir=new File(System.getProperty("user.home"),FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
		userInterfacesFile=new File(dir,PATH_INTERFACES_DEFINITION_XML);
		try {
			interfacesDoc=util.readerToDocument(new FileReader(userInterfacesFile));
			
			children=DOMUtil.getChildrenByTagName(interfacesDoc.getDocumentElement(), "interface");
		
			for(int i=0;i<children.size();i++)
			{
				final String itemtext = this.createItemName((Element)children.get(i));
				
				item=new TreeItem(this.interfaceTree,SWT.NULL);
				
				item.setText(itemtext);
				
			}
			
			
		} catch (final Exception e) {
			
		} 
		
	}

	private String createItemName(final Element tag) {
		String interfaceName;
		String interfaceVersion;
		
		interfaceName=tag.getAttribute(ATTRIBUTE_NAME);
		interfaceVersion=tag.getAttribute(ATTRIBUTE_VERSION);
		
		final String itemtext=interfaceName+" version "+interfaceVersion;
		
		return itemtext;
	}

	private String extractInterfaceName(final String itemText) {
		int versionIndex=0;
		
		versionIndex=itemText.indexOf(" version");
		
		return itemText.substring(0,versionIndex);
	}
	
	private String extractInterfaceVersion(final String itemText) {
		int versionIndex=0;
		
		versionIndex=itemText.indexOf("version");
		
		return itemText.substring(versionIndex+8);
	}
	
	public String getSelectedInterfaceName()
	{
		String selected=null;
		
		selected=this.interfaceTree.getSelection()[0].getText();
		return this.extractInterfaceName(selected);
	}
	
	public String getSelectedInterfaceVersion()
	{
		String selected=null;
		
		selected=this.interfaceTree.getSelection()[0].getText();
		return this.extractInterfaceVersion(selected);
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
	 * @param e
	 */
	private void checkSelection(final SelectionEvent e) {
		TreeItem selItem=null;
		TreeItem[] items=null;
		
		selItem=(TreeItem)e.item;
		if(selItem.getChecked())
		{
			items=this.interfaceTree.getItems();
			for (TreeItem element : items) {
				element.setChecked(false);
			}
			
			selItem.setChecked(true);
		}
		
		this.isComplete=true;
		this.updateStatus(null);

	}



	/**
	 * Dialog changed.
	 */

	private void dialogChanged() {

		this.updateStatus(null);
	}
	
	
	public String getSelected()
	{
		TreeItem item=null;
		String path=null;
		
		item=(this.interfaceTree.getSelection())[0];
		
		do
		{
			if(path==null) {
				path=item.getText();
			} else {
				path=item.getText()+"/"+path;
			}
			
			item=item.getParentItem();
		}
		while(item!=null);
	
		return path;
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
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
	}
		
	private Tree interfaceTree;
	
}
