/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptMultiPageEditorPart.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.8 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptMultiPageEditorPart.java,v 1.8 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
// -------------------------------------------------------------------------------------------
//
// * Developed By: Intecs.
// * File Name: $RCSfile: TscriptMultiPageEditorPart.java,v $
// * Version: $Name: HEAD $
// * File Revision: $Revision: 1.8 $
// * Revision Author: $Author: fanciulli $
// * Revision Date: $Date: 2007/01/22 14:05:45 $
// * File ID: $Id: TscriptMultiPageEditorPart.java,v 1.8 2007/01/22 14:05:45 fanciulli Exp $
// -------------------------------------------------------------------------------------------
package it.intecs.pisa.develenv.ui.editors;

import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.tabletree.IDesignViewer;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeViewer;

/**
 * The Class TscriptMultiPageEditorPart.
 */
public class TscriptMultiPageEditorPart extends XMLMultiPageEditorPart {
	
	/**
	 * The Constructor.
	 */
	public TscriptMultiPageEditorPart(){
		super();
		
		
	}

	/**
	 * My get text editor.
	 * 
	 * @return the structured text editor
	 */
	public StructuredTextEditor myGetTextEditor() {	
		return (StructuredTextEditor) super.getActiveEditor();
	}
	
	/**
	 * Gets the text editor.
	 * 
	 * @return the text editor
	 */
	public StructuredTextEditor getTextEditor()
	{	
		try
		{
			this.editor=(StructuredTextEditor)this.getEditor(1);
					}
		catch(final Exception e)
		{
			e.printStackTrace();
			this.editor=null;
		}
		return this.editor;
	}
	
	
	
	/**
	 * Gets the tree editor.
	 * 
	 * @return the tree editor
	 */
	public  XMLTableTreeViewer getTreeEditor()
	{
		return this.designPage;
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart#createDesignPage()
	 */
	@Override
	protected IDesignViewer createDesignPage() {
		this.designPage=(XMLTableTreeViewer)super.createDesignPage();
			
		return this.designPage;
	}
	
	
	/**
	 * The design page.
	 */
	protected XMLTableTreeViewer designPage;
	
	/**
	 * The editor.
	 */
	protected StructuredTextEditor editor;

}
