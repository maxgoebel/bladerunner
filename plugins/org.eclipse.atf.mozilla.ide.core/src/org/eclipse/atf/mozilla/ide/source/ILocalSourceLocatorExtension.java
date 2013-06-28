package org.eclipse.atf.mozilla.ide.source;

/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 *     Zend Technologies - initial API and implementation 
 *******************************************************************************/
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IStorage;

/**
 * This class resolves whether the extension is valid for this scenario
 * 
 * @author Roy, 2010
 * 
 */
public interface ILocalSourceLocatorExtension {

	/**
	 * 
	 * @param url
	 * @param basePathHint
	 * @param projectHint
	 * @param source
	 * @return true if the resolver finds that the context is relevant
	 */
	public boolean isValid(URL url, String basePathHint, IProject projectHint,
			IStorage source);

	/**
	 * @return the context of this resolver
	 */
	public ILocalSourceLocatorContext createContext();

}
