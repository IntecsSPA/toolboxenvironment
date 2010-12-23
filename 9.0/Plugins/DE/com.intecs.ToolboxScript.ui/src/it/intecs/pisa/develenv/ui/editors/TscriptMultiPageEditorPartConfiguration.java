/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptMultiPageEditorPartConfiguration.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.3 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptMultiPageEditorPartConfiguration.java,v 1.3 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.editors;

import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;

/**
 * The Class TscriptMultiPageEditorPartConfiguration.
 */
public class TscriptMultiPageEditorPartConfiguration extends StructuredTextViewerConfigurationXML {
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML#getTextHover(org.eclipse.jface.text.source.ISourceViewer, java.lang.String, int)
	 */
	@Override
	public ITextHover getTextHover(final ISourceViewer sourceViewer, final String contentType, final int stateMask) {
		return new TscriptMultiPageEditorTextHover();
	}

	/**
	 * The Constructor.
	 */
	public TscriptMultiPageEditorPartConfiguration()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML#getContentFormatter(org.eclipse.jface.text.source.ISourceViewer)
	 */
	@Override
	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return new TscriptMultiPageEditorTextFormatter();
	}
	
	
}
