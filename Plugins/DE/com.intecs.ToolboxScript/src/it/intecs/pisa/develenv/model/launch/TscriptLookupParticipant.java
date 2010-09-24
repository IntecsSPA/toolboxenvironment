package it.intecs.pisa.develenv.model.launch;

import it.intecs.pisa.develenv.model.debug.TscriptStackframe;
import it.intecs.pisa.develenv.model.project.ToolboxEclipseProjectUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.sourcelookup.ISourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class TscriptLookupParticipant implements ISourceLookupParticipant {

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public Object[] findSourceElements(Object object) throws CoreException {
		IWorkspace wrkSpace = null;
		IWorkspaceRoot root=null;
		TscriptStackframe stackFrame;
		TscriptStackframe[] stacks;
		String path="";
		IFile file;
				
		if (object instanceof TscriptStackframe) {
			wrkSpace = ResourcesPlugin.getWorkspace();
			root = wrkSpace.getRoot();
			
			stackFrame = (TscriptStackframe) object;
			stacks = (TscriptStackframe[]) stackFrame.getThread().getStackFrames();

			for(int i=stacks.length-1;i>=0;i--)
			{
				path+="/"+stacks[i].getName();
			}
			file=root.getFile(new Path(path));
			
			if(file!=null)
				return new Object[] {file};
			else return new Object[0];
		}
		else return new Object[0];
	}

	public String getSourceName(Object object) throws CoreException {
		TscriptStackframe stackFrame;
		TscriptStackframe[] stacks;
		String path="";
		
		try {
			if (object instanceof TscriptStackframe) {
				stackFrame = (TscriptStackframe) object;
				stacks = (TscriptStackframe[]) stackFrame.getThread().getStackFrames();

				for(int i=stacks.length-1;i>=0;i--)
				{
					path+="/"+stacks[i].getName();
				}
				
				return path;
			}
			else return null;
		} catch (Exception e) {
			return null;
		}
	}

	public void init(ISourceLookupDirector director) {
		// TODO Auto-generated method stub
		return;
	}

	public void sourceContainersChanged(ISourceLookupDirector director) {
		// TODO Auto-generated method stub
		return;
	}

}
