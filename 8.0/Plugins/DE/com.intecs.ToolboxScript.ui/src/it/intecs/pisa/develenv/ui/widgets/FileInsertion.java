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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileInsertion extends Composite {
	private static final String LABEL_BROWSE = "Browse..";
	
	private Text fileText=null;
	private Button fileBrowseButton=null;
	private Label label=null;
	
	private String labelStr=null;
	private String[] admittedExtensions=null;
	
	public FileInsertion(Composite parent, int style) {
		super(parent, style);
		
		init(parent);
	}
	
	public FileInsertion(Composite parent, int style,String labelToDisplay) {
		super(parent, style);
		
		labelStr=labelToDisplay;
		init(parent);
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
		
		this.fileBrowseButton.setEnabled(enabled);
		this.fileText.setEnabled(enabled);
		
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
		
		fileText=new Text(this,SWT.BORDER);
		gd = new GridData(SWT.FILL,SWT.NULL,true,false);
		fileText.setLayoutData(gd);
		fileText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				
				FileInsertion.this.checkPath();
			}
		});
		
		fileBrowseButton=new Button(this,SWT.NULL);
		fileBrowseButton.setText(LABEL_BROWSE);
		
		fileBrowseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent e) {
			}
			
			public void widgetSelected(final SelectionEvent e){
				FileInsertion.this.browseForFile();
			}
		});
		
	}
	
	public String getSelectedFile()
	{
		if(fileText!=null) {
			return fileText.getText();
		} else {
			return null;
		}
	}

	private void checkPath() {
		String newPath=null;
		Event event=null;
		
		newPath=this.fileText.getText();
		final File pathFile=new File(newPath);
		
		event=new Event();
		
		
		if(pathFile.exists()==false) {
			event.text="The file doesn't exists!";
			this.notifyListeners(SWT.Modify, event);
		} 
		
		this.notifyListeners(SWT.Modify, event);
	}
	
	
	public void browseForFile()
	{
		
		final Shell s = new Shell();
    
		final FileDialog fd = new FileDialog(s, SWT.OPEN);
		
		if(admittedExtensions!=null)
			fd.setFilterExtensions(admittedExtensions);
		
	    fd.setText("Select file");

	    final String selected = fd.open(); 

	    if(selected!=null)
	    {
	    	this.fileText.setText(selected);
	    }
	    
	    checkPath();	 
	}

	/**
	 * @param admittedExtension the admittedExtension to set
	 */
	public void setAdmittedExtension(String[] admittedExtension) {
		admittedExtensions = admittedExtension;
	}
}
