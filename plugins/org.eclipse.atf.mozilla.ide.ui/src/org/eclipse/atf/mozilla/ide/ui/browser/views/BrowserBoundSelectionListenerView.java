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
package org.eclipse.atf.mozilla.ide.ui.browser.views;

import org.eclipse.atf.mozilla.ide.common.IDOMNodeSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * Browser bound view that listens to changes in selections and sends them to
 * selection listening pages. Responsible for registering the page as a
 * selection listener and changes the selection of the page when created.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public abstract class BrowserBoundSelectionListenerView extends BrowserBoundView implements ISelectionListener {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.browser.views.BrowserBoundView#setupPage(org.eclipse.ui.part.IPageBookViewPage, org.eclipse.ui.IWorkbenchPart)
	 */
	public void setupPage(IPageBookViewPage page, IWorkbenchPart part) {
		//check if the active part has an IDOMNodeSelection
        IDOMNodeSelection selection = (IDOMNodeSelection)part.getAdapter( IDOMNodeSelection.class );
        if( page instanceof ISelectionListener && selection != null ) {
        	((ISelectionListener)page).selectionChanged( part, selection );        	
        }
	}

	/**
	 * Removes this view as a selection listener from the WorkbenchPage.
	 */
	public void dispose() {
		getSite().getPage().removeSelectionListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#init(org.eclipse.ui.IViewSite)
	 */
	public void init(IViewSite site) throws PartInitException {
        site.getPage().addSelectionListener(this); //setting as selection listener
        super.init(site);
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (part == this || selection == null)
            return;

        IPage page = getCurrentPage();
        if( page != null && getAdapterClass().isInstance(page) && page instanceof ISelectionListener ) {
        	((ISelectionListener)page).selectionChanged(part, selection);
        }
	}

}
