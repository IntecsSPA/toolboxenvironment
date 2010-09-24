/**
 * 
 */
package it.intecs.pisa.develenv.ui.wizards;


/**
 * @author Massimiliano
 *
 */
public class TBX_JarsImportSelectionWizardPage extends TBX_ImportSelectionWizardPage{

	private static final String TITLE = "Jars import wizard";
		
	protected TBX_JarsImportSelectionWizardPage(String pageName) {
		super(TITLE);
		this.setTitle(TITLE);
		this.setDescription("Descripton");
		
		admittedFileExtension=new String[]{"*.jar"};
	}

	


}
