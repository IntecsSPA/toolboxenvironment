package it.intecs.pisa.develenv.ui.wizards;

import java.io.File;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.develenv.model.utils.FilePathComputing;
import it.intecs.pisa.develenv.model.utils.FileSystemUtil;
import it.intecs.pisa.util.IOUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class TBX_PortalXSLImportWizard extends Wizard implements IImportWizard {

	protected static final String FOLDER_SSE_PORTAL_XSL = "SSEPortalXSL";
	protected static final String FOLDER_ADDITIONAL_RESOURCES = "AdditionalResources";
	TBX_PortalXSLImportWizardPage fileSelectionPage;
	TBX_PortalXSLServiceSelectionWizardPage serviceSelectionPage;
	
	public TBX_PortalXSLImportWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		String selProject;
		String filePath;
		IProject prj;
		IFile styleSheetFile;
		IFile commonStyleSheetFile;
		IFile massStyleSheetFile;
		IFile propertiesFile;
		IFolder additionalFolder;
		IFolder portalFolder;
		String[] resource;
		try
		{
			selProject=serviceSelectionPage.getSelectedProjectName();
			filePath=fileSelectionPage.getFilePath();
			
			prj=ToolboxEclipseProjectUtil.getProject(selProject);
			additionalFolder=prj.getFolder(FOLDER_ADDITIONAL_RESOURCES);
			if(additionalFolder.exists()==false)
				additionalFolder.create(true, true, null);
			
			portalFolder=additionalFolder.getFolder(FOLDER_SSE_PORTAL_XSL);
			if(portalFolder.exists()==false)
				portalFolder.create(true, true, null);
			
			styleSheetFile=portalFolder.getFile("SSEXSL.xsl");
			commonStyleSheetFile=portalFolder.getFile("sse_common.xsl");
			massStyleSheetFile=portalFolder.getFile("massCatalogue.xsl");
			propertiesFile=portalFolder.getFile("INTECS_TEST_OPERATION.properties");
				
			FileSystemUtil.copyFile(filePath, styleSheetFile.getLocationURI().getPath());
			
			resource=FilePathComputing.computeFilePathForBundle("com.intecs.ToolboxScript.editorFiles", "massCatalogue.xsl");
			FileSystemUtil.copyFile(resource[0], massStyleSheetFile.getLocationURI().getPath());
			
			resource=FilePathComputing.computeFilePathForBundle("com.intecs.ToolboxScript.editorFiles", "sse_common.xsl");
			FileSystemUtil.copyFile(resource[0], commonStyleSheetFile.getLocationURI().getPath());
			
			resource=FilePathComputing.computeFilePathForBundle("com.intecs.ToolboxScript.editorFiles", "INTECS_TEST_OPERATION.properties");
			FileSystemUtil.copyFile(resource[0], propertiesFile.getLocationURI().getPath());
			
			prj.refreshLocal(IResource.DEPTH_INFINITE, null);
			showWarning();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
		
	}
	
	private void showWarning() {
		final Shell s = new Shell();
		MessageBox warningDiag = new MessageBox(s, SWT.ICON_WARNING | SWT.OK);

		warningDiag.setText("Important");
		warningDiag.setMessage("The file has been successfully imported into the workspace with the name SSEXSL.xsl. It is important to not modify the structure of those files under the AdditionalResources directory. It is allowed to modify the content of the imported stylesheet.");
		warningDiag.open();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
		serviceSelectionPage=new TBX_PortalXSLServiceSelectionWizardPage(selection);
		fileSelectionPage=new TBX_PortalXSLImportWizardPage("SSE stylesheet import");
	}
	

	@Override
	public void addPages() {
		addPage(serviceSelectionPage);
		addPage(fileSelectionPage);
		
	}

}
