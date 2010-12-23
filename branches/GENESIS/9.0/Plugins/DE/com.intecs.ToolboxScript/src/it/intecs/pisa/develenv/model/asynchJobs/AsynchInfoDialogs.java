package it.intecs.pisa.develenv.model.asynchJobs;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import java.lang.Runnable;

/**
 * This class is used to display message dialogs. Dialogs are displayed asynchronously
 * in order to avoid any problem with thread access. 
 * 
 * @author Massimiliano
 *
 */
public class AsynchInfoDialogs {
	
	/**
	 * This methos displays an Info dialog.
	 * @param title Title of the dialog
	 * @param text Text of the dialog
	 */
	public static void showInfoDialog(String title, String text)
	{
		final String infoTitle=title;
		final String infoText=text;
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run() {
				MessageDialog.openInformation(null, infoTitle, infoText);
					
			}}
		);
	}
	
	/**
	 * This methos displays a confirmation dialog.
	 * @param title Title of the dialog
	 * @param text Text of the dialog
	 */
	public static void showConfirmDialog(String title, String text)
	{
		final String infoTitle=title;
		final String infoText=text;
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run() {
				MessageDialog.openConfirm(null, infoTitle, infoText);
			}}
		);
	}
	
	/**
	 * This methos displays an Error dialog.
	 * @param title Title of the dialog
	 * @param text Text of the dialog
	 */
	public static void showErrorDialog(String title, String text)
	{
		final String infoTitle=title;
		final String infoText=text;
		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run() {
				MessageDialog.openError(null, infoTitle, infoText);
			}}
		);
	}
}
