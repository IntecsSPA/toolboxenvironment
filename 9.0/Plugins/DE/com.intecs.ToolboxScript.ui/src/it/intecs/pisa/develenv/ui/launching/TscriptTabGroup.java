/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: TscriptTabGroup.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.2 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/01/22 14:05:45 $
 * File ID: $Id: TscriptTabGroup.java,v 1.2 2007/01/22 14:05:45 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.ui.launching;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

/**
 * The Class TscriptTabGroup.
 */
public class TscriptTabGroup extends AbstractLaunchConfigurationTabGroup {
	/* (non-Javadoc)
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTabGroup#createTabs(org.eclipse.debug.ui.ILaunchConfigurationDialog, java.lang.String)
	 */
	public void createTabs(final ILaunchConfigurationDialog dialog, final String mode) {
		this.setTabs(new ILaunchConfigurationTab[] {
				new TscriptMainTab(),
				new SourceLookupTab(),
				new CommonTab()
		});
	}
	
	
	
}
