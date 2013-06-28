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

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIConsoleService;
import org.mozilla.xpcom.Mozilla;

/*
 * This view will show a formatted list of the JavaScript console entries
 * of the currently activated MozBrowserEditor. 
 * 
 * NOTE: Since the current support in Mozilla is for a Singleton Console Service,
 * this View will currently have on Page shared by all Mozilla Browser instances.
 * I will keep it as a PageBookView in order to allow for an easier transition between
 * a Singleton Console and one per browser instance.
 */
public class JavaScriptConsoleView extends PageBookView {

	public static final String ID = "org.eclipse.atf.mozilla.ide.ui.views.console";

	public static boolean SINGLETON_MODE = false; //only one page shared by all browser instances
	protected JavaScriptConsolePage consolePage = null;

	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage("No Active Browser");
		return page;
	}

	protected PageRec doCreatePage(IWorkbenchPart part) {

		Object adapterObj = part.getAdapter(IJavaScriptConsoleViewAdapter.class);

		if (adapterObj != null && adapterObj instanceof IJavaScriptConsoleViewAdapter) {

			/*
			 * Leave this switch here to enable the quick change to a Console pager per
			 * Document
			 */
			if (SINGLETON_MODE) {

				if (consolePage != null) //already initialized
					return new PageRec(part, consolePage);

				//create page and register as listener (once)
				consolePage = new JavaScriptConsolePage();

				try {
					//registering the listener
					nsIConsoleService consoleService = (nsIConsoleService) Mozilla.getInstance().getServiceManager().getServiceByContractID("@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID);
					consoleService.registerListener(new JavaScriptConsoleListener(consolePage));

					//populate cons0le with initial items
					nsIConsoleMessage[][] messageArray = new nsIConsoleMessage[1][];
					consoleService.getMessageArray(messageArray, null);

					for (int i = 0; i < messageArray[0].length; i++)
						consolePage.logConsoleMessage(messageArray[0][i]);

				} catch (Exception e) {
					MozIDEUIPlugin.log(e);
				}

				initPage(consolePage);
				consolePage.createControl(getPageBook());
				return new PageRec(part, consolePage); //registering the Singleton console with this part

			} else {
				IJavaScriptConsoleViewAdapter adapter = (IJavaScriptConsoleViewAdapter) adapterObj;
				JavaScriptConsolePage page = (JavaScriptConsolePage) adapter.getJavaScriptConsole();
				initPage(page);
				page.createControl(getPageBook());
				return new PageRec(part, page);
			}

		}

		return null; //nothing to create

	}

	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		JavaScriptConsolePage page = (JavaScriptConsolePage) pageRecord.page;
		page.dispose();
		pageRecord.dispose();

		consolePage = null;
	}

	/*
	 * Try to return the active editor
	 */
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null)
			return page.getActiveEditor();

		return null;
	}

	/*
	 * Only IEditorPart parts
	 */
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof IEditorPart;
	}

	public void dispose() {
		if (consolePage != null) {
			consolePage.dispose();
			consolePage = null;
		}
		super.dispose();
	}
}
