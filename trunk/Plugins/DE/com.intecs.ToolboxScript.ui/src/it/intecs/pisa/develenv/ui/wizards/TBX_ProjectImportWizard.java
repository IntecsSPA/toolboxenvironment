package it.intecs.pisa.develenv.ui.wizards;

import java.io.File;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.common.tbx.servicePackage;
import it.intecs.pisa.common.tbx.exceptions.serviceAlreadyExists;
import it.intecs.pisa.common.tbx.exceptions.wrongServiceVersion;
import it.intecs.pisa.common.tbx.exceptions.zipIsNotATBXService;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.utils.InterfaceDescriptionLoader;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.SchemaSetRelocator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;
import org.w3c.dom.Document;

public class TBX_ProjectImportWizard extends Wizard implements IImportWizard {

	private TBX_ProjectImportWizardPage page;

	public TBX_ProjectImportWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		String zipFilePath = null;
		String projectName = null;
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project = null;
		servicePackage pack = null;
		IFolder schemaFolder = null;

		try {
			zipFilePath = page.getZipPath();
			wrkSpace = ResourcesPlugin.getWorkspace();
			root = wrkSpace.getRoot();

			pack = servicePackage.loadFromFile(zipFilePath);

			project = root.getProject(pack.getServiceName());
			if (project.exists())
				throw new serviceAlreadyExists();

			project.create(null);
			// project.open(null);

			pack.extractFromPackage(project.getLocationURI().getPath(), false);

			project.open(null);

			// check if the version is the latest
			if (checkImportedServiceVersion(project)) {

				// shall relocate script files and schema location
				schemaFolder = project
						.getFolder(ToolboxEclipseProject.FOLDER_SCHEMAS);

				SchemaSetRelocator.updateSchemaLocationToAbsolute(schemaFolder
						.getLocation().toFile(), schemaFolder.getLocationURI());

				// project.close(null);

				project.refreshLocal(IResource.DEPTH_INFINITE, null);
				root.refreshLocal(IResource.DEPTH_INFINITE, null);
			} else {
				project.delete(true, null);
				throw new wrongServiceVersion();
			}

		} catch (Exception e) {
			final Shell s = new Shell();
			MessageBox alert = new MessageBox(s, SWT.ICON_ERROR | SWT.OK);

			alert.setText("Error importing the TOOLBOX service");

			if (e instanceof serviceAlreadyExists)
				alert
						.setMessage("A service with the same name already exists!");
			else if (e instanceof zipIsNotATBXService)
				alert
						.setMessage("The Zip file doesn't contains a TOOLBOX service!");
			else if (e instanceof wrongServiceVersion)
				alert
						.setMessage("The Zip file contains a service whose version is not supported by this Toolbox Development Environment");

			alert.open();
		}

		return true;
	}

	private boolean checkImportedServiceVersion(IProject project) {
		IFile descriptor = null;
		Service serviceDescriptor = null;
		Document descr;
		DOMUtil util;
		String version;
		ToolboxEclipseProject eclipseProject;
		try {
			util = new DOMUtil();

			descriptor = project
					.getFile(servicePackage.FILE_SERVICE_DESCRIPTOR);

			descr = util.fileToDocument(new File(descriptor.getLocationURI()
					.getPath()));

			serviceDescriptor = new Service();
			serviceDescriptor.initializeFromXMLDescriptor(descr.getDocumentElement());

			version = serviceDescriptor.getVersion();

			if (version.equals("2.1") || version.equals("2.2"))
				return true;
			else if (version.equals("2.0")) {
				final Shell s = new Shell();
				MessageBox alert = new MessageBox(s, SWT.ICON_INFORMATION| SWT.OK | SWT.CANCEL);

				alert.setText("Confirm");
				alert.setMessage("Service project needs to be updated. Continue?");
				
				if (alert.open() == SWT.OK) {
					eclipseProject = new ToolboxEclipseProject(
							serviceDescriptor);

					boolean exitStatus=eclipseProject.convert20To21(project);
					
					if(exitStatus==true)
					{
						alert = new MessageBox(s, SWT.ICON_INFORMATION| SWT.OK);

						alert.setText("");
						alert.setMessage("The service has been imported and updated. Take a look to the operation scripts since they can have been updated.");

						alert.open();
					}
					
					return exitStatus;
				} else
					return false;
			} else
				return false;
		} catch (Exception e) {
			return false;
		} finally {
			Document doc;

			if (serviceDescriptor != null && descriptor != null) {
				doc = serviceDescriptor.createServiceDescriptor();
				try {
					DOMUtil.dumpXML(doc, new File(descriptor.getLocationURI()
							.getPath()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		page = new TBX_ProjectImportWizardPage("Select service to import");

	}

	@Override
	public void addPages() {
		// TODO Auto-generated method stub
		this.addPage(page);
	}

}
