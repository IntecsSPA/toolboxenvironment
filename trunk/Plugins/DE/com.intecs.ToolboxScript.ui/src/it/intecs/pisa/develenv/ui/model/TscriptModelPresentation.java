/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptModelPresentation.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.2 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptModelPresentation.java,v 1.2 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * The Class TscriptModelPresentation.
 */
public class TscriptModelPresentation extends LabelProvider implements IDebugModelPresentation {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(final String attribute, final Object value) {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(final Object element) {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(final Object element) {
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.IDebugModelPresentation#computeDetail(org.eclipse.debug.core.model.IValue, org.eclipse.debug.ui.IValueDetailListener)
	 */
	public void computeDetail(final IValue value, final IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (final DebugException e) {
		}
		listener.detailComputed(value, detail);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(java.lang.Object)
	 */
	public IEditorInput getEditorInput(final Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile)element);
		}
		if (element instanceof ILineBreakpoint) {
			return new FileEditorInput((IFile)((ILineBreakpoint)element).getMarker().getResource());
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(org.eclipse.ui.IEditorInput, java.lang.Object)
	 */
	public String getEditorId(final IEditorInput input, final Object element) {
		if ((element instanceof IFile) || (element instanceof ILineBreakpoint)) {
			return "com.intecs.ToolboxScript.editor";
		}
		return null;
	}
}
