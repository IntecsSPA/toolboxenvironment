/**
 * 
 */
package it.intecs.pisa.develenv.ui.wizards;

import java.io.File;
import java.net.URL;

import it.intecs.pisa.develenv.ui.widgets.DirectoryInsertion;
import it.intecs.pisa.develenv.ui.widgets.FileInsertion;
import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.framework.Bundle;

/**
 * @author Massimiliano
 *
 */
public class TBX_ProjectImportOldDescriptorWizardPage extends WizardPage implements Listener{
	
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";

	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private FileInsertion fileIns=null;
		
	protected TBX_ProjectImportOldDescriptorWizardPage(String pageName) {
		super(pageName);
		this.setTitle(pageName);
		this.setDescription("Select the service to import");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		GridLayout layout=null;
		GridData gd=null;
		Composite container=null;
		
		container=new Composite(parent,SWT.NULL);
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		
		container.setLayoutData(gd);
		layout=new GridLayout();
		layout.numColumns=1;
		container.setLayout(layout);
		
		createDirectoryRow(container);
				
		initialize();
		this.setControl(parent);
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
		
		this.setPageComplete(false);
	}
	

	private void createDirectoryRow(Composite parent) {
		GridData gd=null;
		
		fileIns=new FileInsertion(parent,SWT.NULL,"Descriptor:");
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		fileIns.setLayoutData(gd);
		fileIns.setEnabled(true);
		fileIns.addListener(SWT.Modify, this);
	}

	public void handleEvent(Event event) {
		File selFile;
		String serviceName;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project =null;
		
		selFile=new File(fileIns.getSelectedFile());
		serviceName=selFile.getName().substring(0,selFile.getName().indexOf('.'));
		
		wrkSpace = ResourcesPlugin.getWorkspace();
		root = wrkSpace.getRoot();
		project = root.getProject(serviceName);
		if(project.exists())
		{
			this.updateStatus("A Service with the provided name already exists.");
			return;
		}
		
		this.updateStatus(event.text);
	}
	
	/**
	 * Update status.
	 * 
	 * @param message the message
	 */
	private void updateStatus(String message) {
		this.setErrorMessage(message);
		this.setPageComplete(message == null);
	}

	public String getDescriptorPath() {
		return this.fileIns.getSelectedFile();
	}


}
