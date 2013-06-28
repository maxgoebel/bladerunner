/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.ui.source;

import org.eclipse.atf.mozilla.ide.ui.browser.views.BrowserBoundSelectionListenerView;

/**
 * DOM Source view that is bound to a browser instance
 * and listens to DOM node selections.
 *
 */
public class DOMSourceView extends BrowserBoundSelectionListenerView {

	public static final String ID = "org.eclipse.atf.mozilla.ide.ui.views.domsource";
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.browser.views.BrowserBoundView#getAdapterClass()
	 */
	public Class getAdapterClass() {
		return IDOMSourcePage.class;
	}
}
