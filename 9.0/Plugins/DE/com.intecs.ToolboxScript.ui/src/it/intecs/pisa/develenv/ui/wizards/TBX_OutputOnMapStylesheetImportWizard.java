package it.intecs.pisa.develenv.ui.wizards;

import it.intecs.pisa.common.tbx.exceptions.serviceAlreadyExists;
import it.intecs.pisa.common.tbx.exceptions.wrongServiceVersion;
import it.intecs.pisa.common.tbx.exceptions.zipIsNotATBXService;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;
import it.intecs.pisa.develenv.model.utils.FileSystemUtil;

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

public class TBX_OutputOnMapStylesheetImportWizard extends Wizard implements IImportWizard {

	protected static final String FOLDER_OUTPUT_ON_MAP_XSL = "OutputOnMap";
	protected static final String FOLDER_ADDITIONAL_RESOURCES = "AdditionalResources";
	
	TBX_OutputOnMapStylesheetFileSelectionWizardPage fileSelectionPage;
	TBX_OutputOnMapServiceSelectionWizardPage serviceSelectionPage;
	
	public TBX_OutputOnMapStylesheetImportWizard() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean performFinish() {
		String selProject;
		String filePath;
		IProject prj;
		IFile styleSheetFile;
		IFolder additionalFolder;
		IFolder portalFolder;
		try
		{
			selProject=serviceSelectionPage.getSelectedProjectName();
			filePath=fileSelectionPage.getFilePath();
			
			prj=ToolboxEclipseProjectUtil.getProject(selProject);
			additionalFolder=prj.getFolder(FOLDER_ADDITIONAL_RESOURCES);
			if(additionalFolder.exists()==false)
				additionalFolder.create(true, true, null);
			
			portalFolder=additionalFolder.getFolder(FOLDER_OUTPUT_ON_MAP_XSL);
			if(portalFolder.exists()==false)
				portalFolder.create(true, true, null);
			
			styleSheetFile=portalFolder.getFile("OUTPUTONMAPXSL.xsl");
						
			FileSystemUtil.copyFile(filePath, styleSheetFile.getLocationURI().getPath());

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
		warningDiag.setMessage("The file has been successfully imported into the workspace with the name OUTPUTONMAPXSL.xsl. It is important to not modify the structure of those files under the AdditionalResources directory. It is allowed to modify the content of the imported stylesheet.");
		warningDiag.open();
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
		serviceSelectionPage=new TBX_OutputOnMapServiceSelectionWizardPage(selection);
		fileSelectionPage=new TBX_OutputOnMapStylesheetFileSelectionWizardPage("\"Output on Map\" stylesheet import");
	}
	

	@Override
	public void addPages() {
		addPage(serviceSelectionPage);
		addPage(fileSelectionPage);
		
	}

}
