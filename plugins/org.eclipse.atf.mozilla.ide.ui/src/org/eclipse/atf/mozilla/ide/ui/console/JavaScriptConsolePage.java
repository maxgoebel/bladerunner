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

package org.eclipse.atf.mozilla.ide.ui.console;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.util.SourceDisplayUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.Page;
import org.mozilla.interfaces.nsIConsoleMessage;

public class JavaScriptConsolePage extends Page implements IJavaScriptConsole {

	protected TableViewer tableViewer = null;

	//actions
	protected Action showErrorsAction = null;
	protected Action showWarningsAction = null;
	protected Action showMessagesAction = null;
	protected Action clearConsoleAction = null;
	protected Action showCSSMessages = null;
	protected Action showJSMessages = null;
	protected Action showXMLMessages = null;

	//filter
	protected ConsoleMessageFilter filter = null;
	protected ConsoleCategoryFilter catFilter = null;

	//model for the console
	protected List messages = new ArrayList();

	//helper to locate the source file from URL
	protected SourceLocatorUtil locatorUtil = SourceLocatorUtil.getInstance();

	//helper to display the source in an editor
	protected SourceDisplayUtil sourceDisplayUtil = new SourceDisplayUtil();

	private ConsoleMessageLabelProvider labelProvider;

	public void createControl(Composite parent) {

		tableViewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		styleTable(tableViewer.getTable());
		createColumns(tableViewer.getTable());

		//content provider
		tableViewer.setContentProvider(new ArrayContentProvider());

		//label provider
		labelProvider = new ConsoleMessageLabelProvider();
		tableViewer.setLabelProvider(labelProvider);

		tableViewer.setInput(messages);

		//setting filter
		filter = new ConsoleMessageFilter();
		filter.setShowMode(IJavaScriptConsole.SHOW_ALL);
		tableViewer.addFilter(filter);

		catFilter = new ConsoleCategoryFilter();
		catFilter.displayCSS(true);
		catFilter.displayJavascript(true);
		catFilter.displayXML(true);
		tableViewer.addFilter(catFilter);

		//setting dbl-click behavior for Rows to open URL in an editor
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();

				if (selection.isEmpty())
					return; //exit immediately
				else {

					if (selection instanceof IStructuredSelection) {

						nsIConsoleMessage selectedMessage = (nsIConsoleMessage) ((IStructuredSelection) selection).getFirstElement();

						String sourceName = labelProvider.getSourceName(selectedMessage);
						int line = labelProvider.getLineNum(selectedMessage);

						if (sourceName != null) {
							try {
								openInEditor(new URL(sourceName), line);
							} catch (MalformedURLException e) {
								// ignore, labelProvider.getSourceName already checks if it's possible to create URL
							}
						}

					}

				}
			}

		});

		//setting actions
		createActions();

		IToolBarManager toolBarManager = getSite().getActionBars().getToolBarManager();

		toolBarManager.add(showErrorsAction);
		toolBarManager.add(showWarningsAction);
		toolBarManager.add(showMessagesAction);
		toolBarManager.add(new Separator());
		toolBarManager.add(clearConsoleAction);
		IMenuManager menuManager = getSite().getActionBars().getMenuManager();
		menuManager.add(showCSSMessages);
		menuManager.add(showJSMessages);
		menuManager.add(showXMLMessages);

	}

	public Control getControl() {
		if (tableViewer == null)
			return null;
		else
			return tableViewer.getControl();
	}

	public void setFocus() {
		tableViewer.getControl().setFocus();
	}

	protected void styleTable(Table table) {
		table.setLinesVisible(true);
		table.setFont(table.getParent().getFont());

	}

	protected void createColumns(Table table) {
		TableLayout layout = new TableLayout();
		table.setLayout(layout);
		table.setHeaderVisible(true);

		//Flag Image column
		layout.addColumnData(new ColumnPixelData(20));
		TableColumn tc = new TableColumn(table, SWT.NONE, 0);
		tc.setMoveable(false);
		tc.setResizable(false);
		tc.setAlignment(SWT.RIGHT);

		//Message
		layout.addColumnData(new ColumnWeightData(50, true));
		tc = new TableColumn(table, SWT.NONE, 1);
		tc.setText("Message");
		tc.setMoveable(true);

		//Source File
		layout.addColumnData(new ColumnWeightData(25, true));
		tc = new TableColumn(table, SWT.NONE, 2);
		tc.setText("File");
		tc.setMoveable(true);

		//Line Number
		layout.addColumnData(new ColumnWeightData(15, true));
		tc = new TableColumn(table, SWT.NONE, 3);
		tc.setText("Line Number");
		tc.setMoveable(true);

	}

	protected void createActions() {
		showCSSMessages = new Action("Show CSS messages", Action.AS_CHECK_BOX) {
			public void run() {
				catFilter.displayCSS(isChecked());
				if (tableViewer != null && !tableViewer.getControl().isDisposed())
					tableViewer.refresh();
			}
		};
		showCSSMessages.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CSSFILTER_ID));
		showCSSMessages.setChecked(true);

		showJSMessages = new Action("Show Javascript messages", Action.AS_CHECK_BOX) {
			public void run() {
				catFilter.displayJavascript(isChecked());
				if (tableViewer != null && !tableViewer.getControl().isDisposed())
					tableViewer.refresh();
			}
		};
		showJSMessages.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.JSFILTER_ID));
		showJSMessages.setChecked(true);

		showXMLMessages = new Action("Show XML messages", Action.AS_CHECK_BOX) {
			public void run() {
				catFilter.displayXML(isChecked());
				if (tableViewer != null && !tableViewer.getControl().isDisposed())
					tableViewer.refresh();
			}
		};
		showXMLMessages.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.HTMLFILTER_ID));
		showXMLMessages.setChecked(true);

		/*
		 * ERROR, WARNING, and INFO actions behave sort of like Radio buttons but
		 * all can be unchecked. This is why each action manages the state of the
		 * others when checked to make sure that they are the only ones.
		 */
		showErrorsAction = new Action("Errors", Action.AS_CHECK_BOX) {
			public void run() {
				if (isChecked()) {
					setShowMode(IJavaScriptConsole.SHOW_ERRORS);

					//uncheck the other filter actions
					if (showWarningsAction.isChecked())
						showWarningsAction.setChecked(false);
					else if (showMessagesAction.isChecked())
						showMessagesAction.setChecked(false);

				} else {
					setShowMode(IJavaScriptConsole.SHOW_ALL);
				}
			}
		};

		showErrorsAction.setText("Errors");
		showErrorsAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.ERROR_IMG_ID));
		showErrorsAction.setToolTipText("Show only errors.");
		showErrorsAction.setChecked(false);

		showWarningsAction = new Action("Warnings", Action.AS_CHECK_BOX) {
			public void run() {
				if (isChecked()) {
					setShowMode(IJavaScriptConsole.SHOW_WARNINGS);

					//uncheck the other filter actions
					if (showErrorsAction.isChecked())
						showErrorsAction.setChecked(false);
					else if (showMessagesAction.isChecked())
						showMessagesAction.setChecked(false);
				} else {
					setShowMode(IJavaScriptConsole.SHOW_ALL);
				}
			}
		};

		showWarningsAction.setText("Warnings");
		showWarningsAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.WARNING_IMG_ID));
		showWarningsAction.setToolTipText("Show only warnings.");
		showWarningsAction.setChecked(false);

		showMessagesAction = new Action("Info", Action.AS_CHECK_BOX) {
			public void run() {
				if (isChecked()) {
					setShowMode(IJavaScriptConsole.SHOW_MESSAGES);

					//uncheck the other filter actions
					if (showErrorsAction.isChecked())
						showErrorsAction.setChecked(false);
					else if (showWarningsAction.isChecked())
						showWarningsAction.setChecked(false);

				} else {
					setShowMode(IJavaScriptConsole.SHOW_ALL);
				}
			}
		};

		showMessagesAction.setText("Info");
		showMessagesAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.INFO_IMG_ID));
		showMessagesAction.setToolTipText("Show only info messages.");
		showMessagesAction.setChecked(false);

		clearConsoleAction = new Action() {
			public void run() {
				clearMessages();
			}
		};

		clearConsoleAction.setText("Clear");
		clearConsoleAction.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CLEAR_IMG_ID));
		clearConsoleAction.setToolTipText("Clear all messages.");
	}

	/*
	 * IJavaScriptConsole Interface
	 */
	public void logConsoleMessage(String message) {
		//there seems to be a limit to the number of messages kept in mem
		Object messageToRemove = null;
		if (messages.size() == MAX_MESSAGE_COUNT) {
			messageToRemove = messages.remove(0);
		}

		messages.add(message);

		if (tableViewer != null && !tableViewer.getControl().isDisposed()) {
			if (messageToRemove != null)
				tableViewer.remove(messageToRemove);

			tableViewer.add(message);
			tableViewer.reveal(message);
		}
	}

	public void logConsoleMessage(nsIConsoleMessage message) {
		//there seems to be a limit to the number of messages kept in mem
		Object messageToRemove = null;
		if (messages.size() == MAX_MESSAGE_COUNT) {
			messageToRemove = messages.remove(0);
		}

		messages.add(message);

		if (tableViewer != null && !tableViewer.getControl().isDisposed()) {
			if (messageToRemove != null)
				tableViewer.remove(messageToRemove);

			tableViewer.add(message);
			tableViewer.reveal(message);
		}
	}

	public void clearMessages() {
		messages.clear();
		if (tableViewer != null && !tableViewer.getControl().isDisposed())
			tableViewer.refresh();
	}

	public void setShowMode(int mode) {
		filter.setShowMode(mode);
		if (tableViewer != null && !tableViewer.getControl().isDisposed())
			tableViewer.refresh();
	}

	/*
	 * This method is used to reveal the error line using one of the editors available in Eclipse.
	 * If the file exists in a Project it will be available for edit, else it will be loaded as
	 * read-only.
	 */
	private void openInEditor(URL sourceURL, int lineNumber) {

		//first try to find it locally
		Object source = locatorUtil.getSourceElement(sourceURL);

		if (source != null) {
			try {
				sourceDisplayUtil.openInEditor(sourceDisplayUtil.getEditorInput(source), lineNumber);
			} catch (PartInitException e) {
				MozIDEUIPlugin.log(e);
			}
		}
	}
}
