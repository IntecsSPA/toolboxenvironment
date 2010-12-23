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

import java.net.URI;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.runtime.CoreException;
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

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewInterfaceMainSchemaSelectorWizardPage extends WizardPage {

	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
;
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Add an user-defined interface to the Interface Repository";
	private static final String WIZARD_DESCRIPTION = "This page lets you select the root schema file.";
	private boolean isComplete=false;
	

	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewInterfaceMainSchemaSelectorWizardPage(final ISelection selection)
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
		this.fileTree =new Tree (container, SWT.VIRTUAL| SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.fileTree.setLayoutData(gd);
		this.fileTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewInterfaceMainSchemaSelectorWizardPage.this.checkSelection(e);
			}
		});
		
		this.initialize();
		this.dialogChanged();
		this.setControl(container);
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

	private void parseDir(final IFileStore dirStore,final TreeItem parent) throws CoreException
	{
		IFileStore[] fileStore=null;
		IFileInfo fileInfo=null;
		TreeItem item=null;
		String elName=null;
		
		fileStore=dirStore.childStores(0,null);
		
		for (IFileStore element : fileStore) {
			fileInfo=element.fetchInfo();
			elName=fileInfo.getName();
			if(fileInfo.isDirectory() &&
				(elName.startsWith(".")==false))
			{
				item=this.createNewItem(parent,SWT.NULL);
				item.setText(fileInfo.getName());
				
				this.parseDir(element,item);
			}
			else if(elName.endsWith(".xsd"))
			{
				item=this.createNewItem(parent,SWT.NULL);
				item.setText(elName);
			}
		}
		
	}

	private TreeItem createNewItem(final TreeItem parent, final int style) {
		if(parent==null) {
			return new TreeItem(this.fileTree,style);
		} else {
			return new TreeItem(parent,style);
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
			items=this.fileTree.getItems();
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
		
		item=(this.fileTree.getSelection())[0];
		
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
	public void setVisible(final boolean visible)
	{
		IFileStore schemaDirStore=null;
		URI dirURI=null;
		
		if(visible==true)
		{
			try {
				this.fileTree.removeAll();
				
				this.isComplete=false;
				
				this.schemaDirWizardPage=(TBX_NewInterfaceSchemaSetSelectionWizardPage)this.getPreviousPage();
				//filling tree
				
				dirURI=URIUtil.toURI(this.schemaDirWizardPage.getSchemaRootDir());
				schemaDirStore=EFS.getStore(dirURI);
							
				this.parseDir(schemaDirStore,null);
			} catch (final CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		super.setVisible(visible);
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
		
	private Tree fileTree;
	
	private TBX_NewInterfaceSchemaSetSelectionWizardPage schemaDirWizardPage=null;
	
	
}
