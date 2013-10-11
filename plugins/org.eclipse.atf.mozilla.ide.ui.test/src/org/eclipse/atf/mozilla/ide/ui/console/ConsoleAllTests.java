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
package org.eclipse.atf.mozilla.ide.ui.console;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ConsoleAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.eclipse.atf.mozilla.ide.ui.console");
		suite.addTestSuite(BrowserInitTests.class);
		suite.addTestSuite(JavaScriptConsolePageTest.class);
		return suite;
	}

}