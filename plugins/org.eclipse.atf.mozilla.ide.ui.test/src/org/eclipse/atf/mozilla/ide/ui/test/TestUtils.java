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
package org.eclipse.atf.mozilla.ide.ui.test;

import java.io.ByteArrayInputStream;

import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditor;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public final class TestUtils {

	static class PageLoadDoneProgressProvider extends ProgressAdapter {
		public boolean done = false;

		public void completed(ProgressEvent event) {
			done = true;
		}
	}

	public static MozBrowserEditor openInMozillaBrowserEditor(String content) throws CoreException {
		IFile file = createFile(content);
		IFileEditorInput input = createFileEditorInput(file);
		return openMozillaBrowser(input);
	}

	private static MozBrowserEditor openMozillaBrowser(IFileEditorInput input) throws PartInitException {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage workbenchPage = window.getActivePage();

		MozBrowserEditor editor = (MozBrowserEditor) workbenchPage.openEditor(input, MozBrowserEditor.ID);
		Browser browser = editor.getMozillaBrowser();
		PageLoadDoneProgressProvider tpa = new PageLoadDoneProgressProvider();
		browser.addProgressListener(tpa);

		// process all queued events, e.g. new messages coming to browser console
		while (!tpa.done) {
			Display.getCurrent().readAndDispatch();
		}

		return editor;
	}

	public static IFileEditorInput createFileEditorInput(IFile file) {
		return new FileEditorInput(file);
	}

	public static IFile createFile(String content) throws CoreException {
		String name = "mozillatestproject/" + System.currentTimeMillis();
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(name));
		IProject project = file.getProject();
		if (!project.exists()) {
			project.create(null);
			project.open(null);
		}
		file.create(new ByteArrayInputStream(content.getBytes()), true, null);
		return file;
	}

}
