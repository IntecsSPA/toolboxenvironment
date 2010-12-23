/**
 * 
 */
package it.intecs.pisa.develenv.model.project.workspaceJob;

import it.intecs.pisa.develenv.model.project.ToolboxEclipseProject;

/**
 * @author Massimiliano
 *
 */
public class ToolboxTestFolderRecreation extends ToolboxMainFolderRecreation {
	
	public ToolboxTestFolderRecreation(String projectName) {
		super(projectName,ToolboxEclipseProject.FOLDER_TEST_DIR);

	}

}
