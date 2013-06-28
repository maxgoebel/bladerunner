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

import java.net.URL;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.browser.util.MozBrowserUtil;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.AbstractWebBrowser;

public class InternalWebBrowser extends AbstractWebBrowser {

	private IWebBrowser browser;
	private Browser browserWidget;
	private WorkbenchBrowserSupport support;

	/**
	 * Creates the browser instance.
	 * 
	 * @param support
	 * @param id
	 */
	public InternalWebBrowser(WorkbenchBrowserSupport support, String browserId) {
		super(browserId);
		this.support = support;
	}

	public void openURL(URL url) throws PartInitException {
		String urlStr = url != null ? url.toString() : "about:blank";

		if ((browser == null) || (browserWidget == null) || (browserWidget.isDisposed())) {
			try {
				browser = MozBrowserUtil.openMozillaBrowser(urlStr);
			} catch (CoreException e) {
				throw new PartInitException(e.getMessage(), e);
			}

			browserWidget = (Browser) browser.getAdapter(Browser.class);
			browserWidget.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					close();
				}
			});
			support.fireBrowserCreated(this);

		} else {
			browserWidget.setUrl(urlStr);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.browser.IWebBrowser#close()
	 */
	public boolean close() {
		support.unregisterBrowser(this);
		return super.close();
	}

	public IWebBrowser getWebBrowser() {
		return browser;
	}
}
