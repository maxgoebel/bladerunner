package org.eclipse.atf.mozilla.ide.ui.internal.browser.support;

import org.eclipse.ui.browser.IWebBrowser;

public interface BrowserListener {

	public void browserCreated(IWebBrowser browser);

	public void browserClosed(IWebBrowser browser);

}
