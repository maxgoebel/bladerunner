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
package org.eclipse.atf.debug.test.breakpoint;

import org.eclipse.atf.debug.test.AbstractATFTest;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;

/**
 * Basic test cases for breakpoints.
 */
public class BreakpointTest extends AbstractATFTest {

	/**
	 * Sets a breakpoint in a JS file included from an HTML file, and tests if
	 * it works.
	 */
	public void testBreakpointOnSeparateJSFile() throws Throwable {
		runSimpleBkpTest(new SimpleBkpTestData("Breakpoints", "test.js", 14, 3, //$NON-NLS-1$ //$NON-NLS-2$
				new String[] { "simple" })); //$NON-NLS-1$
	}

	/**
	 * Eval special case - tests if a user breakpoint set on PC0 of a function
	 * stops correctly.
	 */
	public void testBreakpointOnPC0ShouldStop() throws Throwable {
		runSimpleBkpTest(new SimpleBkpTestData("Breakpoints", //$NON-NLS-1$
				"test.js", 12, 3, new String[] { "simple" })); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Sets a breakpoint in a JavaScript snippet embedded into an HTML file, and
	 * checks if it works.
	 * 
	 * This test should fail until the bug in the Mozilla engine for dealing
	 * with embedded JavaScript is fixed.
	 */
	public void testBreakpointOnSameFile() throws Throwable {
		runSimpleBkpTest(new SimpleBkpTestData("Breakpoints", //$NON-NLS-1$
				"Breakpoints.html", 31, 5, new String[] { "callback" })); //$NON-NLS-1$ //$NON-NLS-2$
	}

	// Breakpoint test "template".
	private void runSimpleBkpTest(final SimpleBkpTestData data)
			throws Throwable {
		IBreakpoint jsBkp = getHelper().ensureBreakpoint(data._bkpFile,
				data._bkpLine);
		IThread thread = getHelper().launchToBreakpoint(jsBkp,
				"Breakpoints", true); //$NON-NLS-1$

		getHelper().verifyThreadState(thread, data._bkpLine, data._stackLength,
				data._functionNames);
	}

	@Override
	public void tearDown() {
		try {
			getHelper().terminateAndWait();
		} catch (Throwable t) {
			t.printStackTrace();
			fail();
		}
	}

	/**
	 * Parameter aggregator for breakpoint tests.
	 */
	private class SimpleBkpTestData {
		public String _launchConfig;

		public String _bkpFile;
		public int _bkpLine;

		public int _stackLength;
		public String[] _functionNames;

		public SimpleBkpTestData(String config, String fileName, int line,
				int stackLength, String[] functionNames) {
			_launchConfig = config;
			_bkpFile = fileName;
			_bkpLine = line;
			_stackLength = stackLength;
			_functionNames = functionNames;
		}
	}
}
