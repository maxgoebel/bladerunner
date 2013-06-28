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
import java.util.List;

import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.ITimedEvent;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class NetworkMonitorTable {

	protected static final String STATE_COL = "state";
	protected static final String URL_COL = "url";
	protected static final String METHOD_COL = "method";
	protected static final String START_COL = "start";
	protected static final String STOP_COL = "stop";
	protected static final String ELAPSED_COL = "elapsed";
	protected String[] columnNames = { STATE_COL, URL_COL, METHOD_COL, START_COL, STOP_COL, ELAPSED_COL };

	protected TableViewer callsViewer = null;
	protected Table table = null;
	private EventRecorder callList;

	private class NetworkCallListContentProvider implements IStructuredContentProvider {

		// Return the variables as an array of Objects
		public Object[] getElements(Object parent) {
			IApplicationEvent[] result = callList.getAll();
			return eventsToCalls(result);
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	public static INetworkCall[] eventsToCalls(IApplicationEvent[] events) {
		List<INetworkCall> filtered = new ArrayList<INetworkCall>();
		for (int i = 0; i < events.length; i++) {
			INetworkCall event = eventToCall(events[i]);
			if (event != null) {
				filtered.add(event);
			}
		}
		return filtered.toArray(new INetworkCall[filtered.size()]);
	}

	public static INetworkCall eventToCall(IApplicationEvent event) {
		if (event instanceof ITimedEvent) {
			ITimedEvent tevent = (ITimedEvent) event;
			if (tevent.getData() instanceof INetworkCall) {
				return (INetworkCall) tevent.getData();
			}
		}

		return null;
	}

	public NetworkMonitorTable(SashForm displayArea) {
		createTable(displayArea);

		callsViewer.setLabelProvider(new NetworkCallLabelProvider());
		callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.START_TIME));
		callsViewer.setContentProvider(new NetworkCallListContentProvider());
	}

	protected void createTable(SashForm displayArea) {

		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;

		Table newTable = new Table(displayArea, style);
		table = newTable;

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		newTable.setLayoutData(gridData);

		newTable.setLinesVisible(true);
		newTable.setHeaderVisible(true);

		createTableViewer(newTable);

		// 1st column 
		TableColumn column = new TableColumn(newTable, SWT.LEFT, 0);
		column.setText("");
		column.setWidth(25);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.STATE) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.STATE, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.STATE));
				}
			}
		});

		// 2nd column 
		column = new TableColumn(newTable, SWT.LEFT, 1);
		column.setText("URL");
		column.setWidth(300);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.URL) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.URL, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.URL));
				}
			}
		});

		// 3rd column 
		column = new TableColumn(newTable, SWT.LEFT, 2);
		column.setText("Method");
		column.setWidth(75);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.METHOD) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.METHOD, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.METHOD));
				}
			}
		});

		// 4th column
		column = new TableColumn(newTable, SWT.LEFT, 3);
		column.setText("Start Time");
		column.setWidth(175);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.START_TIME) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.START_TIME, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.START_TIME));
				}
			}
		});

		// 5th column
		column = new TableColumn(newTable, SWT.LEFT, 4);
		column.setText("Stop Time");
		column.setWidth(175);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.STOP_TIME) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.STOP_TIME, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.STOP_TIME));
				}
			}
		});

		// 6th column
		column = new TableColumn(newTable, SWT.LEFT, 5);
		column.setText("Elapsed Time");
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.ELAPSED_TIME) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.ELAPSED_TIME, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.ELAPSED_TIME));
				}
			}
		});

		//7th column STATUS CODE
		column = new TableColumn(newTable, SWT.LEFT, 6);
		column.setText("Status Code");
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				NetworkCallSorter sorter = (NetworkCallSorter) callsViewer.getSorter();
				if (sorter != null && sorter.getColumnCriteria() == NetworkCallSorter.STATE) {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.STATE, !sorter.isAscending()));
				} else {
					callsViewer.setSorter(new NetworkCallSorter(NetworkCallSorter.STATE));
				}
			}
		});
	}

	protected void createTableViewer(Table table) {

		callsViewer = new TableViewer(table);
		callsViewer.setUseHashlookup(true);

		callsViewer.setColumnProperties(columnNames);

		/*		// Create the cell editors
				CellEditor[] editors = new CellEditor[columnNames.length];

				// Column 1 : State (text)
				TextCellEditor textEditor = new TextCellEditor(table, SWT.READ_ONLY);
				((Text) textEditor.getControl()).setTextLimit(50);
				editors[0] = textEditor;

				// Column 2 : URL (text)
				textEditor = new TextCellEditor(table, SWT.READ_ONLY);
				((Text) textEditor.getControl()).setTextLimit(150);
				editors[1] = textEditor;

				// Column 3 : Method (text) 
				textEditor = new TextCellEditor(table, SWT.READ_ONLY);
				((Text) textEditor.getControl()).setTextLimit(75);
				editors[2] = textEditor;
				
				// Column 4 : Start Time (Text)
				textEditor = new TextCellEditor(table, SWT.READ_ONLY);
				((Text) textEditor.getControl()).setTextLimit(100);
				editors[3] = textEditor;
				
				// Column 5 : Stop Time (Text)
				textEditor = new TextCellEditor(table, SWT.READ_ONLY);
				((Text) textEditor.getControl()).setTextLimit(100);
				editors[4] = textEditor;
				
				// Column 6 : Elapsed Time (Checkbox)
				textEditor = new TextCellEditor(table, SWT.READ_ONLY);
				((Text) textEditor.getControl()).setTextLimit(100);
				editors[5] = textEditor;
						
				// Assign the cell editors to the viewer 
				callsViewer.setCellEditors(editors);
				
				// Set the cell modifier for the viewer
				callsViewer.setCellModifier(new XHRCellModifier());*/
	}

	public void setFilters(ViewerFilter[] filters) {
		callsViewer.setFilters(filters);

	}

	public Table getControl() {
		return table;
	}

	public TableViewer getViewer() {
		return callsViewer;
	}

	public void setCallList(EventRecorder callList) {
		this.callList = callList;
		callsViewer.setInput(callList);
	}

	public void notifyCallAdded(final INetworkCall call) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				callsViewer.add(call);
			}
		});
	}

	public void notifyCallUpdated(final INetworkCall call) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				callsViewer.update(call, null);
			}
		});

		//@GINO: TODO: Probably need to check if it is the selection to update the details area
	}
}
