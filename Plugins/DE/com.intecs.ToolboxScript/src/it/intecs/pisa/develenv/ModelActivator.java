/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: ModelActivatorvator.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.3 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: ModelActivatorlActivator.java,v 1.3 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

// TODO: Auto-generated Javadoc
/**ModelActivatorClass ModelActivator.
 */
public class ModelActivator extends AbstractUIPlugin {

	// The plug-in ID
	/**
	 * The Constant PLUGIN_ID.
	 */
	public static final String PLUGIN_ID = "com.intecs.ToolboxScript";

	// The shared instance
	/**
	 * The plugin.
	 */
	private static ModelActivator plugin;

	/**
	 * The Constructor.
	 */
	public ModelActivator() {
		plugin = this;
	}


	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		//registering change listener
				
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Gets the default.
	 * 
	 * @return the defauModelActivator
	public static ModelActivator getDefault() {
		return plugin;
	}

	/**
	 * Gets the image descriptor.
	 * 
	 * @param path the path
	 * 
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		//return imageDescriptorFromPlugin(PLUGIN_ID, path);
		return null;
	}
}
