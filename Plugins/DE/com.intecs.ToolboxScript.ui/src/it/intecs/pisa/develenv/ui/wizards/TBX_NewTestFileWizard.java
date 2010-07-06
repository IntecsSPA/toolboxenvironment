package it.intecs.pisa.develenv.ui.wizards;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Vector;

import it.intecs.pisa.common.stream.XmlDirectivesFilterStream;
import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;
import it.intecs.pisa.develenv.model.utils.FilePathComputing;
import it.intecs.pisa.util.DOMUtil;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;

public class TBX_NewTestFileWizard extends Wizard implements INewWizard {

	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";
	
	private TBX_NewTestFileProjectSelectionWizardPage projectPage=null;
	private TBX_NewTestFileOperationSelectionWizardPage operationPage=null;
	private TBX_NewTestFileNameInsertionWizardPage namePage=null;
	
	public TBX_NewTestFileWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		InputStream input=null;
		IFile file = null;
		IProject project = null;
		IFolder testFolder = null;
		IFolder testOperationFolder = null;
		boolean addHeader=false;
		
		try {
			project=getProject(getSelectedProject());
			
			testFolder = project.getFolder(ToolboxEclipseProject.FOLDER_TEST_DIR);
			testOperationFolder = testFolder.getFolder(getSelectedOperation());
			if(!testOperationFolder.exists())
				testOperationFolder.create(true, true, null);
			
			file = testOperationFolder.getFile(namePage.getFileName());
						
			addHeader=this.namePage.isSOAPEnvelopeNeeded();
			input=getStreamForTestFile(addHeader);
			
			file.create(input, true,null);
			
			DOMUtil.fileIndent(file.getLocationURI().getPath());
		} catch (Exception e) {
			
		}
		
		
		return true;
	}

	private IProject getProject(String projectName) {
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root = null;
		IProject project = null;
		
		wrkSpace = ResourcesPlugin.getWorkspace();
		root = wrkSpace.getRoot();
		project = root.getProject(projectName);
		
		return project;
	}
	
	
	private InputStream getStreamForTestFile(boolean addHeader)
	{
		IProject project = null;
		IFile serviceDescriptor=null;
		InputStream finalStream=null;
		
		Service descriptor=null;
		
		try {
			project=getProject(getSelectedProject());
			serviceDescriptor=project.getFile("serviceDescriptor.xml");
			
			descriptor=new Service();
			descriptor.loadFromFile(serviceDescriptor.getLocationURI().getPath());
			
			finalStream=ToolboxEclipseProject.getTestFileStream(descriptor.getServiceName(),descriptor.getImplementedInterface(),getSelectedOperation(), addHeader);
					
			return finalStream;
		} catch (Exception e) {
			return null;
		}
	}

	
	
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		projectPage=new TBX_NewTestFileProjectSelectionWizardPage(selection);
		operationPage=new TBX_NewTestFileOperationSelectionWizardPage(selection);
		namePage=new TBX_NewTestFileNameInsertionWizardPage(selection);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		addPage(projectPage);
		addPage(operationPage);
		addPage(namePage);
	}
	
	public String getSelectedProject()
	{
		return projectPage.getSelectedProjectName();
	}
	
	public String getSelectedOperation()
	{
		return operationPage.getSelectedOperationName();
	}

}
