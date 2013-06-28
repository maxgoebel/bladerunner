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

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventListener;
import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.Page;

/**
 * Implementation of a Page that visualizes all the Network calls. It is implemented
 * so that it is independent of the platform (i.e. Mozilla...).
 * 
 * It shows a table with all the network calls that have been logged. For each call, a
 * panel can be opened that shows details of the Request and Response side of the call.
 * 
 * For HTTP calls, it will show a list of headers and body for each side of the call.
 * 
 * @author Gino Bustelo, Kevin Sawicki
 *
 */
public class NetworkMonitorPage extends Page implements IBrowserView, INetworkMonitor, IApplicationEventListener {

	protected IWebBrowser webBrowser;

	protected RequestResponsePanel requestResponsePanel;
	private NetworkMonitorPageFilters filtersMenu;
	private NetworkMonitorTable table;

	protected SashForm displayArea = null;

	protected Action collapseAction = null;

	public void createControl(Composite parent) {
		EventRecorder callList = MozIDEUIPlugin.getDefault().getCallListsManager().getCallList(webBrowser);
		callList.addListener(this);

		displayArea = new SashForm(parent, SWT.VERTICAL);

		table = new NetworkMonitorTable(displayArea);
		table.setCallList(callList);

		requestResponsePanel = new RequestResponsePanel(displayArea);

		table.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				requestResponsePanel.setSelection((IStructuredSelection) event.getSelection());
			}
		});

		table.getControl().addMouseListener(new MouseAdapter() {

			public void mouseDoubleClick(MouseEvent e) {
				if (displayArea.getMaximizedControl() != null) {
					displayArea.setMaximizedControl(null);
					collapseAction.setChecked(false);
				}
			}
		});

		filtersMenu = new NetworkMonitorPageFilters(getSite().getActionBars().getToolBarManager()) {

			protected void clear() {
				doClear();
			}

			protected void setFilters(ViewerFilter[] filters) {
				table.setFilters(filters);
			}
		};

		//Initially hide the request/response panel
		displayArea.setMaximizedControl(table.getControl());

		createActions();
	}

	public void setFilters(ViewerFilter[] filters) {
		table.setFilters(filters);
	}

	protected void createActions() {
		collapseAction = new Action(null, Action.AS_CHECK_BOX) {
			public void run() {
				if (isChecked()) {
					displayArea.setMaximizedControl(table.getControl());
				} else {
					displayArea.setMaximizedControl(null);
					requestResponsePanel.setSelection((IStructuredSelection) table.getViewer().getSelection());
				}
			}
		};

		collapseAction.setImageDescriptor(MozIDEUIPlugin.getImageDescriptor("icons/xhrmon/collapse.gif"));
		collapseAction.setToolTipText("Hide the request/response content panel");
		collapseAction.setChecked(true);

		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		toolBarManager.add(new Separator());

		toolBarManager.add(collapseAction);
	}

	public Control getControl() {
		return displayArea;
	}

	public void setFocus() {
		table.getControl().setFocus();

	}

	public void dispose() {
		EventRecorder callList = MozIDEUIPlugin.getDefault().getCallListsManager().getCallList(webBrowser);
		callList.removeListener(this);
		requestResponsePanel.dispose();
		requestResponsePanel = null;
		super.dispose();
	}

	public void setWebBrowser(IWebBrowser documentContainer) {
		this.webBrowser = documentContainer;
	}

	private void doClear() {
		EventRecorder callList = MozIDEUIPlugin.getDefault().getCallListsManager().getCallList(webBrowser);
		callList.clear();
		table.getViewer().refresh();
		requestResponsePanel.clear();
	}

	private static INetworkCall eventToCall(IApplicationEvent event) {
		if (event instanceof ITimedEvent) {
			ITimedEvent tevent = (ITimedEvent) event;
			if (tevent.getData() instanceof INetworkCall) {
				return (INetworkCall) tevent.getData();
			}
		}

		return null;
	}

	public void onEvent(IApplicationEvent event) {
		final INetworkCall call = eventToCall(event);
		if (call == null) {
			return;
		}

		table.notifyCallAdded(call);

		if (!filtersMenu.getScrollLock()) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					table.getViewer().setSelection(new StructuredSelection(call));
					table.getViewer().reveal(call);
				}
			});
		}
	}
}
