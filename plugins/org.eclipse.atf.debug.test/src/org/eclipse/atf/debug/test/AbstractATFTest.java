/*******************************************************************************
 * Copyright (c) 2008 nexB Inc. and EasyEclipse.org. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     nexB Inc. and EasyEclipse.org - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.debug.test;

import junit.framework.TestCase;

/**
 * Base implementation for ATF tests.
 */
public abstract class AbstractATFTest extends TestCase {

	private final TestHelper fHelper = new TestHelper();

	protected TestHelper getHelper() {
		return fHelper;
	}

}
