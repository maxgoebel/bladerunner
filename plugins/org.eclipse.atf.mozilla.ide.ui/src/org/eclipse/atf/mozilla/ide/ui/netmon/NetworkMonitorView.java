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

package org.eclipse.atf.mozilla.ide.ui.netmon;

import org.eclipse.atf.mozilla.ide.ui.browser.views.BrowserBoundView;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPageBookViewPage;

public class NetworkMonitorView extends BrowserBoundView {

	public static final String ID = "org.eclipse.atf.mozilla.ide.ui.views.netmon";

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.browser.views.BrowserBoundView#getAdapterClass()
	 */
	public Class getAdapterClass() {
		return INetworkMonitor.class;
	}

	public void setupPage(IPageBookViewPage page, IWorkbenchPart part) {
	}
}
