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

import org.eclipse.core.resources.IStorage;

/**
 * Applies the IStorage and rules for this context
 * 
 * @author Roy, 2010
 */
public abstract class LocalSourceLocatorResolver {

	private ILocalSourceLocatorContext context;

	public ILocalSourceLocatorContext getContext() {
		return context;
	}

	public void setContext(ILocalSourceLocatorContext context) {
		this.context = context;
	}

	public abstract IStorage resolve();

}
