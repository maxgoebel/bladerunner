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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IThread;

public class ATFBreakpointWaiter extends ThreadEventWaiter {

	private final IBreakpoint _bkp;

	public ATFBreakpointWaiter(IBreakpoint bkp) {
		super(null, DebugEvent.SUSPEND, DebugEvent.BREAKPOINT);

		_bkp = bkp;
	}

	@Override
	protected boolean matches(DebugEvent evt) {

		// Scans the list of breakpoints hit by the thread
		// to see if our breakpoint is among them.
		IThread source = (IThread) evt.getSource();
		IBreakpoint[] bkps = source.getBreakpoints();

		for (IBreakpoint bkp : bkps) {
			if (bkp.equals(_bkp)) {
				return true;
			}
		}

		return false;
	}
}
