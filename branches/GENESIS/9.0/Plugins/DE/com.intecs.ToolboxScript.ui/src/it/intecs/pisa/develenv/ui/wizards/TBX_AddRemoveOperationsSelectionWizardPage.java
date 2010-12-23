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
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class TBX_NewProjectWizardPage.
 */
public class TBX_AddRemoveOperationsSelectionWizardPage extends WizardPage
 {
	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";
	private static final String PATH_INTERFACES_DEFINITION_XML = "/interfacesDefinition.xml";
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	
	private static final String ATTRIBUTE_VERSION = "version";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String TAG_INTERFACE = "interface";
	private static final String FILE_SERVICE_DESCRIPTOR_XML = "serviceDescriptor.xml";
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private static final String MAIN_TITLE = "Add a service operation";
	private static final String WIZARD_DESCRIPTION = "Select/deselect operations in order to add them to the service.";
	
	private Tree operationTree;
	private InterfacesDescription interfDescription=null;
	
	private TBX_AddRemoveOperationsWizardPage previousPage=null;
	private String projectName=null;
	private Service descriptor=null;
	/**
	 * The Constructor.
	 * 
	 * @param selection the selection
	 */
	public TBX_AddRemoveOperationsSelectionWizardPage(final ISelection selection)
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
		this.operationTree =new Tree (container, SWT.VIRTUAL| SWT.BORDER | SWT.CHECK);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.operationTree.setLayoutData(gd);
		this.operationTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBX_AddRemoveOperationsSelectionWizardPage.this.checkSelection(e);
			}

		});
		
		this.initialize();
		this.setControl(container);
	}
	

	protected void checkSelection(SelectionEvent e) {
		// TODO Auto-generated method stub
		
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
		
		previousPage=(TBX_AddRemoveOperationsWizardPage) this.getPreviousPage();		
	}



	private Operation[] getImplementedOperations() {
		Operation[] operations;
		String selectedProject;
		
		IWorkspaceRoot root;
		IProject project;
		IFile file;
		File descriptorFile;
		
		root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(projectName);
		file = project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
		
		descriptorFile = new File(root.getLocation().toFile(),file.getFullPath().toOSString());
		
		descriptor=new Service();
		descriptor.loadFromFile(descriptorFile);
		
		operations=descriptor.getImplementedInterface().getOperations();
		
		return operations;
	}

	
	
	private void addTreeEntry(String projectName,boolean checked) {
		// TODO Auto-generated method stub
		TreeItem item=null;
		
		item=new TreeItem(operationTree,SWT.NULL);
		item.setText(projectName);
		item.setChecked(checked);
	}

	private Operation[] getInterfaceOperations()
	{
		File pluginInterfaceFile=null;
		File userInterfaceFile=null;
		File userInterfaceDirectory=null;
		Bundle interfacesPlugin;
		URL entry=null;
		String userHome=null;
		Interface interf=null;
	
		
		if(interfDescription==null)
		{
			interfDescription=new InterfacesDescription();
			
			try {
				interfacesPlugin = Platform.getBundle(BUNDLE_INTERFACES);
				entry=interfacesPlugin.getEntry(PATH_INTERFACES_DEFINITION_XML);
				
				pluginInterfaceFile = new File(FileLocator.toFileURL(entry).getPath());
				if(pluginInterfaceFile!=null && pluginInterfaceFile.exists())
				{
					interfDescription.loadFromFile(pluginInterfaceFile, true);
				}
			} catch (Exception e) {
				//must notify that plugin interface file is not available.
			}
			
			try
			{
				userHome = System.getProperty("user.home");
				
				userInterfaceDirectory=new File(userHome,FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
				userInterfaceFile=new File(userInterfaceDirectory,PATH_INTERFACES_DEFINITION_XML);
		
				interfDescription.loadFromFile(userInterfaceFile, false);
			}
			catch(Exception usrExc)
			{
				//should notify error
			}
		
		}
		if(descriptor!=null)
		{
			interf=interfDescription.getInterface(descriptor.getImplementedInterface().getName(), descriptor.getImplementedInterface().getVersion());
		}
		
		
		//returning true if Interface reference is returned by store
		return interf.getOperations();
	}
	
	public void setVisible(boolean visible)
{
		Operation[] implementedOperations=null;
		Operation[] interfaceOperations=null;
		TreeItem[] items=null;
		boolean implemented=false;
		
		if(visible==true)
		{
			projectName=previousPage.getSelectedProjectName();
			
			implementedOperations=getImplementedOperations();
			interfaceOperations=getInterfaceOperations();

			items=operationTree.getItems();
			
			for(TreeItem item: items)
			{
				item.dispose();
			}
			
			for(Operation oper: interfaceOperations)
			{
				implemented=false;
				
				for(Operation implOper:implementedOperations)
				{
					if(oper.getName().equals(implOper.getName()))
					{
						implemented=true;
					}
				}
				
				if(implemented==false)
					addTreeEntry(oper.getName(),implemented);
				
			}
		}
		
		super.setVisible(visible);
}
	
private Operation[] getImplementedOperations(String projectName)
	{
		String[] TBXProjects=null;
		Vector<IProject> tbxProjectVector=null;
		IWorkspaceRoot root=null;
		IProject project=null;
		IFile file=null;
		int i=0;
		Service descr=null;
		
		tbxProjectVector=new Vector<IProject>();
		
		root = ResourcesPlugin.getWorkspace().getRoot();
		project=root.getProject(projectName);
		
		file=project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
		if(file!=null && file.exists())
		{
			descr=new Service();
			descr.loadFromFile(file.getLocation().toFile());
			return descr.getImplementedInterface().getOperations();
		}
		else return new Operation[0];
		
	}
	
	public String getSelectedProjectName()
	{
		TreeItem[] selected=null;
		
		selected=operationTree.getSelection();
		return selected[0].getText();
	}
	
	public boolean canFlipToNextPage()
	{
		TreeItem[] selection=null;
		
		if(operationTree.getItemCount()>0)
		{
			selection=operationTree.getSelection();
			if(selection.length>0)
				return true;
			else return false;
		}
		else return false;
	}
	
	public String[] getCheckedOperations()
	{
		//must return all checked operation in order to add/remove them
		TreeItem[] items=null;
		String[] checkedOperations=null;
		int checkedCount=0;
		int i=0;
		
		items=operationTree.getItems();
		for(TreeItem item:items)
		{
			if(item.getChecked())
				checkedCount++;
		}
		
		checkedOperations=new String[checkedCount];
		for(TreeItem item:items)
		{
			if(item.getChecked())
			{
				checkedOperations[i]=item.getText();
				i++;
			}		
		}
				
		return checkedOperations;
	}

}
