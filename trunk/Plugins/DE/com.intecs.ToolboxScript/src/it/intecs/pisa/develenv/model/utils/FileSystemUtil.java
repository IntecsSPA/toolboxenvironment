package it.intecs.pisa.develenv.model.utils;

import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IResource;

public class FileSystemUtil {

	public static void delete(String toDelete)
	{
		URI sourceURI=null;
				
		try
		{
			sourceURI=URIUtil.toURI(toDelete);	
								
			delete(sourceURI);
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}
	
	public static void delete(URI toDelete)
	{
		IFileStore deleteStore=null;
		
		try
		{	
			deleteStore=EFS.getStore(toDelete);
			deleteStore.delete(IResource.NONE, null);
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}
	
	
	public static void copyFile(String source,String destination)
	{
		URI sourceURI=null;
		URI destURI=null;
		
		try
		{
			sourceURI=URIUtil.toURI(source);	
			destURI=URIUtil.toURI(destination);
					
			copyFile(sourceURI,destURI);
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}
	
	public static void copyFile(URI source,URI destination)
	{
		IFileStore sourceStore=null;
		IFileStore destStore=null;	
		IFileStore parentStore=null;
		
		try
		{	
			sourceStore=EFS.getStore(source);
			
			destStore=EFS.getStore(destination);
			parentStore=destStore.getParent();
			
			parentStore.mkdir(IResource.NONE, null);
		
			sourceStore.copy(destStore, EFS.OVERWRITE, null);
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}

	public static void copyDirectory(URI source,URI destination)
	{
		IFileStore sourceStore=null;
		IFileStore destStore=null;	
		
		try
		{	
			sourceStore=EFS.getStore(source);
			
			destStore=EFS.getStore(destination);
			
			sourceStore.copy(destStore, EFS.OVERWRITE, null);
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}
	
	public static void copyDirectory(String source,String destination)
	{
		URI sourceURI=null;
		URI destURI=null;
		
		try
		{
			sourceURI=URIUtil.toURI(source);	
			destURI=URIUtil.toURI(destination);
					
			copyDirectory(sourceURI,destURI);
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}
	
	public static void copyDirectory(String source,String destination,String[] admittedExtensions)
	{
		URI sourceURI=null;
		URI destURI=null;
		IFileStore sourceStore=null;
		IFileStore destStore=null;	
		IFileStore sourceChildDirStore=null;
		IFileStore destChildDirStore=null;
		IFileInfo[] children=null;
		boolean match=false;
		String childName=null;
		String sourcePath=null;
		String destPath=null;
		
		try
		{
			sourceURI=URIUtil.toURI(source);	
			destURI=URIUtil.toURI(destination);
					
			sourceStore=EFS.getStore(sourceURI);
			destStore=EFS.getStore(destURI);
			
			children=sourceStore.childInfos(0,null);
			for(IFileInfo child:children)
			{
				childName=child.getName();
				if(child.isDirectory())
				{
					sourceChildDirStore=sourceStore.getChild(childName);
					sourcePath=sourceChildDirStore.toURI().getPath();
					
					destChildDirStore=destStore.getChild(childName);
					//destChildDirStore.mkdir(0, null);
					destPath=destChildDirStore.toURI().getPath();
					
					copyDirectory(sourcePath,destPath,admittedExtensions);
				}
				else 
				{
					for(String ext:admittedExtensions)
					{
						if(childName.endsWith(ext))
							match=true;
					}
					
					if(match)
					{
						//shall copy this file
						sourceChildDirStore=sourceStore.getChild(childName);
						sourcePath=sourceChildDirStore.toURI().getPath();
						
						destChildDirStore=destStore.getChild(childName);
						//destChildDirStore.mkdir(0, null);
						destPath=destChildDirStore.toURI().getPath();
						
						copyFile(sourcePath,destPath);
						
						match=false;
					}
				}
			}
		}
		catch(Exception e)
		{
			//TODO shall raise exception
			
		}
	}
	
	
	
}
