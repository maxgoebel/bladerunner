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
package org.eclipse.atf.mozilla.ide.ui.browser.toolbar;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Loads toolbar extension points and creates and configures them for a given
 * browser.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public class ToolbarExtensionManager {

	/**
	 * XML strings used for toolbar extension point
	 */
	public static final String TOOLBAR_POINT = "org.eclipse.atf.mozilla.ide.ui.toolbar";
	public static final String TOOLBAR_NODE = "toolbar";
	public static final String NAME_ATTR = "name";
	public static final String BROWSER_ATTR = "browser";
	public static final String ICON_ATTR = "icon";
	public static final String SHOW_ATTR = "show";
	public static final String CLASS_ATTR = "class";

	public static final String TOOLTIP_PREFIX = "Toggles the visibility of the ";
	public static final String MENUTEXT_PREFIX = "Show ";

	private static List configurables = null;

	/**
	 * Creates a list of toolbars configured to be displayed in the given
	 * display area and linked to menus added to the navigation bar. This method
	 * creates the toolbars declared via the extension point and returns them as
	 * a list to be used to change their node and hover selections.
	 * 
	 * @param navBar -  navigation bar receing menu actions to toggle visibility
	 * @param displayArea - display area to create toolbars in
	 * @param container - browser to configure toolbars for
	 * @return - list of configure IBrowserToolbar instances.
	 */
	public static List create(NavigationBar navBar, Composite displayArea, IWebBrowser container) {
		if (configurables == null) {
			fillConfigurables();
		}
		List toolbars = new ArrayList();
		for (int i = 0; i < configurables.size(); i++) {
			IConfigurationElement ce = (IConfigurationElement) configurables.get(i);
			String name = ce.getAttribute(NAME_ATTR);
			String browser = ce.getAttribute(BROWSER_ATTR);
			String icon = ce.getAttribute(ICON_ATTR);
			String show = ce.getAttribute(SHOW_ATTR);
			if (name != null && browser != null && container.getType().getType().equals(browser)) {
				try {
					String tooltip = TOOLTIP_PREFIX + name;
					String menuText = MENUTEXT_PREFIX + name;
					Object item = ce.createExecutableExtension(CLASS_ATTR);
					if (item instanceof BrowserToolbar) {
						final BrowserToolbar tb = (BrowserToolbar) item;
						tb.createControl(displayArea);
						tb.setBrowser(container);
						Action action = new Action(menuText, Action.AS_CHECK_BOX) {

							private final BrowserToolbar toolbar = tb;

							public void run() {
								toolbar.show(isChecked());
							}

						};
						tb.setAction(action);
						if (show != null) {
							try {
								action.setChecked(Boolean.valueOf(show).booleanValue());
								tb.show(Boolean.valueOf(show).booleanValue());
							} catch (Exception e) {
							}
						}
						if (icon != null) {
							String pluginID = ce.getContributor().getName();
							action.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(pluginID, icon));
						}
						action.setToolTipText(tooltip);
						navBar.addMenuExtensionAction(action);
						toolbars.add(tb);
					}
				} catch (Exception e) {
					MozIDEUIPlugin.log(e);
				}

			}
		}
		return toolbars;
	}

	/**
	 * Puts all extension point configuration elements into a list.
	 *
	 */
	private static void fillConfigurables() {
		configurables = new ArrayList();
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(TOOLBAR_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				if (ce[j].getName().equals(TOOLBAR_NODE) && ce[j].getAttribute(NAME_ATTR) != null && ce[j].getAttribute(BROWSER_ATTR) != null) {
					configurables.add(ce[j]);
				}
			}
		}
	}

}
