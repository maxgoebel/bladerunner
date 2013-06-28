/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.source;

import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

/**
 * 
 * @author Roy, 2010
 * 
 */
public class LocalSourceLocatorContext implements
		ILocalSourceLocatorContext {

	private final URL url;
	private final IStorage storage;
	private final String basePath;
	private final IProject project;

	public LocalSourceLocatorContext(URL url, IStorage storage,
			String basePath, IProject project) {
		super();
		this.url = url;
		this.storage = storage;
		this.basePath = basePath;
		this.project = project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.atf.mozilla.ide.source.ILocalSourceLocatorContext#getURL()
	 */
	public URL getURL() {
		return url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.atf.mozilla.ide.source.ILocalSourceLocatorContext#getStorage
	 * ()
	 */
	public IStorage getStorage() {
		return storage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.atf.mozilla.ide.source.ILocalSourceLocatorContext#getBasePath
	 * ()
	 */
	public String getBasePath() {
		return basePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.atf.mozilla.ide.source.ILocalSourceLocatorContext#getProject
	 * ()
	 */
	public IProject getProject() {
		return project;
	}

}
