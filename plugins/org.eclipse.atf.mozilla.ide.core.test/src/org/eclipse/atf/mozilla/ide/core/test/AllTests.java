/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtilTest;

public class AllTests extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.eclipse.atf.mozilla.ide.core");
		suite.addTestSuite(SourceLocatorUtilTest.class);
		return suite;
	}
}
