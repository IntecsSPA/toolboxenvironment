/**
 * 
 */
package it.intecs.pisa.develenv.ui.widgets;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Massimiliano
 *
 */
public class ServiceOperationsTree extends Composite {
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String FILE_SERVICE_DESCRIPTOR_XML = "serviceDescriptor.xml";
	private static final String FILE_ICON = "/icons/decorator/operation.png";
	
		private Tree operationsTree;
	
	private Image itemImage=null;
	private String selProject;
	/**
	 * @param parent
	 * @param style
	 */
	public ServiceOperationsTree(Composite parent) {
		super(parent,SWT.NULL);
		
		initControls(parent);
		init();
	}

	public ServiceOperationsTree(Composite parent,String selectedProject) {
		super(parent,SWT.NULL);
		
		this.selProject=selectedProject;
		
		initControls(parent);
		init();
	}
	/**
	 * This method initializes the component filling it with TOOLBOX projects
	 */
	private void init() {
		
		//Filling tree with operations
		reloadOperations();
	}

	private void reloadOperations() {
		String[] serviceOperations=null;
				
		clearOperationTree();
		serviceOperations=getServiceOperations();	
		addEntries(serviceOperations);
	}

	private void addEntries(String[] serviceOperations) {
		for(String projectName:serviceOperations)
		{
			addTreeEntry(projectName);
		}
	}

	private void clearOperationTree() {
		TreeItem[] items=null;
		
		items=operationsTree.getItems();
		for(TreeItem item:items)
		{
			item.dispose();
		}
	}

	private void initControls(Composite parent) {
		GridData gd=null;
		GridLayout layout = new GridLayout();
		Bundle bundle=null;
		URL iconUrl=null;
		Label label=null;
		
		this.setLayout(layout);
		layout.numColumns=1;
				
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		this.setLayoutData(gd);
		
		label=new Label(this,SWT.NULL);
		label.setText("Select service:");
		
		operationsTree=new Tree(this,SWT.SINGLE|SWT.VIRTUAL| SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		operationsTree.setLayoutData(gd);
		operationsTree.setLinesVisible(false);
		// TODO Auto-generated method stub
		operationsTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				ServiceOperationsTree.this.checkSelection(e);
			}

		});
		
		try {
			bundle = Platform.getBundle(BUNDLE_NAME);
			
			iconUrl=bundle.getEntry(FILE_ICON);
			
			ImageDescriptor imageDescr = ImageDescriptor.createFromURL(iconUrl);
			this.itemImage=imageDescr.createImage();
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
	}
		
	protected void checkSelection(SelectionEvent e) {
		// TODO Auto-generated method stub
		Event event=null;
					
		event=new Event();
		
		this.notifyListeners(SWT.Modify, event);
	}
	
	private void addTreeEntry(String projectName) {
		// TODO Auto-generated method stub
		TreeItem item=null;
		
		item=new TreeItem(operationsTree,SWT.NULL);
		item.setText(projectName);
		if(itemImage!=null)
			item.setImage(itemImage);
	}

	private String[] getServiceOperations()
	{
		String[] implementedOperations=null;
		IWorkspaceRoot root=null;
		IWorkspace workspace=null;
		IProject project=null;
		IFile file=null;
		File descriptorFile=null;
		Service serviceDescr=null;
		Interface implementedInterface=null;
		Operation[] operations=null;
		
		int i=0;
		
		try
		{
			if(selProject==null)
				return new String[0];
	
			workspace=ResourcesPlugin.getWorkspace();
			root = workspace.getRoot();
			
			project=root.getProject(this.selProject);
			file=project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
			descriptorFile=new File(root.getLocation().toFile(),file.getFullPath().toOSString());
			
			serviceDescr=new Service();
			serviceDescr.loadFromFile(descriptorFile);
			
			implementedInterface=serviceDescr.getImplementedInterface();
			operations=implementedInterface.getOperations();
			
			implementedOperations=new String[operations.length];
			for(Operation op:operations)
			{
				implementedOperations[i]=op.getName();
				i++;
			}
		}
		catch(Exception e)
		{
			implementedOperations=new String[0];
		}
		return implementedOperations;
	}
	
	
	
	public String getSelectedProjectName()
	{
		TreeItem[] selected=null;
		
		selected=operationsTree.getSelection();
		return selected[0].getText();
	}
	
	public void setSelectedProject(String projectName)
	{
		this.selProject=projectName;
		this.reloadOperations();
	}

	public int getItemCount() {
		// TODO Auto-generated method stub
		return this.operationsTree.getItemCount();
	}

	public TreeItem[] getSelection() {
		// TODO Auto-generated method stub
		return this.operationsTree.getSelection();
	}

}
