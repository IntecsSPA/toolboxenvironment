/**
 * 
 */
package it.intecs.pisa.develenv.ui.wizards;

import java.net.URL;

import it.intecs.pisa.develenv.ui.widgets.DirectoryInsertion;
import it.intecs.pisa.develenv.ui.widgets.FileInsertion;
import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;

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
public class TBX_ProjectExportWizardPage extends WizardPage implements Listener{
	
	private static final String TITLE_ICON_PATH = "/icons/wizards/toolbox_gear.jpg";

	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private DirectoryInsertion dirIns=null;
	private TBXProjectTree prjtree=null;

	private String selProject=null;
	
	protected TBX_ProjectExportWizardPage(String pageName) {
		super(pageName);
		this.setTitle(pageName);
		this.setDescription("Select the project to export and the directory where data shall be stored");
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
		createProjectSelection(container);
		
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
	private void createProjectSelection(Composite container) {
		GridData gd=null;
		
		if(selProject==null)
			prjtree=new TBXProjectTree(container);
		else prjtree=new TBXProjectTree(container,selProject);
		
		gd = new GridData(SWT.FILL,SWT.FILL,true,true);
		prjtree.setLayoutData(gd);
	}

	private void createDirectoryRow(Composite parent) {
		GridData gd=null;
		
		dirIns=new DirectoryInsertion(parent,SWT.NULL,"Export to:");
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		dirIns.setLayoutData(gd);
		dirIns.setEnabled(true);
		dirIns.addListener(SWT.Modify, this);
	}

	public void handleEvent(Event event) {
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

	public void setSelectedProject(String selProject) {
		this.selProject=selProject;
		
	}
	
	public String getSelectedProject() {
		return this.prjtree.getSelectedProjectName();
	}

	@Override
	public boolean isPageComplete() {
		return this.prjtree.getItemCount()>0 && super.isPageComplete();
	}

	public String getDestinationPath() {
		return this.dirIns.getSelectedDirectory();
	}


}
