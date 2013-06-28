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

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.inspector.properties.DOMAttributeAddDialog;
import org.eclipse.atf.mozilla.ide.ui.inspector.properties.DOMAttributeEditDialog;
import org.eclipse.atf.mozilla.ide.ui.inspector.properties.DOMAttributeProperty;
import org.eclipse.atf.mozilla.ide.ui.inspector.properties.IDOMInspectorProperty;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNamedNodeMap;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.xpcom.XPCOMException;

public class DOMAttributeViewer extends TableViewer {

	protected static final String[] attributesColumns = { "Attribute", "Value" };

	protected Menu contextMenu = null;

	public DOMAttributeViewer(Composite parent, int style) {
		super(parent, style);

		setupTable();
		setupProviders();
		setupEdit();
		setupMenu();
	}

	protected void setupTable() {
		Table table = getTable();

		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(attributesColumns[0]);

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(attributesColumns[1]);

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		//handle first resize to set column width in relation to parent width
		table.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				handleResize(e);
			}
		});

	}

	protected void setupProviders() {

		setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == 0)
					return ((IDOMInspectorProperty) element).getDisplayName();
				else if (columnIndex == 1) {

					nsIDOMNode node = (nsIDOMNode) getInput();

					return ((IDOMInspectorProperty) element).getValue(node);
				} else
					return null;
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}

		});

		setContentProvider(new IStructuredContentProvider() {

			public Object[] getElements(Object inputElement) {

				if (((nsIDOMNode) inputElement).getNodeType() == nsIDOMNode.ELEMENT_NODE) {
					try {
						//get all the attributes name
						nsIDOMNode node = (nsIDOMNode) inputElement;
						nsIDOMElement element = (nsIDOMElement) node.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);

						nsIDOMNamedNodeMap attrMap = element.getAttributes();
						IDOMInspectorProperty[] properties = new IDOMInspectorProperty[(int) attrMap.getLength()];
						for (int i = 0; i < (int) attrMap.getLength(); i++) {

							nsIDOMNode attr = attrMap.item(i);
							properties[i] = new DOMAttributeProperty(attr.getNodeName(), attr.getNodeName());

						}
						return properties;
					} catch (Exception e) {
						//something when wrong so return empty
						MozIDEUIPlugin.log(e);
						return new Object[0];
					}
				}

				else
					return new Object[0];
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

		});
	}

	protected void setupEdit() {
		//allow the attribute values to be editable
		Table table = getTable();
		CellEditor[] editors = new CellEditor[table.getColumnCount()];
		TextCellEditor valueEditor = new TextCellEditor(table);
		editors[1] = valueEditor;

		setCellEditors(editors);

		setColumnProperties(attributesColumns); //needed for the modifier to work

		setCellModifier(new ICellModifier() {

			public boolean canModify(Object element, String property) {
				if (property == attributesColumns[1] && element instanceof DOMAttributeProperty) {
					return true;
				}

				else
					return false;
			}

			public Object getValue(Object element, String property) {
				DOMAttributeProperty attrProperty = (DOMAttributeProperty) element;
				return attrProperty.getValue((nsIDOMNode) getInput());
			}

			public void modify(Object element, String property, Object value) {
				if (element instanceof TableItem) {
					//for some reason they send the TableItem in instead of the (DOMAttributeProperty)
					TableItem item = (TableItem) element;

					//check if the value is different before setting (it actually came in handy that they send the TableItem
					if (!item.getText().equals(value)) {
						DOMAttributeProperty attrProperty = (DOMAttributeProperty) item.getData();
						;
						attrProperty.setValue((nsIDOMNode) getInput(), (String) value);
						refresh(); //refresh the view (should probably do this per attributes based on listening to a change event from the DOMElement
					}
				}
			}

		});

	}

	protected void setupMenu() {
		contextMenu = new Menu(getControl());

		/*
		 * ADD MENU
		 */
		MenuItem addItem = new MenuItem(contextMenu, SWT.PUSH);
		addItem.setText("Add");
		addItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				//MozIDEUIPlugin.debug( "Add menu pressed..." );

				DOMAttributeAddDialog dialog = new DOMAttributeAddDialog(DOMAttributeViewer.this.getControl().getShell());

				int rc = dialog.open();

				if (rc != Dialog.OK)
					return;

				String attributeName = dialog.getAttributeName();
				String attributeValue = dialog.getAttributeValue();

				if (attributeName != null && attributeValue != null && !"".equals(attributeName) && !"".equals(attributeValue)) {
					Object inputNode = DOMAttributeViewer.this.getInput();
					if (inputNode instanceof nsIDOMNode && ((nsIDOMNode) inputNode).getNodeType() == nsIDOMNode.ELEMENT_NODE) {

						try {

							nsIDOMElement inputElement = (nsIDOMElement) ((nsIDOMNode) inputNode).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
							inputElement.setAttribute(attributeName, attributeValue);
						} catch (XPCOMException xpcome) {
							MozIDEUIPlugin.log(xpcome);
						}
					}
				}
			}

		});

		/*
		 * REMOVE MENU
		 */
		MenuItem removeItem = new MenuItem(contextMenu, SWT.PUSH);
		removeItem.setText("Remove");
		removeItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				//MozIDEUIPlugin.debug( "Remove menu pressed..." );

				Object inputNode = DOMAttributeViewer.this.getInput();
				if (inputNode instanceof nsIDOMNode && ((nsIDOMNode) inputNode).getNodeType() == nsIDOMNode.ELEMENT_NODE) {

					//get the attribute selection
					IStructuredSelection attributeTableSelection = (IStructuredSelection) DOMAttributeViewer.this.getSelection();

					Object selectedObject = attributeTableSelection.getFirstElement();
					if (selectedObject != null) {

						//extract the attribute name from the selection
						DOMAttributeProperty attrProp = (DOMAttributeProperty) selectedObject;

						try {
							nsIDOMElement inputElement = (nsIDOMElement) ((nsIDOMNode) inputNode).queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
							inputElement.removeAttribute(attrProp.getName());
						} catch (XPCOMException xpcome) {
							MozIDEUIPlugin.log(xpcome);
						}
					}

				}

			}

		});

		/*
		 * EDIT MENU
		 */
		MenuItem editItem = new MenuItem(contextMenu, SWT.PUSH);
		editItem.setText("Edit");
		editItem.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				//MozIDEUIPlugin.debug( "Edit menu pressed..." );

				Object inputNode = DOMAttributeViewer.this.getInput();
				if (inputNode instanceof nsIDOMNode && ((nsIDOMNode) inputNode).getNodeType() == nsIDOMNode.ELEMENT_NODE) {

					nsIDOMNode inputDOMNode = (nsIDOMNode) inputNode;

					//get the attribute selection
					IStructuredSelection attributeTableSelection = (IStructuredSelection) DOMAttributeViewer.this.getSelection();

					Object selectedObject = attributeTableSelection.getFirstElement();
					if (selectedObject != null) {

						//extract the attribute name from the selection
						DOMAttributeProperty attrProp = (DOMAttributeProperty) selectedObject;

						DOMAttributeEditDialog dialog = new DOMAttributeEditDialog(DOMAttributeViewer.this.getControl().getShell(), attrProp.getName(), attrProp.getValue(inputDOMNode));

						int rc = dialog.open();

						if (rc != Dialog.OK)
							return;

						String attributeValue = dialog.getAttributeValue();

						if (attributeValue == null)
							attributeValue = ""; //allow clearing an attribute value when editing

						try {

							nsIDOMElement inputDOMElement = (nsIDOMElement) inputDOMNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
							inputDOMElement.setAttribute(attrProp.getName(), attributeValue);
						} catch (XPCOMException xpcome) {
							MozIDEUIPlugin.log(xpcome);
						}
					} else {
						//notify that there is no attribute selection (should not happend)
					}

				}

			}

		});

		getControl().setMenu(contextMenu);

		//selection listener used to enable the correct menu items according to the selectiom
		contextMenu.addMenuListener(new MenuListener() {

			public void menuHidden(MenuEvent e) {
			}

			public void menuShown(MenuEvent e) {
				Object inputNode = DOMAttributeViewer.this.getInput();

				if (inputNode instanceof nsIDOMNode && ((nsIDOMNode) inputNode).getNodeType() == nsIDOMNode.ELEMENT_NODE) {

					contextMenu.getItem(0).setEnabled(true);

					/*
					 * Only check the status of the Table selection if what is selected on the Tree is an actual Element node
					 */
					ISelection selection = DOMAttributeViewer.this.getSelection();

					if (selection != null) {
						IStructuredSelection strSelection = (IStructuredSelection) selection;

						//table only support SINGLE selection
						if (strSelection.size() == 1) {
							//enable EDIT and REMOVE
							contextMenu.getItem(1).setEnabled(true); //item 1 is Remove
							contextMenu.getItem(2).setEnabled(true); //item 2 is Edit
						}

						else {
							//disable EDIT and REMOVE
							contextMenu.getItem(1).setEnabled(false); //item 1 is Remove
							contextMenu.getItem(2).setEnabled(false); //item 2 is Edit
						}
					}
				} else {
					contextMenu.getItem(0).setEnabled(false);
					contextMenu.getItem(1).setEnabled(false);
					contextMenu.getItem(2).setEnabled(false);
				}

			}
		});
	}

	protected boolean initialResize = true;

	/*
	 * This resize handler makes sure that the 2nd column is always stretched out to take over the remaining
	 * client area that the 1st column is not occupying.
	 */
	protected void handleResize(ControlEvent e) {
		Rectangle area = getTable().getClientArea();
		TableColumn[] columns = getTable().getColumns();
		if (initialResize) {
			if (area.width > 0 && columns[0].getWidth() == 0) {
				columns[0].setWidth(area.width * 40 / 100);
				columns[1].setWidth(area.width - columns[0].getWidth() - 4);

			}
			initialResize = false;
		} else {
			if (columns[0].getWidth() < area.width) {
				columns[1].setWidth(area.width - columns[0].getWidth());
			}

		}
	}

}
