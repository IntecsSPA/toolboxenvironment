package it.intecs.pisa.develenv.ui.propertyPages;

import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectPreferences;
import it.intecs.pisa.develenv.ui.widgets.ServicePropertyComposite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PropertyPage;

public class ServiceOperationPropertyPage extends PropertyPage implements Listener{
	
	private ServicePropertyComposite properties=null;
	
	/**
	 * Constructor for SamplePropertyPage.
	 */
	public ServiceOperationPropertyPage() {
		super();
	}

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		properties=new ServicePropertyComposite(parent,(IProject) this.getElement());
			
		this.noDefaultAndApplyButton();
		properties.addListener(SWT.Modify, this);
		
		return properties;
	}
	
	
	@Override
	public boolean isValid()
	{
		//currently we only need to check ssl path
		return true;
	}
	
	@Override
	protected void performDefaults() {

	}
	

	@Override
	public boolean performOk() {
		updateServiceDescriptor();
		saveToolboxServerPreference();
		return true;
	}

	/**
	 *  This setter method is used to save the Toolbox Deploy host url into the
	 *  preference store connected to the project
	 */
	private void saveToolboxServerPreference() {
	    	IProject project=null;
		String url=null;
		
		url=properties.getToolboxHost();
		
		project=(IProject) this.getElement();
		ToolboxEclipseProjectPreferences.setToolboxHostURL(project,url);
	}

	private void updateServiceDescriptor() {
		IProject project=null;
		IFile descrFile=null;
		Service descr=null;
		
		project=(IProject) this.getElement();
		descrFile=project.getFile(ToolboxEclipseProject.FILE_SERVICE_DESCRIPTOR);
		
		descr=new Service();
		descr.loadFromFile(descrFile.getLocationURI().getPath());
		
		descr.setQueuing(properties.getQueuing());
		if(properties.needsSecureCommunication())
		{
			descr.setSSLcertificate(properties.getSSLCertificate());
		}
		else
		{
			descr.setSSLcertificate("");
		}
		
		descr.setSuspendMode(properties.getSuspendMode());
		
		descr.dumpToDisk(descrFile.getLocationURI().getPath());
	}

	public void handleEvent(Event event) {
	    this.setErrorMessage(event.text);
	    
	}
	

}