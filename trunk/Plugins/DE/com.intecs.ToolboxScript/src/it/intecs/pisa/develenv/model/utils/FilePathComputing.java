/* -----------------------------------------------------------------------------------------
 * Developed By: Intecs.
 * File Name: $RCSfile: FilePathComputing.java,v $
 * Version: $Name: HEAD $
 * File Revision: $Revision: 1.5 $
 * Revision Author: $Author: fanciulli $
 * Revision Date: $Date: 2007/02/23 08:42:32 $
 * File ID: $Id: FilePathComputing.java,v 1.5 2007/02/23 08:42:32 fanciulli Exp $
 ------------------------------------------------------------------------------------------*/
package it.intecs.pisa.develenv.model.utils;

import it.intecs.pisa.develenv.model.utils.FilePathComputing;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * The Class FilePathComputing.
 */
public class FilePathComputing {

	public static String compute(URL url) throws Exception {
		String result = null;
		String urlString;

		URL tmpurl = FileLocator.toFileURL(url);

		urlString = tmpurl.toString();
		urlString = urlString.replaceAll(" ", "%20");

		URI uri = new URI(urlString);
		File f = new File(uri);
		result = f.getAbsolutePath();

		return result;
	}

	public static String[] computeFilePathForBundle(String bundleName,
			String file) throws Exception {
		Bundle bundle;
		String[] result;
		Enumeration e;
		Vector urls;

		bundle = Platform.getBundle(bundleName);
		if (bundle == null)
			return new String[0];

		// e= FileLocator.find(bundle,new Path("/"),null);

		e = bundle.findEntries("/", file, true);

		if (e != null && e.hasMoreElements() == true) {
			urls = new Vector();

			while (e.hasMoreElements()) {
				URL url = (URL) e.nextElement();

				urls.add(url);
			}

			result = new String[urls.size()];

			for (int i = 0; i < urls.size(); i++)
				result[i] = new String(FilePathComputing.compute((URL) urls
						.get(i)));

			return result;
		} else
			return new String[0];
	}

	public static String[] computeFilePathForBundleRelative(String bundleName,
			String file) throws Exception {
		Bundle bundle;
		String[] result;
		Enumeration e;
		Vector urls;

		bundle = Platform.getBundle(bundleName);
		if (bundle == null)
			return new String[0];

		// e= FileLocator.find(bundle,new Path("/"),null);

		e = bundle.findEntries("/", file, true);

		if (e != null && e.hasMoreElements() == true) {
			urls = new Vector();

			while (e.hasMoreElements()) {
				URL url = (URL) e.nextElement();

				String relativeUrl;

				relativeUrl = url.toExternalForm();

				urls.add(relativeUrl);

			}

			result = new String[urls.size()];

			for (int i = 0; i < urls.size(); i++)
				result[i] = (String) urls.get(i);

			return result;
		} else
			return new String[0];
	}
	
	public static String computeDirPathForBundle(String bundleName) throws Exception
	{

		Bundle bundle;
		
		bundle=Platform.getBundle(bundleName);
		String location=bundle.getLocation();
		
		location=location.substring(location.indexOf('@')+1);
		String installPath = FilePathComputing.compute(Platform.getInstallLocation().getURL());
		
		String path = installPath+File.separatorChar+location;
		
		path=path.replace('/',File.separatorChar);
		
		return path; 
	}

}
