package it.intecs.pisa.develenv.model.utils;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class CommonPaths {
	public static File getScriptSchema()
	{
		File schemaFile;
		
		Bundle interfacesPlugin = Platform.getBundle("com.intecs.ToolboxScript.editorFiles");
		URL entry = interfacesPlugin.getEntry("schemas/xmlScript.xsd");
		
		try {
			schemaFile=new File(FileLocator.toFileURL(entry).toURI());
		} catch (Exception e) {
			schemaFile=null;
		}
		
		return schemaFile;
	}
	
}
