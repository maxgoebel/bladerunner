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
 * Context for the source locator resolver
 * 
 * @author Roy, 2010
 * 
 */
public interface ILocalSourceLocatorContext {

	/**
	 * @return the URL of the request
	 */
	public abstract URL getURL();

	/**
	 * @return local storage (hint)
	 */
	public abstract IStorage getStorage();

	/**
	 * @return the base path (hint)
	 */
	public abstract String getBasePath();

	/**
	 * @return the project (hint)
	 */
	public abstract IProject getProject();

}
