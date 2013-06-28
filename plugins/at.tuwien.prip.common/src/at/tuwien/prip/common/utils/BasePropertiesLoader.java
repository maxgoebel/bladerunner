/*******************************************************************************
 * Copyright (c) 2013 Max Göbel.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Max Göbel - initial API and implementation
 ******************************************************************************/
package at.tuwien.prip.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import at.tuwien.prip.common.exceptions.PropertyLoadException;
import at.tuwien.prip.common.log.ErrorDump;

/**
 * 
 * BasePropertiesLoader.java
 *
 *
 * Helper class to load base project properties.
 *
 * Created: Jun 19, 2009 8:55:21 PM
 *
 * @author mcg <goebel@gmail.com>
 * @version 1.0
 */
public class BasePropertiesLoader {

	/**
	 * 
	 * Load a particular property.
	 * 
	 * @param key
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String loadProperty (String key) 
	throws PropertyLoadException 
	{
		Properties properties = loadAllProperties();
		return properties.getProperty(key);
	}
	
	/**
	 * Load a path with variable resolution.
	 * 
	 * @param key
	 * @return
	 * @throws PropertyLoadException
	 */
	public static String loadPath (String key) 
	throws PropertyLoadException
	{
		Properties properties = loadAllProperties();
		String path = properties.getProperty(key);
		
		if (path.contains("ROOT")) 
		{
			String root = properties.getProperty("ROOT");
			path = path.replaceAll("ROOT", root);
		}
		if (path.contains("HOME")) 
		{
			path = path.replaceAll("HOME", System.getenv("HOME"));
		}
		return path;
	}

	/**
	 * 
	 * @param source
	 * @param property
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static String loadProperty (String source, String property) 
	throws PropertyLoadException 
	{
		Properties properties = loadAllProperties(source);
		return properties.getProperty(property);
	}
	
	public static int loadPropertyAsInt (String source, String property) 
	throws PropertyLoadException 
	{
		int result = -1;
		Properties properties = loadAllProperties(source);
		String value = properties.getProperty(property).trim();
		
		try {
			
			result = Integer.parseInt(value);
			
		} catch (NumberFormatException e) {
			ErrorDump.error(BasePropertiesLoader.class, "Error formatting property as number: "+property);
			result = -1;
		}
		return result;
	}

	/**
	 *
	 * Load the DOCWRAP project properties.
	 *
	 * try reading from mysql.properties file
	 * the file should be located within the root of the plugins
	 *
	 * @return
	 * @throws PropertyLoadException 
	 * @throws FileNotFoundException 
	 */
	public static Properties loadAllProperties () 
	throws PropertyLoadException  
	{
		return loadAllProperties("docwrap.properties");
	}

	/**
	 * 
	 * Load a specific properties file.
	 * 
	 * @param source
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static Properties loadAllProperties (String source) 
	throws PropertyLoadException 
	{
		Properties properties = null;
		if (!source.endsWith(".properties")) {
			source += ".properties";
		}

		try {

			//search for requested properties file/home/max/dev/projects/tuwien/plugins/at.tuwien.prip.docwrap/conf/
			URL confURL = BasePropertiesLoader.getConfDirectory();
			File confDir = new File(confURL.toURI());
			
			//load requested properties file if available
			File propFile = null;
			for (File file : confDir.listFiles()) {
				if (file.getName().equals(source)) {
					propFile = file;
					break;
				}
			}

			if (propFile==null) {
				throw new PropertyLoadException(
				"BasePropertiesLoader:: cannot find properties file: ");
			}
			
			FileInputStream fstream = null;
			try {
				fstream = new FileInputStream(propFile);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}

			if (fstream != null)
			{
				properties = new Properties ();
				try {
				
					URL resource = propFile.toURI().toURL();      
					properties.load(new InputStreamReader(resource.openStream(), "UTF8"));

//					properties.load (fstream);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			throw new PropertyLoadException(
					"BasePropertiesLoader:: cannot find properties file: "+source);
		}
		
		return properties;
	}

	/**
	 * 
	 * @return
	 */
	public static URL getConfDirectory() {

		URL result = null;

		try 
		{
			result = getBundleDirectory();
			// move one more directory just above the install dir
			File f = new File(result.toURI());
//			f = f.getParentFile();
			String confDirName = f.getAbsoluteFile() + File.separator + "conf";
			File confFile = new File(confDirName);
			boolean success = true;
			if (!confFile.exists() || ! confFile.isDirectory()) {
				if (confFile.exists() && !confFile.isDirectory()) {
					success = confFile.delete();
				}
				if (success) {
					success = confFile.mkdir();
				}
			}
			if (success) {
				result = confFile.toURI().toURL();
			}

		} catch (Exception e) {
			ErrorDump.debug(BasePropertiesLoader.class, "Problems loading conf directory");
		}
		return result;
	}

	/**
	 * 
	 * @param dirName
	 * @return
	 */
	public static URL getInstallationDirectory (String dirName) {

		URL result = null;
		try {
			result = getBundleDirectory();

			// move up until 'plugins'
			File f = new File(result.toURI());
			do {
				f = f.getParentFile();
			} while (!f.getName().equals(dirName));

			String path = f.getAbsolutePath();
			try {
				result = new File(path).toURI().toURL();
			} catch (MalformedURLException e) {
				throw new PropertyLoadException(
				"DocWrapProperties:: cannot find installation directory");
			}

		} catch (PropertyLoadException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 
	 * @return
	 */
	public static URL getBaseInstallDirectory () {
		return getInstallationDirectory("docwrap");
	}

	/**
	 * 
	 * @return
	 */
	public static URL getPluginInstallDirectory () {
		return getInstallationDirectory("plugins");
	}

	/**
	 *
	 * Get the installation directory.
	 *
	 * @return
	 * @throws PropertyLoadException
	 */
	public static URL getBundleDirectory () throws PropertyLoadException {
		
		URL result = null;
		String path = null;
		
		//try loading the RCP bundle first...
		String bundleName = "at.tuwien.prip.docwrap";
		Bundle testerPlugin = Platform.getBundle(bundleName);
		if (testerPlugin!=null) { //eclipse not initialized
			
			URL u = FileLocator.find(testerPlugin, new Path("."), null);

			try 
			{
				path = FileLocator.toFileURL(u).getPath();
				path = path.substring(0,path.length()-2);
				
			} catch (IOException e) {
				throw new PropertyLoadException(
				"Cannot find installation directory");
			}

		} 

		if (path==null) {
			URL tmp = getBaseInstallDirectoryStandalone("plugins");
			if (tmp!=null) {
				path= tmp.getPath() + bundleName;
			}
		}

		try {
			
			result = new File(path).toURI().toURL();
			
		} catch (MalformedURLException e) {
			throw new PropertyLoadException(
					"Cannot find installation directory");
		}
		

		return result;
	}

	public static URL getBaseInstallDirectoryStandalone (String dirName) {
		URL result = null;
		String path = System.getProperty("user.dir");
		File f = new File(path);
		while (!dirName.equals(f.getName())) {
			f = f.getParentFile();
		}
		
		try {
			result = f.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 *
	 * Get the absolute file path to> the RDF data file
	 *
	 * @return
	 * @throws PropertyLoadException
	 */
	public static String[] getAbsoluteRDFDataFileNames (String...strings) 
	throws PropertyLoadException {

		String[] result = null;

		String ontologyPath;
		try {
			ontologyPath = loadAllProperties().getProperty("ontologyPath");


			if (strings==null) {
				strings=new String[1];
				strings[0] = "ontologyFile"; //the default name
			}

			result = new String[strings.length];
			for (int i=0; i<strings.length; i++) {
				String dataFileName = loadAllProperties().getProperty(strings[i]);
				if (dataFileName==null) continue;

				String installDir = getBaseInstallDirectory().getFile();
				StringBuilder sb = new StringBuilder(installDir);
				sb.append(ontologyPath);
				sb.append(dataFileName);
				result[i]=sb.toString();
			}

		} catch (PropertyLoadException e) {
			e.printStackTrace();
		}
		return result;
	}

}//WeblearnProjectProperties
