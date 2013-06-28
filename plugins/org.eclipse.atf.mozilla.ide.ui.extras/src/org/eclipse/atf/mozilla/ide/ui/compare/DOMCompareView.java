/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.mozilla.interfaces.nsIDOMCSSStyleDeclaration;
import org.mozilla.interfaces.nsIDOMDocumentView;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMViewCSS;

/**
 * View for DOMCompare
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class DOMCompareView extends ViewPart implements ISelectionListener {
	
	public static final String ID = "org.eclipse.atf.mozilla.ide.ui.views.compare";
	
	protected Composite leftComp;
	protected Composite listComp;
	protected Composite listButtonsComp;
	protected Composite rightComp;
	protected ListViewer compareViewer;
	protected List compare;
	protected Map nodesToCompare = new HashMap();
	protected Table nodes;
	protected TableViewer nodeTable;
	protected SashForm displayArea;
	protected Composite buttons;
	protected Label compareLabel;
	protected Label compareListLabel;
	private Menu menu;
	private MenuItem rename;
	protected Button attributes;
	protected Button children;
	protected Button css;
	protected Button executeCompare;
	protected Button clear;
	protected Button remove;
	protected nsIDOMNode selectedNode;
	protected Color green = new Color(Display.getCurrent(), new RGB(128,255,128));
	protected Color red = new Color(Display.getCurrent(), new RGB(255,128,128));
	
	public void createPartControl(Composite parent) {
		displayArea = new SashForm(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		leftComp = new Composite(displayArea, SWT.NONE);
		leftComp.setLayout(layout);
		leftComp.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true) );
		
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		listComp = new Composite(leftComp, SWT.NONE);
		listComp.setLayout(layout);
		listComp.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true) );
		
		compareListLabel = new Label(listComp,SWT.WRAP);
		compareListLabel.setText("Compare List:");
		
		compare = new List(listComp, SWT.MULTI );
		compareViewer = new ListViewer(compare);
		compare.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true) );
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		compareViewer.addDropSupport(ops, new Transfer[]{TextTransfer.getInstance()},  new DropTargetAdapter() {
			public void drop(DropTargetEvent event) {
				if( selectedNode != null && selectedNode.toString().equals(event.data) ) {
					addNodeToCompare(selectedNode);
				}
			}
		});
		
		compare.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection)compareViewer.getSelection();
				rename.setEnabled( selection.size() == 1);
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {}
		
		});
		
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = true;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		listButtonsComp = new Composite(listComp, SWT.NONE);
		listButtonsComp.setLayout(layout);
		listButtonsComp.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, false) );
		remove = new Button(listButtonsComp, SWT.PUSH);
		remove.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.CLOSE_TB_ID));
		remove.setToolTipText("Remove Node");
		remove.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) {
				Object[] selection = ((IStructuredSelection)compareViewer.getSelection()).toArray();
				for( int i = 0; i < selection.length; i++ ) {
					compareViewer.remove(selection[i]);
					nodesToCompare.remove(selection[i]);
				}
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {}
		
		});
		
		clear = new Button(listButtonsComp, SWT.PUSH);
		clear.setImage(MozIDEUIPlugin.getDefault().getImage(MozIDEUIPlugin.CLEAR_IMG_ID));
		clear.setToolTipText("Clear List");
		clear.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) {
				compare.removeAll();
				nodesToCompare.clear();
				clearTable();
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {}
		
		});
		
		buttons = new Composite(leftComp, SWT.BORDER);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		buttons.setLayout(layout);
		buttons.setLayoutData( new GridData(GridData.FILL, GridData.FILL, false, false) );
		compareLabel = new Label(buttons, SWT.NONE);
		compareLabel.setText("Compare:");
		attributes = new Button(buttons, SWT.RADIO);
		attributes.setText("DOM Attributes");
		attributes.setSelection(true);
		children = new Button(buttons, SWT.RADIO);
		children.setText("Child Nodes");
		css = new Button(buttons, SWT.RADIO);
		css.setText("CSS");
		executeCompare = new Button(buttons, SWT.PUSH);
		executeCompare.setText("Compare");
		executeCompare.setToolTipText("Compare nodes");
		executeCompare.addSelectionListener(new SelectionListener() {
		
			public void widgetSelected(SelectionEvent e) {
				if( attributes.getSelection() ) {
					compareAttributes();
				} else if( css.getSelection() ) {
					compareCSS();
				} else if( children.getSelection() ) {
					compareChildren();
				}
			}
		
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		rightComp = new Composite(displayArea, SWT.NONE);
		rightComp.setLayout(layout);
		rightComp.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true) );
		nodes = new Table(rightComp, SWT.FULL_SELECTION );
		nodes.setHeaderVisible(true);
		nodes.setLinesVisible(true);
		nodeTable = new TableViewer(nodes);
		nodes.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true) );
		menu = new Menu(compare);
		menu.setEnabled(true);
		rename = new MenuItem(menu,SWT.PUSH);
		rename.setText("Rename node");
		rename.setEnabled(false);
		rename.addSelectionListener(new SelectionListener() {
			
			public void widgetSelected(SelectionEvent e) {
				String node = compare.getSelection()[0];
				int index = compare.getSelectionIndex();
				InputDialog renameDialog = new InputDialog(rename.getParent().getShell(), 
															"Rename node", "Node Name:",
															node, null);
				if( renameDialog.open() == InputDialog.OK ) {
					compare.setItem(index, renameDialog.getValue());
					nodesToCompare.put(renameDialog.getValue(), nodesToCompare.remove(node));
					nodeTable.refresh();
				}
			}
			
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		compare.setMenu(menu);
		displayArea.setWeights(new int[]{15,85});
	}
	
	/**
	 * Public method for adding DOM nodes to the DOM Compare view.
	 * 
	 * @param node - node to add
	 */
	public void addNodeToCompare(nsIDOMNode node) {
		String hash = hashNode(node);
		compareViewer.add(hash);
		nodesToCompare.put(hash, node);
	}
	
	/**
	 * Clears the table of all columns and entries.
	 *
	 */
	private void clearTable() {
		nodes.removeAll();
		TableColumn[] columns = nodes.getColumns();
		for( int count = 0; count < columns.length; count++) {
			columns[count].dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	public void dispose() {
		green.dispose();
		red.dispose();
		super.dispose();
	}
	
	/**
	 * Creates a string label for a DOM node. Uses the id attribute
	 * if defined, else uses the hashcode.
	 * 
	 * @param node - node to create a label for
	 * @return - String label
	 */
	private String hashNode(nsIDOMNode node) {
		String hash = node.getNodeName() + "["+node.hashCode()+"]";
		if( node.getNodeType() == nsIDOMNode.ELEMENT_NODE ) {
			nsIDOMElement element = (nsIDOMElement)node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			if( element.getAttribute("id") != null && element.getAttribute("id").length() > 0 ) {
				hash = node.getNodeName() + "["+element.getAttribute("id")+"]";
			} 
		}
		return hash;
	}

	/**
	 * Compares DOM attributes of all nodes in the list.
	 *
	 */
	private void compareAttributes() {
		clearTable();
		ArrayList tableItems = new ArrayList();
		Object[] keys = nodesToCompare.keySet().toArray();
		int space = nodes.getBounds().width;
		for( int i = 0; i < keys.length; i++ ) {
			nsIDOMNode node = (nsIDOMNode)nodesToCompare.get(keys[i]);
			nsIDOMNamedNodeMap attrs = node.getAttributes();
			TableColumn column = new TableColumn(nodes, SWT.LEFT);
			column.setText(keys[i].toString());
			column.setWidth(space/keys.length);
		    column.setResizable( true );
			if( attrs != null ) {
				for( int j = 0; j < attrs.getLength(); j++ ) {
					String name = attrs.item(j).getNodeName();
					String value = attrs.item(j).getNodeValue();
					TableItem tableItem;
					if( j < tableItems.size() ) {
						tableItem = (TableItem)tableItems.get(j);
					} else {
						tableItem = new TableItem(nodes, SWT.NONE);
						tableItems.add(tableItem);
					}
					tableItem.setText(i, name+":"+value);
				}
			}
		}
	}
	
	/**
	 * Compares CSS computed styles for all nodes in the list.
	 *
	 */
	private void compareCSS() {
		clearTable();
		ArrayList tableItems = new ArrayList();
		Object[] keys = nodesToCompare.keySet().toArray();
		int space = nodes.getBounds().width;
		for( int i = 0; i < keys.length; i++ ) {
			nsIDOMNode node = (nsIDOMNode)nodesToCompare.get(keys[i]);
			nsIDOMNamedNodeMap attrs = node.getAttributes();
			TableColumn column = new TableColumn(nodes, SWT.LEFT);
			column.setText(keys[i].toString());
			column.setWidth(space/keys.length);
		    column.setResizable( true );
			if( attrs != null ) {
				if( node.getNodeType() == nsIDOMNode.ELEMENT_NODE ) {
					nsIDOMElement domElement = (nsIDOMElement)(node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
					nsIDOMDocumentView documentView = (nsIDOMDocumentView)domElement.getOwnerDocument().queryInterface( nsIDOMDocumentView.NS_IDOMDOCUMENTVIEW_IID );
					nsIDOMViewCSS cssView = (nsIDOMViewCSS)documentView.getDefaultView().queryInterface( nsIDOMViewCSS.NS_IDOMVIEWCSS_IID );
					nsIDOMCSSStyleDeclaration computedStyle = cssView.getComputedStyle( domElement, "" );
					for( int j = 0; j < computedStyle.getLength(); j++ ) {
						String name = computedStyle.item(j);
						String value = computedStyle.getPropertyCSSValue(computedStyle.item(j)).getCssText();
						TableItem tableItem;
						if( j < tableItems.size() ) {
							tableItem = (TableItem)tableItems.get(j);
						} else {
							tableItem = new TableItem(nodes, SWT.NONE);
							tableItems.add(tableItem);
						}
						tableItem.setText(i, name+":"+value);
					}
				}
			}
		}
		highlightTable(tableItems);
	}
	
	/**
	 * Compares the first level children of all nodes in the list.
	 *
	 */
	private void compareChildren() {
		clearTable();
		ArrayList tableItems = new ArrayList();
		Object[] keys = nodesToCompare.keySet().toArray();
		int space = nodes.getBounds().width;
		for( int i = 0; i < keys.length; i++ ) {
			nsIDOMNode node = (nsIDOMNode)nodesToCompare.get(keys[i]);
			nsIDOMNodeList children = node.getChildNodes();
			TableColumn column = new TableColumn(nodes, SWT.LEFT);
			column.setText(keys[i].toString());
			column.setWidth(space/keys.length);
		    column.setResizable( true );
			if( children != null ) {
				for( int j = 0; j < children.getLength(); j++ ) {
					String name = children.item(j).getNodeName();
					TableItem tableItem;
					if( j < tableItems.size() ) {
						tableItem = (TableItem)tableItems.get(j);
					} else {
						tableItem = new TableItem(nodes, SWT.NONE);
						tableItems.add(tableItem);
					}
					tableItem.setText(i, name);
				}
			}
		}
	}
	
	/**
	 * Highlights the table based on matching entries. Attempts to find the
	 * largest group of matching row elements and highlights them green.
	 * Highlights all row elements not matching red. If two row elements occur
	 * an equal number of times. The first group found is colored green.
	 * 
	 * @param tableItems - List of table items to highlight.
	 */
	private void highlightTable(java.util.List tableItems) {
		if( nodesToCompare.size() > 1 ) {
			Iterator iter = tableItems.iterator();
			while( iter.hasNext() ) {
				TableItem item = (TableItem)iter.next();
				int columns = item.getParent().getColumnCount();
				int max_matches = 0;
				String max_string = "";
				for( int i = 0; i < columns; i++ ) {
					int curr_matches = 0;
					for( int j = 0; j < columns; j++ ) {
						if( i != j && item.getText(i).equals(item.getText(j)) ) {
							curr_matches++;
						}
					}
					max_string = curr_matches > max_matches ? item.getText(i) : max_string;
					max_matches = curr_matches > max_matches ? curr_matches : max_matches;
				}
				for( int i = 0; i < columns; i++ ) {
					if( item.getText(i).equals(max_string) ) {
						item.setBackground(i, green);
					} else {
						item.setBackground(i, red);
					}
				}
			}
		}
	}
	
	

	/**
	 * Sets the currently selected node if the selection is an
	 * instance of IDOMNodeSelection.
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if( selection instanceof IDOMNodeSelection ){
			selectedNode = ((IDOMNodeSelection)selection).getSelectedNode();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus() {
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
		site.getPage().addSelectionListener(this); //setting as selection listener
		super.init(site);
	}
	
}
