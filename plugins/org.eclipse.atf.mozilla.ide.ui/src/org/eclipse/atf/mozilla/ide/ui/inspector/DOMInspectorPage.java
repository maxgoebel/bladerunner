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

package org.eclipse.atf.mozilla.ide.ui.inspector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.events.DOMMutationListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.actions.ActionExtensionManager;
import org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView;
import org.eclipse.atf.mozilla.ide.ui.inspector.search.DOMTreePatternFilter;
import org.eclipse.atf.mozilla.ide.ui.inspector.search.SearchFilterTypeAction;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.progress.WorkbenchJob;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;

public class DOMInspectorPage extends Page implements IDOMInspector, IBrowserView, ISelectionProvider, ISelectionListener, ISelectionChangedListener {

	//this reference abstracts the Browser Editor
	protected IWebBrowser documentContainer = null;

	protected TreeViewer treeViewer = null;

	protected Composite contentArea = null;
	protected Composite searchArea = null;
	protected SashForm displayArea = null;

	//Viewers showing the properties of the selected Element
	protected DOMAttributeViewer attributesViewer = null;
	protected Menu attributesViewerMenu = null;

	//members for the filtered search
	protected Text searchText = null;
	protected ToolBarManager searchFilterToolBar = null;
	protected Action toggleSearchFilter = null;
	protected Action selectSearchFilterType = null;
	protected DOMTreePatternFilter patternFilter = null;

	protected Action collapseToBodyAction = null;

	private DOMDocumentListener domDocumentListener = new DOMDocumentListener() {

		public void documentLoaded(DOMDocumentEvent event) {
			if (event.isTop()) {
				if (treeViewer != null)
					treeViewer.refresh(documentContainer);

				//find the body element to reveal
				nsIDOMNodeList nodeList = event.getTargetDocument().getElementsByTagName("body");
				if (nodeList.getLength() > 0) {
					treeViewer.expandToLevel(nodeList.item(0), 1);
				} else {
					treeViewer.expandToLevel(event.getTargetDocument().getDocumentElement(), 1);
				}

				searchText.setEnabled(true);
			} else {

				//@GINO: Todo... handle a loaded from a document in a frame
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.atf.mozilla.ide.ui.common.event.IDOMDocumentListener#documentUnloaded(org.eclipse.atf.mozilla.ide.ui.common.event.DOMDocumentEvent)
		 */
		public void documentUnloaded(DOMDocumentEvent event) {
			//clear the filter search
			searchText.setText("");
			searchText.setEnabled(false);

			if (treeViewer != null) {
				treeViewer.refresh(documentContainer);
				//remove selection
				treeViewer.setSelection(null);
			}
		}
	};

	private DOMMutationListener domMutationListeer = new DOMMutationListener() {
		public void nodeInserted(nsIDOMNode parentNode, nsIDOMNode insertedNode) {
			//MozIDEUIPlugin.debug( "INSERTED:" + node.getNodeName() );
			if (treeViewer == null)
				return;

			//have to do a refresh of the parent because the TreeViewer does not allow 
			//inserting a tree node in a certain position within existing child nodes.

			if (treeViewer.getExpandedState(parentNode)) {
				treeViewer.refresh(parentNode);
			}

		}

		public void nodeRemoved(nsIDOMNode parentNode, nsIDOMNode removedNode) {
			//MozIDEUIPlugin.debug( "REMOVED:" + node.getNodeName() );
			if (treeViewer == null)
				return;

			/*
			 * If the removedNode is the current selection (or any of its descendants), then the 
			 * parentNode need to be set as the current selection before the node is removed from 
			 * the tree. At the time this handler is called, the removedNode is still in the DOM 
			 * so any call by the Viewer to the content provider for children of the parentNode 
			 * will still return the removedNode.
			 * 
			 * BUG Fix: 165181
			 */

			//checking if current selection is descendant of the removedNode
			//if so, then change the selection to the parentNode.
			//This check if necessary because nodes can be removed programatically
			//by the web app, at which point the developer's selection can be somewhere
			//else in the tree where it does not interfere
			if (!selection.isEmpty()) {

				nsIDOMNode node = selection.getSelectedNode();
				while (node != null) {

					if (node.equals(removedNode)) {
						changeSelection(parentNode);
						break;
					}

					node = node.getParentNode();
				}

			}

			//remove the node from the treeViewer (since selection has been fixed, this
			//node should be successfully gone from the tree)
			treeViewer.remove(removedNode);
		}

		public void attributeAdded(nsIDOMElement ownerElement, String attributeName) {
			if (treeViewer == null)
				return;

			//BUG Fix: 163373
			//update the treeviewer if it is the id that is changed
			if ("id".equals(attributeName.toLowerCase())) {
				treeViewer.update(ownerElement.queryInterface(nsIDOMElement.NS_IDOMNODE_IID), null);
			}

			//refresh the properties area if it is the selected node
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

			Object domObjectSelection = selection.getFirstElement(); //only one selection permitted
			if (domObjectSelection instanceof nsIDOMNode) {

				if (ownerElement.equals(((nsIDOMNode) domObjectSelection))) {

					updatePropertyViewers();

				}
			}

		}

		public void attributeRemoved(nsIDOMElement ownerElement, String attributeName) {
			if (treeViewer == null)
				return;

			//BUG Fix: 163373
			//update the treeviewer if it is the id that is changed (need to be done on a delay)
			if ("id".equals(attributeName.toLowerCase())) {
				final nsIDOMNode ownerNode = (nsIDOMNode) ownerElement.queryInterface(nsIDOMElement.NS_IDOMNODE_IID);
				Job refreshJob = new UIJob("Node ID removed refresh") {

					public IStatus runInUIThread(IProgressMonitor monitor) {
						treeViewer.update(ownerNode, null);
						monitor.done();
						return Status.OK_STATUS;
					}

				};
				refreshJob.schedule(500);

			}

			//refresh the properties area if it is the selected node
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

			Object domObjectSelection = selection.getFirstElement(); //only one selection permitted
			if (domObjectSelection instanceof nsIDOMNode) {

				if (ownerElement.equals(((nsIDOMNode) domObjectSelection))) {

					//since this notification is sent before the actual deletion of the attribute is made, we need to delay
					//the refresh
					Job refreshJob = new PropertiesRefreshJob();
					refreshJob.schedule(500);

				}
			}

		}

		public void attributeModified(nsIDOMElement ownerElement, String attributeName, String newValue, String previousValue) {
			if (treeViewer == null)
				return;

			//BUG Fix: 163373
			//update the treeviewer if it is the id that is changed
			if ("id".equals(attributeName.toLowerCase())) {
				treeViewer.update(ownerElement.queryInterface(nsIDOMElement.NS_IDOMNODE_IID), null);
			}

			//refresh the properties area if it is the selected node
			IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

			Object domObjectSelection = selection.getFirstElement(); //only one selection permitted
			if (domObjectSelection instanceof nsIDOMNode) {

				if (ownerElement.equals(((nsIDOMNode) domObjectSelection))) {

					updatePropertyViewers();

				}
			}

		}
	};

	/**
	 * The job used to refresh the tree.
	 */
	private Job refreshJob;

	/*
	 * Selection Support
	 */
	//this selection adapts the TreeViewer selection into the IDOMNodeSelection expected by the other interested parts	
	protected IDOMNodeSelection selection = new IDOMNodeSelection() {

		public nsIDOMNode getSelectedNode() {
			if (treeViewer == null) {
				return null;
			} else {
				IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

				Object selectedNode = selection.getFirstElement();
				if (selectedNode instanceof nsIDOMNode) {
					return (nsIDOMNode) selectedNode;
				} else {
					return null;
				}
			}
		}

		public boolean isEmpty() {
			if (treeViewer == null)
				return true;

			return getSelectedNode() == null;
		}

	};

	protected ListenerList selectionListeners = new ListenerList();

	public void createControl(Composite parent) {
		GridLayout contentAreaLayout = new GridLayout();
		contentAreaLayout.numColumns = 1;
		contentAreaLayout.makeColumnsEqualWidth = false;
		contentAreaLayout.marginHeight = 0;
		contentAreaLayout.marginWidth = 0;
		contentAreaLayout.verticalSpacing = 0;
		contentAreaLayout.horizontalSpacing = 0;

		contentArea = new Composite(parent, SWT.NONE);
		contentArea.setLayout(contentAreaLayout);
		contentArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createSearchArea();
		createRefreshJob();
		configureSearchArea();

		displayArea = new SashForm(contentArea, SWT.VERTICAL);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		displayArea.setLayoutData(data);

		treeViewer = new TreeViewer(displayArea, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.BORDER);

		Menu nodeMenu = ActionExtensionManager.configure(treeViewer.getTree(), selection);
		treeViewer.getTree().setMenu(nodeMenu);

		//| SWT.FULL_SELECTION );
		styleTree(treeViewer.getTree());
		//createColumns( treeViewer.getTree() );

		//content provider
		treeViewer.setContentProvider(new DOMContentProvider());

		//label provider
		treeViewer.setLabelProvider(new DOMLabelProvider());

		//need to set the input here if the documentContainer is not null
		if (documentContainer != null)
			treeViewer.setInput(documentContainer);

		//selection handler
		treeViewer.addSelectionChangedListener(this);

		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { TextTransfer.getInstance() };

		//Drag support: Only allow a drag to start if it is not
		//the BODY, HEAD, HTML, or #document node
		//
		//The data of the drag is set to the hash code of the node selected
		//This is used by DOMCompare and DOMWatcher to verify the selected
		//node matches the one dragged in.  The other solution would
		//be transferring the entire DOM node in the drag but would involve
		//used a new transfer type.
		treeViewer.addDragSupport(ops, transfers, new DragSourceAdapter() {

			public void dragStart(DragSourceEvent event) {
				nsIDOMNode nodeToMove = (nsIDOMNode) ((ITreeSelection) treeViewer.getSelection()).getFirstElement();
				String name = nodeToMove.getNodeName();
				if (name.equalsIgnoreCase("BODY") || name.equalsIgnoreCase("HEAD") || name.equalsIgnoreCase("HTML") || name.equalsIgnoreCase("#document")) {
					event.doit = false;
				}
			}

			public void dragSetData(DragSourceEvent event) {
				event.data = ((ITreeSelection) treeViewer.getSelection()).getFirstElement().toString();
			}

		});

		//Drop Support:  A drop is only accepted if the node dropped is from
		//the it is currently in.  
		//Drops are not accepted into the HTML or #document nodes.  
		//Drops are not accepted when dropping a node inside itself
		//
		//Nodes are only expanded if they have subnodes and a 
		//node will not expand when dragged over itself.
		treeViewer.addDropSupport(ops, transfers, new DropTargetAdapter() {

			public void dropAccept(DropTargetEvent event) {
				if (event.item instanceof TreeItem) {
					if (treeViewer.getTree().equals(((TreeItem) event.item).getParent())) {
						nsIDOMNode node = (nsIDOMNode) event.item.getData();
						if (node.getNodeName().equalsIgnoreCase("HTML") || node.getNodeName().equalsIgnoreCase("#document") || node.getNodeName().equalsIgnoreCase("BODY") || node.getNodeName().equalsIgnoreCase("HEAD")) {
							event.detail = DND.DROP_NONE;
						} else {
							TreeItem item = (TreeItem) event.item;
							TreeItem initial = treeViewer.getTree().getSelection()[0];
							while (item.getParentItem() != null) {
								if (initial.equals(item.getParentItem())) {
									event.detail = DND.DROP_NONE;
								}
								item = item.getParentItem();
							}
						}
					}
				} else {
					event.detail = DND.DROP_NONE;
				}
			}

			public void drop(DropTargetEvent event) {
				try {
					if (event.item != null) {
						Object data = event.item.getData();
						nsIDOMNode node = null;
						if (data instanceof nsIDOMNode) {
							node = (nsIDOMNode) data;
							ISelection selection = treeViewer.getSelection();
							nsIDOMNode nodeToMove = (nsIDOMNode) ((ITreeSelection) selection).getFirstElement();
							nsIDOMNode nodeAfter = node.getNextSibling();
							if (!node.equals(nodeToMove) && !nodeToMove.equals(nodeAfter)) {
								nsIDOMNode parent = node.getParentNode();
								nodeToMove = nodeToMove.getParentNode().removeChild(nodeToMove);
								if (nodeAfter != null) {
									parent.insertBefore(nodeToMove, nodeAfter);
								} else {
									parent.appendChild(nodeToMove);
								}
							}
							treeViewer.setSelection(selection);
						}
					}
				} catch (Exception e) {
				}
			}

			public void dragOver(DropTargetEvent event) {
				if (event.item instanceof TreeItem) {
					TreeItem item = (TreeItem) event.item;
					if (item != null && item.getParent().equals(treeViewer.getTree()) && item.getItemCount() > 0) {
						nsIDOMNode node = (nsIDOMNode) item.getData();
						if (!node.equals(((ITreeSelection) treeViewer.getSelection()).getFirstElement())) {
							event.feedback = DND.FEEDBACK_EXPAND;
						}
					}
				}
			}

		});

		//filter used by the pattern search
		patternFilter = new DOMTreePatternFilter();
		treeViewer.addFilter(patternFilter);

		//filter to remove the SelectionBox DIVS from view (FILTER ORDER IMPORTANT)
		treeViewer.addFilter(new ATFInternalNodeFilter());

		attributesViewer = new DOMAttributeViewer(displayArea, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);

		displayArea.setWeights(new int[] { 60, 40 });

		//required to connect with the Property View (could probably delete this)
		getSite().setSelectionProvider(this);

		//setup toolbar menu
		createToolbarMenu();

	}

	protected void createSearchArea() {
		GridLayout searcAreaLayout = new GridLayout();
		searcAreaLayout.numColumns = 3;
		searcAreaLayout.makeColumnsEqualWidth = false;
		searcAreaLayout.marginHeight = 0;
		searcAreaLayout.marginWidth = 3;
		searcAreaLayout.verticalSpacing = 0;
		searcAreaLayout.horizontalSpacing = 0;

		searchArea = new Composite(contentArea, SWT.NONE);
		searchArea.setLayout(searcAreaLayout);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		searchArea.setLayoutData(data);

		//filter search support
		Label searchLabel = new Label(searchArea, SWT.NONE);
		searchLabel.setText("Search:");

		searchText = new Text(searchArea, SWT.SINGLE | SWT.BORDER);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		searchText.setLayoutData(data);

		ToolBar toolBar = new ToolBar(searchArea, SWT.FLAT | SWT.HORIZONTAL);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = false;
		toolBar.setLayoutData(data);
		searchFilterToolBar = new ToolBarManager(toolBar);

		createSearchFilterToolBar();

		searchFilterToolBar.update(false);
	}

	protected void createSearchFilterToolBar() {

		Action selectFilterTypeAction = new SearchFilterTypeAction();

		selectFilterTypeAction.addPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent event) {
				if (SearchFilterTypeAction.FILTERTYPE_PROP.equals(event.getProperty())) {

					patternFilter.setFilterType(((Integer) event.getNewValue()).intValue());
					textChanged();
				}
			}

		});
		searchFilterToolBar.add(selectFilterTypeAction);
	}

	protected void configureSearchArea() {

		searchText.addKeyListener(new KeyAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
			 */
			public void keyPressed(KeyEvent e) {
				// on a CR we want to transfer focus to the list
				boolean hasItems = treeViewer.getTree().getItemCount() > 0;
				if (hasItems && e.keyCode == SWT.ARROW_DOWN) {
					treeViewer.getTree().setFocus();
				} else if (e.character == SWT.CR) {
					return;
				}
			}
		});

		searchText.addModifyListener(new ModifyListener() {
			/* (non-Javadoc)
			 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
			 */
			public void modifyText(ModifyEvent e) {
				textChanged();
			}
		});
	}

	/**
	 * Update the receiver after the text has changed.
	 */
	protected void textChanged() {
		// cancel currently running job first, to prevent unnecessary redraw
		refreshJob.cancel();
		refreshJob.schedule(200);
	}

	private void createRefreshJob() {
		refreshJob = new WorkbenchJob("Refresh Filter") {//$NON-NLS-1$
			/* (non-Javadoc)
			 * @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
			 */
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (treeViewer.getControl().isDisposed()) {
					return Status.CANCEL_STATUS;
				}

				String text = searchText.getText();
				if (text == null) {
					return Status.OK_STATUS;
				}

				patternFilter.setPattern(text);

				try {
					// don't want the user to see updates that will be made to the tree
					treeViewer.getControl().setRedraw(false);
					treeViewer.refresh(true);

					if (text.length() > 0) {
						/* Expand elements one at a time.  After each is expanded, check
						 * to see if the filter text has been modified.  If it has, then 
						 * cancel the refresh job so the user doesn't have to endure 
						 * expansion of all the nodes.
						 */
						IStructuredContentProvider provider = (IStructuredContentProvider) treeViewer.getContentProvider();
						Object[] elements = provider.getElements(treeViewer.getInput());
						for (int i = 0; i < elements.length; i++) {
							if (monitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							treeViewer.expandToLevel(elements[i], AbstractTreeViewer.ALL_LEVELS);
						}

						TreeItem[] items = treeViewer.getTree().getItems();
						if (items.length > 0) {
							// to prevent scrolling
							treeViewer.getTree().showItem(items[0]);
						}

					}
				} finally {
					// done updating the tree - set redraw back to true
					treeViewer.getControl().setRedraw(true);
				}
				return Status.OK_STATUS;
			}

		};
		refreshJob.setSystem(true);
	}

	public Control getControl() {
		return contentArea;
	}

	public void setFocus() {
		treeViewer.getControl().setFocus();
	}

	protected void styleTree(Tree tree) {
		//tree.setLinesVisible(true);
		tree.setFont(tree.getParent().getFont());

	}

	/*
	 * Only updates the PropertyViewer that is visible and mark the rest to update later
	 */
	protected void updatePropertyViewers() {
		updatePropertyViewer(attributesViewer);
	}

	/*
	 * Check if the input matches the selection, if not change the input. It the input
	 * match, then refresh.
	 */
	protected void updatePropertyViewer(Viewer viewer) {

		Object viewerInput = viewer.getInput();

		try {
			if (selection.isEmpty()) {
				if (viewerInput != null)
					viewer.setInput(null);
			} else {

				//the XPCOMJavaProxy does not support equal when passed in a null
				if (viewerInput == null) {
					viewer.setInput(selection.getSelectedNode());
				} else if (selection.getSelectedNode().equals(viewerInput))
					viewer.refresh();
				else
					viewer.setInput(selection.getSelectedNode());

			}
		} catch (RuntimeException re) {
			//this prevent the exposure of a wiere Assertion problem
			//@GINO: need to research this error in more detail
			MozIDEUIPlugin.log(re);
		}

	}

	protected void createToolbarMenu() {

		collapseToBodyAction = new Action(null, Action.AS_PUSH_BUTTON) {
			public void run() {
				nsIDOMDocument doc = documentContainer.getDocument();
				if (doc != null) {

					//find the body element to collapse
					nsIDOMNodeList nodeList = doc.getElementsByTagName("body");
					if (nodeList.getLength() > 0) {
						treeViewer.collapseToLevel(nodeList.item(0), 1);
					} else {
						treeViewer.collapseToLevel(doc.getDocumentElement(), 1);
					}

				}
			}
		};
		collapseToBodyAction.setImageDescriptor(MozIDEUIPlugin.getImageDescriptor("icons/inspector/collapse.gif"));
		collapseToBodyAction.setToolTipText("Collapse the DOM tree to the Body element.");

		/*
		toggleSearchFilter = new Action(null, Action.AS_CHECK_BOX) {
			public void run(){
				GridData data = (GridData)searchArea.getLayoutData();
				data.exclude = !this.isChecked();
				searchArea.setVisible(this.isChecked());
				contentArea.layout(false);
				
			}
		};
		
		toggleSearchFilter.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CLEAR_IMG_ID));
		toggleSearchFilter.setToolTipText("Show/hide search filter.");
		toggleSearchFilter.setChecked( true );
		*/

		//setup the toolbar
		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		//toolBarManager.add( toggleSearchFilter );
		toolBarManager.add(collapseToBodyAction);
	}

	/*
	 * Do some cleanup by removing listeners hooked to the nsIDOMDocument
	 */
	public void dispose() {

		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(documentContainer, domDocumentListener);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(documentContainer, domMutationListeer);

		refreshJob.cancel();

		super.dispose();
	}

	/*
	 * IDOMInspector interface
	 * 
	 * This method MUST only be called once at init time. It is part of the setup
	 * needed by the DOMInspector.
	 */
	public void setWebBrowser(IWebBrowser documentContainer) {

		//prevent this method to be called more than once
		if (this.documentContainer != null)
			throw new AssertionFailedException("DOMInspectorPage already initialized, cannot call setDOMDocumentContainer() more than once.");

		//save a reference
		this.documentContainer = documentContainer;

		//add as listener so that it gets notified of future changes to the Document in the
		//browser editor
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domDocumentListener);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domMutationListeer);

		//the documentContainer is set as the input of the treeViewer in the createControl method
	}

	//	clears the selection
	protected void clearSelection() {
		if (!this.selection.isEmpty()) {

			//make local changes
			treeViewer.setSelection(null);

			fireSelectionChanged(new SelectionChangedEvent(this, this.selection));
		}
	}

	/*
	 * Sets the input nsIDOMNode as the current selection. Check if the selectedNode
	 * is different from the current selection and only sends out an event if so.
	 */
	protected void changeSelection(final nsIDOMNode selectedNode) {

		if (selectedNode == null) {
			clearSelection();
			//if the current selection is empty, set and fire
		} else if (this.selection.isEmpty()) {
			//change selection locally
			//revealing selection in the treeviewer
			treeViewer.setSelection(new IStructuredSelection() {

				public boolean isEmpty() {
					return false;
				}

				public Object getFirstElement() {
					return selectedNode;
				}

				public Iterator iterator() {
					return toList().iterator();
				}

				public int size() {
					return 1;
				}

				public Object[] toArray() {
					return toList().toArray();
				}

				public List toList() {
					ArrayList list = new ArrayList(1);
					list.add(getFirstElement());
					return list;

				}
			}, true);

			//notify listeners
			fireSelectionChanged(new SelectionChangedEvent(this, this.selection));
		} else {
			//change selection if it is different
			nsIDOMNode currentSelection = this.selection.getSelectedNode();

			if (!currentSelection.equals(selectedNode)) {

				//change selection locally
				//revealing selection in the treeviewer
				treeViewer.setSelection(new IStructuredSelection() {

					public boolean isEmpty() {
						return false;
					}

					public Object getFirstElement() {
						return selectedNode;
					}

					public Iterator iterator() {
						return toList().iterator();
					}

					public int size() {
						return 1;
					}

					public Object[] toArray() {
						return toList().toArray();
					}

					public List toList() {
						ArrayList list = new ArrayList(1);
						list.add(getFirstElement());
						return list;

					}
				}, true);

				fireSelectionChanged(new SelectionChangedEvent(this, this.selection));
			} else {
				treeViewer.getTree().showSelection();
			}
		}
	}

	/*
	 * This handles selection changes that originate in the treeViewer.
	 * 
	 * It also changes the other viewers and widgets in this View.
	 */
	public void selectionChanged(SelectionChangedEvent e) {

		//clearing the DOMInspector
		if (e.getSelection().isEmpty()) {

			updatePropertyViewers();
		}

		IStructuredSelection selection = (IStructuredSelection) e.getSelection();

		Object selectedObj = selection.getFirstElement(); //only one selection permitted
		if (selectedObj instanceof nsIDOMNode) {
			updatePropertyViewers();
		}

		//broadcast selection
		fireSelectionChanged(new SelectionChangedEvent(DOMInspectorPage.this, this.selection));

	}

	/*
	 * This is an implementation of the ISelectionListner interface but it is invoked by
	 * the host PageBookView. The PageBookView is the one that registers as the
	 * ISelectionListener and it routes selectionChanged calls to the page that is
	 * visible.
	 * 
	 * This way, we limit the number of events going arounds that will potentially
	 * get ignored.
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (treeViewer == null)
			return; //do nothing since the treeViewer is where the selection will be revealed

		if (selection instanceof IDOMNodeSelection) {

			nsIDOMNode selectedNode = ((IDOMNodeSelection) selection).getSelectedNode();

			changeSelection(selectedNode);

		}

	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	public ISelection getSelection() {
		return null;
	}

	/*
	 * This is the external way of changing the current selection. For example,
	 * the corresponding IDOMInspector will call this method to control the
	 * selection of the browser.
	 */
	public void setSelection(ISelection selection) {
		//TODO: check that the selection can is part of the document rendered by
		//the browser
		if (selection == null || selection.isEmpty()) {
			clearSelection(); //clears the selection
		} else {
			if (selection instanceof IDOMNodeSelection) {
				//only concerned with one selection at a time
				nsIDOMNode externalSelection = ((IDOMNodeSelection) selection).getSelectedNode();
				changeSelection(externalSelection);
			}
		}
	}

	/*
	 * notify changes to selection to all listeners
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	/*
	 * IDOMMutationListener
	 */

	/*
	 * This Job is to delay the refresh of the Properties area and give time for attributes changes
	 * to take place in the DOM.
	 */
	protected class PropertiesRefreshJob extends UIJob {

		public PropertiesRefreshJob() {
			super("ATF_DOMInspector_PropertiesRefresh");
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			updatePropertyViewers();
			return Status.OK_STATUS;
		}

	}

	public void widgetDefaultSelected(SelectionEvent e) {
		//Does nothing
	}

}
