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
package org.eclipse.atf.mozilla.ide.ui.domwatcher;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.PageBookView;

/**
 * Adds the currently selected DOM node to the
 * DOM Watcher view and starts watching.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class WatchNodeAction extends DOMSelectionAction {
	
	public void run() {
		try {
			IWorkbenchWindow window = MozIDEUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
			PageBookView view = (PageBookView)window.getActivePage().showView(DOMWatcherView.ID);
			DOMWatcherPage page = (DOMWatcherPage)view.getCurrentPage();
			//page.addNodeToWatch(getSelection().getSelectedNode());
		} catch (Exception e) {}
	}

}
