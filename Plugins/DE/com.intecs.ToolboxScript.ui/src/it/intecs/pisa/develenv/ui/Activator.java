/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: ModelActivator.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.2 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: ModelActivator.java,v 1.2 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui;

import it.intecs.pisa.develenv.model.project.ToolboxResourceChangeListener;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The Class ModelActivator.
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	/**
	 * The Constant PLUGIN_ID.
	 */
	public static final String PLUGIN_ID = "com.intecs.ToolboxScript.ui";

	// The shared instance
	/**
	 * The plugin.
	 */
	private static Activator plugin;
	
	/**
	 * The Constructor.
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		
		ToolboxResourceChangeListener listener = new ToolboxResourceChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.PRE_DELETE|IResourceChangeEvent.POST_CHANGE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Gets the default.
	 * 
	 * @return the default
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Gets the image descriptor.
	 * 
	 * @param path the path
	 * 
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
