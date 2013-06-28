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
package org.eclipse.atf.mozilla.ide.ui.netmon.model.impl;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.ApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;

public class NetworkEventsPublisher {

	public static final String NETWORK_EVENT = "org.eclipse.atf.mozilla.ide.network";
	public static final String NETWORK_REQUEST = NETWORK_EVENT + "_request";
	public static final String NETWORK_RESPONSE_START = NETWORK_EVENT + "_response_start";
	public static final String NETWORK_RESPONSE_PROGRESS = NETWORK_EVENT + "_response_progress";
	public static final String NETWORK_RESPONSE_END = NETWORK_EVENT + "_response_end";

	private IWebBrowser browser;

	public NetworkEventsPublisher(IWebBrowser browser) {
		this.browser = browser;
	}

	public void postNetworkEvent(String type, long startTime, long endTime, Object data) {
		ITimedEvent event = new ApplicationEvent(browser, type, null, data, startTime, endTime - startTime);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.postEvent(event);

	}
}
