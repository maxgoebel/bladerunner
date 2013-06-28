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
import org.eclipse.debug.core.model.IThread;

public class ThreadEventWaiter extends DebugEventWaiter {

	private final IThread _threadInstance;

	public ThreadEventWaiter(IThread instance, Integer kind, Integer detail) {
		super(kind, detail, IThread.class);
		_threadInstance = instance;
	}

	public ThreadEventWaiter(Integer kind, Integer detail,
			Class<? extends Object> srcClass) {
		this(null, kind, detail);
	}

	@Override
	public IThread getSource() {
		return (IThread) super.getSource();
	}

	@Override
	protected boolean matches(DebugEvent evt) {
		return (_threadInstance == null) ? true : _threadInstance.equals(evt
				.getSource());
	}

}
