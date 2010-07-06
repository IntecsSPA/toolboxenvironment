/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package it.intecs.pisa.develenv.ui.decorators;

import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * An example showing how to control when an element is decorated. This example
 * decorates only elements that are instances of IResource and whose attribute
 * is 'Read-only'.
 * 
 * @see ILightweightLabelDecorator
 */
public class TOOLBOXProjectDecorator implements ILightweightLabelDecorator {
	/**
	 * String constants for the various icon placement options from the template
	 * wizard.
	 */
	public static final String TOP_RIGHT = "TOP_RIGHT";

	public static final String TOP_LEFT = "TOP_LEFT";

	public static final String BOTTOM_RIGHT = "BOTTOM_RIGHT";

	public static final String BOTTOM_LEFT = "BOTTOM_LEFT";

	public static final String UNDERLAY = "UNDERLAY";

	private static final String FILENAME_SERVICE_DESCRIPTOR = "serviceDescriptor.xml";
	/** The integer value representing the placement options */
	private int quadrant;

	/** The icon image location in the project folder */
	private String iconPath = "icons/Toolbox_gear_green.jpg"; //NON-NLS-1

	/**
	 * The image description used in
	 * <code>addOverlay(ImageDescriptor, int)</code>
	 */
	private ImageDescriptor descriptor;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		/**
		 * Checks that the element is an IResource with the 'Read-only' attribute
		 * and adds the decorator based on the specified image description and the
		 * integer representation of the placement option.
		 */
		
		if(isTBXProject(element))
		{
			iconPath=getIconByType(element);
			if(iconPath!=null)
			{
				IResource resource = (IResource) element;
				ResourceAttributes attrs = resource.getResourceAttributes();
				URL url = Platform.find(
							Platform.getBundle("com.intecs.ToolboxScript.ui"), new Path(iconPath)); //NON-NLS-1
	
				if (url == null)
					return;
				
				descriptor = ImageDescriptor.createFromURL(url);			
				quadrant = IDecoration.BOTTOM_RIGHT;
				decoration.addOverlay(descriptor,quadrant);
			}
			
		}
		
		
	}

	private String getIconByType(Object element) {
		IFolder folder=null;
		
		if(element instanceof IProject)
			return "icons/decorator/project.jpg";
		
		if(element instanceof IFolder)
		{
			folder=(IFolder) element;
			
			if(folder.getName().equals("Info"))
				return "icons/decorator/info.png";
			
			if(folder.getName().equals("Operations")|| folder.getParent().getName().equals("Operations"))
				return "icons/decorator/operation.png";
			
			if(folder.getName().equals("Resources"))
				return "icons/decorator/resources.png";
			
			if(folder.getName().equals("Schemas"))
				return "icons/decorator/schemas.png";
			
			if(folder.getName().equals("Test Files"))
				return "icons/decorator/testFiles.png";
			
			if( folder.getParent().getName().equals("Test Files"))
				return "icons/decorator/operation.png";
			
			if(folder.getName().equals("Test Results") || folder.getParent().getName().equals("Test Results"))
				return "icons/decorator/testResults.png";
			
			
			if(folder.getName().equals("AdditionalResources") || folder.getParent().getName().equals("AdditionalResources"))
				return "icons/decorator/plugin.png";
			//now checking second level directories
			IResource el=null;
			IContainer parent=null;
			
			el=(IResource) element;
			parent=el.getParent();
			if(parent.getName().equals("Operations"))
				return "icons/operation.png";
				
			/*if(parent.getName().equals("Resources") && folder.getName().equals("Common Scripts"))
				return "icons/Toolbox_gear_blue.jpg";*/
			
			if(parent.getName().equals("Test Files"))
				return "icons/operation.png";
			
			
		}
		
		return null;
	}

	private boolean isTBXProject(Object element) {
		// TODO Auto-generated method stub
		IFile descr=null;
		IProject project=null;
		
		if(element instanceof IProject)
			project=(IProject) element;
		else
			project=((IResource)element).getProject();
		
		descr=project.getFile(FILENAME_SERVICE_DESCRIPTOR);
		
		if(descr!=null && descr.exists())
			return true;
		else return false;
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}
}