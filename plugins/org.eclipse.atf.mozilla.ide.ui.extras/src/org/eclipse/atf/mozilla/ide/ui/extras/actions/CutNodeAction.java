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
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.mozilla.xpcom.Mozilla;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMSerializer;

/**
 * Cuts a node from the current DOM and puts it on the
 * clipboard.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 *
 */
public class CutNodeAction extends DOMSelectionAction {

	private Clipboard clipboard;
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.actions.DOMSelectionAction#run()
	 */
	public void run() {
		if( getSelection() != null && !getSelection().isEmpty() ) {
			try {
				nsIDOMNode node = getSelection().getSelectedNode();
				nsIDOMSerializer domSerializer = 
					(nsIDOMSerializer)Mozilla.getInstance().getComponentManager().
						createInstanceByContractID("@mozilla.org/xmlextras/xmlserializer;1", 
													null, nsIDOMSerializer.NS_IDOMSERIALIZER_IID);
				clipboard = new Clipboard(Display.getCurrent());
				clipboard.setContents(new Object[]{domSerializer.serializeToString(node)}, new Transfer[]{TextTransfer.getInstance()});
				clipboard.dispose();
				node.getParentNode().removeChild(node);
			} catch(Exception e) {
				
			} finally {
				if( clipboard != null ) {
					clipboard.dispose();
				}
			}
		}
	}

}
