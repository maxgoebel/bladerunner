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
package org.eclipse.atf.mozilla.ide.ui.netmon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.atf.mozilla.ide.network.IRequest;
import org.eclipse.atf.mozilla.ide.network.IStatusChange;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public abstract class NetworkMonitorPageFilters {

	public static final String VIEW_POINT = "org.eclipse.atf.mozilla.ide.ui.filter";
	public static final String FILTER_NODE = "filter";
	public static final String CLASS_ATTR = "class";
	public static final String ICON_ATTR = "icon";
	public static final String TOOLTIP_ATTR = "tooltip";

	protected Action clearAction = null;
	protected Action scrollLockAction = null;

	protected Action filterXHR = null;
	protected Action filterHTTP = null;

	//boolean to determine if new XHR calls are revealed by the viewer
	private boolean scrollLock = false;

	protected ViewerFilter xhrFilter = new ViewerFilter() {

		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IStatusChange)
				element = ((IStatusChange) element).getCall();

			if (element instanceof ITimedEvent)
				element = ((ITimedEvent) element).getData();

			if (!(element instanceof INetworkCall))
				return true;

			//need to check the request side of the call to see if it is XHR
			INetworkCall call = (INetworkCall) element;
			IRequest req = call.getRequest();

			if (req instanceof IHTTPRequest)
				return ((IHTTPRequest) req).isXHR();
			else
				return false;
		}

	};

	protected ViewerFilter httpFilter = new ViewerFilter() {

		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IStatusChange)
				element = ((IStatusChange) element).getCall();

			if (element instanceof ITimedEvent)
				element = ((ITimedEvent) element).getData();

			if (!(element instanceof INetworkCall))
				return true;

			//need to check the request side is http but not XHR
			INetworkCall call = (INetworkCall) element;
			IRequest req = call.getRequest();

			if (req instanceof IHTTPRequest)
				return !((IHTTPRequest) req).isXHR();
			else
				return false;

		}

	};

	protected List filters = new ArrayList();
	protected List actions = new ArrayList();

	private Set enabledFilters = new HashSet();

	public NetworkMonitorPageFilters(IToolBarManager toolBarManager) {

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg.getExtensionPoint(VIEW_POINT);
		IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] ce = extensions[i].getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				try {
					if (ce[j].getName().equals(FILTER_NODE)) {
						String filter = ce[j].getAttribute(CLASS_ATTR);
						String icon = ce[j].getAttribute(ICON_ATTR);
						String tooltip = ce[j].getAttribute(TOOLTIP_ATTR);
						if (filter != null && icon != null && tooltip != null) {
							final ViewerFilter filterObj = (ViewerFilter) ce[j].createExecutableExtension(CLASS_ATTR);
							IAction action = new Action(null, Action.AS_CHECK_BOX) {

								private ViewerFilter filter = filterObj;

								public void run() {
									if (isChecked()) {
										disableOtherActions(this);
										enabledFilters.removeAll(filters);
										enabledFilters.add(filter);
									} else {
										enabledFilters.remove(filter);
									}
									updateFilters();
								}

							};
							action.setImageDescriptor(MozIDEUIPlugin.getImageDescriptor(icon));
							action.setToolTipText(tooltip);
							filters.add(filterObj);
							actions.add(action);
							toolBarManager.add(action);
						}
					}
				} catch (Exception e) {
				}
			}
		}

		filterXHR = new Action(null, Action.AS_CHECK_BOX) {

			public void run() {
				if (isChecked()) {
					filterHTTP.setChecked(false);
					enabledFilters.remove(httpFilter);
					enabledFilters.add(xhrFilter);
				} else {
					enabledFilters.remove(xhrFilter);
				}
				updateFilters();
			}

		};
		filterXHR.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.FILTERXHR_ID));
		filterXHR.setToolTipText("Show XHR only");

		filterHTTP = new Action(null, Action.AS_CHECK_BOX) {

			public void run() {
				if (isChecked()) {
					filterXHR.setChecked(false);
					enabledFilters.remove(xhrFilter);
					enabledFilters.add(httpFilter);
				} else {
					enabledFilters.remove(httpFilter);
				}
				updateFilters();
			}

		};
		filterHTTP.setToolTipText("Show HTTP only");
		filterHTTP.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.FILTERHTTP_ID));

		//scroll lock action
		scrollLockAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				scrollLock = isChecked();
			}
		};

		scrollLockAction.setImageDescriptor(MozIDEUIPlugin.getImageDescriptor("icons/xhrmon/scrolllock.gif"));
		scrollLockAction.setToolTipText("Lock/Unlock the scroll of the content pane");
		scrollLockAction.setChecked(false);

		clearAction = new Action(null, Action.AS_PUSH_BUTTON) {
			public void run() {
				clear();
			}
		};
		clearAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CLEAR_IMG_ID));
		clearAction.setToolTipText("Clear the call list");

		toolBarManager.add(new Separator());

		toolBarManager.add(filterXHR);
		toolBarManager.add(filterHTTP);

		toolBarManager.add(new Separator());
		toolBarManager.add(scrollLockAction);
		toolBarManager.add(clearAction);
	}

	private void disableOtherActions(IAction action) {
		for (int i = 0; i < actions.size(); i++) {
			if (!actions.get(i).equals(action)) {
				((IAction) actions.get(i)).setChecked(false);
			}
		}
	}

	private void updateFilters() {
		ViewerFilter[] newFilters = (ViewerFilter[]) enabledFilters.toArray(new ViewerFilter[enabledFilters.size()]);
		setFilters(newFilters);
	}

	public boolean getScrollLock() {
		return scrollLock;
	}

	abstract protected void setFilters(ViewerFilter[] filters);

	abstract protected void clear();
}
