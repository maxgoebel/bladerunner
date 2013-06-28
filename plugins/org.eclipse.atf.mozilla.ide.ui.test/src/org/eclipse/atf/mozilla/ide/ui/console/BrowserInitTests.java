package org.eclipse.atf.mozilla.ide.ui.console;

import junit.framework.TestCase;

import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditor;
import org.eclipse.atf.mozilla.ide.ui.test.TestUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class BrowserInitTests extends TestCase {

	/**
	 * test if Mozilla browser can be instantiated
	 */
	public void testSWTBrowser() {
		System.out.println("testSWTBrowser"); // println, because in case of crash tests are not written
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		Shell s = window.getShell();

		Shell newShell = new Shell(s.getDisplay());
		Browser b = new Browser(newShell, SWT.MOZILLA);
		newShell.dispose();
	}

	/**
	 * test if ATF editor can be instantiated
	 * 
	 * @throws CoreException
	 */
	public void testATFEditor() throws CoreException {
		System.out.println("testATFEditor"); // println, because in case of crash tests are not written
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = window.getActivePage();
		IFileEditorInput input = TestUtils.createFileEditorInput(TestUtils.createFile(""));

		IEditorPart editor = workbenchPage.openEditor(input, MozBrowserEditor.ID);

		assertTrue(editor instanceof MozBrowserEditor);

		workbenchPage.closeEditor(editor, false);
	}
}
