/**
 * 
 */
package it.intecs.pisa.develenv.model.project;

import it.intecs.pisa.develenv.model.project.ToolboxResourceDeltaVisitor;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Massimiliano
 *
 */
public class ToolboxResourceChangeListener implements IResourceChangeListener {

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		switch(event.getType())
		{
			case IResourceChangeEvent.PRE_DELETE:
				processPreDelete(event);
			break;
			
			case IResourceChangeEvent.POST_CHANGE:
				processPostChange(event);
			break;
		}

	}

	private void processPostChange(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new ToolboxResourceDeltaVisitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void processPreDelete(IResourceChangeEvent event) {
		
	}

}
