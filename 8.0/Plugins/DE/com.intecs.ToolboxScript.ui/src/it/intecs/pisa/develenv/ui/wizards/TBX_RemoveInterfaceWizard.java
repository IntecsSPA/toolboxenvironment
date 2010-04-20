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

import it.intecs.pisa.util.DOMUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.LinkedList;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * The Class TscriptAsynchronousNewWizard.
 */

public class TBX_RemoveInterfaceWizard extends Wizard implements INewWizard {
	
	private static final String ATTRIBUTE_VERSION = "version";


	private static final String ATTRIBUTE_NAME = "name";


	private static final String JAVA_PROPERTY_USER_HOME = "user.home";


	private static final String TAG_INTERFACE = "interface";


	/**
	 * The page.
	 */
	private TBX_RemoveInterfaceWizardPage selectpage=null;
	
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	private static final String PATH_INTERFACES_DEFINITION_XML = "interfacesDefinition.xml";
	
	/**
	 * The selection.
	 */
	private ISelection selection;

	/**
	 * The Constructor.
	 */
	public TBX_RemoveInterfaceWizard() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		try {
			this.selectpage = new TBX_RemoveInterfaceWizardPage(this.selection);
			this.addPage(this.selectpage);
			
		
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String interfaceName=this.selectpage.getSelectedInterfaceName();
		final String interfaceVersion=this.selectpage.getSelectedInterfaceVersion();
		
		
		
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					TBX_RemoveInterfaceWizard.this.doFinish(interfaceName,interfaceVersion,monitor);
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
	private void doFinish(final String interfaceName,final String interfaceVersion,final IProgressMonitor monitor) throws CoreException {
		String userHomeDir=null;
		File tbxConfigurationsDir=null;
		File userDefinedInterfacesFile=null;
		File repositoryDir=null;
		Document userDefinedInterfacesDoc=null;
		Element interfaceToRemove=null;
		Element ithTag=null;
		LinkedList children=null;
		DOMUtil domutil=null;
		Element documentElement=null;
		
		try
		{
			domutil=new DOMUtil();
			
			userHomeDir=System.getProperty(JAVA_PROPERTY_USER_HOME);
			tbxConfigurationsDir=new File(userHomeDir,FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
			if(tbxConfigurationsDir.exists())
			{
				userDefinedInterfacesFile=new File(tbxConfigurationsDir,PATH_INTERFACES_DEFINITION_XML);
				userDefinedInterfacesDoc=domutil.fileToDocument(userDefinedInterfacesFile);
				
				documentElement = userDefinedInterfacesDoc.getDocumentElement();
				children=DOMUtil.getChildrenByTagName(documentElement, TAG_INTERFACE);
				
				for(int i=0;i<children.size();i++)
				{
					ithTag=(Element)children.get(i);
					
					if(ithTag.getAttribute(ATTRIBUTE_NAME).equals(interfaceName) &&
					   ithTag.getAttribute(ATTRIBUTE_VERSION).equals(interfaceVersion))
					{
						interfaceToRemove=ithTag;
					}					
				}
				
				if(interfaceToRemove!=null)
				{
					//removing interface from XML document
					documentElement.removeChild(interfaceToRemove);
					DOMUtil.dumpXML(userDefinedInterfacesDoc, userDefinedInterfacesFile);
				
					String interfaceSubDir=null;
					
					interfaceSubDir=interfaceName+"_"+interfaceVersion;
					//removing interface files
					repositoryDir = new File(System.getProperty("user.home"),FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
					
					//removing wsdl
					File toDelete = new File(repositoryDir,"wsdl"+File.separatorChar+interfaceSubDir);
					URI toDeleteURI = toDelete.toURI();
					IFileStore toDeleteStore = EFS.getStore(toDeleteURI);
					toDeleteStore.delete(0, monitor);
					
					//removing schemas
					toDelete = new File(repositoryDir,"schemas"+File.separatorChar+interfaceSubDir);
					toDeleteURI = toDelete.toURI();
					toDeleteStore = EFS.getStore(toDeleteURI);
					toDeleteStore.delete(0, monitor);
					
					//removing schemas
					toDelete = new File(repositoryDir,"templates"+File.separatorChar+interfaceSubDir);
					toDeleteURI = toDelete.toURI();
					toDeleteStore = EFS.getStore(toDeleteURI);
					toDeleteStore.delete(0, monitor);
					
				}
			}

		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.selection = selection;
	}
}