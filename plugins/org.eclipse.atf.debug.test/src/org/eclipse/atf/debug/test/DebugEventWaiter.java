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

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;

public class DebugEventWaiter implements IDebugEventSetListener {

	private final CountDownLatch _latch = new CountDownLatch(1);

	private final Integer _kind;

	private final Integer _detail;

	private final Class<? extends Object> _sourceClass;

	private Object _source;

	public DebugEventWaiter(Integer kind, Integer detail) {
		this(kind, detail, Object.class);
	}

	public DebugEventWaiter(Integer kind, Integer detail,
			Class<? extends Object> sourceClass) {
		_kind = kind;
		_detail = detail;
		_sourceClass = sourceClass;

		// I'd be wary of leaking references in a constructor,
		// but since the JDT folks do similar thing, and since
		// this is test code, I won't worry about this now.
		DebugPlugin.getDefault().addDebugEventListener(this);
	}

	public void handleDebugEvents(DebugEvent[] events) {
		for (DebugEvent evt : events) {
			if (typeMatches(evt) && matches(evt)) {
				DebugPlugin.getDefault().removeDebugEventListener(this);
				setSource(evt.getSource());
				_latch.countDown();
				break;
			}
		}
	}

	public boolean doWait(long timeout) throws Exception {
		return _latch.await(timeout, TimeUnit.MILLISECONDS);
	}

	protected boolean typeMatches(DebugEvent evt) {
		boolean matches = true;
		if (_kind != null) {
			matches &= (evt.getKind() == _kind);
		}

		return isEqual(_kind, evt.getKind())
				&& isEqual(_detail, evt.getDetail())
				&& _sourceClass.isAssignableFrom(evt.getSource().getClass());
	}

	private boolean isEqual(Integer i1, Integer i2) {
		return (i1 == null) ? true : i2.equals(i1);
	}

	private synchronized void setSource(Object source) {
		_source = source;
	}

	public synchronized Object getSource() {
		return _source;
	}

	protected boolean matches(DebugEvent evt) {
		return true;
	}

}