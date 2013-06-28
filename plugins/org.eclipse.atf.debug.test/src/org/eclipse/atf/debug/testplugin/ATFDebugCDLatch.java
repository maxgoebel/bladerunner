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

import org.eclipse.atf.debug.testplugin.ForkedTestSuite.ForkedSuiteCDLatch;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.Workbench;

public class ATFDebugCDLatch implements ForkedTestSuite.ForkedSuiteCDLatch {

	private final Display fDisplay;

	private volatile int fTestCount;

	private ATFDebugCDLatch(int count, Display display) {
		fDisplay = display;
		fTestCount = count;
	}

	public void countDown() {
		fTestCount = Math.max(0, fTestCount - 1);
	}

	public void await() throws InterruptedException {
		while (fTestCount != 0 && !Workbench.getInstance().isClosing()) {
			try {
				if (!fDisplay.readAndDispatch()) {
					fDisplay.sleep();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} catch (Error err) {
				throw err;
			}
		}
	}

	private static final ForkedTestSuite.LatchFactory fFactory = new ForkedTestSuite.LatchFactory() {

		public ForkedSuiteCDLatch createCountDownLatch(int count)
				throws Exception {
			Display disp = Display.getCurrent();
			if (disp == null) {
				throw new IllegalStateException(
						"ATFDebugCDLatch must be constructed from the UI thread.");
			}

			return new ATFDebugCDLatch(count, disp);
		}

	};

	public static ForkedTestSuite.LatchFactory getFactory() {
		return fFactory;
	}
}
