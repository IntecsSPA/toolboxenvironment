/**
 * 
 */
package it.intecs.pisa.develenv.ui.wizards;

import java.net.URL;

import it.intecs.pisa.develenv.ui.widgets.DirectoryInsertion;
import it.intecs.pisa.develenv.ui.widgets.FileInsertion;
import it.intecs.pisa.develenv.ui.widgets.TBXProjectTree;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.osgi.framework.Bundle;

/**
 * @author Massimiliano
 *
 */
public class TBX_ResourceImportSelectionWizardPage extends TBX_ImportSelectionWizardPage{

	private static final String TITLE = "Resource import wizard";

	protected TBX_ResourceImportSelectionWizardPage(String pageName) {
		super(pageName);
		this.setTitle(pageName);
		this.setDescription(TITLE);
		
		admittedFileExtension=new String[]{"*.*"};
	}

	


}
