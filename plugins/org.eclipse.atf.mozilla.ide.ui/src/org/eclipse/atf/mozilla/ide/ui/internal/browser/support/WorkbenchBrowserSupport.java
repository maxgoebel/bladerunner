/*******************************************************************************
 * Copyright (c) 2010 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.internal.browser.support;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.AbstractWorkbenchBrowserSupport;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.BrowserManager;
import org.eclipse.ui.internal.browser.ExternalBrowserInstance;
import org.eclipse.ui.internal.browser.IBrowserDescriptor;
import org.eclipse.ui.internal.browser.IBrowserExt;
import org.eclipse.ui.internal.browser.InternalBrowserEditorInstance;
import org.eclipse.ui.internal.browser.InternalBrowserViewInstance;
import org.eclipse.ui.internal.browser.SystemBrowserDescriptor;
import org.eclipse.ui.internal.browser.SystemBrowserInstance;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.internal.browser.WebBrowserUIPlugin;
import org.eclipse.ui.internal.browser.WebBrowserUtil;

public class WorkbenchBrowserSupport extends AbstractWorkbenchBrowserSupport {

	private static final String DEFAULT_BROWSER_ID_BASE = "org.eclipse.ui.defaultBrowser"; //$NON-NLS-1$
	private static final String HELP_BROWSER_ID = "org.eclipse.help.ui"; //$NON-NLS-1$

	private static Hashtable<String, IWebBrowser> browsers = new Hashtable<String, IWebBrowser>();

	private static Set<BrowserListener> listeners = new HashSet<BrowserListener>();

	private static WorkbenchBrowserSupport instance;

	public static WorkbenchBrowserSupport getInstance() {
		if (instance == null) {
			new WorkbenchBrowserSupport();
		}
		return instance;
	}

	/**
	 * Use getInstance() instead.
	 */
	@Deprecated
	public WorkbenchBrowserSupport() {
		// singleton, but cannot be private to let Eclipse Registry instantiate
		// it
		if (instance == null) {
			instance = this;
		}
	}

	void registerBrowser(IWebBrowser browser) {
		// don't track non-internal browsers, because we have no way of tracking
		// when they're closed
		if (!(browser instanceof InternalWebBrowser)) {
			return;
		}

		browsers.put(browser.getId(), browser);
	}

	void unregisterBrowser(IWebBrowser browser) {
		browsers.remove(browser.getId());
		fireBrowserClosed(browser);
	}

	IWebBrowser findBrowser(String id) {
		return (IWebBrowser) browsers.get(id);
	}

	protected IWebBrowser doCreateBrowser(int style, String browserId,
			String name, String tooltip) throws PartInitException {

		// external browser is not required by called or user
		if (((style & AS_EXTERNAL) == 0)
				&& (WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.INTERNAL)) {
			return new InternalWebBrowser(this, browserId);
		}

		IWebBrowser webBrowser = null;

		// AS_EXTERNAL will force the external browser regardless of the user
		// preference
		// The help editor will open in an editor regardless of the user
		// preferences
		boolean isHelpEditor = ((style & AS_EDITOR) != 0)
				&& HELP_BROWSER_ID.equals(browserId);
		if ((style & AS_EXTERNAL) != 0
				|| (WebBrowserPreference.getBrowserChoice() != WebBrowserPreference.INTERNAL && !isHelpEditor)
				|| !WebBrowserUtil.canUseInternalWebBrowser()) {
			IBrowserDescriptor ewb = BrowserManager.getInstance()
					.getCurrentWebBrowser();
			if (ewb == null)
				throw new PartInitException("Error No Browser detected");

			if (ewb instanceof SystemBrowserDescriptor)
				webBrowser = new SystemBrowserInstance(browserId);
			else {
				IBrowserExt ext = null;
				if (ewb != null)
					ext = WebBrowserUIPlugin.findBrowsers(getLocation(ewb));
				if (ext != null)
					webBrowser = ext.createBrowser(browserId, getLocation(ewb),
							ewb.getParameters());
				if (webBrowser == null)
					webBrowser = new ExternalBrowserInstance(browserId, ewb);
			}
		} else {
			if ((style & IWorkbenchBrowserSupport.AS_VIEW) != 0)
				webBrowser = new InternalBrowserViewInstance(browserId, style,
						name, tooltip);
			else
				webBrowser = new InternalBrowserEditorInstance(browserId,
						style, name, tooltip);
		}

		return webBrowser;
	}

	private String getLocation(IBrowserDescriptor ewb) {
		final String location = ewb.getLocation();
		if (location != null) {
			File browser = new File(location);
			browser = adjustFor64BitWindows(browser);
			if (browser.exists())
				return browser.toString();
		}

		return location;
	}

	private File adjustFor64BitWindows(File browser) {
		if ("win32".equals(Platform.getOS())
				&& browser
						.toString()
						.toLowerCase()
						.endsWith(
								"\\program files\\internet explorer\\iexplore.exe")) {
			File IE_x86 = new File(browser.getParentFile().getParentFile()
					.getParentFile(),
					"Program Files (x86)\\Internet Explorer\\iexplore.exe");
			if (IE_x86.exists())
				return IE_x86;
		}
		return browser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.browser.IWorkbenchBrowserSupport#createBrowser(int,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public IWebBrowser createBrowser(int style, String browserId, String name,
			String tooltip) throws PartInitException {
		String id = browserId == null ? getDefaultId() : browserId;
		IWebBrowser browser = findBrowser(id);
		if (browser != null) {
			return browser;
		}
		browser = doCreateBrowser(style, id, name, tooltip);
		registerBrowser(browser);
		return browser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.browser.IWorkbenchBrowserSupport#createBrowser(java.lang
	 * .String)
	 */
	public IWebBrowser createBrowser(String browserId) throws PartInitException {
		return createBrowser(AS_EDITOR, browserId, null, null);
	}

	private String getDefaultId() {
		String id = null;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			id = DEFAULT_BROWSER_ID_BASE + i;
			if (browsers.get(id) == null)
				break;
		}
		return id;
	}

	@Override
	public boolean isInternalWebBrowserAvailable() {
		return true;
	}

	public IWebBrowser getBrowser(String key) {
		return browsers.get(key);

	}

	public void addBrowserListener(BrowserListener listener) {
		listeners.add(listener);
	}

	public void removeBrowserListener(BrowserListener listener) {
		listeners.remove(listener);
	}

	void fireBrowserCreated(IWebBrowser browser) {
		BrowserListener[] listeners = this.listeners
				.toArray(new BrowserListener[0]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].browserCreated(browser);
		}
	}

	private void fireBrowserClosed(IWebBrowser browser) {
		BrowserListener[] listeners = this.listeners
				.toArray(new BrowserListener[0]);
		for (int i = 0; i < listeners.length; i++) {
			listeners[i].browserClosed(browser);
		}
	}
}
