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
package org.eclipse.atf.mozilla.ide.ui.browser.toolbar;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.atf.mozilla.ide.common.IWebBrowser;

/**
 * Interface for browser toolbars. The browser configured with the toolbar is
 * responsible for notifying the toolbar when the hover or node selection has
 * changed.
 * 
 * The toolbar can set the current node selection for the browser and subsequent
 * views by using the setSelection method provided by the IDOMDocumentContainer
 * interface.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public interface IBrowserToolbar {
	
	/**
	 * Sets the selection of the toolbar to the selection
	 * passed in.  Called by the browser when the selected
	 * node changes.
	 * 
	 * @param selection - Node selection
	 */
	public void setNodeSelection(IDOMNodeSelection selection);
	
	/**
	 * Sets the hover selection of the toolbar to the selection
	 * passed in.  Called by the browserr when the user hover
	 * over a DOM node.
	 * @param selection - Hover selection
	 */
	public void setHoverSelection(IDOMNodeSelection selection);
	
	/**
	 * Sets the browser for this toolbar.  The toolbar may
	 * interact with the browser via methods provided
	 * by the IDOMDocumenContainer interface.
	 * 
	 * @param browser - Browser containing this toolbar
	 */
	public void setBrowser(IWebBrowser browser);
	
}
