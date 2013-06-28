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
package org.eclipse.atf.mozilla.ide.ui.browser;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/*
 * This is an implementation fo the Browser chrome as a Dialog. This is used
 * for popups.
 * 
 * The current support is limited to just a shell with no controls other than the
 * browser.
 */
public class MozBrowserDialog extends Dialog {

	protected Browser browser = null;
	
	protected MozBrowserDialog(Shell parentShell) {
		super(parentShell);
		
		setShellStyle( SWT.DIALOG_TRIM | SWT.RESIZE | getDefaultOrientation());
		setBlockOnOpen(false);
	}

	protected Control createDialogArea(Composite parent) {
		browser = new Browser( parent, SWT.MOZILLA );
		
		browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		initialize( browser );
		
		return browser;
	}
	
	private void initialize(Browser browser) {
		browser.addOpenWindowListener( new PopupWindowBrowserListener( new IShellProvider(){

			public Shell getShell() {
				return MozBrowserDialog.this.getShell();
			}
			
		}) );
		
		browser.addVisibilityWindowListener(new VisibilityWindowListener() {
			public void hide(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				Shell shell = browser.getShell();
				shell.setVisible(false);
			}
			public void show(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				Shell shell = browser.getShell();
				if (event.location != null)
					shell.setLocation(event.location);
				if (event.size != null) {
					Point size = event.size;
					shell.setSize(shell.computeSize(size.x, size.y));
				}
				shell.open();
			}
		});
		
		browser.addCloseWindowListener(new CloseWindowListener() {
			public void close(WindowEvent event) {
				Browser browser = (Browser) event.widget;
				Shell shell = browser.getShell();
				shell.close();
			}
		});
		
		browser.addTitleListener(new TitleListener() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.browser.TitleListener#changed(org.eclipse.swt.browser.TitleEvent)
			 */
			public void changed(TitleEvent event) {
				if (event.title != null && event.title.length() > 0) {
					Browser browser = (Browser) event.widget;
					Shell shell = browser.getShell();
					shell.setText(event.title);
				}
			}
		});
		
	}
	
	protected Control createButtonBar(Composite parent) { return null; }

	public Browser getMozillaBrowser(){
		return browser;
	}
}
