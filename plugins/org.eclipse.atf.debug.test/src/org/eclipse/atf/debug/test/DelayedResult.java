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
/*
 * DelayedResult.java
 *
 * Created on March 26, 2007, 9:13 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.eclipse.atf.debug.test;

/**
 * DelayedResult is a generic synchronization object, which can be used as a
 * handle to an asynchronous computation.
 * 
 * @see FutureTask
 */

import java.util.concurrent.FutureTask;

public class DelayedResult<V> extends FutureTask<V> {

	public DelayedResult() {
		super(new Runnable() {
			public void run() {
			}
		}, null);
	}

	/**
	 * 
	 */
	@Override
	public synchronized void set(V value) {
		super.set(value);
	}

	/**
	 * 
	 */
	@Override
	public synchronized void setException(Throwable t) {
		super.setException(t);
	}
}
