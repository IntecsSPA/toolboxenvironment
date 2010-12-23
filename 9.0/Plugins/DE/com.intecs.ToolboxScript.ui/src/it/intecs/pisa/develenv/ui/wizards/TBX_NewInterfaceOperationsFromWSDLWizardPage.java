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


import it.intecs.pisa.develenv.ui.widgets.NewInterfaceOperationDefinitionTab;
import it.intecs.pisa.util.wsdl.Binding;
import it.intecs.pisa.util.wsdl.BoundedOperation;
import it.intecs.pisa.util.wsdl.Operation;
import it.intecs.pisa.util.wsdl.PortTypes;
import it.intecs.pisa.util.wsdl.SOAPBoundedOperation;
import it.intecs.pisa.util.wsdl.WSDL;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_NewInterfaceOperationsFromWSDLWizardPage extends WizardPage {

	
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	public static final String OPERATION_DETAIL_CALLBACK_SOAP_ACTION = "OPERATION_DETAIL_CALLBACK_SOAP_ACTION";
	public static final String OPERATION_DETAIL_TYPE = "OPERATION_DETAIL_TYPE";
	public static final String OPERATION_DETAIL_SOAP_ACTION = "OPERATION_DETAIL_SOAP_ACTION";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String MAIN_TITLE = "Add an user-defined interface to the Interface Repository";
	private static final String WIZARD_DESCRIPTION = "This page lets you define which operation to add to the interface.";
	private int oldWidth=640;
	private int oldEight=480;
	
	private WSDL wsdl=null;
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_NewInterfaceOperationsFromWSDLWizardPage(final ISelection selection)
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
		Composite container;
		Shell shell;
		
		//This is the page container
		container = new Composite(parent, SWT.NULL);
		final GridLayout mainLayout = new GridLayout();
		container.setLayout(mainLayout);
		
		mainLayout.numColumns=1;
		
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
		
		this.operationtable=new TabFolder(container,SWT.NULL);
		final GridLayout tableLayout = new GridLayout();
		this.operationtable.setLayout(tableLayout);
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		this.operationtable.setLayoutData(gd);
		
		
		this.operationtable.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				//checkSelection(e);
			}
		});

		//Adding selected checkbox
				
		this.initialize();
		this.dialogChanged();
		this.setControl(container);
		

		
		shell=this.getShell();
		
		if(shell!=null)
		{
			this.oldWidth=shell.getBounds().width;
			this.oldEight=shell.getBounds().height;
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
			this.operationComposites=new NewInterfaceOperationDefinitionTab[0];
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
	}

	private void addTabs() {
		//initializing tabbed pane
		PortTypes[] ports=null;
		Operation[] operations=null;
		int operationCount=0;
		int index=0;
				
		TBX_NewInterfaceWSDLSelectionWizardPage previous=null;
		
		File wsdlFile=null;
		
		previous=(TBX_NewInterfaceWSDLSelectionWizardPage)this.getPreviousPage();
		
		wsdlFile=new File(previous.getSelectedWSDL());
	
		wsdl=new WSDL(wsdlFile);
	
		ports=wsdl.getPortTypes();
				
		for (PortTypes element : ports) {
			operations=element.getOperations();
			
			operationCount+=operations.length;
		}
		
		this.operationComposites=new NewInterfaceOperationDefinitionTab[operationCount];
		
		for (PortTypes element : ports) {
			operations=element.getOperations();
			
			for (Operation element0 : operations) {
				TabItem operationTab=null;
				String operationName=null;
				
				operationName=element0.getName();
				operationTab=new TabItem(this.operationtable,SWT.LEFT);
				operationTab.setText(operationName);
				
				NewInterfaceOperationDefinitionTab content=null;
				
				content=new NewInterfaceOperationDefinitionTab(this.operationtable,operationName, wsdl);
				operationTab.setControl(content);
				operationTab.addListener(SWT.Selection,  new Listener(){
				
					public void handleEvent(final Event event) {
						// TODO Auto-generated method stub
						//if(event.text!=null && event.equals("")==false)
						TBX_NewInterfaceOperationsFromWSDLWizardPage.this.updateStatus(event.text);
					}});
			
				this.operationComposites[index]=content;
				index++;
			}
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
		this.setPageComplete((message == null) && this.isPageComplete());
	}
	
	@Override
	public boolean isPageComplete()
	{
		//Cycle over all tabs
		
		final boolean returnValue=true;
		/*
		for(NewInterfaceOperationDefinitionTab composite : operationComposites)
		{
			returnValue=returnValue&&composite.isOperationDefinitionComplete();
		}*/
		return returnValue;
	}
	
	@Override
	public void setVisible(final boolean visible)
	{
		Shell shell;
		
		shell=this.getShell();
				
		if(visible==true)
		{
			this.addTabs();
			if(shell!=null)
				shell.setSize(oldWidth,oldEight+100);
		}
		else
		{
			if(shell!=null)
				shell.setSize(oldWidth,oldEight);
			
			TabItem[] items=null;
			
			items=this.operationtable.getItems();
			
			for(final TabItem item: items)
			{
				item.dispose();
			}
				
				final Control[] children=this.operationtable.getChildren();
				
				for(final Control c: children)
				{
					c.dispose();
				}
			
		}
		
		super.setVisible(visible);
	}
	

	private NewInterfaceOperationDefinitionTab[] operationComposites=null;
	private TabFolder operationtable=null;

	public it.intecs.pisa.common.tbx.Operation[] getSelectedOperations() {
		//For each operation a sub-Hashtable is added.  Inside all parameters are set.
		int selectedCount=0;
		int index=0;
		it.intecs.pisa.common.tbx.Operation[] enabledOperations=null;
		it.intecs.pisa.common.tbx.Operation op=null;
		String templatePath=null;
		String operationName=null;
		Operation operation=null;
		PortTypes port=null;
		Binding binding=null;
		SOAPBoundedOperation sbop=null;
		Hashtable<String,String> scriptPathsTable;
		
		for(final NewInterfaceOperationDefinitionTab c: this.operationComposites)
		{
			if(c.isEnabled())
			{
				selectedCount++;
			}
		}
		
		enabledOperations=new it.intecs.pisa.common.tbx.Operation[selectedCount];
		
		for(final NewInterfaceOperationDefinitionTab c: this.operationComposites)
		{
			if(c.isEnabled())
			{
				op=new it.intecs.pisa.common.tbx.Operation();
				
				op.setName(c.getOperationName());	
				op.setSoapAction(c.getSOAPAction());
				
				binding=wsdl.getBindingByName(c.getBinding());
				port=wsdl.getPortTypeByID(binding.getType());
				operation=port.getOperationByName(c.getOperationName());
				
				
				op.setInputType(operation.getInputNameType());
				op.setInputTypeNameSpace(operation.getInputNameNameSpace());
				
				op.setOutputType(operation.getOutputNameType());
				op.setOutputTypeNameSpace(operation.getOutputNameNameSpace());
				
				if(c.isAsynchronous())
				{
					op.setType(it.intecs.pisa.common.tbx.Operation.OPERATION_TYPE_ASYNCHRONOUS);
					op.setCallbackSoapAction(c.getCallbackSOAPAction());	
					
					binding=wsdl.getBindingByName(c.getCallbackBinding());
					sbop=binding.getSOAPOperation(c.getCallbackSOAPAction());
					port=wsdl.getPortTypeByID(binding.getType());
					operation=port.getOperationByName(sbop.getName());
					
					op.setCallbackInputType(operation.getInputNameType());
					op.setCallbackInputTypeNameSpace(operation.getInputNameNameSpace());
					op.setCallbackOutputType(operation.getOutputNameType());
					op.setCallbackOutputTypeNameSpace(operation.getOutputNameNameSpace());
				}
				else
				{
					op.setType(it.intecs.pisa.common.tbx.Operation.OPERATION_TYPE_SYNCHRONOUS);	
				}
				
				scriptPathsTable=c.getScriptFiles();
				Enumeration enumeration;
				String key;
				
				enumeration=scriptPathsTable.keys();
				
				while(enumeration.hasMoreElements())
				{
				    key=(String)enumeration.nextElement();
				    op.getScript(key).setPath(scriptPathsTable.get(key));
				}
								
				enabledOperations[index]=op;
				index++;
			}
		}
		
		return enabledOperations;
	}

	public WSDL getWsdl() {
		return wsdl;
	}
}


