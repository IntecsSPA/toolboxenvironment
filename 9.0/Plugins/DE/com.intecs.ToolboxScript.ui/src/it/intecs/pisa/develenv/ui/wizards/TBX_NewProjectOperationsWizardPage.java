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
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.util.DOMUtil;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.w3c.dom.Element;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewProjectOperationsWizardPage extends WizardPage {

	private static final String TEXT_SYNCHRONOUS = "synchronous";
	private static final String TEXT_ASYNCHRONOUS = "asynchronous";
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Create a new TOOLBOX Service";
	private static final String WIZARD_DESCRIPTION = "This page will let you select one or more operations. The scheleton of these operations will be automatically implemented by the wizard.";
	
	private Tree operationTree;
	
	private TBX_NewProjectInterfaceWizardPage interfaceWizardPage=null;
	
	private String builtForInterface=null;
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewProjectOperationsWizardPage(final ISelection selection)
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
				
		this.operationTree =new Tree (container, SWT.VIRTUAL| SWT.BORDER| SWT.CHECK);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.operationTree.setLayoutData(gd);
		this.operationTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_NewProjectOperationsWizardPage.this.checkSelection(e);
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
		
		this.interfaceWizardPage=(TBX_NewProjectInterfaceWizardPage)this.getPreviousPage();
	}

	
	/**
	 * @param e
	 */
	private void checkSelection(final SelectionEvent e) {
		TreeItem selItem=null;
		TreeItem[] items=null;
		String selectedText=null;
		
		selItem=(TreeItem)e.item;
		if(selItem.getChecked())
		{
			selectedText=selItem.getText();
			
			items=this.operationTree.getItems();
			
			if(items.length>1)
			{
				final String operName=this.extractOperationNameFromDisplayName(selectedText);
				final String operType=this.extractOperationTypeFromDisplayName(selectedText);
				String coupledItemText=operName+"(";
				if(operType.equals(TEXT_SYNCHRONOUS))
				{
					coupledItemText+=TEXT_ASYNCHRONOUS+")";
				}
				else
				{
					coupledItemText+=TEXT_SYNCHRONOUS+")";
				}
				for (TreeItem element : items) {
					if(element.getText().equals(coupledItemText))
					{
						//checking if selected
						if(element.getChecked())
						{
							this.updateStatus("You cannot select both synchronous and asynchronous types for an operation");
							selItem.setChecked(false);
							return;
						}
					}
				}
			}
		}
		
		this.updateStatus(null);
		
		
	}

	private String extractOperationTypeFromDisplayName(final String selectedText) {
		return selectedText.substring(selectedText.lastIndexOf("(")+1,selectedText.lastIndexOf(")"));
	}

	private String extractOperationNameFromDisplayName(final String selectedText) {
		return selectedText.substring(0,selectedText.lastIndexOf("(")-1);
	}
	/**
	 * Dialog changed.
	 */

	private void dialogChanged() {

		this.updateStatus(null);
	}
	
	private void addOperationsFromInterfaceDescriptor(Interface interf)
	{
		TreeItem item=null;
		Operation[] operations=null;
		int operCount=0;
		
		try {
			operations=interf.getOperations();
			
			operCount=operations.length;
			
			if(operCount==0)
			{
				this.setPageComplete(false);
			}
			else
			{
				for(Operation oper: operations)
				{					
					item=new TreeItem(operationTree,SWT.NULL);
						
					item.setText(this.createDisplayName(oper.getType(), oper.getName()));
				}
			}
			
		} catch (final Exception e) {
			e.printStackTrace();
		} 
		
	}

	private String createDisplayName(final String operationType, final String itemText) {
		return itemText+" ("+operationType+")";
	}
	
	
	@Override
	public void setVisible(final boolean visible)
	{
		Interface interf=null;
		if(visible==true)
		{
			try
			{
				//checking if list has already been filled for the selected interface
				if((this.builtForInterface==null) || (this.builtForInterface.equals(this.interfaceWizardPage.getSelectedInterfaceDisplayedName())==false))
				{
					//removing all entries
					this.operationTree.removeAll();
					
					interf=interfaceWizardPage.getSelectedInterface();
					addOperationsFromInterfaceDescriptor(interf);
					
					this.builtForInterface=this.interfaceWizardPage.getSelectedInterfaceDisplayedName();
				}
			}
			catch(final Exception usrExc)
			{
				
			}
		}
		
		super.setVisible(visible);
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
	
	public Operation[] getSelectedOperations()
	{
		TreeItem[] selection=null;
		Operation[] operations=null;
		Operation[] selectedOperations=null;
		Interface interf=null;
		int i=0;
		int checkedCount=0;
		String name=null;
		String type=null;
		String displayName=null;
		
		selection=this.operationTree.getItems();
		for(TreeItem item:selection)
		{
			if(item.getChecked())
				checkedCount++;
		}
		
		interf = interfaceWizardPage.getSelectedInterface();
		operations= interf.getOperations();
		
		selectedOperations=new Operation[checkedCount];
		
		for(TreeItem item:selection)
		{
			if(item.getChecked())
			{
				displayName=item.getText();
				name=extractOperationNameFromDisplayName(displayName);
				type=extractOperationTypeFromDisplayName(displayName);
				
				for(Operation oper:operations)
				{
					if(oper.getName().equals(name) &&
					   oper.getType().equals(type))
					{
						selectedOperations[i]=oper;
						i++;
					}	
				}
			}
			
			
		}
		
		return selectedOperations;
		
	}

}
