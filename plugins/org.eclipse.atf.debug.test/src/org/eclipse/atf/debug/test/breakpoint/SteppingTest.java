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
import org.eclipse.atf.debug.test.TestHelper;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;

/**
 * Basic test cases for stepping modes.
 */
public class SteppingTest extends AbstractATFTest {

	private static final int BUGGY_ENGINE_FACTOR = 2;

	public void testStepsIntoFunctionCall() throws Throwable {
		runSimpleSteppingTest(18, 4, 22, "another", TestHelper.STEP_INTO);
	}

	public void testStepsOverFunctionCall() throws Throwable {
		// This is the currently broken, expected Mozilla engine behavior.
		runSimpleSteppingTest(18, 2, 16, "load", TestHelper.STEP_OVER);
	}

	public void testRepeatedStepping() throws Throwable {
		IBreakpoint bkp = getHelper().ensureBreakpoint("test.js", 14);

		IThread thread = getHelper().launchToBreakpoint(bkp, "Breakpoints",
				true);

		for (int i = 0; i < (20 * BUGGY_ENGINE_FACTOR); i++) {
			getHelper().step(thread, TestHelper.STEP_OVER);
			getHelper().verifyThreadState(thread, 15 - (i % 2), 3, "simple");
			System.out.println(i);
		}

		getHelper().step(thread, TestHelper.STEP_OVER);
		getHelper().verifyThreadState(thread, 18, 3, "simple");
	}

	public void testRepeatedConcurrentStepping() throws Throwable {
		fail();
	}

	public void testStepReturnFromFunctionCall() {
		fail();
	}

	public void testStepIntoEvaledCode() {
		fail();
	}

	private void runSimpleSteppingTest(int bkpLine, int targetStackSize,
			int targetLine, String targetMethodName,
			TestHelper.IStepper stepMode) throws Throwable {

		IBreakpoint bkp = getHelper().ensureBreakpoint("test.js", bkpLine);

		IThread thread = getHelper().launchToBreakpoint(bkp, "Breakpoints",
				true);

		getHelper().verifyThreadState(thread, bkpLine, 3, "simple");
		getHelper().step(thread, stepMode);
		getHelper().verifyThreadState(thread, targetLine, targetStackSize,
				targetMethodName);
		thread.resume();

	}

	@Override
	public void tearDown() throws Exception {
		getHelper().terminateAndWait();
	}
}
