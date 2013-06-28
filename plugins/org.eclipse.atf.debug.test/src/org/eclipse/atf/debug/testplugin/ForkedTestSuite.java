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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class ForkedTestSuite implements Test {

	public static final String RUN_SYNCHRONOUSLY = "runSynchronously";

	private final ExecutorService fExecutor = Executors
			.newSingleThreadExecutor();

	private final List<Test> fTests = Collections
			.synchronizedList(new ArrayList<Test>());

	private final LatchFactory fFactory;

	public ForkedTestSuite() {
		this(new DefaultLatchFactory());
	}

	public ForkedTestSuite(LatchFactory factory) {
		fFactory = factory;
	}

	/**
	 * Adds a test to the suite.
	 */
	public void addTest(Test test) {
		fTests.add(test);
	}

	/**
	 * Adds the tests from the given class to the suite
	 */
	public void addTestSuite(Class<? extends TestCase> testClass) {
		addTest(new TestSuite(testClass));
	}

	public void run(TestResult result) {
		waitUntilFinished(runSuite(result));
	}

	private ForkedSuiteCDLatch runSuite(final TestResult result) {
		ForkedSuiteCDLatch latch;
		try {
			latch = fFactory.createCountDownLatch(fTests.size());
		} catch (Exception ex) {
			// Fallback on exception
			ex.printStackTrace();
			DefaultLatchFactory def = new DefaultLatchFactory();
			latch = def.createCountDownLatch(fTests.size());
		}

		for (final Test test : fTests) {

			Runnable runnable = getTestRunnable(test, result, latch);
			Boolean sync = runsSynchronously(test, result);

			if (sync == null) {
				continue;
			}

			if (sync) {
				runnable.run();
			} else {
				fExecutor.execute(runnable);
			}
		}

		return latch;
	}

	private Boolean runsSynchronously(Test test, TestResult result) {
		Method method;

		try {
			method = test.getClass().getMethod(RUN_SYNCHRONOUSLY,
					new Class[] {});
			Boolean bool = (Boolean) method.invoke(test, new Object[] {});
			return bool;
		} catch (NoSuchMethodException ex) {
			return false;
		} catch (Exception ex) {
			result.addError(test, ex);
			return null;
		}
	}

	private Runnable getTestRunnable(final Test test, final TestResult result,
			final ForkedSuiteCDLatch latch) {
		return new Runnable() {
			public void run() {
				test.run(result);
				latch.countDown();
			}
		};
	}

	private void waitUntilFinished(ForkedSuiteCDLatch latch) {
		try {
			latch.await();
			fExecutor.shutdown();
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public int countTestCases() {
		int total = 0;
		for (final Test test : fTests) {
			total += test.countTestCases();
		}

		return total;
	}

	public static interface LatchFactory {
		public ForkedSuiteCDLatch createCountDownLatch(int count)
				throws Exception;
	}

	public static interface ForkedSuiteCDLatch {
		public void countDown();

		public void await() throws InterruptedException;
	}

	private static class DefaultLatchFactory implements LatchFactory {
		public ForkedSuiteCDLatch createCountDownLatch(int count) {
			return new DefaultCDLatch(count);
		}
	}

	private static class DefaultCDLatch implements ForkedSuiteCDLatch {

		private final CountDownLatch fLatch;

		public DefaultCDLatch(int count) {
			fLatch = new CountDownLatch(count);
		}

		public void await() throws InterruptedException {
			fLatch.await();
		}

		public void countDown() {
			fLatch.countDown();
		}
	}
}
