package org.eclipse.atf.mozilla.ide.ui.netmon;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.BrowserCreatedEvent;
import org.eclipse.atf.mozilla.ide.ui.browser.BrowserDisposedEvent;

/**
 * Manages call lists for living browsers
 * 
 */
public class CallListsManager implements IApplicationEventListener {

	private Map<IWebBrowser, EventRecorder> callLists = new HashMap<IWebBrowser, EventRecorder>();

	public EventRecorder getCallList(IWebBrowser browser) {
		return callLists.get(browser);
	}

	private void browserCreated(IWebBrowser webBrowser) {
		EventRecorder callList = new EventRecorder();
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(webBrowser, callList);
		callLists.put(webBrowser, callList);
	}

	private void browserDisposed(IWebBrowser webBrowser) {
		EventRecorder callList = callLists.remove(webBrowser);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(webBrowser, callList);

	}

	public void onEvent(IApplicationEvent event) {
		if (event instanceof BrowserCreatedEvent)
			browserCreated(event.getBrowser());
		else if (event instanceof BrowserDisposedEvent)
			browserDisposed(event.getBrowser());

	}

}
