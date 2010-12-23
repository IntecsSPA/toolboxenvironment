package it.intecs.pisa.develenv.ui.views;


import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.xml.sax.SAXException;

import it.intecs.pisa.develenv.model.interfaces.ContentChangeNotifier;
import it.intecs.pisa.util.DOMUtil;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ExecutionTreeView extends ViewPart implements ContentChangeNotifier,IDocumentListener{
	private TextViewer viewer;
	protected Document document;
	/**
	 * The constructor.
	 */
	public ExecutionTreeView() {
		document=new Document("No Execution Tree available\nWait for breakpoint for a new tree");
		document.addDocumentListener(this);
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TextViewer(parent,SWT.H_SCROLL | SWT.V_SCROLL |SWT.MULTI);
		viewer.setDocument(document);
		viewer.setEditable(false);
		viewer.setInput(getViewSite());		
	}


	public void setText(Document doc)
	{
		viewer.setDocument(doc);
		viewer.refresh();
	}

	


	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Execution Tree View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void contentChanged(final String  newContent) {		
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable()
		{
			public void run() {
				document.set(newContent);
				/*try {
					viewer.prependAutoEditStrategy(new DefaultIndentLineAutoEditStrategy() , document.getContentType(0));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				viewer.refresh();
			}}
		);
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void documentChanged(DocumentEvent event) {
		// TODO Auto-generated method stub
		
	}
}