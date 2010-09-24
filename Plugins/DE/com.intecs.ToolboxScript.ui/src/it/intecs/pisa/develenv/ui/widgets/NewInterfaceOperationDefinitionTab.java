/**
 * 
 */
package it.intecs.pisa.develenv.ui.widgets;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.util.wsdl.Binding;
import it.intecs.pisa.util.wsdl.BoundedOperation;
import it.intecs.pisa.util.wsdl.SOAPBoundedOperation;
import it.intecs.pisa.util.wsdl.WSDL;

import java.util.Enumeration;
import java.util.Hashtable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Massimiliano
 * 
 */
public class NewInterfaceOperationDefinitionTab extends Composite {
	private FileInsertion responseBuilderFileInsertion;

	private FileInsertion firstScriptFileInsertion;

	private FileInsertion secondScriptFileInsertion;

	private FileInsertion thirdScriptFileInsertion;
	
	private FileInsertion globalErrorFileInsertion;
	
	private FileInsertion errorOnResponseBuilderFileInsertion;

	private Button enabledCheckbox = null;

	private Button asynchCheckbox = null;

	private Label bindingLabel = null;

	private Combo bindingsCombo = null;

	private Label asynchBindingLabel = null;

	private Combo asynchBindingsCombo = null;

	private WSDL wsdl = null;

	private String operationName = null;

	private String[] soapActions = null;

	private String[] bindingNames = null;

	public NewInterfaceOperationDefinitionTab(final Composite parent,
			final String operationName, final WSDL wsdl) {
		super(parent, SWT.BORDER);
		GridData gd;

		this.wsdl = wsdl;
		this.operationName = operationName;

		// This is the page container
		final GridLayout mainLayout = new GridLayout();
		this.setLayout(mainLayout);

		mainLayout.numColumns = 2;

		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.setLayoutData(gd);

		// Adding enabled checkbox
		this.addEnabledCheckbox();
		// Adding bindings
		this.addBindings();

		this.addAsynchronousCheckbox();

		this.addCallbackBindings();

		this.addTemplatePathControls();
	}

	private void addEnabledCheckbox() {
		GridData gd;
		this.enabledCheckbox = new Button(this, SWT.CHECK);
		this.enabledCheckbox.setText("Enabled");
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		this.enabledCheckbox.setLayoutData(gd);

		this.enabledCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			public void widgetSelected(final SelectionEvent e) {
				NewInterfaceOperationDefinitionTab.this
						.checkEnabledSelection(e);
			}
		});
	}

	protected void checkEnabledSelection(final SelectionEvent e) {
		// TODO Auto-generated method stub
		boolean state = false;
		boolean asynchState = false;
		boolean templateState = false;

		state = this.enabledCheckbox.getSelection();
		asynchState = this.asynchCheckbox.getSelection();
		// templateState=this.templateCheckbox.getSelection();

		this.bindingLabel.setEnabled(state);
		this.bindingsCombo.setEnabled(state);
		this.asynchCheckbox.setEnabled(state);

		this.asynchBindingLabel.setEnabled(state && asynchState);
		this.asynchBindingsCombo.setEnabled(state && asynchState);

		this.globalErrorFileInsertion.setEnabled(state);
		this.firstScriptFileInsertion.setEnabled(state);
		
		/*
		 * this.templateCheckbox.setEnabled(state);
		 * this.templatePath.setEnabled(state&&templateState);
		 * this.templatePathButton.setEnabled(state&&templateState);
		 */
	}

	private void addAsynchronousCheckbox() {
		GridData gd;

		this.asynchCheckbox = new Button(this, SWT.CHECK);
		this.asynchCheckbox.setText("Asynchronous");
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		gd.horizontalSpan = 2;
		this.asynchCheckbox.setLayoutData(gd);

		this.asynchCheckbox.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			public void widgetSelected(final SelectionEvent e) {
				NewInterfaceOperationDefinitionTab.this
						.checkAsynchronousSelection(e);
			}
		});

		this.asynchCheckbox.setEnabled(false);
	}

	protected void checkAsynchronousSelection(final SelectionEvent e) {
		// TODO Auto-generated method stub
		Button selectedButton = null;
		boolean state = false;

		selectedButton = (Button) e.getSource();
		state = selectedButton.getSelection();

		this.asynchBindingLabel.setEnabled(state);
		this.asynchBindingsCombo.setEnabled(state);

		this.responseBuilderFileInsertion.setEnabled(state);
		this.secondScriptFileInsertion.setEnabled(state);
		this.thirdScriptFileInsertion.setEnabled(state);
		this.errorOnResponseBuilderFileInsertion.setEnabled(state);
	}

	private void addCallbackBindings() {
		GridData gd;
		int operationSize = 0;
		BoundedOperation[] boundedOperations = null;
		Binding[] bindings = null;
		String[] bindingStrArray = null;
		int i = 0;

		// creating and setting components
		this.asynchBindingLabel = new Label(this, SWT.NULL);
		this.asynchBindingLabel.setText("Callback SOAP Binding:");

		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.asynchBindingLabel.setLayoutData(gd);
		this.asynchBindingLabel.setEnabled(false);

		this.asynchBindingsCombo = new Combo(this, SWT.BORDER | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.asynchBindingsCombo.setLayoutData(gd);
		this.asynchBindingsCombo.setEnabled(false);

		this.asynchBindingsCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			public void widgetSelected(final SelectionEvent e) {
				NewInterfaceOperationDefinitionTab.this
						.checkBindingSelection(e);
			}
		});

		// building soap action array
		bindings = this.wsdl.getBindings();
		for (Binding element : bindings) {
			for (BoundedOperation bop : element.getBoundedOperations()) {
				if (bop instanceof SOAPBoundedOperation)
					operationSize++;
			}

		}

		bindingStrArray = new String[operationSize];

		for (Binding element : bindings) {
			boundedOperations = element.getBoundedOperations();

			for (BoundedOperation bop : boundedOperations) {
				if (bop instanceof SOAPBoundedOperation) {
					bindingStrArray[i] = element.getName() + ":"
							+ ((SOAPBoundedOperation) bop).getSOAPAction();
					i++;
				}
			}
		}
		this.asynchBindingsCombo.setItems(bindingStrArray);

		if (operationSize > 0) {
			this.asynchBindingsCombo.select(0);
		}

	}

	protected void checkBindingSelection(final SelectionEvent e) {
		// TODO Auto-generated method stub
		int bindingIndex = -1;
		int asynchBinding = -1;

		bindingIndex = this.bindingsCombo.getSelectionIndex();
		asynchBinding = this.asynchBindingsCombo.getSelectionIndex();

		if (this.asynchCheckbox.getSelection()
				&& (bindingIndex == asynchBinding)) {
			Event event = null;
			Composite parent = null;

			event = new Event();
			event.text = "SOAP and Callback bindings shall be different!";

			parent = this.getParent();
			parent.notifyListeners(SWT.Selection, event);

		}
	}

	private void addBindings() {
		GridData gd;
		Hashtable opData = null;
		BoundedOperation[] boundedOperations = null;
		String action = null;
		int operationSize = 0;

		this.bindingLabel = new Label(this, SWT.NULL);
		this.bindingLabel.setText("SOAP Binding:");

		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.bindingLabel.setLayoutData(gd);
		this.bindingLabel.setEnabled(false);

		this.bindingsCombo = new Combo(this, SWT.BORDER | SWT.DROP_DOWN
				| SWT.READ_ONLY);
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.bindingsCombo.setLayoutData(gd);
		this.bindingsCombo.setEnabled(false);

		this.bindingsCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			public void widgetSelected(final SelectionEvent e) {
				NewInterfaceOperationDefinitionTab.this
						.checkBindingSelection(e);
			}
		});

		final Binding[] bindings = this.wsdl.getBindings();
		for (Binding element : bindings) {
			for (BoundedOperation bop : element.getBoundedOperations()) {
				if (bop instanceof SOAPBoundedOperation)
					operationSize++;
			}
		}

		final String[] bindingStrArray = new String[operationSize];
		this.soapActions = new String[operationSize];
		this.bindingNames = new String[operationSize];

		int j = 0;

		for (Binding element : bindings) {
			boundedOperations = element.getBoundedOperations();

			for (BoundedOperation bop : boundedOperations) {
				if (bop instanceof SOAPBoundedOperation) {
					soapActions[j] = ((SOAPBoundedOperation) bop)
							.getSOAPAction();
					bindingNames[j] = element.getName();

					bindingStrArray[j] = bindingNames[j] + ":" + soapActions[j];
					j++;
				}
			}
		}
		this.bindingsCombo.setItems(bindingStrArray);

		if (operationSize > 0) {
			this.bindingsCombo.select(0);
		}

	}

	protected void checkTemplateSelection(final SelectionEvent e) {
		// TODO Auto-generated method stub
		Button selectedButton = null;
		boolean state = false;

		selectedButton = (Button) e.getSource();
		state = selectedButton.getSelection();

		/*
		 * this.templatePath.setEnabled(state);
		 * this.templatePathButton.setEnabled(state);
		 */
	}

	private void addTemplatePathControls() {
		Composite panel = null;
		GridLayout layout = null;
		GridData gd;

		panel = new Composite(this, SWT.NULL);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		panel.setLayoutData(gd);

		layout = new GridLayout();
		panel.setLayout(layout);

		this.responseBuilderFileInsertion = new FileInsertion(panel, SWT.NULL,
				"Response builder file:");
		this.firstScriptFileInsertion = new FileInsertion(panel, SWT.NULL,
				"First script file:");
		this.secondScriptFileInsertion = new FileInsertion(panel, SWT.NULL,
				"Second script file:");
		this.thirdScriptFileInsertion = new FileInsertion(panel, SWT.NULL,
				"Third script file:");
		
		this.errorOnResponseBuilderFileInsertion= new FileInsertion(panel, SWT.NULL,
		"Error on response builder file:");
		
		this.globalErrorFileInsertion=new FileInsertion(panel, SWT.NULL,
		"Global error file:");

		this.responseBuilderFileInsertion.setEnabled(false);
		this.firstScriptFileInsertion.setEnabled(false);
		this.secondScriptFileInsertion.setEnabled(false);
		this.thirdScriptFileInsertion.setEnabled(false);
		this.errorOnResponseBuilderFileInsertion.setEnabled(false);
		this.globalErrorFileInsertion.setEnabled(false);
		
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.responseBuilderFileInsertion.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.firstScriptFileInsertion.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.secondScriptFileInsertion.setLayoutData(gd);

		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.thirdScriptFileInsertion.setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.errorOnResponseBuilderFileInsertion.setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.NULL, true, false);
		this.globalErrorFileInsertion.setLayoutData(gd);
		/*
		 * GridData gd; Composite panel=null; GridLayout layout = null;
		 * 
		 * 
		 * gd=new GridData(SWT.FILL,SWT.NULL,true,false); gd.horizontalSpan=2;
		 * panel.setLayoutData(gd);
		 * 
		 * layout=new GridLayout(); layout.marginWidth=0; layout.numColumns=8;
		 * 
		 * panel.setLayout(layout);
		 * 
		 * //creating template checkbox this.templateCheckbox=new
		 * Button(panel,SWT.CHECK); this.templateCheckbox.setText("Template");
		 * this.templateCheckbox.setEnabled(false);
		 * 
		 * gd=new GridData(SWT.NULL,SWT.FILL,false,true); gd.horizontalSpan=1;
		 * this.templateCheckbox.setLayoutData(gd);
		 * 
		 * this.templateCheckbox.addSelectionListener(new SelectionListener() {
		 * public void widgetDefaultSelected(final SelectionEvent e) { }
		 * 
		 * public void widgetSelected(final SelectionEvent e){
		 * NewInterfaceOperationDefinitionTab.this.checkTemplateSelection(e); }
		 * });
		 * 
		 * this.templateCheckbox.setEnabled(false);
		 * 
		 * this.templatePath=new Text(panel,SWT.BORDER);
		 * this.templatePath.setText("");
		 * 
		 * gd=new GridData(SWT.FILL,SWT.FILL,true,true); gd.horizontalSpan=6;
		 * this.templatePath.setLayoutData(gd);
		 * this.templatePath.setEnabled(false);
		 * 
		 * this.templatePathButton=new Button(panel,SWT.NULL);
		 * this.templatePathButton.setText("Browse.."); gd=new
		 * GridData(SWT.NULL,SWT.FILL,false,true); gd.horizontalSpan=1;
		 * this.templatePathButton.setLayoutData(gd);
		 * this.templatePathButton.setEnabled(false);
		 * 
		 * 
		 * this.templatePathButton.addSelectionListener(new SelectionListener() {
		 * public void widgetDefaultSelected(final SelectionEvent e) { }
		 * 
		 * public void widgetSelected(final SelectionEvent e){
		 * NewInterfaceOperationDefinitionTab.this.templatePathButtonSelected(e); }
		 * });
		 */

	}

	/*
	 * protected void templatePathButtonSelected(SelectionEvent e) { // TODO
	 * Auto-generated method stub final Shell s = new Shell();
	 * 
	 * final DirectoryDialog fd = new DirectoryDialog(s, SWT.OPEN);
	 * fd.setText("Select template directory");
	 * 
	 * final String selected = fd.open();
	 * 
	 * if(selected!=null) { //this.templatePath.setText(selected); }
	 *  }
	 */

	public boolean isAsynchronous() {
		return this.asynchCheckbox.getSelection();
	}

	@Override
	public boolean isEnabled() {
		return this.enabledCheckbox.getSelection();
	}

	/*
	 * public boolean isOperationDefinitionComplete() { return false; }
	 */

	public String getOperationName() {
		return this.operationName;
	}

	public String getSOAPAction() {
		String action = null;

		action = this.soapActions[this.bindingsCombo.getSelectionIndex()];

		return action;
	}

	public String getCallbackSOAPAction() {
		String action = null;

		if (this.isAsynchronous()) {
			action = this.soapActions[this.asynchBindingsCombo
					.getSelectionIndex()];
		}
		return action;
	}

	public String getBinding() {
		return bindingNames[bindingsCombo.getSelectionIndex()];
	}

	public String getCallbackBinding() {
		return bindingNames[asynchBindingsCombo.getSelectionIndex()];
	}

	public Hashtable<String, String> getScriptFiles() {
		Hashtable<String, String> table;

		table = new Hashtable<String, String>();
		table.put(Script.SCRIPT_TYPE_FIRST_SCRIPT,
				this.firstScriptFileInsertion.getSelectedFile());

		table.put(Script.SCRIPT_TYPE_GLOBAL_ERROR, this.globalErrorFileInsertion.getSelectedFile());
		
		if (isAsynchronous()) {
			table.put(Script.SCRIPT_TYPE_RESPONSE_BUILDER,
					this.responseBuilderFileInsertion.getSelectedFile());
			table.put(Script.SCRIPT_TYPE_SECOND_SCRIPT,
					this.secondScriptFileInsertion.getSelectedFile());
			table.put(Script.SCRIPT_TYPE_THIRD_SCRIPT,
					this.thirdScriptFileInsertion.getSelectedFile());
			table.put(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER, this.errorOnResponseBuilderFileInsertion.getSelectedFile());
		}
		return table;
	}

}
