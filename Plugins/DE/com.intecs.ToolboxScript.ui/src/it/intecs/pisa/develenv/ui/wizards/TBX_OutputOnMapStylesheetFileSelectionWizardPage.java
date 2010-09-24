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
public class TBX_OutputOnMapStylesheetFileSelectionWizardPage extends WizardPage implements Listener{
	
	protected static final String WIZARD_PAGE_DESCRIPTION = "This wizard lets you import the \"Output on Map\" stylesheet and associate it with the service. Once the service is deployed TOOLBOX Runtime Environment will display item  contained in the output message on a map.";

	private static final String BUNDLE_NAME = "com.intecs.ToolboxScript.ui";
	
	private FileInsertion fileIns=null;
		
	protected TBX_OutputOnMapStylesheetFileSelectionWizardPage(String pageName) {
		super(pageName);
		this.setTitle(pageName);
		this.setDescription(WIZARD_PAGE_DESCRIPTION);
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
		this.setControl(container);
	}

	/**
	 * Initialize.
	 */

	private void initialize() {
		try {
			final Bundle bundle = Platform.getBundle(BUNDLE_NAME);
			final URL iconUrl=bundle.getEntry("/icons/wizards/new_service_page_2.jpg");	
			
			this.setImageDescriptor(ImageDescriptor.createFromURL(iconUrl));
		} catch (final Exception e) {
			//Doing nothing, wizard icon is not vital
		}
		
		this.setPageComplete(false);
	}
	

	private void createDirectoryRow(Composite parent) {
		GridData gd=null;
		
		fileIns=new FileInsertion(parent,SWT.NULL,"Stylesheet:");
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		fileIns.setLayoutData(gd);
		fileIns.setEnabled(true);
		fileIns.addListener(SWT.Modify, this);
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

	public String getFilePath() {
		return this.fileIns.getSelectedFile();
	}


}
