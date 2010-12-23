/**
 * 
 */
package it.intecs.pisa.develenv.model.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;

/**
 * This class is used to store and retrieve project scoped preferences. It is
 * also used to strongly typing preferences values.
 * 
 * @author Massimiliano
 * 
 */
public class ToolboxEclipseProjectPreferences {
	public static final String PREF_TOOLBOX_RUNTIME_HOST_FOR_DEPLOY = "it.intecs.pisa.develenv.project.ToolboxURL";
	public static final String PREF_TOOLBOX_RUNTIME_USERNAME_FOR_AUTHENTICATION = "it.intecs.pisa.develenv.project.ToolboxUsername";
	public static final String PREF_TOOLBOX_RUNTIME_PASSWORD_FOR_AUTHENTICATION = "it.intecs.pisa.develenv.project.ToolboxPassword";

	private static final String NODE_NAME = "it.intecs.pisa.develenv.project";

	public static String getToolboxHostURL(IProject project) {
		String url = null;
		IEclipsePreferences projectNode = null;

		projectNode = getPreferenceNode(project, NODE_NAME);
		if (projectNode != null) {
			url = projectNode.get(PREF_TOOLBOX_RUNTIME_HOST_FOR_DEPLOY, "");
		}

		return url;
	}

	public static void setToolboxHostURL(IProject project, String url) {
		IEclipsePreferences projectNode = null;

		projectNode = getPreferenceNode(project, NODE_NAME);

		if (projectNode != null) {
			projectNode.put(PREF_TOOLBOX_RUNTIME_HOST_FOR_DEPLOY, url);
		}

		try {
			projectNode.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String getToolboxHostUsername(IProject project) {
		String username = null;
		IEclipsePreferences projectNode = null;

		projectNode = getPreferenceNode(project, NODE_NAME);
		if (projectNode != null) {
			username = projectNode.get(PREF_TOOLBOX_RUNTIME_USERNAME_FOR_AUTHENTICATION, "");
		}

		return username;
	}

	public static void setToolboxHostUsername(IProject project, String username) {
		IEclipsePreferences projectNode = null;

		projectNode = getPreferenceNode(project, NODE_NAME);

		if (projectNode != null) {
			projectNode.put(PREF_TOOLBOX_RUNTIME_USERNAME_FOR_AUTHENTICATION, username);
		}

		try {
			projectNode.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static String getToolboxHostPassword(IProject project) {
		String username = null;
		IEclipsePreferences projectNode = null;

		projectNode = getPreferenceNode(project, NODE_NAME);
		if (projectNode != null) {
			username = projectNode.get(PREF_TOOLBOX_RUNTIME_PASSWORD_FOR_AUTHENTICATION, "");
		}

		return username;
	}

	public static void setToolboxHostPassword(IProject project, String password) {
		IEclipsePreferences projectNode = null;

		projectNode = getPreferenceNode(project, NODE_NAME);

		if (projectNode != null) {
			projectNode.put(PREF_TOOLBOX_RUNTIME_PASSWORD_FOR_AUTHENTICATION, password);
		}

		try {
			projectNode.flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static IEclipsePreferences getPreferenceNode(IProject project,
			String nodeName) {
		IScopeContext projectScope = null;
		IEclipsePreferences projectNode = null;

		projectScope = new ProjectScope(project);

		projectNode = projectScope.getNode(nodeName);

		return projectNode;
	}

}
