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
package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.ApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.events.DOMMutationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.mozilla.interfaces.nsIDOMMutationEvent;

public class DOMEventsPublisher extends DOMDocumentListener {

	public static final String prefix = "org.eclipse.atf.mozilla.ide.ui.browser.DOMEventsPublisher";

	public static final String LOCATION_CHANGED = prefix + ".locationChanged";

	private IWebBrowser browser;

	public DOMEventsPublisher(IWebBrowser browser) {
		this.browser = browser;
	}

	public void locationChanged(String newLocation) {
		ITimedEvent aevent = new ApplicationEvent(browser, LOCATION_CHANGED, null, newLocation, System.currentTimeMillis(), 0);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(aevent);
	}

	public void documentLoaded(DOMDocumentEvent event) {
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(event);
	}

	public void documentUnloaded(DOMDocumentEvent event) {
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(event);
	}

	public void fireAttributeRemoved(nsIDOMMutationEvent event) {
		DOMMutationEvent aevent = DOMMutationEvent.attributeRemoved(browser, event);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(aevent);
	}

	public void fireAttributeModified(nsIDOMMutationEvent event) {
		DOMMutationEvent aevent = DOMMutationEvent.attributeModified(browser, event);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(aevent);
	}

	public void fireAttributeAdded(nsIDOMMutationEvent event) {
		DOMMutationEvent aevent = DOMMutationEvent.attributeAdded(browser, event);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(aevent);
	}

	public void fireNodeRemoved(nsIDOMMutationEvent event) {
		DOMMutationEvent aevent = DOMMutationEvent.nodeRemoved(browser, event);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(aevent);
	}

	public void fireNodeInserted(nsIDOMMutationEvent event) {
		DOMMutationEvent aevent = DOMMutationEvent.nodeInserted(browser, event);
		IApplicationEventAdmin adm = MozIDEUIPlugin.getDefault().getApplicationEventAdmin();
		adm.sendEvent(aevent);
	}

	public void dispose() {
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().dispose(browser);
	}

}
