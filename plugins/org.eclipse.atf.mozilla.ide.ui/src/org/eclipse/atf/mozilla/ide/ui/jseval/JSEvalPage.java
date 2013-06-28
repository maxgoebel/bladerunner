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
package org.eclipse.atf.mozilla.ide.ui.jseval;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentEvent;
import org.eclipse.atf.mozilla.ide.events.DOMDocumentListener;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView;
import org.eclipse.atf.mozilla.ide.ui.inspector.DOMLabelProvider;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.EvalExpression;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.IJSValue;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSBoolean;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSError;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSNumber;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSObject;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSObjectProperty;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.JSString;
import org.eclipse.atf.mozilla.ide.ui.jseval.model.NodeExpression;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.Page;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * 
 * @author Gino Bustelo
 *
 */
public class JSEvalPage extends Page implements IJSEvalPage, IBrowserView {

	private IWebBrowser webBrowser = null;

	//used as the proxy to evaluate javascript on the browser
	protected Evaluator evaluator = new Evaluator();

	//list of evaluated expressions
	protected List expressionList = new ArrayList();

	//UI Elements
	protected Composite contentArea = null;
	protected Text evalExprText = null;
	protected TreeViewer expressionViewer = null;

	private DOMDocumentListener domDocumentListener = new DOMDocumentListener() {
		/*
		 * (non-Javadoc)
		 * @see org.eclipse.atf.mozilla.ide.ui.common.event.IDOMDocumentListener#documentLoaded(org.eclipse.atf.mozilla.ide.ui.common.event.DOMDocumentEvent)
		 */
		public void documentLoaded(DOMDocumentEvent event) {
			if (event.isTop()) {
				evaluator.init(event.getTargetDocument());
				evalExprText.setEnabled(true);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.atf.mozilla.ide.ui.common.event.IDOMDocumentListener#documentUnloaded(org.eclipse.atf.mozilla.ide.ui.common.event.DOMDocumentEvent)
		 */
		public void documentUnloaded(DOMDocumentEvent event) {

			if (getControl().isDisposed())
				return; //nothing to do

			clearAction.run();

			//should uninit evaluator
		}
	};

	//actions
	protected Action clearAction = null;

	protected Color white = new Color(Display.getCurrent(), new RGB(255, 255, 255));
	protected Color blue = new Color(Display.getCurrent(), new RGB(0, 0, 255));
	protected Color red = new Color(Display.getCurrent(), new RGB(255, 0, 0));

	protected static final int NAME_COL_IDX = 0;
	protected static final int VALUE_COL_IDX = 1;

	protected class EvalViewerLabelProvider implements ITableLabelProvider, ITableColorProvider, ILabelProvider {

		private DOMLabelProvider domLabelProvider = new DOMLabelProvider();

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String ret = "";

			if (element instanceof NodeExpression && columnIndex == NAME_COL_IDX) {
				NodeExpression nExpr = (NodeExpression) element;

				ret += ">>> " + domLabelProvider.getText(nExpr.getContext());
			} else if (element instanceof EvalExpression && columnIndex == NAME_COL_IDX) {
				ret += ">>> " + ((EvalExpression) element).getExpression();
			} else if (element instanceof EvalExpression && columnIndex == VALUE_COL_IDX) {

				ret += getColumnText(((EvalExpression) element).getResult(), VALUE_COL_IDX);
			} else if (element instanceof IJSValue) {
				IJSValue jsValue = (IJSValue) element;

				if (columnIndex == NAME_COL_IDX) {
					if (jsValue.getParentProperty() != null) {
						ret += jsValue.getParentProperty().name;

					}
				}

				else if (columnIndex == VALUE_COL_IDX) {
					String typeId = jsValue.getType();
					if (typeId == IJSValue.NUMBER) {
						String val = ((JSNumber) jsValue).value;
						ret += val != null ? val : "";

					} else if (typeId == IJSValue.STRING) {
						String val = ((JSString) jsValue).value;
						ret += val != null ? "\"" + val + "\"" : "null";

					} else if (typeId == IJSValue.FUNCTION) {
						ret += "function()";

					} else if (typeId == IJSValue.OBJECT) {
						ret += "object{}";
					} else if (typeId == IJSValue.NULL) {
						ret += "null";

					} else if (typeId == IJSValue.BOOLEAN) {
						ret += ((JSBoolean) jsValue).value;

					} else if (typeId == IJSValue.UNDEFINED) {
						ret += "undefined";

					} else if (typeId == IJSValue.ERROR) {
						ret += ((JSError) jsValue).message;
					}
				}
			}
			return ret;
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Color getBackground(Object element, int columnIndex) {
			return null;
		}

		public Color getForeground(Object element, int columnIndex) {
			Color c = null;
			if (element instanceof EvalExpression) {
				if (columnIndex == NAME_COL_IDX)
					c = blue;
				else if (columnIndex == VALUE_COL_IDX) {
					IJSValue result = ((EvalExpression) element).getResult();

					String type = result.getType();
					if (type == IJSValue.ERROR)
						c = red;
				}
			}

			return c;
		}

		/*
		 * ILabelProvider interface is used for sorting
		 */
		public Image getImage(Object element) {
			return null;
		}

		public String getText(Object element) {
			return getColumnText(element, NAME_COL_IDX);
		}
	}

	protected class EvalViewerContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {

			if (parentElement == expressionList) {
				return expressionList.toArray();

			} else if (parentElement instanceof EvalExpression) {
				return getChildren(((EvalExpression) parentElement).getResult());

			} else if (parentElement instanceof JSObject) {
				JSObject jsObj = (JSObject) parentElement;
				if (jsObj.evaluated) {

					JSObjectProperty[] props = jsObj.getProperties();
					ArrayList childTypes = new ArrayList(props.length);

					for (int i = 0; i < props.length; i++) {
						childTypes.add(props[i].value);
					}

					return childTypes.toArray();
				} else {

					/*
					 * Although the evaluation is done with events... it is all single thread so the result
					 * should be ready once we return from evaluate
					 */
					evaluate(jsObj);
					return getChildren(jsObj);
				}
			} else if (parentElement instanceof JSObjectProperty) {
				JSObjectProperty prop = (JSObjectProperty) parentElement;
				return getChildren(prop.value);
			}
			return null;
		}

		public Object getParent(Object element) {
			if (element instanceof IJSValue) {
				IJSValue jsValue = (IJSValue) element;

				if (jsValue.getParentProperty() != null) {
					JSObject parentObject = jsValue.getParentProperty().parentObject;
					if (parentObject.getExpression() != null) {
						return parentObject.getExpression();
					} else
						return parentObject;

				}

			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element == expressionList) {
				return expressionList.size() > 0;

			} else if (element instanceof EvalExpression) {
				return hasChildren(((EvalExpression) element).getResult());

			} else if (element instanceof JSObject) {
				JSObject jsObj = (JSObject) element;

				if (jsObj.evaluated) {
					return jsObj.getProperties().length > 0;
				} else {
					return true;
				}
			}
			return false;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	protected class EvalViewerSorter extends ViewerSorter {
		public int compare(Viewer viewer, Object e1, Object e2) {
			//expressions are not sorted so they are kept in the order they
			//appear on the list
			if (e1 instanceof EvalExpression && e2 instanceof EvalExpression)
				return expressionList.indexOf(e1) - expressionList.indexOf(e2);
			else {
				return super.compare(viewer, e1, e2);
			}

		}
	};

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
		watchedNodeLabel.setText("Expression:");

		gridData = new GridData();
		gridData.horizontalSpan = 1;
		//gridData.grabExcessHorizontalSpace = true;
		//gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.BEGINNING;
		gridData.verticalAlignment = GridData.CENTER;
		watchedNodeLabel.setLayoutData(gridData);

		evalExprText = new Text(contentArea, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = true;
		//gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		evalExprText.setLayoutData(gridData);

		//this is disabled until the page side code is ready
		evalExprText.setText("");
		evalExprText.setEnabled(false);

		evalExprText.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			public void keyReleased(KeyEvent e) {
				if (e.character == SWT.CR) {

					evalExprText.setEnabled(false);

					String expressionString = evalExprText.getText();
					IJSValue result = evaluator.evaluate(expressionString);

					evalExprText.setEnabled(true);

					evalExprText.setText("");

					EvalExpression evalExpr = new EvalExpression(expressionString, result);
					expressionList.add(0, evalExpr);

					expressionViewer.insert(expressionList, evalExpr, 0);
					//expressionViewer.insert( expressionList, evalExpr.getResult(), 1 );
					expressionViewer.reveal(evalExpr);

					evalExprText.setFocus();
				}
			}

		});

		//tree showing expression list
		expressionViewer = new TreeViewer(contentArea, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		expressionViewer.getTree().setFont(contentArea.getFont());
		expressionViewer.getTree().setLinesVisible(true);

		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		expressionViewer.getControl().setLayoutData(gridData);

		TreeColumn column = new TreeColumn(expressionViewer.getTree(), SWT.LEFT);
		//column.setText( "Name" );
		column.setResizable(false);
		//column.setWidth( 150 );
		column = new TreeColumn(expressionViewer.getTree(), SWT.LEFT);
		//column.setText( "Value" );
		column.setResizable(false);
		//column.setWidth( 150 );

		expressionViewer.getTree().setHeaderVisible(false);

		final Tree tree = expressionViewer.getTree();
		expressionViewer.getTree().addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = contentArea.getClientArea();
				TreeColumn column1 = tree.getColumn(0);
				TreeColumn column2 = tree.getColumn(1);

				Point preferredSize = tree.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				int width = area.width - 2 * tree.getBorderWidth();
				if (preferredSize.y > area.height + tree.getHeaderHeight()) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = tree.getVerticalBar().getSize();
					width -= vBarSize.x;
				}
				Point oldSize = tree.getSize();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns 
					// smaller first and then resize the table to
					// match the client area width
					column1.setWidth(width / 3);
					column2.setWidth(width - column1.getWidth());
					tree.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table 
					// bigger first and then make the columns wider
					// to match the client area width
					tree.setSize(area.width, area.height);
					column1.setWidth(width / 3);
					column2.setWidth(width - column1.getWidth());
				}
			}

		});

		expressionViewer.setContentProvider(new EvalViewerContentProvider());

		expressionViewer.setLabelProvider(new EvalViewerLabelProvider());

		expressionViewer.setSorter(new EvalViewerSorter());

		expressionViewer.setInput(expressionList);

		createAction();

		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();
		toolBarManager.add(clearAction);

		if (webBrowser.getDocument() != null) {
			evaluator.init(webBrowser.getDocument());
			evalExprText.setEnabled(true);
		}

	}

	protected void evaluate(JSObject jsObject) {

		StringBuffer evalExpression = new StringBuffer();

		evalExpression.append(jsObject.getParentProperty().name);
		JSObject parent = jsObject.getParentProperty().parentObject;
		while (parent != null) {

			//parent is not the root of the expression
			if (parent.getParentProperty() != null) {
				evalExpression.insert(0, '.');
				evalExpression.insert(0, parent.getParentProperty().name);
				parent = parent.getParentProperty().parentObject;
			} else
				break;
		}

		evalExprText.setEnabled(false);
		expressionViewer.getTree().setEnabled(false);

		EvalExpression rootExpression = parent.getExpression(); //must have Expression if is the root

		//run the eval
		IJSValue result = null;
		if (rootExpression instanceof NodeExpression) {
			nsIDOMNode context = ((NodeExpression) rootExpression).getContext();
			MozIDEUIPlugin.debug("<NODE>." + evalExpression.toString());
			result = evaluator.evaluate(context, evalExpression.toString());
		} else {
			evalExpression.insert(0, '.');
			evalExpression.insert(0, rootExpression.getExpression());
			MozIDEUIPlugin.debug(evalExpression.toString());

			result = evaluator.evaluate(evalExpression.toString());
		}

		evalExprText.setEnabled(true);
		expressionViewer.getTree().setEnabled(true);
		expressionViewer.getTree().setFocus();

		//has to be object
		JSObject evaledObject = (JSObject) result;
		jsObject.setProperties(evaledObject.getProperties());
		jsObject.evaluated = true;
	}

	public boolean isReady() {
		if (evaluator != null)
			return evaluator.isReady();
		else
			return false;
	}

	protected void createAction() {

		//CLEAR
		clearAction = new Action("Clear", Action.AS_PUSH_BUTTON) {
			public void run() {
				expressionList.clear();
				expressionViewer.refresh();
			}
		};

		clearAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CLEAR_IMG_ID));
		//startWatchAction.setDisabledImageDescriptor( MozillaExtrasPlugin.imageDescriptorFromPlugin(MozillaExtrasPlugin.PLUGIN_ID, "icons/startWatcher_d.gif") );
		clearAction.setToolTipText("Clear");

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	public void dispose() {
		white.dispose();
		blue.dispose();
		red.dispose();
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().removeEventListener(webBrowser, domDocumentListener);

		super.dispose();
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
	 * @see org.eclipse.atf.mozilla.ide.ui.browser.views.IBrowserView#setDOMDocumentContainer(org.eclipse.atf.mozilla.ide.ui.browser.IDOMDocumentContainer)
	 */
	public void setWebBrowser(IWebBrowser documentContainer) {
		if (this.webBrowser != null)
			throw new AssertionFailedException("DOMWatcherPage already initialized, cannot call setDOMDocumentContainer() more than once.");
		this.webBrowser = documentContainer;
		MozIDEUIPlugin.getDefault().getApplicationEventAdmin().addEventListener(webBrowser, domDocumentListener);

	}

	public void evalElement(nsIDOMNode node) {

		//for sure an object
		IJSValue result = evaluator.evaluate(node, "");
		NodeExpression evalExpr = new NodeExpression(node, "", result);
		expressionList.add(0, evalExpr);

		expressionViewer.insert(expressionList, evalExpr, 0);
		//expressionViewer.insert( expressionList, evalExpr.getResult(), 1 );
		expressionViewer.reveal(evalExpr);
	}

	protected void evalElement(nsIDOMNode node, String expression) {

	}

}
