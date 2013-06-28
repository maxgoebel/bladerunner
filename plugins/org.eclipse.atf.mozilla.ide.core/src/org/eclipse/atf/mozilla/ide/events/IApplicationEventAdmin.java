/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.events;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;

public interface IApplicationEventAdmin {

	/**
	 * Asynchroniously notifies listeners
	 * @param event
	 */
	void postEvent(IApplicationEvent event);

	/**
	 * Synchroniously notifies listeners
	 * @param event
	 */
	void sendEvent(IApplicationEvent event);

	public void addEventListener(IWebBrowser browser, IApplicationEventListener listener);

	public void removeEventListener(IWebBrowser browser, IApplicationEventListener listener);

	void dispose(IWebBrowser browser);

	/**
	 * Registers listener for notifications from all browsers
	 * 
	 * @param listener
	 */
	void addEventListener(IApplicationEventListener listener);

	void removeEventListener(IApplicationEventListener listener);
}
