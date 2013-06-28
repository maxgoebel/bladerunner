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
package org.eclipse.atf.mozilla.ide.ui.domwatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView;
import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.IDOMEvent;
import org.eclipse.atf.mozilla.ide.ui.domwatcher.settings.DOMWatcherSettings;
import org.eclipse.atf.mozilla.ide.ui.domwatcher.settings.IDOMEventSetting;
import org.eclipse.atf.mozilla.ide.ui.util.DOMNodeUtils;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.part.Page;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * @author Gino Bustelo
 *
 */
public class DOMWatcherPage extends Page implements IDOMWatcherPage, IBrowserView, ISelectionListener, IDOMEventWatcherListener {

	private IWebBrowser documentContainer = null;

	//nsIDOMNode -> List<Watched Nodes>
	protected Map nodesToWatchers = new HashMap();

	private DOMDocumentListener domDocumentListener = new DOMDocumentListener() {
		public void documentUnloaded(DOMDocumentEvent event) {
			//need to reset
			for (Iterator iter = nodesToWatchers.values().iterator(); iter.hasNext();) {
				Watcher w = (Watcher) iter.next();

				w.disconnect();

				//need to check if disposed because it can happend while closing or shutting down
				if (!watchedNodesViewer.getControl().isDisposed())
					watchedNodesViewer.refresh(w);
			}

			selectedNode = null;

			//make sure that this view is not disposed already
			if (!eventViewer.getControl().isDisposed())
				updateUI();
		}
	};

	/*
	 * This is the node that is currently in context in this view. This reference can be
	 * set by a selection that was initiated externally (i.e. DOMInspector) or initiated
	 * internally (i.e. combo box)
	 */
	protected nsIDOMNode selectedNode = null;

	//Actions
	protected Action startWatchAction = null;
	protected Action stopWatchAction = null;
	protected Action clearEventsAction = null;
	protected Action openSettingsActions = null;

	//UI Elements
	protected Composite contentArea = null;

	protected CComboViewer watchedNodesViewer = null;
	protected TableViewer eventViewer = null;

	protected Color white = new Color(Display.getCurrent(), new RGB(255, 255, 255));

	public void createControl(Composite parent) {

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;

		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

		contentArea = new Composite(parent, SWT.NONE);
		contentArea.setLayoutData(gridData);
		contentArea.setLayout(layout);

		Label watchedNodeLabel = new Label(contentArea, SWT.NONE);
		watchedNodeLabel.setText("Node:");

		gridData = new GridData();
		gridData.horizontalSpan = 1;
		//gridData.grabExcessHorizontalSpace = true;
		//gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.verticalAlignment = GridData.CENTER;
		watchedNodeLabel.setLayoutData(gridData);

		CCombo combo = new CCombo(contentArea, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		//calculate the font height (needed for MAC)
		//GC gc = new GC( combo );
		//gridData.heightHint = gc.getFontMetrics().getHeight()+12; //extra padding for some reason ???
		combo.setLayoutData(gridData);

		watchedNodesViewer = new CComboViewer(combo);//| SWT.READ_ONLY );

		watchedNodesViewer.getCCombo().setEditable(false);
		watchedNodesViewer.getCCombo().setBackground(white);

		watchedNodesViewer.setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {
				return nodesToWatchers.values().toArray();
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});

		watchedNodesViewer.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				Watcher watcher = (Watcher) element;

				return watcher.toString();
			}
		});

		watchedNodesViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {

				//change the local selection
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();

				//when the selection is cleared externally (no need to do anything)
				if (sel.isEmpty()) {
					return;
				}

				Watcher w = (Watcher) sel.getFirstElement();
				nsIDOMNode node = w.getWatchedNode();

				//make changes only when selecting something different (assuming that node cannot be null)
				if (!node.equals(selectedNode)) {

					//stop listening to current Watcher (if available)
					if (selectedNode != null) {
						//the currently selectedNode might not have a watcher
						if (nodesToWatchers.containsKey(selectedNode)) {
							Watcher currentWatcher = (Watcher) nodesToWatchers.get(selectedNode);
							currentWatcher.removeListener(DOMWatcherPage.this);
						}
					}

					/*
					//node is in the combo so it must have a watcher
					Watcher newWatcher = (Watcher)nodesToWatchers.get( node );
					newWatcher.addListener( DOMWatcherPage.this );
					
					startWatchAction.setEnabled( !newWatcher.isWatching() );
					stopWatchAction.setEnabled( newWatcher.isWatching() );
					
					//set to view
					eventViewer.setInput( newWatcher );
					
					selectedNode = node; //change the internal selection
					*/

					selectedNode = node;
					updateUI();
				}
			}

		});

		watchedNodesViewer.setInput(this);

		eventViewer = new TableViewer(contentArea, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		eventViewer.getControl().setLayoutData(gridData);

		styleTable(eventViewer.getTable());
		createColumns(eventViewer.getTable());

		eventViewer.setLabelProvider(new DOMEventLabelProvider());
		eventViewer.setContentProvider(new DOMEventContentProvider());

		//actions
		createActions();

		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();

		toolBarManager.add(startWatchAction);
		toolBarManager.add(stopWatchAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(clearEventsAction);

		IMenuManager menuManager = getSite().getActionBars().getMenuManager();

		menuManager.add(openSettingsActions);

		updateUI();
	}

	protected void styleTable(Table table) {
		table.setLinesVisible(true);
		table.setFont(table.getParent().getFont());

	}

	protected void createColumns(Table table) {
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);

		//Image column
		/*
		layout.addColumnData(new ColumnPixelData( 20 ));
		TableColumn tc = new TableColumn(table, SWT.NONE, 0);
		tc.setMoveable( false );
		tc.setResizable( false );
		tc.setAlignment( SWT.RIGHT );
		*/

		//Event Type
		layout.addColumnData(new ColumnWeightData(10, true));
		TableColumn tc = new TableColumn(table, SWT.NONE, 0);
		tc.setText("Type");
		tc.setMoveable(true);

		//Event Timestamp
		layout.addColumnData(new ColumnWeightData(10, true));
		tc = new TableColumn(table, SWT.NONE, 1);
		tc.setText("TimeStamp");
		tc.setMoveable(true);

		//Event Details
		layout.addColumnData(new ColumnWeightData(20, true));
		tc = new TableColumn(table, SWT.NONE, 2);
		tc.setText("Details");
		tc.setMoveable(true);

	}

	protected void createActions() {
		//START
		startWatchAction = new Action("Start", Action.AS_PUSH_BUTTON) {
			public void run() {
				startWatch();
			}
		};

		startWatchAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.E_STARTWATCHER_ID));
		startWatchAction.setDisabledImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.D_STARTWATCHER_ID));
		startWatchAction.setToolTipText("Start watching events for this node.");

		//STOP
		stopWatchAction = new Action("Stop", Action.AS_PUSH_BUTTON) {
			public void run() {
				stopWatch();
			}
		};

		stopWatchAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.E_STOPWATCHER_ID));
		stopWatchAction.setDisabledImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.D_STOPWATCHER_ID));
		stopWatchAction.setToolTipText("Stop watching events for this node.");

		//CLEAR
		clearEventsAction = new Action("Clear", Action.AS_PUSH_BUTTON) {
			public void run() {
				clear();
			}
		};

		clearEventsAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CLEAR_IMG_ID));
		clearEventsAction.setToolTipText("Clear event list.");

		openSettingsActions = new Action("Settings...", Action.AS_PUSH_BUTTON) {
			public void run() {

				ListSelectionDialog dialog = new ListSelectionDialog(DOMWatcherPage.this.getSite().getShell(), DOMWatcherSettings.getInstance(),

				new IStructuredContentProvider() {

					public Object[] getElements(Object inputElement) {
						DOMWatcherSettings settings = (DOMWatcherSettings) inputElement;
						return settings.getSettings();
					}

					public void dispose() {
					}

					public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					}

				}, new LabelProvider() {

					public String getText(Object element) {
						IDOMEventSetting setting = (IDOMEventSetting) element;
						return setting.getEventType();
					}

				}, "Select event types to watch.");

				List shouldWatchSettings = new ArrayList();
				IDOMEventSetting[] allSettings = DOMWatcherSettings.getInstance().getSettings();
				for (int i = 0; i < allSettings.length; i++) {
					if (allSettings[i].shouldWatch())
						shouldWatchSettings.add(allSettings[i]);
				}

				dialog.setInitialElementSelections(shouldWatchSettings);
				dialog.setTitle("Supported DOM Event types");

				dialog.open();

				if (dialog.getReturnCode() == Window.OK) {
					Object[] selectedSettings = dialog.getResult();

					for (int i = 0; i < selectedSettings.length; i++) {
						((IDOMEventSetting) selectedSettings[i]).setShouldWatch(true);

						//if still checked just, removed from original list
						if (shouldWatchSettings.contains(selectedSettings[i]))
							shouldWatchSettings.remove(selectedSettings[i]);

						//newly checked settings
						else
							((IDOMEventSetting) selectedSettings[i]).setShouldWatch(true);
					}

					//the remaining settings in the list are the ones that were checked off
					for (Iterator iter = shouldWatchSettings.iterator(); iter.hasNext();) {
						IDOMEventSetting settingToUncheck = (IDOMEventSetting) iter.next();
						settingToUncheck.setShouldWatch(false);
					}

				}

			}
		};

		openSettingsActions.setToolTipText("Select event types to watch.");

	}

	protected void updateUI() {
		if (selectedNode == null) {
			//an empty selection should clear the text and disable the buttons
			startWatchAction.setEnabled(false);
			stopWatchAction.setEnabled(false);
			eventViewer.setInput(null);

			watchedNodesViewer.setSelection(StructuredSelection.EMPTY);
			watchedNodesViewer.getCCombo().setText("");

		} else {

			//check if there is a Watcher already for the node
			if (nodesToWatchers.containsKey(selectedNode)) {
				//watcher is available
				Watcher w = (Watcher) nodesToWatchers.get(selectedNode);

				//attach listener
				if (!w.isDisconnected())
					w.addListener(this);

				//take into accoung if the watcher is disconnected (should not be able to start of stop)
				startWatchAction.setEnabled(!w.isDisconnected() && !w.isWatching());
				stopWatchAction.setEnabled(!w.isDisconnected() && w.isWatching());

				//set to view
				eventViewer.setInput(w);

				//show in combo
				watchedNodesViewer.setSelection(new StructuredSelection(new Object[] { w }), true);

			} else {
				//no Watcher available
				//don't create a Watcher unless necessary (wait for user to start)
				startWatchAction.setEnabled(true);
				stopWatchAction.setEnabled(false);
				eventViewer.setInput(null);

				//show the node as text in the combo			
				watchedNodesViewer.getCCombo().setText(DOMNodeUtils.nodeToString(selectedNode));
				//watchedNodesViewer.setSelection( StructuredSelection.EMPTY );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		white.dispose();
		super.dispose();
	}

	/*
	 * Start a watch on the currently selected node
	 */
	protected void startWatch() {
		//this method assumes that the selection is not empty
		if (nodesToWatchers.containsKey(selectedNode)) {
			//watcher is available
			Watcher w = (Watcher) nodesToWatchers.get(selectedNode);
			w.start();
			startWatchAction.setEnabled(false);
			stopWatchAction.setEnabled(true);
		} else {
			//need to create and register a new watcher
			Watcher w = new Watcher(selectedNode);
			nodesToWatchers.put(selectedNode, w);

			//refresh Combo viewer
			watchedNodesViewer.add(w);
			watchedNodesViewer.setSelection(new StructuredSelection(new Object[] { w }), true);

			//attach listener
			w.addListener(this);

			//view to watcher
			eventViewer.setInput(w);

			w.start();
			startWatchAction.setEnabled(false);
			stopWatchAction.setEnabled(true);
		}
	}

	/*
	 * Stop watch on the currently selected node
	 */
	protected void stopWatch() {
		//this method assumes that the selection is not empty and that it has a Watcher that is running
		Watcher w = (Watcher) nodesToWatchers.get(selectedNode);
		w.stop();
		startWatchAction.setEnabled(true);
		stopWatchAction.setEnabled(false);

	}

	protected void clear() {
		if (nodesToWatchers.containsKey(selectedNode)) {
			//watcher is available
			Watcher w = (Watcher) nodesToWatchers.get(selectedNode);
			w.clear();
			eventViewer.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		return contentArea;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	public void setFocus() {
		//does nothing
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.domwatcher2.IDOMEventWatcherListener#newEvent()
	 */
	public void newEvent(IDOMEvent event) {
		//update the viewer
		if (!eventViewer.getControl().isDisposed()) {
			eventViewer.add(event);
			eventViewer.reveal(event);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView#setDOMDocumentContainer(org.eclipse.atf.mozilla.ide.ui.browser.IDOMDocumentContainer)
	 */
	public void setWebBrowser(IWebBrowser documentContainer) {
		if (this.documentContainer != null)
			throw new AssertionFailedException("DOMWatcherPage already initialized, cannot call setDOMDocumentContainer() more than once.");
		this.documentContainer = documentContainer;
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domDocumentListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (selection instanceof IDOMNodeSelection) {

			//check to remove as listener to previous selection
			if (selectedNode != null) {

				if (nodesToWatchers.containsKey(selectedNode)) {
					Watcher w = (Watcher) nodesToWatchers.get(selectedNode);
					w.removeListener(this);
				}
			}

			selectedNode = ((IDOMNodeSelection) selection).getSelectedNode();

			updateUI();
		}
	}
}
