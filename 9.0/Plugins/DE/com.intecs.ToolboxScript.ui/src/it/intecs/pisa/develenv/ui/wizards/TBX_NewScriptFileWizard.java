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
import it.intecs.pisa.util.wsdl.WSDL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;


/**
 * The Class TscriptAsynchronousNewWizard.
 */

public class TBX_NewScriptFileWizard extends Wizard implements INewWizard {
	
	private static final String WIZARD_PAGE_TITLE = "This wizard creates a new TOOLBOX script file. You must select an operation or the cmmon folder and provide a script file name.";

	/**
	 * The page.
	 */
	private TBX_NewScriptFileWizardPage page=null;
	
	/**
	 * The selection.
	 */
	private ISelection selection;

	/**
	 * The Constructor.
	 */
	public TBX_NewScriptFileWizard() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		try {
			page=new TBX_NewScriptFileWizardPage("name",this.selection);
			page.setMessage(WIZARD_PAGE_TITLE);
			this.addPage(page);
			
			
			
		} catch (final Exception e) {
			
		}
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final IFile file=page.createNewFile();

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
	private void doFinish(IFile file,final IProgressMonitor monitor) throws CoreException {
		
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
			return page.validatePage();
	}
}