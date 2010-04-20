/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptMainTab.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.8 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/03/23 12:45:03 $
 * File ID: $Id: TscriptMainTab.java,v 1.8 2007/03/23 12:45:03 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.launching;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.develenv.model.launch.ToolboxScriptLaunchConfiguration;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;

/**
 * The Class TscriptMainTab.
 */
public class TscriptMainTab extends AbstractLaunchConfigurationTab {
	protected Button browseTestFiles;
	protected Combo operationCombo;
	protected Label operationLabel;
	
	protected Button pollingRateCheckbox;
	protected Combo pollingRateCombo;
	protected Text pollingRateText;

	
	protected Combo serviceCombo;
	protected Label serviceLabel;
	
	protected Text testFilepathText;
	protected Label testFilesLabel;
	
	private void addOperationCombo(Composite parent) {
		Composite container;
		GridLayout layout;
		GridData gd;
		String[] items;
		
		container=new Composite(parent,SWT.NULL);
		layout=new GridLayout();
		layout.numColumns=2;
		container.setLayout(layout);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		container.setLayoutData(gd);
		
		operationLabel=new Label(container,SWT.NULL);
		operationLabel.setText("Operation:");
		
		gd=new GridData(SWT.NULL,SWT.NULL,false,false);
		operationLabel.setLayoutData(gd);
		
		operationCombo=new Combo(container,SWT.BORDER|SWT.READ_ONLY);
		items=new String[0];
		operationCombo.setItems(items);
		//operationCombo.select(0);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		operationCombo.setLayoutData(gd);
		
		operationCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TscriptMainTab.this.updateOperationSelection();
			}

		
		});
		
		operationCombo.setEnabled(false);
	}
	
	private void addPollingControls(Composite parent) {
		Composite container;
		GridLayout layout;
		GridData gd;
		String[] items={"Seconds","Minutes","Hours","Days","Weeks"};
		
		container=new Composite(parent,SWT.NULL);
		layout=new GridLayout();
		layout.numColumns=3;
		container.setLayout(layout);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		container.setLayoutData(gd);
		
		
		pollingRateCheckbox=new Button(container,SWT.CHECK);
		pollingRateCheckbox.setText("Overwrite default polling rate. Use this: ");
		pollingRateCheckbox.setEnabled(false);
		
		gd=new GridData(SWT.NULL,SWT.NULL,false,false);
		pollingRateCheckbox.setLayoutData(gd);
		pollingRateCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TscriptMainTab.this.updateAfterPollingRateCheckboxSelection();
			}

		
		});
		
		pollingRateText=new Text(container,SWT.BORDER);
		pollingRateText.setEnabled(false);
		
		gd=new GridData(SWT.NULL,SWT.NULL,false,false);
		pollingRateText.setLayoutData(gd);
		pollingRateText.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				TscriptMainTab.this.updatePollingRate();
			}});
		
		pollingRateCombo=new Combo(container,SWT.BORDER|SWT.READ_ONLY);
		pollingRateCombo.setItems(items);
		pollingRateCombo.select(0);
		pollingRateCombo.setEnabled(false);
		
	}

	private void addServiceCombo(Composite parent) {
		Composite container;
		GridLayout layout;
		GridData gd;
				
		container=new Composite(parent,SWT.NULL);
		layout=new GridLayout();
		layout.numColumns=2;
		container.setLayout(layout);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		container.setLayoutData(gd);
		
		serviceLabel=new Label(container,SWT.NULL);
		serviceLabel.setText("Service:    ");
		
		gd=new GridData(SWT.NULL,SWT.NULL,false,false);
		serviceLabel.setLayoutData(gd);
		
		serviceCombo=new Combo(container,SWT.BORDER|SWT.READ_ONLY);
		populateServiceCombo(null);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		serviceCombo.setLayoutData(gd);
		
		serviceCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TscriptMainTab.this.updateServiceSelection();
			}

		
		});
	}

	private void addTestFilesControls(Composite parent) {
		Composite container;
		GridLayout layout;
		GridData gd;
			
		container=new Composite(parent,SWT.NULL);
		layout=new GridLayout();
		layout.numColumns=3;
		container.setLayout(layout);
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		container.setLayoutData(gd);
		
		
		testFilesLabel=new Label(container,SWT.NULL);
		testFilesLabel.setText("Test file:    ");
		gd=new GridData(SWT.NULL,SWT.NULL,false,false);
		testFilesLabel.setLayoutData(gd);
		
		testFilepathText=new Text(container,SWT.BORDER|SWT.READ_ONLY);
			
		
		gd=new GridData(SWT.FILL,SWT.NULL,true,false);
		testFilepathText.setLayoutData(gd);
		
		browseTestFiles=new Button(container,SWT.NULL);
		browseTestFiles.setText("Browse for test file");
		browseTestFiles.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TscriptMainTab.this.browseForFiles();
			}

		
		});
		
		gd=new GridData(SWT.NULL,SWT.NULL,false,false);
		browseTestFiles.setLayoutData(gd);
		
		testFilepathText.setEnabled(false);
		browseTestFiles.setEnabled(false);
		
	}

	/**
	 * Browse tscript files.
	 */
	protected void browseForFiles() {
		ResourceListSelectionDialog dialog ;
		IProject project;
		String operationName;
		IFolder operationTestFolder;
		
		project=ToolboxEclipseProjectUtil.getProject(serviceCombo.getItem(serviceCombo.getSelectionIndex()));
		operationName=operationCombo.getItem(operationCombo.getSelectionIndex());
		
		operationTestFolder=project.getFolder("Test Files/"+operationName);
		
		dialog = new ResourceListSelectionDialog(this.getShell(), operationTestFolder, IResource.FILE);
		
		dialog.setTitle("Operation test files");
		dialog.setMessage("Select a test file");
		// TODO: single select
		if (dialog.open() == Window.OK) {
			final Object[] files = dialog.getResult();
			final IFile file = (IFile) files[0];
			testFilepathText.setText(file.getFullPath().toString());
			//fScriptText.setText(file.getLocation().toString());
			
		}
		
		updateLaunchConfigurationDialog();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#canSave()
	 */
	@Override
	public boolean canSave() {
		// TODO Auto-generated method stub
		return checkIfConfigurationCanBeSaved() && super.canSave();
	}

	public boolean checkIfConfigurationCanBeSaved()
	{
		boolean isValid=false;
		String testFile;	
		
		testFile=testFilepathText.getText();
		
		isValid=serviceCombo.isEnabled() && operationCombo.isEnabled() && testFile!=null && testFile.equals("")==false;
		if(isValid==true)
		{
			//checking if test file path is valid
			
			
			if(this.pollingRateCheckbox.getSelection())
				isValid=isValid&& checkPollingRate();
		}
		
		return isValid;
	}

	protected boolean checkPollingRate() {
		String pollingRate;
		
		pollingRate=this.pollingRateText.getText();
		try
		{
			Integer.parseInt(pollingRate);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		GridLayout layout;
		Composite container;
		GridData gd;
		
		layout=new GridLayout();
		parent.setLayout(layout);
		
		container=new Composite(parent,SWT.NULL);
		gd=new GridData(SWT.FILL,SWT.FILL,true,true);
		container.setLayoutData(gd);
		
		layout=new GridLayout();
		container.setLayout(layout);
		
		
		//adding service combo
		addServiceCombo(container);
		
		//adding operation combo
		addOperationCombo(container);
		
		//addTestFileControls
		addTestFilesControls(container);
		
		//addPollingControls
		addPollingControls(container);
		
		this.setControl(container);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#getName()
	 */
	public String getName() {
		return "Main";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void initializeFrom(final ILaunchConfiguration configuration) {
		try {		
			String service = configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_SERVICE, (String)null);
			populateServiceCombo(service);
			
			String operation = configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_OPERATION, (String)null);
			populateOperationCombo(service,operation);
			
			String testFile = configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_TESTFILE, (String)null);
			populateTestFileField(testFile);
			
			String userDefPollingRate= configuration.getAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_POLLING_RATE, (String)null);
			populatePollingRate(userDefPollingRate);
			
			updateLaunchConfigurationDialog();
		} catch (CoreException e) {
			this.setErrorMessage(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {

		try {
			String service;
			String operation;
			String testFile;
			String pollingRate;
			int selIndex=-1;
			
			selIndex=serviceCombo.getSelectionIndex();
			if(selIndex>-1)
			{
				service= serviceCombo.getItem(selIndex);
				configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_SERVICE, service);
			}
			
			selIndex=operationCombo.getSelectionIndex();
			if(selIndex>-1)
			{
				operation = operationCombo.getItem(selIndex);
				configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_OPERATION, operation);
			}
			
			testFile=testFilepathText.getText();
			configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_TESTFILE, testFile);
			
			
			pollingRate=null;
			
			if(this.pollingRateCheckbox.getSelection())
			{
				pollingRate=pollingRateText.getText();
				
				if(pollingRate!=null && checkPollingRate())
				{
					switch(pollingRateCombo.getSelectionIndex())
					{
						case 0:
							pollingRate+="s";
						break;
						
						case 1:
							pollingRate+="m";
						break;
							
						case 2:
							pollingRate+="h";
						break;
							
						case 3:
							pollingRate+="d";
						break;
							
						case 4:
							pollingRate+="w";
						break;
					}
				}
			}
			
			configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_POLLING_RATE, pollingRate);
			
		} catch (Exception e) {
			this.setErrorMessage(e.getMessage());
		}
		
	}
	
	
	
	private void populateOperationCombo(String serviceName, String selectedOperation) {
		String[] items;
		int i=0;
		
		if(serviceName!=null && serviceName.equals("")==false)
		{
			items=ToolboxEclipseProjectUtil.getOperationNames(serviceName);
			operationCombo.setItems(items);
			
			if(selectedOperation!=null)
			
			{
				//performing selection
				for(String item:items)
				{
					if(item.equals(selectedOperation))
						{
							operationCombo.select(i);
							
							break;
						}
					else i++;
				}	
			}
			
			operationCombo.setEnabled(true);
		}
		else
		{
			items=new String[0];
			operationCombo.setItems(items);
			operationCombo.select(0);
			operationCombo.setEnabled(false);
		}
	}
	
	
	private void populatePollingRate(String userDefPollingRate) {
		int i;
		String[] items;
		String pollingRateAmount;
		String pollingRateGranularity;
		if (userDefPollingRate != null && userDefPollingRate.equals("")==false) {
			pollingRateAmount=userDefPollingRate.substring(0,userDefPollingRate.length()-1);
			
		
				pollingRateText.setText(pollingRateAmount);
				
				pollingRateGranularity=String.valueOf(userDefPollingRate.charAt(userDefPollingRate.length()-1));
				items=pollingRateCombo.getItems();
				i=0;
				for(String item:items)
				{
					if(item.equals(pollingRateGranularity))
						{
							pollingRateCombo.select(i);
							break;
						}
					else i++;
				}
				this.pollingRateCheckbox.setSelection(true);
				this.pollingRateCheckbox.setEnabled(true);
				this.pollingRateText.setEnabled(true);
				this.pollingRateCombo.setEnabled(true);
				
		}
		else
		{
			this.pollingRateCheckbox.setSelection(false);
			
			this.pollingRateText.setText("1");
			this.pollingRateCombo.select(1);
			
			this.pollingRateCheckbox.setEnabled(false);
			this.pollingRateText.setEnabled(false);
			this.pollingRateCombo.setEnabled(false);
		}
	}

	private void populateServiceCombo(String selectedService) {
		String[] items;
		int i=0;
		
		items=ToolboxEclipseProjectUtil.getProjectNames();
		serviceCombo.setItems(items);
		
		if(selectedService!=null)
		{
			for(String item:items)
			{
				if(item.equals(selectedService))
				{
					serviceCombo.select(i);
					break;
				}
				else i++;
			}
		}
	}

	private void populateTestFileField(String testFile) {
		if (testFile != null && testFile.equals("")==false) {
			testFilepathText.setText(testFile);
			testFilepathText.setEnabled(true);
			browseTestFiles.setEnabled(true);
		}
		else
		{
			testFilepathText.setText("");
			testFilepathText.setEnabled(false);
			browseTestFiles.setEnabled(false);
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setDefaults(final ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_SERVICE, (String)null);
		configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_OPERATION, (String)null);
		configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_TESTFILE, (String)null);
		configuration.setAttribute(ToolboxScriptLaunchConfiguration.CONFIGURATION_POLLING_RATE, (String)null);
	}
	
	protected void updateAfterPollingRateCheckboxSelection() {
		boolean selStatus;
		
		selStatus=pollingRateCheckbox.getSelection();
		
		pollingRateText.setEnabled(selStatus);
		pollingRateCombo.setEnabled(selStatus);
		
		updateLaunchConfigurationDialog();
	}
	

	protected void updateOperationSelection() {
		populateTestFileField(null);
		this.browseTestFiles.setEnabled(true);
		enableDisablePollingRate();
		updateLaunchConfigurationDialog();
	}
	
	
	private void enableDisablePollingRate() {
		String selectedServiceName;
		String selectedOperationname;
		Operation operation;
		
		selectedServiceName=this.serviceCombo.getItem(serviceCombo.getSelectionIndex());
		selectedOperationname=this.operationCombo.getItem(operationCombo.getSelectionIndex());
		
		operation=ToolboxEclipseProjectUtil.getOperation(selectedServiceName, selectedOperationname);
		if(operation.getType().equals(Operation.OPERATION_TYPE_ASYNCHRONOUS))
		{
			this.pollingRateCheckbox.setEnabled(true);
			this.pollingRateText.setEnabled(true);
			this.pollingRateCombo.setEnabled(true);
		}
		else
		{
			this.pollingRateCheckbox.setEnabled(false);
			this.pollingRateText.setEnabled(false);
			this.pollingRateCombo.setEnabled(false);
		}
	
	}

	private void updatePollingRate() {
		boolean isValid=false;
		
		isValid=checkPollingRate();
		if(isValid==false)
			this.setErrorMessage("Polling Rate not valid");
		else this.setErrorMessage(null);
		
		updateLaunchConfigurationDialog();
	}

	protected void updateServiceSelection() {
		String serviceName;
		int selectionIndex=0;
		
		
		selectionIndex=serviceCombo.getSelectionIndex();
		serviceName=serviceCombo.getItem(selectionIndex);
		
		populateOperationCombo(serviceName, null);
	
		populateTestFileField(null);
		
		updateLaunchConfigurationDialog();
	}
}
