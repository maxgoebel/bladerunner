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
package org.eclipse.atf.debug.testplugin;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ForkedTestSuiteTest extends TestCase {

	private static volatile int fCurrentTest = 0;

	public ForkedTestSuiteTest() {
		super(ForkedTestSuiteTest.class.getName());
	}

	public static Test suite() {
		ForkedTestSuite suite = new ForkedTestSuite();
		TestSuite async = new TestSuite();
		TestSuite sync = new TestSuite() {
			public boolean runSynchronously() {
				return true;
			}
		};

		async.addTest(new StandardTest(0));
		async.addTest(new StandardTest(1));
		async.addTest(new StandardTest(2));
		async.addTest(new StandardTest(3));
		async.addTestSuite(TestClass.class);
		async.addTest(new StandardTest(5));

		sync.addTest(new SyncTest(Thread.currentThread()));

		suite.addTest(async);
		suite.addTest(sync);

		return suite;
	}

	public static class TestClass extends TestCase {

		public void testCorrectOrder() {
			assertEquals(4, fCurrentTest);
			fCurrentTest++;
			System.out.println(fCurrentTest);
		}
	}

	public static class StandardTest extends TestCase {

		private final int fIntendedPos;

		public StandardTest(int intentedPos) {
			super("testCorrectOrder");
			fIntendedPos = intentedPos;
		}

		public void testCorrectOrder() {
			assertEquals(fIntendedPos, fCurrentTest);
			fCurrentTest++;
			System.out.println(fCurrentTest);
		}
	}

	public static class SyncTest extends TestCase {

		private final Thread fT;

		public SyncTest(Thread t) {
			super("testRanSynchronously");
			fT = t;
			System.out.println("Ran synchronously.");
		}

		public void testRanSynchronously() {
			assertEquals(fT, Thread.currentThread());
		}
	}
}
