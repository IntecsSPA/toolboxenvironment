package it.intecs.pisa.develenv.ui.perspectives;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class debugPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout runFolder = layout.createFolder("runFolder", IPageLayout.TOP, 0.25f,editorArea);
		runFolder.addView(IDebugUIConstants.ID_DEBUG_VIEW);
		
		IFolderLayout breakpointFolder = layout.createFolder("breakpointFolder", IPageLayout.RIGHT, 0.25f,"runFolder");
		breakpointFolder.addView(IDebugUIConstants.ID_BREAKPOINT_VIEW);
		
		IFolderLayout variableFolder = layout.createFolder("variableFolder", IPageLayout.RIGHT, 0.300f,"breakpointFolder");
		variableFolder.addView(IDebugUIConstants.ID_VARIABLE_VIEW);
		
			
	
		IFolderLayout treeFolder = layout.createFolder("treeFolder", IPageLayout.RIGHT, 0.75f,editorArea);
		treeFolder.addView(/*IPageLayout.ID_OUTLINE*/"it.intecs.pisa.develenv.ui.views.ExecutionTreeView");
		
		layout.addActionSet("org.eclipse.debug.ui.debugActionSet");
		layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
			
	}

}
