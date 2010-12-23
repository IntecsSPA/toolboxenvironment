package it.intecs.pisa.develenv.ui.widgets;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DirectoryInsertion extends Composite {

	private static final String LABEL_BROWSE = "Browse..";
	
	private Text dirText=null;
	private Button dirBrowseButton=null;
	private Label label=null;
	
	private String labelStr=null;

	public DirectoryInsertion(Composite parent, int style) {
		super(parent, style);
		
		init(parent);
	}
	
	public DirectoryInsertion(Composite parent, int style,String labelToDisplay) {
		super(parent, style);
		
		labelStr=labelToDisplay;
		init(parent);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		this.dirBrowseButton.setEnabled(enabled);
		this.dirText.setEnabled(enabled);
		
		if(label!=null)
			label.setEnabled(enabled);
	}

	private void init(Composite parent) {
		GridData gd=null;
		GridLayout layout = new GridLayout();
		
		if(labelStr==null)
			layout.numColumns=2;
		else layout.numColumns=3;
			
		this.setLayout(layout);
		
		if(labelStr!=null)
		{
			label=new Label(this,SWT.NULL);
			label.setText(labelStr);
		}
		
		dirText=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		dirText.setLayoutData(gd);
		dirText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				DirectoryInsertion.this.checkPath();
			}
		});
		
		dirBrowseButton=new Button(this,SWT.NULL);
		dirBrowseButton.setText(LABEL_BROWSE);
		
		dirBrowseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				DirectoryInsertion.this.browseForDir();
			}
		});
		
	}
	
	public String getSelectedDirectory()
	{
		if(dirText!=null) {
			return dirText.getText();
		} else {
			return null;
		}
	}

	private void checkPath() {
		String newPath=null;
		Event event=null;
		
		newPath=this.dirText.getText();
		final File pathFile=new File(newPath);
		
		event=new Event();
		
		
		if(pathFile.exists()==false) {
			event.text="The directory doesn't exists.";
			this.notifyListeners(SWT.Modify, event);
		} 
		
		this.notifyListeners(SWT.Modify, event);
	}
	
	
	public void browseForDir()
	{
		
		final Shell s = new Shell();
    
		final DirectoryDialog fd = new DirectoryDialog(s, SWT.OPEN);
	    fd.setText("Select directory");

	    final String selected = fd.open(); 

	    if(selected != null)
	    {
	    	this.dirText.setText(selected);
	    }
	    checkPath();	 
	}
}
