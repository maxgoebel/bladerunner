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
package org.eclipse.atf.mozilla.ide.ui.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.atf.mozilla.ide.ui.console.ConsoleAllTests;
import org.eclipse.atf.mozilla.ide.ui.css.CSSAllTests;

public class AllTests extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for org.eclipse.atf.mozilla.ide.ui");
		suite.addTest(CSSAllTests.suite());
		suite.addTest(ConsoleAllTests.suite());
		return suite;
	}
}
