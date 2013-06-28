/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.core.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;

import org.eclipse.atf.mozilla.ide.core.MozideCorePlugin;
import org.eclipse.atf.mozilla.ide.source.ILocalSourceLocatorContext;
import org.eclipse.atf.mozilla.ide.source.ILocalSourceLocatorExtension;
import org.eclipse.atf.mozilla.ide.source.LocalSourceLocatorResolver;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;

public class SourceLocatorUtil {

	protected static SourceLocatorUtil instance = null;

	public static SourceLocatorUtil getInstance() {
		if (instance == null)
			instance = new SourceLocatorUtil();

		return instance;
	}

	protected SourceLocatorUtil() {

	}

	/**
	 * Returns the element that represented by the URL. It tries to match it to
	 * a local resource and if not found, then it returns a reference to a
	 * remote artifact.
	 * 
	 * @param sourceURL
	 *            the ulr to the resource (no host and port)
	 * @param basePathHint
	 *            (optional) portion of the ulr's path that maps to the root of
	 *            the project
	 * @param projectHint
	 *            (optional) project where the resources should be available
	 */
	public IStorage getSourceElement(URL sourceURL, String basePathHint,
			IProject projectHint) {
		// first try to find it locally
		IStorage source = findLocalResource(sourceURL, basePathHint,
				projectHint);

		source = checkCompatibility(sourceURL, basePathHint, projectHint,
				source);

		// try to get as remote resource
		if (source == null) {
			source = findRemoteResource(sourceURL);
		}

		return source;

	}

	/**
	 * Checks the compatibility of other server side languages
	 * 
	 * @param sourceURL
	 * @param basePathHint
	 * @param projectHint
	 * @param source
	 * @return the storage
	 */
	private IStorage checkCompatibility(URL sourceURL, String basePathHint,
			IProject projectHint, IStorage source) {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg
				.getConfigurationElementsFor("org.eclipse.atf.mozilla.ide.core.resolver");
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement element = extensions[i];
			try {
				ILocalSourceLocatorExtension ext = (ILocalSourceLocatorExtension) element
						.createExecutableExtension("class");

				boolean valid = ext.isValid(sourceURL, basePathHint,
						projectHint, source);
				if (valid) {
					ILocalSourceLocatorContext context = ext.createContext();
					LocalSourceLocatorResolver resolver = (LocalSourceLocatorResolver) element
							.createExecutableExtension("resolver");
					resolver.setContext(context);
					source = resolver.resolve();
				}

			} catch (CoreException e) {
				MozideCorePlugin.log(e);
			}
		}
		return source;
	}

	public IStorage getSourceElement(URL sourceURL) {
		return getSourceElement(sourceURL, null, null);
	}

	/**
	 * This method tries to resolve the URL to a local resource. Returns null if
	 * not found.
	 * 
	 * 
	 * @param url
	 *            the ulr to the resource (no host and port)
	 * @param basePathHint
	 *            (optional) portion of the ulr's path that maps to the root of
	 *            the project
	 * @param projectHint
	 *            (optional) project where the resources should be available
	 */
	private IFile findLocalResource(URL url, String basePathHint,
			IProject projectHint) {
		IFile res = null;
		String decodedString;
		try {
			decodedString = URLDecoder.decode(url.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			decodedString = url.toString();
		}
		Path urlAsPath = new Path(decodedString);
		// if the basePathHint is not null and contained in the urlPath, convert
		// to relative
		if (basePathHint != null && !"".equals(basePathHint)) {

			Path basePath = new Path(basePathHint);

			// contruct the relative path by removing the base
			// assumming that basepath maps to project's root
			if (basePath.isPrefixOf(urlAsPath)) {
				IPath relativePathToResource = stripParameters(urlAsPath
						.removeFirstSegments(basePath.segmentCount()));

				if (projectHint != null) {

					// find the resource in the project
					res = getResource(projectHint, relativePathToResource);

				} else {
					// assume basePathHint last segment contains the name of the
					// project.
					String possibleProjectName = basePath.lastSegment();
					IProject possibleProject = ResourcesPlugin.getWorkspace()
							.getRoot().getProject(possibleProjectName);

					if (possibleProject != null && possibleProject.exists()) {

						// find the resource in the project
						res = getResource(possibleProject,
								relativePathToResource);

					}
				}
			}

			if (res != null) {
				return res;
			}
		}

		// at this point the basePathHint did not work, try finding using the
		// name of the project as the basePath
		if (projectHint != null) {
			Path basePath = new Path(projectHint.getName());

			// contruct the relative path by removing the base
			// assumming that basepath maps to project's root
			if (basePath.isPrefixOf(urlAsPath)) {
				IPath relativePathToResource = urlAsPath
						.removeFirstSegments(basePath.segmentCount());

				// find the resource in the project
				res = getResource(projectHint, relativePathToResource);

			}
			// else{} maybe do an else here to search in the project with the
			// entire url

			if (res != null) {
				return res;
			}
		}

		// if there are no hints, then assume the first segment of the path is
		// the project name and the rest is the path to resource
		IPath pathPortionOfURL = new Path(url.getPath());
		String possibleProjectName = pathPortionOfURL.segment(0);
		if (possibleProjectName != null) {
			IProject possibleProject = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(possibleProjectName);

			if (possibleProject != null && possibleProject.exists()) {
				IPath relativePathToResource = pathPortionOfURL
						.removeFirstSegments(1); // remove only the first
													// segment

				// find the resource in the project
				res = getResource(possibleProject, relativePathToResource);
			}
		}

		// check if URL is the absolute filesystem path to the workspace
		// resource
		URI workspace = ResourcesPlugin.getWorkspace().getRoot()
				.getLocationURI();
		URI resURI;
		try {
			resURI = new URI(url.toString());
		} catch (URISyntaxException e) {
			return null;
		}

		URI relative = workspace.relativize(resURI);
		if (!relative.isAbsolute()) {
			IPath path = new Path(relative.toString());
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file.exists())
				return file;
		}

		return res;
	}

	/**
	 * This method calculates the full absolute path relative to the Project. In
	 * the case that the project has a designated folder that contains all the
	 * Web content (i.e. WebContent), this is determined and added to the path
	 * so that we can find the resource.
	 * 
	 * @param project
	 *            project that should contain the resource
	 * @param pathToResource
	 *            path to a resource derived from the URL that might not contain
	 *            the full path relative to the project root
	 * @return
	 */
	protected IFile getResource(IProject project, IPath pathToResource) {
		IFile file = null;

		if (project != null) {
			file = project.getFile(pathToResource);

			if (file.exists())
				return file;
		}

		if (pathToResource.segmentCount() > 1) {
			file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(pathToResource);
		}

		if (file != null && file.exists())
			return file;

		return null;
	}

	private IPath stripParameters(IPath urlAsPath) {
		IPath base = urlAsPath.removeLastSegments(1);

		String fileName = urlAsPath.lastSegment();

		if (fileName == null)
			return urlAsPath;

		int idx = fileName.indexOf('?');

		if (idx != -1) {
			fileName = fileName.substring(0, idx);
		}

		return base.append(fileName);
	}

	private IStorage findRemoteResource(URL url) {
		return new URLStorage(url);
	}

	/**
	 * This method gets the content of the resource that maps to the URL. It
	 * tried to find it in the local workspace and if not found, it used a URL
	 * connection.
	 * 
	 * @param sourceURL
	 *            the ulr to the resource (no host and port)
	 * @param basePathHint
	 *            (optional) portion of the ulr's path that maps to the root of
	 *            the project
	 * @param projectHint
	 *            (optional) project where the resources should be available
	 * @return stream with content
	 * @throws CoreException
	 */
	public InputStream getSourceContent(URL sourceURL, String basePathHint,
			IProject projectHint) throws CoreException {
		IStorage storage = getSourceElement(sourceURL, basePathHint,
				projectHint);
		return storage.getContents();
	}

	public URLStorage getURLStorage(String location) {
		URLStorage storage = null;
		try {
			URL url = new URL(location);
			storage = new URLStorage(url);
		} catch (MalformedURLException mue) {
			// return null
		}
		return storage;
	}

	protected class URLStorage extends PlatformObject implements IStorage {

		private URL _url;

		public URLStorage(URL url) {
			_url = url;
		}

		public InputStream getContents() throws CoreException {
			try {
				URLConnection conn = _url.openConnection();
				conn.connect();
				return new BufferedInputStream(conn.getInputStream());
			} catch (MalformedURLException mue) {
				throw new CoreException(new Status(IStatus.ERROR,
						MozideCorePlugin.PLUGIN_ID, IStatus.ERROR,
						"Malformed source URL" /* TODO:i18n */, mue));
			} catch (IOException ioe) {
				throw new CoreException(new Status(IStatus.ERROR,
						MozideCorePlugin.PLUGIN_ID, IStatus.ERROR,
						"I/O Exception reading source" /* TODO:i18n */, ioe));
			}
		}

		public IPath getFullPath() {
			return new Path(this._url.toString());
		}

		public URL getURL() {
			return _url;
		}

		public String getName() {
			return _url.toString();
		}

		public boolean isReadOnly() {
			return true;
		}

		public boolean equals(Object obj) {
			if (obj instanceof URLStorage) {
				return ((URLStorage) obj).getURL().equals(getURL());
			}
			return super.equals(obj);
		}

		public int hashCode() {
			return getURL().hashCode();
		}

		public Object getAdapter(Class adapter) {
			if (URL.class.equals(adapter))
				return getURL();
			return super.getAdapter(adapter);
		}
	}
}
