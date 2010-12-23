/**
 * 
 */
package it.intecs.pisa.develenv.model.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

/**
 * This class contains some utility method for operating with Bundles
 * @author Massimiliano Fanciulli
 *
 */
public class BundleUtil {
	
	/**
	 * 
	 * @param path
	 * @param bundleStr
	 * @return
	 */
	public static URL getEntryFromBundle(String path, String bundleStr) {
		Bundle bundle;
		URL url;
		bundle = Platform.getBundle(bundleStr);
		url = bundle.getEntry(path);
		return url;
	}
	
	/**
	 * 
	 * @param path
	 * @param bundleStr
	 * @return
	 */
	public static InputStream getEntryAsInputStream(String path,String bundleStr) {
		URL url = null;
		FileInputStream stream = null;

		try {
			url = BundleUtil.getEntryFromBundle(path, bundleStr);

			return url.openStream();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stream;
	}
}
