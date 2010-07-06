package it.intecs.pisa.develenv.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class OperationParametersInsertion extends Composite {
	private Text soapAction=null;
	private Text inputType=null;
	private Text inputNameSpace=null;
	private Text outputType=null;
	private Text outputNameSpace=null;
	
	private Label soapActionLabel=null;
	private Label inputTypeLabel=null;
	private Label inputNameSpaceLabel=null;
	private Label outputTypeLabel=null;
	private Label outputNameSpaceLabel=null;
	
	public OperationParametersInsertion(Composite parent, int style) {
		super(parent, style);
		
		init(parent);
	}
	
	private void init(Composite parent) {
		GridData gd=null;
		GridLayout layout = new GridLayout();
					
		layout.numColumns=2;
		this.setLayout(layout);
		
		soapActionLabel=new Label(this,SWT.NULL);
		soapActionLabel.setText("SOAP Action");
		gd = new GridData(SWT.NULL,SWT.NULL,false,false);
		soapActionLabel.setLayoutData(gd);
		
		soapAction=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		soapAction.setLayoutData(gd);
		soapAction.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				OperationParametersInsertion.this.checkModification(e);
			}

		});
		
		inputTypeLabel=new Label(this,SWT.NULL);
		inputTypeLabel.setText("Input message type");
		gd = new GridData(SWT.NULL,SWT.NULL,false,false);
		inputTypeLabel.setLayoutData(gd);
		
		inputType=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		inputType.setLayoutData(gd);
		inputType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				OperationParametersInsertion.this.checkModification(e);
			}

		});
		
		inputNameSpaceLabel=new Label(this,SWT.NULL);
		inputNameSpaceLabel.setText("Input message namespace");
		gd = new GridData(SWT.NULL,SWT.NULL,false,false);
		inputNameSpaceLabel.setLayoutData(gd);
		
		inputNameSpace=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		inputNameSpace.setLayoutData(gd);
		inputNameSpace.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				OperationParametersInsertion.this.checkModification(e);
			}

		});
		
		outputTypeLabel=new Label(this,SWT.NULL);
		outputTypeLabel.setText("Output message type");
		gd = new GridData(SWT.NULL,SWT.NULL,false,false);
		outputTypeLabel.setLayoutData(gd);
		
		outputType=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		outputType.setLayoutData(gd);
		outputType.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				OperationParametersInsertion.this.checkModification(e);
			}

		});
		
		outputNameSpaceLabel=new Label(this,SWT.NULL);
		outputNameSpaceLabel.setText("Output message namespace");
		gd = new GridData(SWT.NULL,SWT.NULL,false,false);
		outputNameSpaceLabel.setLayoutData(gd);
		
		outputNameSpace=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		outputNameSpace.setLayoutData(gd);
		outputNameSpace.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				OperationParametersInsertion.this.checkModification(e);
			}

		});
		
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		soapAction.setEnabled(enabled);
		inputType.setEnabled(enabled);
		inputNameSpace.setEnabled(enabled);
		outputNameSpace.setEnabled(enabled);
		outputType.setEnabled(enabled);
		
		soapActionLabel.setEnabled(enabled);
		inputTypeLabel.setEnabled(enabled);
		inputNameSpaceLabel.setEnabled(enabled);
		outputNameSpaceLabel.setEnabled(enabled);
		outputTypeLabel.setEnabled(enabled);
	}
	

	protected void checkModification(ModifyEvent e) {
		Event event=null;
		
		event=new Event();
		
		this.notifyListeners(SWT.Modify, event);
	}

	public boolean isCompleted() {
		String soapActionStr=null;
		String inputTypeStr=null;
		String inputNamespaceStr=null;
		String outputTypeStr=null;
		String outputNamespaceStr=null;
		
		soapActionStr=soapAction.getText();
		inputTypeStr=inputType.getText();
		inputNamespaceStr=inputNameSpace.getText();
		outputTypeStr=outputType.getText();
		outputNamespaceStr=outputNameSpace.getText();
		
		return soapActionStr!=null && inputTypeStr!=null && inputNamespaceStr!=null &&
				soapActionStr.equals("")==false && inputTypeStr.equals("")==false && inputNamespaceStr.equals("")==false
				&& outputTypeStr!=null && outputNamespaceStr!=null &&
				outputTypeStr.equals("")==false && outputNamespaceStr.equals("")==false;
	}

	public String getSOAPAction() {
		return soapAction.getText();
	}

	public String getInputType() {
		return inputType.getText();
	}

	public String getInputNameSpace() {
		return inputNameSpace.getText();
	}


	public String getOutputType() {
		return outputType.getText();
	}


	public String getOutputNameSpace() {
		return outputNameSpace.getText();
	}
	


	
	
	
	
}
