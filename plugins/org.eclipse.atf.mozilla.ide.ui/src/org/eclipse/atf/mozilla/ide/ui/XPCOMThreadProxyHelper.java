/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. - ongoing enhancements
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui;

import org.eclipse.atf.mozilla.ide.core.IXPCOMThreadProxyHelper;
import org.eclipse.swt.widgets.Display;

/**
 * Defines an environment in which to execute Runnables.  Used
 * by XPCOM code to delegate proxies such that they are all executed
 * on the same thread.
 *
 * @author peller
 */
public class XPCOMThreadProxyHelper implements IXPCOMThreadProxyHelper {
	private Display _display;

	public XPCOMThreadProxyHelper(Display display) {
		_display = display;
	}

	public Thread getThread() {
		return _display.getThread();
	}

	public void syncExec(Runnable runnable) {
		_display.syncExec(runnable);
	}
};