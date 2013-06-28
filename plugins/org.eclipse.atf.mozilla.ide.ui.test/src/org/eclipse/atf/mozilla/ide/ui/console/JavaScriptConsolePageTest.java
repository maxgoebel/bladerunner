/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.ui.console;

import junit.framework.TestCase;

import org.eclipse.atf.mozilla.ide.ui.test.TestUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.PageBookView;

public class JavaScriptConsolePageTest extends TestCase {

	private PageBookView pageBookView;

	public void setUp() throws PartInitException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = window.getActivePage();

		pageBookView = (PageBookView) workbenchPage.showView(JavaScriptConsoleView.ID);
	}

	public void testLongLoadingContent() throws CoreException, InterruptedException {
		long start = System.currentTimeMillis();
		TestUtils.openInMozillaBrowserEditor("<html><script>var start = new Date().getTime(); while (new Date().getTime() < start + 5000);</script></html>");
		long end = System.currentTimeMillis();

		// since javascript took 5secs to load, openInMozillaBrowserEditor must have taken at least 5secs too.
		assertTrue(end - start > 5000);
	}

	public void testBrowserConsoleIsEmpty() throws CoreException {
		JavaScriptConsolePage page = (JavaScriptConsolePage) pageBookView.getCurrentPage();
		Table viewer = (Table) page.getControl();
		int before = viewer.getItemCount();

		TestUtils.openInMozillaBrowserEditor("<html></html>");

		assertEquals(before, viewer.getItemCount());
	}

	public void testBrowserConsoleNotEmpty() throws CoreException, InterruptedException {
		JavaScriptConsolePage page = (JavaScriptConsolePage) pageBookView.getCurrentPage();
		Table viewer = (Table) page.getControl();
		int before = viewer.getItemCount();

		TestUtils.openInMozillaBrowserEditor("<html><script>crap;</script></html>");

		assertEquals(1, viewer.getItemCount() - before);
	}

	public void testBrowserConsoleNotEmpty2() throws CoreException, InterruptedException {
		JavaScriptConsolePage page = (JavaScriptConsolePage) pageBookView.getCurrentPage();
		Table viewer = (Table) page.getControl();
		int before = viewer.getItemCount();

		TestUtils.openInMozillaBrowserEditor("<html><script>crap;crap2;</script></html>");

		// only first error is printed
		assertEquals(1, viewer.getItemCount() - before);
	}
}
