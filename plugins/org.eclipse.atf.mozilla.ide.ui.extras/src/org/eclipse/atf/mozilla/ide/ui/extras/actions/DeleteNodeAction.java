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

import org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction;
import org.mozilla.interfaces.nsIDOMNode;

/**
 * Deletes the selected node.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class DeleteNodeAction extends DOMSelectionAction {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction#run()
	 */
	public void run() {
		if( getSelection() != null && !getSelection().isEmpty() ) {
			try {
				nsIDOMNode node = getSelection().getSelectedNode();
				node.getParentNode().removeChild(node);
			} catch(Exception e) {}
		}
	}

}
