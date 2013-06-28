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
package org.eclipse.atf.mozilla.ide.ui.browser.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

/**
 * Page book view that creates pages when an editor changes. Provides hooks
 * during creation and relies on subclasses to provide an adapter class to
 * create the page.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * 
 */
public abstract class BrowserBoundView extends PageBookView {

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#createDefaultPage(org.eclipse.ui.part.PageBook)
	 */
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("No Active Browser");
        return page;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	protected PageRec doCreatePage(IWorkbenchPart part) {
		Object adapterObj = part.getAdapter( getAdapterClass() );
		
		if( adapterObj != null && getAdapterClass().isInstance(adapterObj) ){
	
			if( adapterObj instanceof IPageBookViewPage ) {
				IPageBookViewPage page = (IPageBookViewPage)adapterObj;
				initPage(page);
		        page.createControl(getPageBook());
		        setupPage(page, part);
		        return new PageRec(part, page);
			}
		}
		
		return null; //nothing to create
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#doDestroyPage(org.eclipse.ui.IWorkbenchPart, org.eclipse.ui.part.PageBookView.PageRec)
	 */
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
        pageRecord.page.dispose();
        pageRecord.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#getBootstrapPart()
	 */
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
        if (page != null)
            return page.getActiveEditor();

        return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.PageBookView#isImportant(org.eclipse.ui.IWorkbenchPart)
	 */
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof IEditorPart;
	}
	
	/**
	 * Hook for subclasses to add functionality when the doCreatePage method
	 * is called for this view.
	 * 
	 * @param page - page provided by adapter
	 * @param part - part provided by editor
	 */
	public abstract void setupPage(IPageBookViewPage page, IWorkbenchPart part);
	
	/**
	 * Hook for subclasses to provided adapter class from which
	 * the editor will return the page to be added to this view.
	 * 
	 * @return - returns the adapter class to use with the editor
	 */
	public abstract Class getAdapterClass();

}
