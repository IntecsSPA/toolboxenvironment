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

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.develenv.ui.widgets.OperationParametersInsertion;
import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_AddRemoveOperationsNoRepositoryInterfaceWizardPage extends WizardPage implements Listener
 {
		
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Add a service operation";
	private static final String WIZARD_DESCRIPTION = "Add operation descriptions";
	

	private OperationParametersInsertion parameters=null;
	private OperationParametersInsertion callbackParameters=null;
	
	private Label nameLabel=null;
	private Text nameText=null;
	private Button check=null;
	
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_AddRemoveOperationsNoRepositoryInterfaceWizardPage(final ISelection selection)
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
		Composite nameContainer=null;
		Composite container = null;
		GridLayout layout=null;
		Composite alignComposite=null;
		
		//creating the whole widgets contaier
		container=new Composite(parent, SWT.NULL);
		
		layout = new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);
		
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
					
		
		//Adding the name row
		nameContainer=new Composite(container,SWT.NULL);
		
		layout=new GridLayout();
		layout.numColumns=2;
		nameContainer.setLayout(layout);
		
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		nameContainer.setLayoutData(gd);
				
		nameLabel=new Label(nameContainer,SWT.NULL);
		nameLabel.setText("Name           ");
		gd = new GridData(SWT.NULL,SWT.NULL,false,false);
		nameLabel.setLayoutData(gd);
		
		
		nameText=new Text(nameContainer,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		nameText.setLayoutData(gd);
		nameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				TBX_AddRemoveOperationsNoRepositoryInterfaceWizardPage.this.handleEvent(null);
			}

		});
		
		//Adding mandatory parameters	
		parameters=new OperationParametersInsertion(container,SWT.NULL);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		parameters.setLayoutData(gd);
		parameters.addListener(SWT.Modify, this);
		
		//		Adding optional parameters
		alignComposite=new Composite(container,SWT.NULL);
		
		layout=new GridLayout();
		layout.numColumns=2;
		alignComposite.setLayout(layout);
		
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		alignComposite.setLayoutData(gd);
		
		check=new Button(alignComposite,SWT.CHECK);
		check.setText("Asynchronous operation");
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		check.setLayoutData(gd);
		
		check.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_AddRemoveOperationsNoRepositoryInterfaceWizardPage.this.selectAsynchronousCheckbox(e);
			}

		});
		
		callbackParameters=new OperationParametersInsertion(container,SWT.NULL);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		callbackParameters.setLayoutData(gd);
		callbackParameters.setEnabled(false);
		callbackParameters.addListener(SWT.Modify, this);
				
		this.initialize();
		this.setControl(container);
	}
	
	protected void selectAsynchronousCheckbox(SelectionEvent e) {
		Button widget=null;
		
		if(e.widget instanceof Button)
		{
			widget=(Button)e.widget;
			
			this.callbackParameters.setEnabled(widget.getSelection());
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
	}
	
	/*public boolean canFlipToNextPage()
	{
		 return isPageComplete();
	}*/

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
	 */
	@Override
	public IWizardPage getNextPage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		if(isSOAPActionOK() && this.parameters.isCompleted())
		{
			if(this.check.getSelection())
				return this.callbackParameters.isCompleted();
			else return true;
		}
		else return false;
	}

	private boolean isSOAPActionOK() {
		String text = null;
		
		text=nameText.getText();
		
		return text!=null && text.equals("")==false;
	}

	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		this.setPageComplete(isPageComplete());
	}

	public String getOperationName() {
		return nameText.getText();
	}

	public boolean isAsynchronous() {
		return check.getSelection();
	}

	public Operation getOperation() {
		Operation operation=null;
		
		operation=new Operation();
		operation.setName(nameText.getText());
		operation.setType(isAsynchronous()?Operation.OPERATION_TYPE_ASYNCHRONOUS:Operation.OPERATION_TYPE_SYNCHRONOUS);
		//Input
		operation.setSoapAction(parameters.getSOAPAction());
		operation.setInputType(parameters.getInputType()); 
		operation.setInputTypeNameSpace(parameters.getInputNameSpace());
		//output
		operation.setOutputType(parameters.getOutputType());
		operation.setOutputTypeNameSpace(parameters.getOutputNameSpace()); 
				
		operation.setTestFile("outOfRepository/testFiles/genericTest.xml");
		if(isAsynchronous())
		{
			operation.setCallbackSoapAction(callbackParameters.getSOAPAction());
			operation.setCallbackInputType(callbackParameters.getInputType()); 
			operation.setCallbackInputTypeNameSpace(callbackParameters.getInputNameSpace());
			
			operation.setCallbackOutputType(callbackParameters.getOutputType());
			operation.setCallbackOutputTypeNameSpace(callbackParameters.getOutputNameSpace()); 
			setScript(operation,Script.SCRIPT_TYPE_RESPONSE_BUILDER,"outOfRepository/templates/asynchronous/response_builder.tscript");
			setScript(operation,Script.SCRIPT_TYPE_FIRST_SCRIPT,"outOfRepository/templates/asynchronous/first_script.tscript");
			setScript(operation,Script.SCRIPT_TYPE_SECOND_SCRIPT,"outOfRepository/templates/asynchronous/second_script.tscript");
			setScript(operation,Script.SCRIPT_TYPE_THIRD_SCRIPT,"outOfRepository/templates/asynchronous/third_script.tscript");
			setScript(operation,Script.SCRIPT_TYPE_GLOBAL_ERROR,"outOfRepository/templates/asynchronous/globalError.tscript");
			setScript(operation,Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER,"outOfRepository/templates/asynchronous/response_builder_error.tscript");
		}
		else
		{
			setScript(operation,Script.SCRIPT_TYPE_FIRST_SCRIPT,"outOfRepository/templates/synchronous/script.tscript");
			setScript(operation,Script.SCRIPT_TYPE_GLOBAL_ERROR,"outOfRepository/templates/synchronous/globalError.tscript");
		}
		
		return operation;
	}
	
	protected void setScript(Operation operation, String type, String path)
	{
		Script script;
		
		script=operation.getScript(type);
		if(script==null)
		{
			script=new Script();
			script.setType(type);
		}
		
		script.setPath(path);
		operation.addScript(script);		
	}
}
