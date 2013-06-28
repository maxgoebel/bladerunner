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
package org.eclipse.atf.mozilla.ide.ui.css;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.events.DOMMutationListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView;
import org.eclipse.atf.mozilla.ide.ui.common.SelectionProviderHandler;
import org.eclipse.atf.mozilla.ide.ui.common.configs.CSSViewerConfiguration;
import org.eclipse.atf.mozilla.ide.ui.util.SourceDisplayUtil;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.Page;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentTraversal;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeFilter;
import org.mozilla.interfaces.nsIDOMTreeWalker;

/**
 * Page for CSS inspection
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class CSSPage extends Page implements ICSSPage, ISelectionListener, IBrowserView, SelectionListener, ISelectionChangedListener {

	protected CSSPropertyLocator cpl = new CSSPropertyLocator();

	//Interface components
	protected SashForm ruleDisplay;
	protected Tree ruleTree;
	protected TreeViewer ruleViewer;
	private StyleRulesContentProvider ruleViewerContentProvider;
	protected ColorSelector selector;
	protected IWebBrowser documentContainer;
	protected nsIDOMNode currentlySelectedNode = null;
	protected SashForm diffs;
	protected SourceViewer originalViewer;
	protected Document originalDoc;
	protected SourceViewer modifiedViewer;
	protected Document modifiedDoc;
	protected SelectionProviderHandler provider = new SelectionProviderHandler();

	protected SashForm compDisplay;
	protected Tree compTree;
	protected TreeViewer compViewer;

	protected SashForm boxDisplay;
	protected Tree boxTree;
	protected TreeViewer boxViewer;

	protected SashForm diffDisplay;

	protected boolean canHighlight = false;
	protected boolean shouldIndex = false;

	//Tabs
	protected CTabFolder tabFolder;
	protected CTabItem rulesTab;
	protected CTabItem computedTab;
	protected CTabItem boxTab;
	protected CTabItem diffTab;
	protected SashForm comp;
	protected Composite top;
	protected boolean refreshRules = true;
	protected boolean refreshBox = true;
	protected boolean refreshComputed = true;
	protected boolean refreshDiff = true;

	//Box model buttons
	protected Button leftBox;
	protected Button rightBox;
	protected Button upBox;
	protected Button downBox;

	//Actions to place on tool bar
	protected Action addAction = null;
	protected Action toggleHighlight = null;
	protected Action openAction = null;

	protected Button showGlobal;
	protected Button showSelected;

	protected Canvas canvas;

	protected Menu menu;
	protected MenuItem openFile;
	protected MenuItem addProperty;
	protected MenuItem toggleHighlighting;

	protected SourceLocatorUtil locatorUtil;
	protected SourceDisplayUtil sourceDisplayUtil;
	protected BoxModelPaintListener boxModelPaintListener;

	private DOMDocumentListener domDocumentListener = new DOMDocumentListener() {

		public void documentUnloaded(DOMDocumentEvent event) {
			clearTree();
		}
	};

	private DOMMutationListener domMutationListener = new DOMMutationListener() {

		public void attributeAdded(nsIDOMElement ownerElement, String attributeName) {
			checkStyle(ownerElement, attributeName);
		}

		public void attributeModified(nsIDOMElement ownerElement, String attributeName, String newValue, String previousValue) {
			checkStyle(ownerElement, attributeName);
		}

		public void attributeRemoved(nsIDOMElement ownerElement, String attributeName) {
			checkStyle(ownerElement, attributeName);
		}
	};

	//Colors
	protected Color defaultBGColor = new Color(Display.getCurrent(), new RGB(255, 255, 255));

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		cpl = new CSSPropertyLocator();
		locatorUtil = SourceLocatorUtil.getInstance();
		sourceDisplayUtil = new SourceDisplayUtil();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		top = new Composite(parent, SWT.NONE);
		top.setLayout(layout);
		top.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		comp = new SashForm(top, SWT.VERTICAL);
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		tabFolder = new CTabFolder(comp, SWT.TOP);
		tabFolder.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (currentlySelectedNode != null) {
					if (e.item.equals(diffTab)) {
						showDiffTab();
					} else if (e.item.equals(computedTab)) {
						showComputedTab();
					} else if (e.item.equals(boxTab)) {
						showBoxTab();
					} else if (e.item.equals(rulesTab)) {
						showRulesTab();
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		tabFolder.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		rulesTab = new CTabItem(tabFolder, SWT.NONE);
		rulesTab.setText("Style Rules");
		computedTab = new CTabItem(tabFolder, SWT.NONE);
		computedTab.setText("Computed Styles");
		boxTab = new CTabItem(tabFolder, SWT.NONE);
		boxTab.setText("Box Model");
		diffTab = new CTabItem(tabFolder, SWT.NONE);
		diffTab.setText("Diffs");
		ruleDisplay = new SashForm(tabFolder, SWT.BORDER);

		createBoxModelTab();
		createComputedStylesTab();
		createStyleRulesTab();
		createDiffsTab();

		diffTab.setControl(diffDisplay);
		computedTab.setControl(compDisplay);
		boxTab.setControl(boxDisplay);
		rulesTab.setControl(ruleDisplay);
		tabFolder.setSelection(rulesTab);

		//Set up the toolbar
		createActions();
		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		toolBarManager.add(addAction);
		toolBarManager.add(openAction);
		toolBarManager.add(toggleHighlight);

		provider.addSelectionChangedListener(this);
		getSite().setSelectionProvider(provider);
	}

	private void createBoxModelTab() {
		boxDisplay = new SashForm(tabFolder, SWT.BORDER);
		boxTree = new Tree(boxDisplay, SWT.FULL_SELECTION | SWT.SINGLE);
		boxTree.setHeaderVisible(true);
		boxTree.setEnabled(true);
		boxTree.setLinesVisible(true);
		boxViewer = new TreeViewer(boxTree);
		boxViewer.setContentProvider(new BoxModelContentProvider());
		boxViewer.setLabelProvider(new CSSLabelProvider(boxTree.getDisplay()));
		boxViewer.setAutoExpandLevel(2);
		boxViewer.addSelectionChangedListener(this);

		Composite boxComp = new Composite(boxDisplay, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 5;
		boxComp.setLayout(layout);
		boxComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite navComp = new Composite(boxComp, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		navComp.setLayout(layout);
		navComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		navComp.setBackground(defaultBGColor);
		Label navLabel = new Label(navComp, SWT.CENTER);
		navLabel.setText("Navigation Controls:");
		navLabel.setBackground(defaultBGColor);
		Composite boxButtons = new Composite(navComp, SWT.NONE);
		boxButtons.setBackground(defaultBGColor);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.makeColumnsEqualWidth = true;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		boxButtons.setLayout(layout);
		boxButtons.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, false, false));
		upBox = new Button(boxButtons, SWT.ARROW);
		upBox.setAlignment(SWT.UP);
		upBox.setToolTipText("Show parent box model");
		upBox.setEnabled(false);
		upBox.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				nsIDOMDocumentTraversal docTraversal = (nsIDOMDocumentTraversal) currentlySelectedNode.getOwnerDocument().queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
				nsIDOMTreeWalker treeWalker = docTraversal.createTreeWalker(currentlySelectedNode.getOwnerDocument().getDocumentElement(), nsIDOMNodeFilter.SHOW_ELEMENT, null, true);
				treeWalker.setCurrentNode(currentlySelectedNode);
				final nsIDOMNode n = treeWalker.parentNode();
				if (n != null) {
					IDOMNodeSelection selection = new IDOMNodeSelection() {

						public boolean isEmpty() {
							return n == null;
						}

						public nsIDOMNode getSelectedNode() {
							return n;
						}

					};
					provider.fireSelection(selection);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});

		GridData boxButtonsGData = new GridData();
		boxButtonsGData.horizontalAlignment = GridData.CENTER;
		boxButtonsGData.horizontalSpan = 3;
		upBox.setLayoutData(boxButtonsGData);

		leftBox = new Button(boxButtons, SWT.ARROW);
		leftBox.setAlignment(SWT.LEFT);
		leftBox.setToolTipText("Show previous sibling box model");
		leftBox.setEnabled(false);
		leftBox.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				nsIDOMDocumentTraversal docTraversal = (nsIDOMDocumentTraversal) currentlySelectedNode.getOwnerDocument().queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
				nsIDOMTreeWalker treeWalker = docTraversal.createTreeWalker(currentlySelectedNode.getOwnerDocument().getDocumentElement(), nsIDOMNodeFilter.SHOW_ELEMENT, null, true);
				treeWalker.setCurrentNode(currentlySelectedNode);
				final nsIDOMNode n = treeWalker.previousSibling();
				if (n != null) {
					IDOMNodeSelection selection = new IDOMNodeSelection() {

						public boolean isEmpty() {
							return n == null;
						}

						public nsIDOMNode getSelectedNode() {
							return n;
						}

					};
					provider.fireSelection(selection);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		boxButtonsGData = new GridData();
		boxButtonsGData.horizontalAlignment = GridData.BEGINNING;
		boxButtonsGData.horizontalSpan = 1;

		rightBox = new Button(boxButtons, SWT.ARROW);
		rightBox.setAlignment(SWT.RIGHT);
		rightBox.setToolTipText("Show next sibling box model");
		rightBox.setEnabled(false);
		rightBox.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				nsIDOMDocumentTraversal docTraversal = (nsIDOMDocumentTraversal) currentlySelectedNode.getOwnerDocument().queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
				nsIDOMTreeWalker treeWalker = docTraversal.createTreeWalker(currentlySelectedNode.getOwnerDocument().getDocumentElement(), nsIDOMNodeFilter.SHOW_ELEMENT, null, true);
				treeWalker.setCurrentNode(currentlySelectedNode);
				final nsIDOMNode n = treeWalker.nextSibling();
				if (n != null) {
					IDOMNodeSelection selection = new IDOMNodeSelection() {

						public boolean isEmpty() {
							return n == null;
						}

						public nsIDOMNode getSelectedNode() {
							return n;
						}

					};
					provider.fireSelection(selection);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		boxButtonsGData = new GridData();
		boxButtonsGData.horizontalAlignment = GridData.END;
		boxButtonsGData.horizontalSpan = 2;
		rightBox.setLayoutData(boxButtonsGData);

		downBox = new Button(boxButtons, SWT.ARROW);
		downBox.setAlignment(SWT.DOWN);
		downBox.setToolTipText("Show first child box model");
		downBox.setEnabled(false);
		downBox.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				nsIDOMDocumentTraversal docTraversal = (nsIDOMDocumentTraversal) currentlySelectedNode.getOwnerDocument().queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
				nsIDOMTreeWalker treeWalker = docTraversal.createTreeWalker(currentlySelectedNode.getOwnerDocument().getDocumentElement(), nsIDOMNodeFilter.SHOW_ELEMENT, null, true);
				treeWalker.setCurrentNode(currentlySelectedNode);
				final nsIDOMNode n = treeWalker.firstChild();
				if (n != null) {
					IDOMNodeSelection selection = new IDOMNodeSelection() {

						public boolean isEmpty() {
							return n == null;
						}

						public nsIDOMNode getSelectedNode() {
							return n;
						}

					};
					provider.fireSelection(selection);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}

		});
		boxButtonsGData = new GridData();
		boxButtonsGData.horizontalAlignment = GridData.CENTER;
		boxButtonsGData.horizontalSpan = 3;
		downBox.setLayoutData(boxButtonsGData);

		canvas = new Canvas(boxComp, SWT.DOUBLE_BUFFERED | SWT.BORDER);
		canvas.setBackground(defaultBGColor);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		canvas.setLayout(layout);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		boxModelPaintListener = new BoxModelPaintListener();
		canvas.addPaintListener(boxModelPaintListener);
		canvas.redraw();
		boxModelPaintListener.setCanvas(canvas);

		//Set up box table
		TreeColumn column = new TreeColumn(boxTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[0]);
		column.setWidth(150);
		column.setResizable(true);
		column = new TreeColumn(boxTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[1]);
		column.setWidth(150);
		column.setResizable(true);
		column.addSelectionListener(this);
		column = new TreeColumn(boxTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[2]);
		column.setWidth(150);
		column.setResizable(true);
	}

	private void createComputedStylesTab() {
		compDisplay = new SashForm(tabFolder, SWT.BORDER);
		compTree = new Tree(compDisplay, SWT.FULL_SELECTION | SWT.SINGLE);
		compTree.setHeaderVisible(true);
		compTree.setEnabled(true);
		compTree.setLinesVisible(true);
		compViewer = new TreeViewer(compTree);
		compViewer.setContentProvider(new ComputedStylesContentProvider());
		compViewer.setLabelProvider(new CSSLabelProvider(compTree.getDisplay()));
		compViewer.setAutoExpandLevel(2);
		compViewer.addSelectionChangedListener(this);

		//Set up computed table
		TreeColumn column = new TreeColumn(compTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[0]);
		column.setWidth(150);
		column.setResizable(true);
		column = new TreeColumn(compTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[1]);
		column.setWidth(150);
		column.setResizable(true);
		column.addSelectionListener(this);
		column = new TreeColumn(compTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[2]);
		column.setWidth(125);
		column.setResizable(true);
		column = new TreeColumn(compTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[3]);
		column.setWidth(275);
		column.setResizable(true);
		column = new TreeColumn(compTree, SWT.LEFT);
		column.setText(CSSLabelProvider.COLUMNS[4]);
		column.setWidth(75);
		column.setResizable(true);

		selector = new ColorSelector(tabFolder);
	}

	private void createStyleRulesTab() {
		//Set up CSS tree
		ruleTree = new Tree(ruleDisplay, SWT.SINGLE | SWT.FULL_SELECTION);
		ruleTree.setHeaderVisible(true);
		ruleTree.setEnabled(true);
		ruleTree.setLinesVisible(true);
		TreeColumn tc = new TreeColumn(ruleTree, SWT.LEFT);
		tc.setText(CSSLabelProvider.COLUMNS[0]);
		tc.setWidth(150);
		tc.setResizable(true);
		tc = new TreeColumn(ruleTree, SWT.LEFT);
		tc.setText(CSSLabelProvider.COLUMNS[1]);
		tc.setWidth(150);
		tc.setResizable(true);
		tc.addSelectionListener(this);
		TreeColumn valueColumn = new TreeColumn(ruleTree, SWT.LEFT);
		valueColumn.setText(CSSLabelProvider.COLUMNS[2]);
		valueColumn.setWidth(125);
		valueColumn.setResizable(true);
		tc = new TreeColumn(ruleTree, SWT.LEFT);
		tc.setText(CSSLabelProvider.COLUMNS[3]);
		tc.setWidth(275);
		tc.setResizable(true);
		tc = new TreeColumn(ruleTree, SWT.LEFT);
		tc.setText(CSSLabelProvider.COLUMNS[4]);
		tc.setWidth(75);
		tc.setResizable(true);

		//Set up tree viewer
		ruleViewer = new TreeViewer(ruleTree);
		ruleViewerContentProvider = new StyleRulesContentProvider();
		ruleViewer.setContentProvider(ruleViewerContentProvider);
		CSSLabelProvider labelProvider = new CSSLabelProvider(ruleViewer.getControl().getDisplay());
		ruleViewer.setLabelProvider(labelProvider);
		ruleViewer.addSelectionChangedListener(this);
		ruleViewer.setAutoExpandLevel(2);

		TreeViewerColumn tvc = new TreeViewerColumn(ruleViewer, valueColumn);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((CSSProperty) element).getValue();
			}
		});
		tvc.setEditingSupport(new CSSPropertyEditingSupport(ruleViewer, ruleViewerContentProvider));
		ruleViewer.setColumnProperties(new String[] { null, null, "value", null, null });

		menu = new Menu(ruleViewer.getControl());
		menu.setEnabled(true);
		openFile = new MenuItem(menu, SWT.PUSH);
		openFile.setText("Open CSS file");
		openFile.setEnabled(false);
		openFile.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				openInFile();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		openFile.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.OPENFILE_ID));
		addProperty = new MenuItem(menu, SWT.PUSH);
		addProperty.setText("Add property");
		addProperty.setEnabled(false);
		addProperty.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.ADDPROPERTY_ID));
		addProperty.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addProperty();
			}
		});
		toggleHighlighting = new MenuItem(menu, SWT.PUSH);
		toggleHighlighting.setText("Toggle highlighting");
		toggleHighlighting.setEnabled(false);
		toggleHighlighting.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				toggleHighlight();
				toggleHighlight.setChecked(!toggleHighlight.isChecked());
			}
		});
		toggleHighlighting.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.HIGHLIGHT_ID));
		ruleViewer.getControl().setMenu(menu);
	}

	private void createDiffsTab() {
		//Diffs
		diffDisplay = new SashForm(tabFolder, SWT.BORDER);
		diffDisplay.setLayout(new GridLayout(1, true));

		diffDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite diffComp = new Composite(diffDisplay, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		diffComp.setLayout(layout);
		diffComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite radios = new Composite(diffComp, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		radios.setLayout(layout);
		radios.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		showSelected = new Button(radios, SWT.RADIO);
		showSelected.setText("Show selection CSS diff");
		showSelected.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				showSelectedDiff();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		showSelected.setSelection(true);
		showGlobal = new Button(radios, SWT.RADIO);
		showGlobal.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				showGlobalDiff();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		showGlobal.setText("Show global CSS diff");
		diffs = new SashForm(diffComp, SWT.HORIZONTAL);
		diffs.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		originalDoc = new Document();
		originalViewer = new SourceViewer(diffs, new VerticalRuler(1), SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		originalViewer.configure(new CSSViewerConfiguration());
		originalViewer.setEditable(false);
		originalViewer.setDocument(originalDoc);
		modifiedDoc = new Document();
		modifiedViewer = new SourceViewer(diffs, new VerticalRuler(1), SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		modifiedViewer.configure(new CSSViewerConfiguration());
		modifiedViewer.setEditable(false);
		modifiedViewer.setDocument(modifiedDoc);
		diffs.setVisible(true);
	}

	private void showRulesTab() {
		nsIDOMDocument document = documentContainer.getDocument();
		nsIDOMDocument oldDoc = cpl.getDocument();

		if (document.equals(oldDoc))
			shouldIndex = false;

		cpl.setNode(currentlySelectedNode);
		cpl.setDocumentContainer(documentContainer);
		if (shouldIndex) {
			canHighlight = false;
			cpl.load(new IJobChangeListener() {
				public void sleeping(IJobChangeEvent event) {
				}

				public void scheduled(IJobChangeEvent event) {
				}

				public void running(IJobChangeEvent event) {
				}

				public void done(IJobChangeEvent event) {
					canHighlight = true;
				}

				public void awake(IJobChangeEvent event) {
				}

				public void aboutToRun(IJobChangeEvent event) {
				}
			});
			shouldIndex = false;
		}
		ruleViewer.setInput(currentlySelectedNode);
		if (ruleTree.getItemCount() > 0) {
			ruleTree.showItem(ruleTree.getItem(0));
			ruleTree.showColumn(ruleTree.getColumn(0));
		}
		ruleDisplay.setMaximizedControl(ruleTree);
	}

	private void showBoxTab() {
		boxViewer.setInput(currentlySelectedNode);
		boxModelPaintListener.setNode(currentlySelectedNode);
		canvas.redraw();
		canvas.setVisible(true);
		nsIDOMDocumentTraversal docTraversal = (nsIDOMDocumentTraversal) currentlySelectedNode.getOwnerDocument().queryInterface(nsIDOMDocumentTraversal.NS_IDOMDOCUMENTTRAVERSAL_IID);
		nsIDOMTreeWalker treeWalker = docTraversal.createTreeWalker(currentlySelectedNode.getOwnerDocument().getDocumentElement(), nsIDOMNodeFilter.SHOW_ELEMENT, null, true);
		treeWalker.setCurrentNode(currentlySelectedNode);
		upBox.setEnabled(treeWalker.parentNode() != null);
		treeWalker.setCurrentNode(currentlySelectedNode);
		rightBox.setEnabled(treeWalker.nextSibling() != null);
		treeWalker.setCurrentNode(currentlySelectedNode);
		leftBox.setEnabled(treeWalker.previousSibling() != null);
		treeWalker.setCurrentNode(currentlySelectedNode);
		downBox.setEnabled(treeWalker.firstChild() != null);
		toggleAction(false);
	}

	private void showDiffTab() {
		ruleViewer.setInput(currentlySelectedNode);
		if (showGlobal.getSelection()) {
			showGlobalDiff();
		} else {
			showSelectedDiff();
		}
		toggleAction(false);
	}

	private void toggleAction(boolean state) {
		toggleHighlight.setEnabled(state);
		openAction.setEnabled(state);
		addAction.setEnabled(state);
	}

	private void showComputedTab() {
		compViewer.setInput(currentlySelectedNode);
		if (compTree.getItemCount() > 0) {
			compTree.showItem(compTree.getItem(0));
			compTree.showColumn(compTree.getColumn(0));
		}
		toggleAction(false);
	}

	private void openInFile() {
		try {
			CSSProperty property = ((CSSProperty) ((TreeSelection) ruleViewer.getSelection()).getFirstElement());
			IStorage source = locatorUtil.getSourceElement(new URL(property.getURL()));
			if (source != null) {
				sourceDisplayUtil.openInEditor(sourceDisplayUtil.getEditorInput(source), Integer.parseInt(property.getLineNumber()));
			}
		} catch (MalformedURLException e) {
			MozIDEUIPlugin.log(e);
		} catch (PartInitException e) {
			MozIDEUIPlugin.log(e);
		}
	}

	private void toggleHighlight() {
		cpl.match((CSSProperty) ((TreeSelection) ruleViewer.getSelection()).getFirstElement());
	}

	/**
	 * Create actions for showing rules, computed styles, and
	 * viewing differences. 
	 *
	 */
	private void createActions() {
		toggleHighlight = new Action("", SWT.TOGGLE) {

			public void run() {
				toggleHighlight();
			}

		};
		toggleHighlight.setToolTipText("Toggle highlighting");
		toggleHighlight.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.HIGHLIGHT_ID));
		toggleHighlight.setEnabled(false);

		addAction = new Action() {

			public void run() {
				addProperty();
			}

		};
		addAction.setToolTipText("Add property");
		addAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.ADDPROPERTY_ID));
		addAction.setEnabled(false);

		openAction = new Action() {

			public void run() {
				openInFile();
			}

		};
		openAction.setToolTipText("Open CSS file");
		openAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.OPENFILE_ID));
		openAction.setEnabled(false);
	}

	private void showGlobalDiff() {
		((StyleRulesContentProvider) ruleViewer.getContentProvider()).generateGlobalDiff();
		showDiff();
	}

	private void showSelectedDiff() {
		((StyleRulesContentProvider) ruleViewer.getContentProvider()).generateSelectionDiff();
		showDiff();
	}

	/**
	 * Shows the original and values for rule properties and any changed
	 * properties with the new value.
	 *
	 */
	private void showDiff() {
		originalDoc.set(((StyleRulesContentProvider) ruleViewer.getContentProvider()).getOriginal());
		originalViewer.setDocument(originalDoc);
		modifiedDoc.set(((StyleRulesContentProvider) ruleViewer.getContentProvider()).getModified());
		modifiedViewer.setDocument(modifiedDoc);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	public Control getControl() {
		return top;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#setFocus()
	 */
	public void setFocus() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.css.ICSSPage#setDOMDocumentContainer(org.eclipse.atf.mozilla.ide.ui.browser.IDOMDocumentContainer)
	 */
	public void setWebBrowser(IWebBrowser documentContainer) {
		Assert.isTrue(this.documentContainer == null, "CSSPage already initialized, cannot call setDOMDocumentContainer() more than once.");
		this.documentContainer = documentContainer;

		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domDocumentListener);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(documentContainer, domMutationListener);
	}

	public void dispose() {
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(documentContainer, domDocumentListener);
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(documentContainer, domMutationListener);

		//dispose of colors
		defaultBGColor.dispose();

		//disposing colors used for the Box Metrics Widget
		boxModelPaintListener.dispose();

		cpl.disable();
		cpl.dispose();
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection instanceof IDOMNodeSelection) {
			cpl.disable();
			if (selection.isEmpty()) {
				clearTree();
			} else {
				if (currentlySelectedNode == null) {
					displayStyle(((IDOMNodeSelection) selection).getSelectedNode());
				} else if (!((IDOMNodeSelection) selection).getSelectedNode().equals(currentlySelectedNode)) {
					displayStyle(((IDOMNodeSelection) selection).getSelectedNode());
				}

			}
		}

	}

	/**
	 * 
	 * @param selectedNode
	 */
	private void displayStyle(nsIDOMNode selectedNode) {
		if (selectedNode != null) {
			currentlySelectedNode = selectedNode;
			shouldIndex = true;
			if (selectedNode.getNodeType() == nsIDOMNode.ELEMENT_NODE) {
				if (tabFolder.getSelection().equals(rulesTab)) {
					showRulesTab();
				} else if (tabFolder.getSelection().equals(boxTab)) {
					showBoxTab();
				} else if (tabFolder.getSelection().equals(computedTab)) {
					showComputedTab();
				} else if (tabFolder.getSelection().equals(diffTab)) {
					showDiffTab();
				}
			} else {
				clearTree();
			}
		}
	}

	private void clearCanvas() {
		canvas.setVisible(false);
		upBox.setEnabled(false);
		rightBox.setEnabled(false);
		leftBox.setEnabled(false);
		downBox.setEnabled(false);
	}

	/**
	 * Clears the tree of all values
	 *
	 */
	public void clearTree() {
		clearCanvas();
		ruleTree.clearAll(true);
		ruleViewer.setInput(null);
		compTree.clearAll(true);
		compViewer.setInput(null);
		boxTree.clearAll(true);
		boxViewer.setInput(null);
		currentlySelectedNode = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		Object selection = ((TreeSelection) ruleViewer.getSelection()).getFirstElement();
		if (selection != null && selection instanceof CSSProperty) {
			CSSProperty property = (CSSProperty) selection;
			if (e.getSource().equals("delete")) {
				((StyleRulesContentProvider) ruleViewer.getContentProvider()).deleteProperty(property);

			}
		}
	}

	private void addProperty() {
		Object selection = ((TreeSelection) ruleViewer.getSelection()).getFirstElement();
		CSSProperty property = null;
		if (selection != null && selection instanceof CSSProperty) {
			property = (CSSProperty) selection;
		}

		InputDialog nameDialog = new InputDialog(getControl().getShell(), "Add Property", "Type property name and value separated by colon", "name: value", new IInputValidator() {
			public String isValid(String newText) {
				if (newText.indexOf(':') == -1)
					return "Use \"name : value\" format.";
				int colon = newText.indexOf(':');

				String name = newText.substring(0, colon).trim();
				if ((name.length() == 0) || (!ruleViewerContentProvider.isPropertyName(name)))
					return "Unknown property name \"" + name + "\"";

				String value = newText.substring(colon + 1).trim();
				if (value.length() == 0)
					return "Value cannot be empty";

				return null;

			}
		});
		if (nameDialog.open() != InputDialog.OK)
			return;

		String val = nameDialog.getValue();
		int colon = val.indexOf(':');

		String name = val.substring(0, colon).trim();
		String value = val.substring(colon + 1);

		CSSProperty newProperty = new CSSProperty(name, value, property.getURL(), property.getLineNumber(), property.getRule());
		newProperty.setNewRule(true);
		newProperty.setInline(property.isInline());

		ruleViewerContentProvider.updateProperty(newProperty);
		ruleViewer.refresh();

		showRulesTab();//XXX workaround to make sure ruleViewer refreshes
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		if (!event.getSelection().isEmpty() && event.getSource().equals(ruleViewer)) {
			CSSProperty property = ((CSSProperty) ((TreeSelection) ruleViewer.getSelection()).getFirstElement());
			if (!isInternal(property.getURL())) {
				if (property.isRule()) {
					addProperty.setEnabled(true);
					toggleHighlight.setChecked(false);
					addAction.setEnabled(true);
				}
				openFile.setEnabled(!property.isInline());
				openAction.setEnabled(!property.isInline());
			} else {
				openFile.setEnabled(false);
				openAction.setEnabled(false);
				addAction.setEnabled(false);
				addProperty.setEnabled(false);
			}
			toggleHighlighting.setEnabled(canHighlight || property.isInline());
			toggleHighlight.setEnabled(canHighlight || property.isInline());
			toggleHighlight.setChecked(false);
			cpl.disable();
			ruleDisplay.setMaximizedControl(ruleTree);
		} else {
			addAction.setEnabled(false);
			addProperty.setEnabled(false);
			openAction.setEnabled(false);
			openFile.setEnabled(false);
			toggleHighlight.setEnabled(false);
			toggleHighlight.setChecked(false);
			toggleHighlighting.setEnabled(false);
			cpl.disable();
		}
		if (!event.getSelection().isEmpty() && event.getSource().equals(compViewer)) {
			cpl.disable();
		}
		if (!event.getSelection().isEmpty() && event.getSelection() instanceof IDOMNodeSelection) {
			displayStyle(((IDOMNodeSelection) event.getSelection()).getSelectedNode());
		}
	}

	private static boolean isInternal(String url) {
		return url.startsWith("resource") || url.startsWith("about:");
	}

	private void checkStyle(nsIDOMElement element, String name) {
		if (currentlySelectedNode != null && element.equals(currentlySelectedNode) && name.equals("style")) {
			displayStyle(element);
		}
	}
}