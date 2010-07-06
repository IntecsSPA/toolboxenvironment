/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptBreakpointProvider.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.3 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptBreakpointProvider.java,v 1.3 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.model;

import it.intecs.pisa.develenv.model.debug.TscriptLineBreakpoint;
import it.intecs.pisa.develenv.model.utils.XPathResolver;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;

/**
 * The Class TscriptBreakpointProvider.
 */
public class TscriptBreakpointProvider implements IBreakpointProvider, IExecutableExtension {
	
	/**
	 * The f class pattern.
	 */
	private String fClassPattern = null;

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider#addBreakpoint(org.eclipse.jface.text.IDocument, org.eclipse.ui.IEditorInput, int, int)
	 */
	public IStatus addBreakpoint(final IDocument document, final IEditorInput input, final int editorLineNumber, final int offset) throws CoreException {
		
		IStatus status = null;
	
		final XPathResolver resolver=new XPathResolver(document);
		
		final IResource resource = (IResource) input.getAdapter(IResource.class);
		
		final TscriptLineBreakpoint lineBreakpoint = new TscriptLineBreakpoint(resource, editorLineNumber);
		
		lineBreakpoint.setXPath(resolver.computeFromLine(editorLineNumber));
		lineBreakpoint.setFilePath(resource.getFullPath().toOSString());
				
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
		status = new Status(IStatus.OK, "com.intecs.toolboxscript", IStatus.OK, "OK", null);
		return status;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider#getResource(org.eclipse.ui.IEditorInput)
	 */
	public IResource getResource(final IEditorInput input) {
		return this.getResourceFromInput(input);
	}

	/**
	 * Gets the resource from input.
	 * 
	 * @param input the input
	 * 
	 * @return the resource from input
	 */
	private IResource getResourceFromInput(final IEditorInput input) {
		IResource resource = (IResource) input.getAdapter(IFile.class);
		if (resource == null) {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		return resource;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		if (data != null) {
			if ((data instanceof String) && (data.toString().length() > 0)) {
				this.fClassPattern = (String) data;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider#setSourceEditingTextTools(org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools)
	 */
	public void setSourceEditingTextTools(final ISourceEditingTextTools tools) {
		// not used
	}
}
