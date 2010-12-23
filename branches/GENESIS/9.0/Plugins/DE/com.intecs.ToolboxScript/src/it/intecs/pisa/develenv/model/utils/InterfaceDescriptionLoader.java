package it.intecs.pisa.develenv.model.utils;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import it.intecs.pisa.common.tbx.InterfacesDescription;

public class InterfaceDescriptionLoader {
	private static final String BUNDLE_INTERFACES = "com.intecs.ToolboxScript.interfaces";
	private static final String PATH_INTERFACES_DEFINITION_XML = "/interfacesDefinition.xml";
	private static final String FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY = "TOOLBOXUserConfigurations";
	
	public InterfaceDescriptionLoader()
	{
		
	}
	
	public static InterfacesDescription load()
	{
		InterfacesDescription descr=null;
		Bundle interfacesPlugin=null;
		URL entry=null;
		File file=null;
		String userHome=null;
		File tbxConfigurationsDir=null;
		File interfacesFile=null;
		
		descr=new InterfacesDescription();
		
		//getting path for interface definition xml document
		interfacesPlugin=Platform.getBundle(BUNDLE_INTERFACES);
		entry=interfacesPlugin.getEntry(PATH_INTERFACES_DEFINITION_XML);
		
		try {
			file = new File(FileLocator.toFileURL(entry).getPath());
			
			//loading user defined
			descr.loadFromFile(file, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try
		{
			userHome=System.getProperty("user.home");
			
			tbxConfigurationsDir=new File(userHome,FILE_TOOLBOXUSER_CONFIGURATIONS_DIRECTORY);
			interfacesFile=new File(tbxConfigurationsDir,PATH_INTERFACES_DEFINITION_XML);
	
			//loading eclipse defined
			descr.loadFromFile(interfacesFile, false);
		}
		catch(Exception usrExc)
		{
			
		}
		
		return descr;
	}
}
