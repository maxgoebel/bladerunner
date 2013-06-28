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
package org.eclipse.atf.mozilla.ide.ui.browser.toolbar;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Browser toolbar superclass that is meant to be extended to add a functional
 * toolbar to the browser. A toolbar is configured with an action that toggles
 * whether it should be displayed. Also the toolbar has a generic close action
 * which sets the toolbar to be not visible but does not dispose of toolbar. The
 * toolbar is disposed when the browser is closed.
 * 
 * This superclass provides a factory method (fillToolbar that allows subclasses
 * to fill the available space of the toolbar.
 * 
 * Subclasses must implement the methods of IBrowserToolbar.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public abstract class BrowserToolbar implements IBrowserToolbar {
	
	private ToolBarManager toolbar;
	private Action closeToolbar;
	private Label seperator;
	private Composite fill;
	private Action toggleShow;
	private Composite composite;
	
	/**
	 * Generic constructor that does nothing.
	 *
	 */
	public BrowserToolbar() {
		//Does nothing
	}
	
	/**
	 * Creates the toolbar UI with and configures it with a close button, and
	 * call the abstract method fillToolbar.
	 * 
	 * @param parent - parent container of this toolbar
	 */
	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 1;
		gridLayout.verticalSpacing = 0;
		composite.setLayout(gridLayout);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.BEGINNING;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = false;
		composite.setLayoutData(data);
		
		seperator = new Label( composite, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.LINE_SOLID);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		seperator.setLayoutData(data);
		
		//this is the client area of the BrowserBar
		fill = new Composite( composite, SWT.NONE );
		gridLayout = new GridLayout();
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		data.horizontalSpan = 1;
		fill.setLayoutData(data);
		fill.setLayout(gridLayout);
		
		toolbar = new ToolBarManager();
		closeToolbar = new Action(){
		
			public void run() {
				show(false);
			}
		
		};
		closeToolbar.setImageDescriptor(MozIDEUIPlugin.getDefault().getImageDescriptorFromRegistry(MozIDEUIPlugin.CLOSE_TB_ID));
		closeToolbar.setToolTipText("Close");
		toolbar.add(closeToolbar);
		toolbar.createControl(fill);
		fillToolbar(fill);
	}
	
	/**
	 * Sets the action that this toolbar will use to synchronize its opening and
	 * closing with the specified acton's checked state.
	 * 
	 * @param action - action to toggle checked statte
	 */
	public void setAction(Action action) {
		this.toggleShow = action;
	}
	
	/**
	 * Sets the visibility of this toolbar.
	 * @param show - true to show the toolbar, false to hide the toolbar.
	 */
	public void show(boolean show) {
		((GridData)composite.getLayoutData()).exclude = !show;
		composite.setVisible(show);
		composite.getParent().layout(true);
		toggleShow.setChecked(show);
	}
	
	/**
	 * Hook for subclasses to fill area of the toolbar not including the close
	 * button and the seperator.
	 * 
	 * @param fillArea - space to fill
	 */
	public abstract void fillToolbar(Composite fillArea);
	
}
