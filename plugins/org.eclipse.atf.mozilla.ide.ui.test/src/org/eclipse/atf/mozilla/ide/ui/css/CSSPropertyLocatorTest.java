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
package org.eclipse.atf.mozilla.ide.ui.css;

import junit.framework.TestCase;

import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditor;
import org.eclipse.atf.mozilla.ide.ui.test.TestUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.browser.Browser;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIWebBrowser;

public class CSSPropertyLocatorTest extends TestCase {

	public void testLoad() throws CoreException, InterruptedException {
		final MozBrowserEditor editor = TestUtils.openInMozillaBrowserEditor("<html><script>var start = new Date().getTime(); while (new Date().getTime() < start + 5000);</script></html>");
		Browser browser = editor.getMozillaBrowser();

		nsIWebBrowser webBrowser = (nsIWebBrowser) browser.getWebBrowser();
		final nsIDOMNode node = webBrowser.getContentDOMWindow().getDocument().getElementsByTagName("HTML").item(0);

		Runnable go = new Runnable() {
			public void run() {
				CSSPropertyLocator cpl = new CSSPropertyLocator();
				cpl.setDocumentContainer(editor);
				IJobChangeListener listener = new JobChangeAdapter();
				cpl.setNode(node);
				for (int c = 0; c < 1000; c++) { // 1000 is an arbitrary value, enough to hit synchronization issues due to bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=284678
					cpl.load(listener);
				}
			}
		};

		Thread ta = new Thread(go);
		Thread tb = new Thread(go);

		ta.start();
		tb.start();

		// ReadAndDispatch loop instead of usual join, because test is called from main thread and Threads ta and tb
		// need to access main, which would be blocked when join was used.
		while ((ta.getState() != Thread.State.TERMINATED) && (tb.getState() != Thread.State.TERMINATED))
			browser.getDisplay().readAndDispatch();
	}
}
