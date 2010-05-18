/*
 * ****************************************************************************
 *  Copyright 2003*2004 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package it.intecs.pisa.develenv.model.launch;


import it.intecs.pisa.develenv.model.debug.ToolboxScriptDebugLaunch;

import java.io.IOException;
import java.net.ServerSocket;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;



/**
 * This class is the launch delegate forrunning and debugging purpouses
 * 
 * @author Massimiliano
 *
 */
public class ToolboxScriptLaunchDelegate extends
		/*AbstractJavaLaunchConfigurationDelegate*/ LaunchConfigurationDelegate {
	
	/**
	 * Find free port.
	 * 
	 * @return the int
	 */
	public static int findFreePort() {
		ServerSocket socket = null;
		try {
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
		return -1;
	}

	

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			ToolboxScriptDebugLaunch.execute(configuration, mode, launch, monitor);
		}
		else
		{
			ToolboxScriptRunLaunch.execute(configuration, mode, launch, monitor);
		}
	}
}
