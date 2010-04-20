/**
 * 
 */
package it.intecs.pisa.develenv.ui.widgets;

import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectPreferences;

import java.io.File;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * This class is a composite that implements a property panel with informations
 * concerning TOLBOX RE host and port for deploy/run/debug and informations
 * about service queuing and security
 * 
 * @author Massimiliano
 * 
 */
public class ServicePropertyComposite extends Composite {
	private static final String LABEL_HARD_SUSPEND_MODE = "Hard suspend mode";

	private static final String LABEL_SOFT_SUSPEND_MODE = "Soft suspend mode";
	
	private static final String LABEL_USERNAME = "Username";
	
	private static final String LABEL_PASSWORD = "Password";

	private Button checkQueueIncoming;

	private Button checkSecureCommunication;

	private Service descr = null;

	private Button hardSuspendRadio;

	private Button softSuspendRadio;

	private Text sslCertificate;

	private Text hostText;

	private Label hostLabel;

	private Button sslCertificateBrowseButton;

	private IProject project = null;

	private Label usernameLabel;

	private Text usernameText;

	private Label passwordLabel;

	private Text passwordText;

	private static final String FILE_SERVICE_DESCRIPTOR_XML = "serviceDescriptor.xml";

	private static final String LABEL_BROWSE = "Browse";

	private static final String LABEL_QUEUE_INCOMING_SERVICE_REQUESTS = "Queue incoming service requests";

	private static final String LABEL_SECURE_COMMUNICATION = "Secure communication";

	public static final Integer EVENT_OK = 0;

	public static final Integer EVENT_INVALID_DEBUG_PORT = 1;

	public static final Integer EVENT_INVALID_SSL = 2;
	
	public static final Integer EVENT_INVALID_TOOLBOX_HOST = 3;

	/**
	 * @param parent
	 * @param style
	 */
	public ServicePropertyComposite(Composite parent, IProject project) {
		super(parent, SWT.NULL);

		this.project = project;

		initControls(parent);
		if (project != null)
			initializeComponents();
	}

	private void initControls(Composite parent) {
		GridData gd;
		Label separator = null;

		GridLayout layout = new GridLayout();
		this.setLayout(layout);

		// Adding remote host controls
		Composite remoteHostContainer = new Composite(this, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		remoteHostContainer.setLayout(layout);
		remoteHostContainer.setLayoutData(gd);

		this.hostLabel = new Label(remoteHostContainer, SWT.NULL);
		this.hostLabel.setText("Toolbox host:");

		this.hostText = new Text(remoteHostContainer, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.hostText.setLayoutData(gd);
		this.hostText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				ServicePropertyComposite.this.checkToolboxHost();
			}

		});

		Composite sslCerificateContainer = new Composite(this, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		sslCerificateContainer.setLayout(layout);
		sslCerificateContainer.setLayoutData(gd);

		this.checkSecureCommunication = new Button(sslCerificateContainer,
				SWT.CHECK);
		this.checkSecureCommunication.setText(LABEL_SECURE_COMMUNICATION);
		this.checkSecureCommunication
				.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(final SelectionEvent e) {
					}

					public void widgetSelected(final SelectionEvent e) {
						ServicePropertyComposite.this.updateSSLSelection();
					}

				});

		this.sslCertificate = new Text(sslCerificateContainer, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.sslCertificate.setLayoutData(gd);
		this.sslCertificate.setEnabled(false);
		this.sslCertificate.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
				ServicePropertyComposite.this.checkSSLPath();
			}

		});

		this.sslCertificateBrowseButton = new Button(sslCerificateContainer,
				SWT.NULL);
		this.sslCertificateBrowseButton.setText(LABEL_BROWSE);
		this.sslCertificateBrowseButton.setEnabled(false);
		this.sslCertificateBrowseButton
				.addSelectionListener(new SelectionListener() {
					public void widgetDefaultSelected(final SelectionEvent e) {
					}

					public void widgetSelected(final SelectionEvent e) {
						ServicePropertyComposite.this.browseForSSLCertificate();
					}

				});

		separator = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		separator.setLayoutData(gd);

		this.checkQueueIncoming = new Button(this, SWT.CHECK);
		this.checkQueueIncoming.setText(LABEL_QUEUE_INCOMING_SERVICE_REQUESTS);

		this.softSuspendRadio = new Button(this, SWT.RADIO);
		this.softSuspendRadio.setText(LABEL_SOFT_SUSPEND_MODE);
		softSuspendRadio.setSelection(true);

		this.hardSuspendRadio = new Button(this, SWT.RADIO);
		this.hardSuspendRadio.setText(LABEL_HARD_SUSPEND_MODE);
		
		this.usernameLabel = new Label(remoteHostContainer, SWT.NULL);
		this.usernameLabel.setText(LABEL_USERNAME);

		this.usernameText = new Text(remoteHostContainer, SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.usernameText.setLayoutData(gd);
		this.usernameText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
			}
		});
		
		this.passwordLabel = new Label(remoteHostContainer, SWT.NULL);
		this.passwordLabel.setText(LABEL_PASSWORD);

		this.passwordText = new Text(remoteHostContainer, SWT.BORDER|SWT.PASSWORD);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.passwordText.setLayoutData(gd);
		this.passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(final ModifyEvent e) {
			}
		});

	}
	
	/**
	 * This method initializes all components that builds the composite
	 */
	private void initializeComponents() {
		IFile descrFile = null;
		String sslCertificate = null;
		String url = null;
		String username = null;
		String password = null;
		
		// retrieving deploy host url from preferences
		url = ToolboxEclipseProjectPreferences.getToolboxHostURL(project);
		username =ToolboxEclipseProjectPreferences.getToolboxHostUsername(project);
		password= ToolboxEclipseProjectPreferences.getToolboxHostPassword(project);
		
		hostText.setText(url);
		usernameText.setText(username);
		passwordText.setText(password);
		
		// loading other prefernces from service descriptor.
		descrFile = project.getFile(FILE_SERVICE_DESCRIPTOR_XML);
		if ((descrFile != null) && descrFile.exists()) {
			this.descr = new Service();
			this.descr.loadFromFile(descrFile.getLocationURI().getPath());

			this.checkQueueIncoming.setSelection(this.descr.isQueuing());

			sslCertificate = this.descr.getSSLcertificate();
			if ((sslCertificate != null)
					&& (sslCertificate.equals("") == false)) {
				this.checkSecureCommunication.setSelection(true);
				this.sslCertificate.setEnabled(true);
				this.sslCertificate.setText(sslCertificate);
				this.sslCertificateBrowseButton.setEnabled(true);
			}

			if (this.descr.getSuspendMode().equals(
					Service.SUSPEND_MODE_HARD)) {
				this.hardSuspendRadio.setSelection(true);
				this.softSuspendRadio.setSelection(false);
			} else {
				this.hardSuspendRadio.setSelection(false);
				this.softSuspendRadio.setSelection(true);
			}
		}
	}
	
	/**
	 * This method is an handler that notifies listeners when the Toolbox host is
	 * not valid
	 */
	protected void checkToolboxHost() {
		String hostStr;
	    Event event=null;
		
	    event=new Event();
	    
	    hostStr=this.hostText.getText();
	    if(hostStr.startsWith("http://")&&hostStr.endsWith("/TOOLBOX"))
		this.notifyListeners(SWT.Modify, event);
	    else {
		event.text="Toolbox host shall be http://<host>:<port>/TOOLBOX";
		this.notifyListeners(SWT.Modify, event);
	    }
	    
	   /* if(hostStr.matches("http:///TOOLBOX"))
		this.notifyListeners(SWT.Modify, event);
	    else {
		event.text="Toolbox host shall be http://<host>:<port>/TOOLBOX";
		this.notifyListeners(SWT.Modify, event);
	    }*/

	}

	protected void checkSelection(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	private void browseForSSLCertificate() {
		if (this.sslCertificateBrowseButton.isEnabled()) {
			final Shell s = new Shell();

			final FileDialog fd = new FileDialog(s, SWT.OPEN);
			fd.setText("Open");
			final String[] filterExt = { "*.*" };
			fd.setFilterExtensions(filterExt);
			final String selected = fd.open();

			this.sslCertificate.setText(selected);
		}
	}

	protected void checkSSLPath() {
		Event event = null;

		event = new Event();

		if (this.isSSLPathValid() == true) {
			event.data = EVENT_OK;
			this.notifyListeners(SWT.Modify, event);
		} else {
			event.data = EVENT_INVALID_SSL;
			event.text = "SSL path is invalid";
			this.notifyListeners(SWT.Modify, event);
		}
	}

	

	protected boolean isSSLPathValid() {
		String newPath;
		File pathFile;

		newPath = this.sslCertificate.getText();
		if ((newPath != null) && (newPath.equals("") == false)) {
			pathFile = new File(newPath);

			if ((pathFile != null)
					&& ((pathFile.exists() == false) || (pathFile.isFile() == false))) {
				return false;
			}
		}
		return true;
	}

	private void updateSSLSelection() {
		final boolean selectionStatus = this.checkSecureCommunication
				.getSelection();

		this.sslCertificate.setEnabled(selectionStatus);
		this.sslCertificateBrowseButton.setEnabled(selectionStatus);
	}

	public boolean getQueuing() {
		return this.checkQueueIncoming.getSelection();
	}

	public void setQueuing(boolean queuing) {
		this.checkQueueIncoming.setSelection(queuing);
	}

	public boolean needsSecureCommunication() {
		return this.checkSecureCommunication.getSelection();
	}

	public void setNeedSecureCommunication(boolean secure) {
		this.checkSecureCommunication.setSelection(secure);
	}

	public String getSSLCertificate() {
		return this.sslCertificate.getText();
	}

	public void setSSLCertificate(String sslCertificate) {
		this.sslCertificate.setText(sslCertificate);
		if (sslCertificate == null || sslCertificate.equals(""))
			this.checkSecureCommunication.setSelection(false);
		else
			this.checkSecureCommunication.setSelection(true);
	}

	public String getSuspendMode() {
		if (this.hardSuspendRadio.getSelection()) {
			return Service.SUSPEND_MODE_HARD;
		} else {
			return Service.SUSPEND_MODE_SOFT;
		}
	}

	public void setSuspendMode(String mode) {
		if (mode.equals(Service.SUSPEND_MODE_HARD)) {
			this.hardSuspendRadio.setSelection(true);
			this.softSuspendRadio.setSelection(false);
		} else {
			this.hardSuspendRadio.setSelection(false);
			this.softSuspendRadio.setSelection(true);
		}

	}

	/**
	 * This method is used to determine wheter or not the inserted TOOLBOX host
	 * is valid. The inserted URL is not checked in order to determine if
	 * correspond to a TOOLBOX excpetion.
	 * 
	 * @return true or false the URL is valid
	 */
	protected boolean isToolboxHostValid() {
		URL url = null;
		String host = null;

		try {
			host = this.getToolboxHost();

			if (host != null && host.equals("") == false) {
				url = new URL(host);
				return true;
			} else
				return true;
		} catch (Exception e) {
			return false;
		}

	}

	public String getToolboxHost() {
		return hostText.getText();
	}

	/*public void setToolboxHost(String host) {
		hostText.setText(host);
	}*/

	/**
	 * This method is used to determine if all data inserted is valid
	 * 
	 * @return true if data is valid, false otherwise
	 */
	public boolean isValid() {
		return isSSLPathValid() && isToolboxHostValid();

	}

	public String getToolboxAuthUsername() {
		// TODO Auto-generated method stub
		return usernameText.getText();
	}
	
	public String getToolboxAuthPassword() {
		// TODO Auto-generated method stub
		return passwordText.getText();
	}
}
