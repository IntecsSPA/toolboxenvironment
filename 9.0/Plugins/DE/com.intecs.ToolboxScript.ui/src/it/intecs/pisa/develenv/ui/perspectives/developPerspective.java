package it.intecs.pisa.develenv.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class developPerspective implements IPerspectiveFactory {
	
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f,editorArea);
		topLeft.addView(IPageLayout.ID_RES_NAV);
		
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.80f,"topLeft");
		bottomLeft.addView(IPageLayout.ID_PROGRESS_VIEW);
			
		layout.addActionSet("org.eclipse.debug.ui.debugActionSet");
		layout.addActionSet("org.eclipse.debug.ui.launchActionSet");
	}

}
