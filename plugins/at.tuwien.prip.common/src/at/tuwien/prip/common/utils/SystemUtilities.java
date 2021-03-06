/*
Copyright 2000-2005 University Duisburg-Essen, Working group "Information Systems"

Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.
*/

/*
 * Modifications by Max Goebel
 *
 */

// $Id: SystemUtilities.java,v 1.19 2005/02/21 17:29:29 huesselbeck Exp $
package at.tuwien.prip.common.utils;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import at.tuwien.prip.common.log.Log;

/**
 * A collection of system utility methods.
 *
 * @author Henrik Nottelmann
 * @since 2003-06-20
 * @version $Revision: 1.19 $, $Date: 2005/02/21 17:29:29 $
 */
public class SystemUtilities {

	/**
	 * Returns a reader for the first file with the given name
	 * (prefixed with <code>conf/</conf>).<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path, without "conf" prefix
	 * @return corresponding reader
	 */
	public static Reader getConfAsStream(String relativePath) {
		String sep = System.getProperty("file.separator");
		return getResourceAsStream("conf" + sep + relativePath);
	}

	/**
	 * Returns a URL for the first file with the given name
	 * (prefixed with <code>conf/</conf>).<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 *
	 * @param relativePath relative path, without "conf" prefix
	 * @return corresponding URL
	 */
	public static URL getConfURL(String relativePath) {
		String sep = System.getProperty("file.separator");
		return getResourceURL("conf" + sep + relativePath);
	}

	/**
	 * Returns an array of URLs for all files with the given name
	 * (prefixed with <code>conf/</conf>).<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path, without "conf" prefix
	 * @return array of corresponding URLs
	 */
	public static URL[] getConfURLs(String relativePath) {
		String sep = System.getProperty("file.separator");
		return getResourceURLs("conf" + sep + relativePath);
	}

	/**
	 * Returns an array of readers for all files with the given name
	 * (prefixed with <code>conf/</conf>).<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path, without "conf" prefix
	 * @return array of corresponding readers
	 */
	public static Reader[] getConfAsStreams(String relativePath) {
		String sep = System.getProperty("file.separator");
		return getResourceAsStreams("conf" + sep + relativePath);
	}

	/**
	 * Returns a reader for the first file with the given name.<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path
	 * @return corresponding reader
	 */
	public static Reader getResourceAsStream(String relativePath) {
		try {
			return new InputStreamReader(
				getResourceURL(relativePath).openStream());
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Returns a URL for the first file with the given name.<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path
	 * @return corresponding URL
	 */
	public static URL getResourceURL(String relativePath) {
		relativePath = "/" + relativePath;
		try {
			// for JARs and directories on the classpath
			URL url = SystemUtilities.class.getResource(relativePath);
			//URL url = ClassLoader.getSystemResource(relativePath);
			if (url == null) {
				// for parent directories
				for (Enumeration<URL> enumer = ClassLoader.getSystemResources(".");
					enumer.hasMoreElements();
					) {
					URL u = (URL) enumer.nextElement();
					File dir = new File(u.getFile()).getParentFile();
					File file = new File(dir, relativePath);
					if (file.exists()) {
						url = file.toURI().toURL();
						break;
					}
				}
			}
			return url;
		} catch (Exception ex) {
			Log.error(ex);
		}
		return null;
	}

	/**
	 * Returns an array of URLs for all files with the given name.<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path
	 * @return array of corresponding URLs
	 */
	public static URL[] getResourceURLs(String relativePath) {
		List<URL> urls = new ArrayList<URL>();
		try {
			// "de" necessary for working with Jars
			for (Enumeration<URL> enumer = ClassLoader.getSystemResources("de");
				enumer.hasMoreElements();
				) {
				String u = ((URL) enumer.nextElement()).toExternalForm();
				u = u.substring(0, u.lastIndexOf("/"));
				InputStream is = null;
				try {
					URL url = new URL(u + "/" + relativePath);
					is = url.openStream();
					urls.add(url);
				} catch (Exception ex) {
				} finally {
					if (is != null)
						is.close();
				}
				u = u.substring(0, u.lastIndexOf("/"));
				is = null;
				try {
					URL url = new URL(u + "/" + relativePath);
					is = url.openStream();
					urls.add(url);
				} catch (Exception ex) {
				} finally {
					if (is != null)
						is.close();
				}
			}
		} catch (Exception ex) {
			Log.error(ex);
		}
		return ListUtils.toArray(urls, URL.class);
	}

	/**
	 * Returns an array of readers for all files with the given name.<p>
	 *
	 * This method scans the classpath. For each JAR, the file must be
	 * located in the beginning (<code>jar:file://...!/file</code>). For
	 * each directory, the file must be located in that directoy or in its
	 * direct parent directory.
	 *
	 * @param relativePath relative path
	 * @return array of corresponding readers
	 */
	public static Reader[] getResourceAsStreams(String relativePath) {
		List<Reader> streams = new ArrayList<Reader>();
		try {
			// "de" necessary for working with Jars
			for (Enumeration<URL> enumer = ClassLoader.getSystemResources("de");
				enumer.hasMoreElements();
				) {
				String u = ((URL) enumer.nextElement()).toExternalForm();
				u = u.substring(0, u.lastIndexOf("/"));
				try {
					URL url = new URL(u + "/" + relativePath);
					streams.add(new InputStreamReader(url.openStream()));
				} catch (Exception ex) {
				}
				u = u.substring(0, u.lastIndexOf("/"));
				try {
					URL url = new URL(u + "/" + relativePath);
					streams.add(new InputStreamReader(url.openStream()));
				} catch (Exception ex) {
				}
			}
		} catch (Exception ex) {
			Log.error(ex);
		}
		return (Reader[]) streams.toArray(new Reader[streams.size()]);
	}

}//SystemUtilities
