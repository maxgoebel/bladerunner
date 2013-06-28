package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;

public class BrowserCreatedEvent implements IApplicationEvent {

	private IWebBrowser browser;

	public BrowserCreatedEvent(IWebBrowser browser) {
		this.browser = browser;
	}

	public String getType() {
		return this.getClass().getName();
	}

	public IWebBrowser getBrowser() {
		return browser;
	}

}
