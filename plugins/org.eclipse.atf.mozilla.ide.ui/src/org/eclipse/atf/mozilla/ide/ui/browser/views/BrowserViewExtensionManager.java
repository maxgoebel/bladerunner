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
package org.eclipse.atf.mozilla.ide.ui.browser.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * Manager for retrieving view extension points.
 * Views specify a class, adapter type, and browser
 * type.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class BrowserViewExtensionManager {

	/**
	 * XML Extension point strings
	 */
	public static final String VIEW_NODE = "view";
	public static final String CLASS_ATTR = "class";
	public static final String ADAPTER_ATTR = "type";
	public static final String PRIORITY_ATTR = "priority";
	public static final String BROWSER_ATTR = "browser";
	public static final String VIEW_POINT = "org.eclipse.atf.mozilla.ide.ui.view";

	//The manager instance
	private static BrowserViewExtensionManager manager = null;

	/**
	 * Map of views retrieved from reading in the extension
	 * points.
	 */
	//<String<browser> -> HashMap<String<type> -> IConfigurationElement>>
	private Map views = new HashMap();

	private BrowserViewExtensionManager() {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(VIEW_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				if (ce[j].getName().equals(VIEW_NODE)) {
					String view = ce[j].getAttribute(CLASS_ATTR);
					String adapter = ce[j].getAttribute(ADAPTER_ATTR);
					String browser = ce[j].getAttribute(BROWSER_ATTR);
					String priority = ce[j].getAttribute(PRIORITY_ATTR);
					if (view != null && adapter != null && browser != null) {
						try {
							Map browserViews;
							if (views.containsKey(browser)) {
								browserViews = (Map) views.get(browser);
							} else {
								browserViews = new HashMap();
								views.put(browser, browserViews);
							}

							IConfigurationElement current = (IConfigurationElement) browserViews.get(adapter);
							if (current == null) {
								browserViews.put(adapter, ce[j]);
							} else if (priority != null) {
								String currentPriority = current.getAttribute(PRIORITY_ATTR);
								if ((currentPriority == null) || (priority.compareTo(currentPriority) > 0)) {
									browserViews.put(adapter, ce[j]);
								}
							}
						} catch (Exception e) {
							MozIDEUIPlugin.log(e);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the manager instance setup for the container specified.
	 * 
	 * @return - manager instance
	 */
	public static BrowserViewExtensionManager getInstance() {
		if (manager == null) {
			manager = new BrowserViewExtensionManager();
		}
		return manager;
	}

	/**
	 * Retrieves the adapter object for the class specified.
	 * 
	 * @param c - class to find adapter object for
	 * @return - object for the class passed in
	 */
	public Object getAdapter(IWebBrowser browser, Class c) {
		Object ret = null;
		Map browserViews = null;
		if (views.containsKey(browser.getType().getType())) {
			browserViews = (Map) views.get(browser.getType().getType());
			IConfigurationElement element = (IConfigurationElement) browserViews.get(c.getName());
			if (element != null) {
				try {
					ret = element.createExecutableExtension(CLASS_ATTR);
					if (ret instanceof IBrowserView) {
						((IBrowserView) ret).setWebBrowser(browser);
					}
				} catch (Exception e) {
				}
			}
		}
		return ret;
	}

}
