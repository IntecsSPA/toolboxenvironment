/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptLineBreakpoint.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.10 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/03/14 08:44:58 $
 * File ID: $Id: TscriptLineBreakpoint.java,v 1.10 2007/03/14 08:44:58 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.model.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

/**
 * The Class TscriptLineBreakpoint.
 */
public class TscriptLineBreakpoint extends LineBreakpoint {
	@Override
	public IMarker getMarker() {
		// TODO Auto-generated method stub
		return super.getMarker();
	}

	protected final String ATTR_FILE_PATH="ATTR_FILE_PATH";
		
	/**
	 * The Constructor.
	 */
	public TscriptLineBreakpoint() {
		
	}

	/**
	 * The Constructor.
	 * 
	 * @param resource the resource
	 * @param lineNumber the line number
	 * 
	 * @throws CoreException the core exception
	 */
	public TscriptLineBreakpoint(final IResource resource, final int lineNumber)
			throws CoreException {

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource.createMarker("com.intecs.ToolboxScript.lineBreakpoint.marker");

				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, Boolean.TRUE);
				marker.setAttribute(IMarker.LINE_NUMBER, new Integer(lineNumber));
				marker.setAttribute(IBreakpoint.ID, "com.intecs.toolboxscript.debug");
				marker.setAttribute(IMarker.TRANSIENT, false);
				marker.setAttribute(IMarker.MESSAGE, "Line Breakpoint: "
						+ resource.getName() + " [line: " + lineNumber + "]");
				
				
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	
	public String getModelIdentifier() {
		return "com.intecs.toolboxscript.debug";
	}

	/**
	 * Sets the X path.
	 * 
	 * @param xpath the x path
	 */
	public void setXPath(String xpath) {
		try {
			this.getMarker().setAttribute(IMarker.LOCATION, xpath);
		} catch (Exception e) {

		}
	}

	/**
	 * Gets the X path.
	 * 
	 * @return the x path
	 */
	public String getXPath() {
		try {
			return (String)this.getMarker().getAttribute(IMarker.LOCATION);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			return "/";
		}
	}  

	public void setFilePath(String path) {
		try {
			this.getMarker().setAttribute(ATTR_FILE_PATH, path.replace('\\', '/'));
		} catch (Exception e) {

		}
		
	}
	public String getFilePath() {
		try {
			return (String)this.getMarker().getAttribute(ATTR_FILE_PATH);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			return "/";
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.LineBreakpoint#getLineNumber()
	 */
	public int getLineNumber()
    throws CoreException
    {
		try {
			return ((Integer)this.getMarker().getAttribute(IMarker.LINE_NUMBER)).intValue();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			return 0;
		}
    }
	
}
