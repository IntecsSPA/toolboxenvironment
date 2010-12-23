/**
 * 
 */
package it.intecs.pisa.develenv.model.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;

/**
 * @author Massimiliano
 *
 */
public class SelectionParser {
	public static IProject getOwnerProject(ISelection selRes)
	{
		IProject project=null;
		ITreeSelection tselection=null;
		TreePath[] paths=null;
		Object segment=null;
		
		try
		{
			tselection = (ITreeSelection) selRes;
			
			paths = tselection.getPaths();
			
			//gettting first selected project, if any
			for(TreePath pathItem : paths)
			{
				segment = pathItem.getFirstSegment();
				if(segment instanceof IProject)
				{
					project=(IProject)segment;	
				}
			}
			
		}
		catch(Exception e)
		{
			
		}

		return project;
	}
}
