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
package org.eclipse.atf.mozilla.ide.ui.extras.actions;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction;
import org.eclipse.atf.mozilla.ide.ui.compare.DOMCompareView;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class AddCompareNode extends DOMSelectionAction {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction#run()
	 */
	public void run() {
		try {
			if( getSelection() != null && !getSelection().isEmpty() ) {
				IWorkbenchWindow window = MozIDEUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
				DOMCompareView view = (DOMCompareView)window.getActivePage().showView(DOMCompareView.ID);
				view.addNodeToCompare(getSelection().getSelectedNode());
			}
		} catch (Exception e) {}
	}

}
