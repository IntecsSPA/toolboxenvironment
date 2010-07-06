/**
 * 
 */
package it.intecs.pisa.develenv.ui.widgets;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
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
public class TBXProjectTree extends Composite {
	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";
	private static final String PATH_INTERFACES_DEFINITION_XML = "/interfacesDefinition.xml";
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	private static final String FILE_SERVICE_DESCRIPTOR_XML = "serviceDescriptor.xml";
	private static final String FILE_ICON = "/icons/Toolbox_gear_T.jpg";
	
	private static final String ATTRIBUTE_VERSION = "version";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String TAG_INTERFACE = "interface";
	
	private InterfacesDescription interfDescription=null;
	private Tree prjTree;
	
	private Image itemImage=null;
	private String selProject;
	/**
	 * @param parent
	 * @param style
	 */
	public TBXProjectTree(Composite parent) {
		super(parent,SWT.NULL);
		
		initControls(parent);
		init();
	}

	public TBXProjectTree(Composite parent,String selectedProject) {
		super(parent,SWT.NULL);
		
		this.selProject=selectedProject;
		
		initControls(parent);
		init();
	}
	/**
	 * This method initializes the component filling it with TOOLBOX projects
	 */
	private void init() {
		String[] tbxProjects=null;
		TreeItem[] items=null;
		
		tbxProjects=getConfigurableProjects();
			
		for(String projectName:tbxProjects)
		{
			addTreeEntry(projectName);
		}
			
		selectProject();
	}

	private void selectProject() {
		TreeItem[] items;
		if(prjTree!=null && prjTree.getItemCount()>0)
		{
			if(selProject==null)
				prjTree.setSelection(prjTree.getItem(0));
			else
			{
				items=this.prjTree.getItems();
				
				for(TreeItem item:items)
				{
					if(item.getText().equals(selProject))
						prjTree.setSelection(item);
				}
			}
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
		
		prjTree=new Tree(this,SWT.SINGLE|SWT.VIRTUAL| SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		prjTree.setLayoutData(gd);
		prjTree.setLinesVisible(false);
		// TODO Auto-generated method stub
		prjTree.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				TBXProjectTree.this.checkSelection(e);
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
		Event event;
		
		event=new Event();
		event.text=((TreeItem)e.item).getText();
		this.notifyListeners(SWT.SELECTED, event);
		
	}
	
	private void addTreeEntry(String projectName) {
		// TODO Auto-generated method stub
		TreeItem item=null;
		
		item=new TreeItem(prjTree,SWT.NULL);
		item.setText(projectName);
		if(itemImage!=null)
			item.setImage(itemImage);
	}

	private String[] getConfigurableProjects()
	{
		String[] TBXProjects=null;
		IWorkspaceRoot root=null;
		IWorkspace workspace=null;
		IProject project=null;
		Vector<IProject> tbxProjectVector=null;
		IFile file=null;
		DOMUtil util=null;
		Document doc=null;
		Element rootEl=null;
		Element interfaceEl=null;
		String interfaceName=null;
		String interfaceVersion=null;
		File descriptorFile=null;
		
		int i=0;
		
		util=new DOMUtil();
		
		tbxProjectVector=new Vector<IProject>();
		workspace=ResourcesPlugin.getWorkspace();
		root = workspace.getRoot();
		TBXProjects=parseForTOOLBOXProjects();
		
		for(String projectName:TBXProjects)
		{
			try
			{
				project=root.getProject(projectName);
				file=project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
				
				descriptorFile=new File(root.getLocation().toFile(),file.getFullPath().toOSString());
				doc=util.fileToDocument(descriptorFile);
				if(doc!=null)
				{
					rootEl=doc.getDocumentElement();
					interfaceEl=DOMUtil.getChildByTagName(rootEl, TAG_INTERFACE);
					
					interfaceName=interfaceEl.getAttribute(ATTRIBUTE_NAME);
					interfaceVersion=interfaceEl.getAttribute(ATTRIBUTE_VERSION);
					
					//if(isInterfaceStillInRepository(interfaceName,interfaceVersion)==true)
					{
						tbxProjectVector.add(project);
					}
				}
			}
			catch(Exception e)
			{
				
			}
		}
		TBXProjects=new String[tbxProjectVector.size()];
		for(IProject prj: tbxProjectVector)
		{
			TBXProjects[i]=prj.getName();
			i++;
		}
		return TBXProjects;
	}
	
	private String[] parseForTOOLBOXProjects()
	{
		String[] TBXProjects=null;
		Vector<IProject> tbxProjectVector=null;
		IWorkspaceRoot root=null;
		IProject[] projects=null;
		IFile file=null;
		int i=0;
		
		tbxProjectVector=new Vector<IProject>();
		
		root = ResourcesPlugin.getWorkspace().getRoot();
		projects=root.getProjects();
		
		for(IProject project:projects)
		{
			file=project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
			if(file!=null && file.exists())
			{
				tbxProjectVector.add(project);
			}
		}
		
		TBXProjects=new String[tbxProjectVector.size()];
		for(IProject prj:tbxProjectVector)
		{
			TBXProjects[i]=prj.getName();
			i++;
		}
	
		return TBXProjects;
		
	}
	
	public String getSelectedProjectName()
	{
		TreeItem[] selected=null;
		
		selected=prjTree.getSelection();
		return selected[0].getText();
	}
	
	public void setSelectedProject(String projectName)
	{
		this.selProject=projectName;
		this.selectProject();
	}

	private boolean isInterfaceStillInRepository(String interfaceName, String interfaceVersion) {
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
		if(interfaceName.equals("") && interfaceVersion.equals(""))
			return true; //no interface selected during service creation
		
		interf=interfDescription.getInterface(interfaceName, interfaceVersion);
		
		//returning true if Interface reference is returned by store
		return interf!=null?true:false;
	}

	public int getItemCount() {
		// TODO Auto-generated method stub
		return this.prjTree.getItemCount();
	}

	public TreeItem[] getSelection() {
		// TODO Auto-generated method stub
		return this.prjTree.getSelection();
	}

}
