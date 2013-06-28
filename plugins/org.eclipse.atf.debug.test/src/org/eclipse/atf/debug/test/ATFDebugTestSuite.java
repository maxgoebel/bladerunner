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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.atf.debug.test.breakpoint.BreakpointTest;
import org.eclipse.atf.debug.test.breakpoint.SteppingTest;
import org.eclipse.atf.debug.testplugin.ATFDebugCDLatch;
import org.eclipse.atf.debug.testplugin.ForkedTestSuite;

public class ATFDebugTestSuite extends TestCase {

	public ATFDebugTestSuite() {
		super(ATFDebugTestSuite.class.getName());
	}

	public static Test suite() {
		ForkedTestSuite forked = new ForkedTestSuite(ATFDebugCDLatch
				.getFactory());

		// These will run on a separate thread.
		TestSuite async = new TestSuite();

		// These will run on the main thread.
		TestSuite sync = new SyncTestSuite();

		// Test List.
		sync.addTestSuite(ProjectCreatorTest.class);
		async.addTestSuite(BreakpointTest.class);
		async.addTestSuite(SteppingTest.class);

		forked.addTest(sync);
		forked.addTest(async);

		return forked;
	}

	public static class SyncTestSuite extends TestSuite {
		public boolean runSynchronously() {
			return true;
		}
	}
}