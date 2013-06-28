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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;

public class BreakpointWaiter implements IBreakpointListener {

	public static final Object ADDITION = new Object();
	public static final Object REMOVAL = new Object();

	private int _line;
	private IResource _resource;
	private IBreakpoint _bkp;

	private final Object _mode;
	private final CountDownLatch _latch = new CountDownLatch(1);

	public BreakpointWaiter(IResource res, int line, Object mode) {
		setResource(res);
		setLine(line);
		_mode = mode;
		registerAsListener();
	}

	public BreakpointWaiter(IBreakpoint bkp, Object mode) {
		setBkp(bkp);
		_mode = mode;
		registerAsListener();
	}

	private void registerAsListener() {
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(
				this);
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		matchBkp(breakpoint, ADDITION);
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		matchBkp(breakpoint, REMOVAL);
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	private void matchBkp(IBreakpoint breakpoint, Object expectedMode) {

		try {

			if (expectedMode != _mode) {
				return;
			}

			boolean matches = false;

			if (getBkp() != null) {
				matches = breakpoint.equals(getBkp());
			} else {
				matches = matchByInformation(breakpoint);
			}

			if (matches) {
				matched();
			}

		} catch (CoreException ex) {
			ex.printStackTrace();
		}
	}

	private boolean matchByInformation(IBreakpoint bkp) throws CoreException {
		IMarker marker = bkp.getMarker();
		if (marker == null) {
			return false;
		}

		IResource resource = marker.getResource();
		Integer line = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);
		if (resource == null || line == null) {
			return false;
		}

		return line.equals(getLine()) && resource.equals(getResource());
	}

	private void matched() {
		DebugPlugin.getDefault().getBreakpointManager()
				.removeBreakpointListener(this);
		_latch.countDown();
	}

	public boolean await(int milliseconds) {
		try {
			return _latch.await(milliseconds, TimeUnit.MILLISECONDS);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return false;
		}
	}

	private synchronized void setLine(int line) {
		_line = line;
	}

	private synchronized void setResource(IResource res) {
		_resource = res;
	}

	private synchronized IResource getResource() {
		return _resource;
	}

	private synchronized int getLine() {
		return _line;
	}

	private synchronized IBreakpoint getBkp() {
		return _bkp;
	}

	private synchronized void setBkp(IBreakpoint _bkp) {
		this._bkp = _bkp;
	}
}
