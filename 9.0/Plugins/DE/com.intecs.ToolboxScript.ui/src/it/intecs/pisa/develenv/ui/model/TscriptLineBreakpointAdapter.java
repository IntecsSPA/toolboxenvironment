/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptLineBreakpointAdapter.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.3 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:59:22 $
 * File ID: $Id: TscriptLineBreakpointAdapter.java,v 1.3 2007/01/22 14:59:22 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.model;

import it.intecs.pisa.develenv.ui.editors.TscriptMultiPageEditorPart;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.debug.ToggleBreakpointsTarget;


/**
 * The Class TscriptLineBreakpointAdapter.
 */
public class TscriptLineBreakpointAdapter implements IToggleBreakpointsTarget {
	
	/**
	 * The m_toggle breakpoint.
	 */
	private ToggleBreakpointsTarget m_toggleBreakpoint;
	
	
	/**
	 * The Constructor.
	 */
	public TscriptLineBreakpointAdapter(){
		this.m_toggleBreakpoint = (ToggleBreakpointsTarget) ToggleBreakpointsTarget.getInstance();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	/*
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor != null) {
			IResource resource = (IResource) textEditor.getEditorInput().getAdapter(IResource.class);
			ITextSelection textSelection = (ITextSelection) selection;
			int lineNumber = textSelection.getStartLine();
			IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(ToolboxScriptConstants.ID_TSCRIPT_DEBUG_MODEL);
			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (resource.equals(breakpoint.getMarker().getResource())) {
					if (((ILineBreakpoint)breakpoint).getLineNumber() == (lineNumber + 1)) {
						// remove
						breakpoint.delete();
						return;
					}
				}
			}
			// create line breakpoint (doc line numbers start at 0)
			TscriptLineBreakpoint lineBreakpoint = new TscriptLineBreakpoint(resource, lineNumber + 1);
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
		}
	}*/
	
	public void toggleLineBreakpoints(final IWorkbenchPart part, final ISelection selection) throws CoreException {
		this.m_toggleBreakpoint.toggleLineBreakpoints(part, selection);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleLineBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleLineBreakpoints(final IWorkbenchPart part, final ISelection selection) {
		return this.getEditor(part) != null;
		//return true;
	}
	
	/**
	 * Gets the editor.
	 * 
	 * @param part the part
	 * 
	 * @return the editor
	 */
	private ITextEditor getEditor(final IWorkbenchPart part) {
	
		//if (part instanceof ITextEditor) {
		if (part instanceof TscriptMultiPageEditorPart) {
			final TscriptMultiPageEditorPart editorPart = (TscriptMultiPageEditorPart) part;
			final IResource resource = (IResource) editorPart.getEditorInput().getAdapter(IResource.class);
			if (resource != null) {
				final String extension = resource.getFileExtension();
				if ((extension != null) && extension.equals("tscript")) {
					final StructuredTextEditor sse = editorPart.myGetTextEditor();
					return sse;
				}
			}
		}
		return null;		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleMethodBreakpoints(final IWorkbenchPart part, final ISelection selection) throws CoreException {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleMethodBreakpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleMethodBreakpoints(final IWorkbenchPart part, final ISelection selection) {
		return false;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#toggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void toggleWatchpoints(final IWorkbenchPart part, final ISelection selection) throws CoreException {
	}
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.actions.IToggleBreakpointsTarget#canToggleWatchpoints(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public boolean canToggleWatchpoints(final IWorkbenchPart part, final ISelection selection) {
		return false;
	}
}
