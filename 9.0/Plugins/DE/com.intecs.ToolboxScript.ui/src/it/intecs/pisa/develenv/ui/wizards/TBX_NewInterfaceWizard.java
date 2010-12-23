/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptAsynchronousNewWizard.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.5 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/02/19 13:08:52 $
 * File ID: $Id: TscriptAsynchronousNewWizard.java,v 1.5 2007/02/19 13:08:52 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.wizards;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.InterfacesDescription;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.develenv.model.utils.FileSystemUtil;
import it.intecs.pisa.util.wsdl.WSDL;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;


/**
 * The Class TscriptAsynchronousNewWizard.
 */

public class TBX_NewInterfaceWizard extends Wizard implements INewWizard {
	
	private static final String PATH_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	private static final String PATH_INTERFACES_DEFINITION_XML = "interfacesDefinition.xml";
	/**
	 * The page.
	 */
	private TBX_NewInterfaceNameWizardPage namepage=null;
	private TBX_NewInterfaceSchemaSetSelectionWizardPage schemapage=null;
	private TBX_NewInterfaceMainSchemaSelectorWizardPage mainSchemaSelectorpage=null;
	private TBX_NewInterfaceWSDLSelectionWizardPage wsdlPage=null;
	private TBX_NewInterfaceOperationsFromWSDLWizardPage operationFromWSDLpage=null;
	
	/**
	 * The selection.
	 */
	private ISelection selection;

	/**
	 * The Constructor.
	 */
	public TBX_NewInterfaceWizard() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		try {
			this.namepage = new TBX_NewInterfaceNameWizardPage(this.selection);
			this.addPage(this.namepage);
			
			this.schemapage=new TBX_NewInterfaceSchemaSetSelectionWizardPage(this.selection);
			this.addPage(this.schemapage);
			
			this.mainSchemaSelectorpage=new TBX_NewInterfaceMainSchemaSelectorWizardPage(this.selection);
			this.addPage(this.mainSchemaSelectorpage);
			
			this.wsdlPage=new TBX_NewInterfaceWSDLSelectionWizardPage(this.selection);
			this.addPage(this.wsdlPage);
			
			this.operationFromWSDLpage=new TBX_NewInterfaceOperationsFromWSDLWizardPage(this.selection);
			this.addPage(this.operationFromWSDLpage);
			
		} catch (final Exception e) {
			
		}
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String interfaceName=this.namepage.getInterfaceName();
		final String interfaceVersion=this.namepage.getInterfaceVersion();
		final String schemaPath=this.schemapage.getSchemaRootDir();
		final String rootSchemaPath=this.mainSchemaSelectorpage.getSelected();
		final String wsdlPath=this.wsdlPage.getSelectedWSDL();
		final WSDL wsdl=this.operationFromWSDLpage.getWsdl();
		final it.intecs.pisa.common.tbx.Operation[] operationDescription=this.operationFromWSDLpage.getSelectedOperations();
		
		
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					TBX_NewInterfaceWizard.this.doFinish(interfaceName,interfaceVersion,schemaPath,rootSchemaPath,wsdlPath,operationDescription,wsdl,monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			this.getContainer().run(true, false, op);
		} catch (final InterruptedException e) {
			return false;
		} catch (final InvocationTargetException e) {
			final Throwable realException = e.getTargetException();
			MessageDialog.openError(this.getShell(), "Error", realException
					.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * Do finish.
	 * 
	 * @param operationName the operation name
	 * @param containerName the container name
	 * @param monitor the monitor
	 * 
	 * @throws CoreException the core exception
	 */
	private void doFinish(final String interfaceName,final String interfaceVersion, final String schemaPath,final String rootSchemaPath,final String wsdlPath,final it.intecs.pisa.common.tbx.Operation[] operationDescription,
			WSDL wsdl,final IProgressMonitor monitor) throws CoreException {
		InterfacesDescription interfdescription=null;
		Interface newInterf=null;
		
		String userHomeDir=null;
		File tbxConfigurationsDir=null;
		File userDefinedInterfacesFile=null;
			
		try
		{
			userHomeDir=System.getProperty("user.home");
			tbxConfigurationsDir=new File(userHomeDir,PATH_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
			if(tbxConfigurationsDir.exists())
			{
				userDefinedInterfacesFile=new File(tbxConfigurationsDir,PATH_INTERFACES_DEFINITION_XML);
			}
			else
			{
				tbxConfigurationsDir.mkdir();
				userDefinedInterfacesFile=new File(tbxConfigurationsDir,PATH_INTERFACES_DEFINITION_XML);
			}
			
			
			//Adding the new interface
			interfdescription=new InterfacesDescription();
			
			if(userDefinedInterfacesFile.exists())
				interfdescription.loadFromFile(userDefinedInterfacesFile, true);
			
			newInterf=new Interface();
			
			newInterf.setName(interfaceName);
			newInterf.setVersion(interfaceVersion);
			newInterf.setSchemaDir(schemaPath);
			newInterf.setSchemaRoot(rootSchemaPath);
			newInterf.setWsdlPath(wsdlPath);
			newInterf.setTargetNameSpace(wsdl.getTargetNameSpace());
			newInterf.setOperations(operationDescription);
						
			interfdescription.addInterface(newInterf);
			
			//creating repository.. Must be done before dumping interface to disk because
			// it may override some paths
			createRepository(newInterf, monitor);	
			
			interfdescription.dumpToDisk(userDefinedInterfacesFile);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}

	}

	private void createRepository(Interface descr, final IProgressMonitor monitor) throws CoreException {
		URI sourceFolderURI =null;
		URI destFolder=null;
		IFileStore sourceStore=null;
		IFileStore destStore=null;
		String userDefinedPath=null;
		String repositoryPath=null;
		File destDir=null;
		File repositoryDir=null;
		File fileIntoRepository=null;
		String interfaceSubDir=null;
		String path;
		String fileName;
		    
		repositoryDir=new File(System.getProperty("user.home"),PATH_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);

		interfaceSubDir=descr.getName()+"_"+descr.getVersion();
		
		//creating schema rep in user home
		userDefinedPath=descr.getSchemaDir();
		repositoryPath="schemas"+File.separatorChar+interfaceSubDir;
		descr.setSchemaDir(repositoryPath);
		
		sourceFolderURI=URIUtil.toURI(userDefinedPath);				
		sourceStore = EFS.getStore(sourceFolderURI);
		
		destDir=new File(repositoryDir,repositoryPath);
		destFolder=destDir.toURI();	
		destStore = EFS.getStore(destFolder);
		
		sourceStore.copy(destStore, EFS.OVERWRITE, monitor);
		
		for(Operation oper: descr.getOperations())
		{
		    //copying first script
		    path=oper.getScript(Script.SCRIPT_TYPE_FIRST_SCRIPT).getPath();
		    fileName=path.substring(path.lastIndexOf(File.separator)+1);
		    
		    repositoryPath="templates"+File.separatorChar+interfaceSubDir+File.separatorChar+oper.getName()+File.separatorChar+fileName;
		    fileIntoRepository=new File(repositoryDir,repositoryPath);
		    
		    FileSystemUtil.copyFile(URIUtil.toURI(path), fileIntoRepository.toURI());
		    oper.getScript(Script.SCRIPT_TYPE_FIRST_SCRIPT).setPath(repositoryPath);
		    
		    path=oper.getScript(Script.SCRIPT_TYPE_GLOBAL_ERROR).getPath();
		    fileName=path.substring(path.lastIndexOf(File.separator)+1);
		    
		    repositoryPath="templates"+File.separatorChar+interfaceSubDir+File.separatorChar+oper.getName()+File.separatorChar+fileName;
		    fileIntoRepository=new File(repositoryDir,repositoryPath);
		    
		    FileSystemUtil.copyFile(URIUtil.toURI(path), fileIntoRepository.toURI());
		    oper.getScript(Script.SCRIPT_TYPE_GLOBAL_ERROR).setPath(repositoryPath);
		    
		    if(oper.getType().equals(Operation.OPERATION_TYPE_ASYNCHRONOUS))
		    {
			//copying response builder
			path=oper.getScript(Script.SCRIPT_TYPE_RESPONSE_BUILDER).getPath();
			fileName=path.substring(path.lastIndexOf(File.separator)+1);
    		    
			repositoryPath="templates"+File.separatorChar+interfaceSubDir+File.separatorChar+oper.getName()+File.separatorChar+fileName;
			fileIntoRepository=new File(repositoryDir,repositoryPath);
    		    
			FileSystemUtil.copyFile(URIUtil.toURI(path), fileIntoRepository.toURI());
			oper.getScript(Script.SCRIPT_TYPE_RESPONSE_BUILDER).setPath(repositoryPath);
			
			//copying second file
			path=oper.getScript(Script.SCRIPT_TYPE_SECOND_SCRIPT).getPath();
			fileName=path.substring(path.lastIndexOf(File.separator)+1);
    		    
			repositoryPath="templates"+File.separatorChar+interfaceSubDir+File.separatorChar+oper.getName()+File.separatorChar+fileName;
			fileIntoRepository=new File(repositoryDir,repositoryPath);
    		    
			FileSystemUtil.copyFile(URIUtil.toURI(path), fileIntoRepository.toURI());
			oper.getScript(Script.SCRIPT_TYPE_SECOND_SCRIPT).setPath(repositoryPath);
			
			//copying third file
			path=oper.getScript(Script.SCRIPT_TYPE_THIRD_SCRIPT).getPath();
			fileName=path.substring(path.lastIndexOf(File.separator)+1);
    		    
			repositoryPath="templates"+File.separatorChar+interfaceSubDir+File.separatorChar+oper.getName()+File.separatorChar+fileName;
			fileIntoRepository=new File(repositoryDir,repositoryPath);
    		    
			FileSystemUtil.copyFile(URIUtil.toURI(path), fileIntoRepository.toURI());
			oper.getScript(Script.SCRIPT_TYPE_THIRD_SCRIPT).setPath(repositoryPath);
			
			path=oper.getScript(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER).getPath();
			fileName=path.substring(path.lastIndexOf(File.separator)+1);
    		    
			repositoryPath="templates"+File.separatorChar+interfaceSubDir+File.separatorChar+oper.getName()+File.separatorChar+fileName;
			fileIntoRepository=new File(repositoryDir,repositoryPath);
    		    
			FileSystemUtil.copyFile(URIUtil.toURI(path), fileIntoRepository.toURI());
			oper.getScript(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER).setPath(repositoryPath);
		    }
		    
		    
		}		
		
		//copying WSDl
		userDefinedPath=descr.getWsdlPath();
		repositoryPath="wsdl"+File.separatorChar+interfaceSubDir; //+File.separatorChar+descr.getName()+".wsdl";
		descr.setWsdlPath(repositoryPath+File.separatorChar+descr.getName()+".wsdl");
		
		sourceFolderURI=URIUtil.toURI(userDefinedPath);				
		sourceStore = EFS.getStore(sourceFolderURI);
		
		destDir=new File(repositoryDir,repositoryPath);
		destFolder=destDir.toURI();	
		destStore = EFS.getStore(destFolder);
		
		destStore.mkdir(0, monitor);
		
		sourceStore.copy(destStore.getChild(descr.getName()+".wsdl"), EFS.OVERWRITE, monitor);
	
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}
	
	@Override
	public boolean canFinish()
	{
			return true;
	}
}