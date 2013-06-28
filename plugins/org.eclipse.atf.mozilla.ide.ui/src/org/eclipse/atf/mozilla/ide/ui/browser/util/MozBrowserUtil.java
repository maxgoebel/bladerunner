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

package org.eclipse.atf.mozilla.ide.ui.browser.util;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditor;
import org.eclipse.atf.mozilla.ide.ui.browser.MozBrowserEditorInput;
import org.eclipse.atf.mozilla.ide.ui.perspective.MozillaPerspective;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.ide.IDE;

/*
 * Collection of helper methods
 */
public class MozBrowserUtil {
	public static final String SWITCH_TO_WEB_DEVELOPMENT_PERSPECTIVE = "switch_to_web_development_perspective";

	/*
	 * Open a Browser Editor to the specified URL and in the page provided
	 */
	public static IWebBrowser openMozillaBrowser(String url, IWorkbenchPage page) throws CoreException {

		MozBrowserEditorInput editorInput = new MozBrowserEditorInput(url);

		IEditorPart editor = IDE.openEditor(page, editorInput, MozBrowserEditor.ID);

		if (editor instanceof MozBrowserEditor) {
			return ((MozBrowserEditor) editor);
		} else {
			throw new CoreException(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.ERROR, "Error opening Mozilla Browser... EditorPart did not initialize properly!", null));
		}
	}

	/*
	 * Open a Browser Editor to the specified URL and in the currently active page
	 */
	public static IWebBrowser openMozillaBrowser(String url) throws CoreException {

		IWorkbenchWindow activeWindow = MozIDEUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (activeWindow == null) {
			throw new CoreException(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.ERROR, "Error opening Mozilla Browser... failed to retrieve active workbench window!", null));
		}
		IWorkbenchPage activePage = activeWindow.getActivePage();
		if (activePage == null) {
			throw new CoreException(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, IStatus.ERROR, "Error opening Mozilla Browser... could not retrieve active page!", null));
		}

		return openMozillaBrowser(url, activePage);

	}

	public static void openMozillaPerspective() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();

		if (window != null) {
			String currentPerspective = null;
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				IPerspectiveDescriptor descr = page.getPerspective();
				if (descr != null) {
					currentPerspective = descr.getId();
				}
			}

			if (MozillaPerspective.ID.equals(currentPerspective))
				return; // browser perspective is already set. No need to change anything

			IPreferenceStore store = MozIDEUIPlugin.getDefault().getPreferenceStore();
			if (!store.contains(SWITCH_TO_WEB_DEVELOPMENT_PERSPECTIVE)) {
				MessageDialogWithToggle toggle = MessageDialogWithToggle
						.openYesNoQuestion(window.getShell(),"Open Associated Perspective?",
								"This kind of launch is configured to open the Web Browser Tools perspective.\n\nThis Web Browser Tools perspective provides additional views for inspecting browser internals such as Document Object Model, network events, JavaScript, Cascading Style Sheets definitions and more.\n\nDo you want to open this perspective now?",
								null, false, store,	SWITCH_TO_WEB_DEVELOPMENT_PERSPECTIVE);
				if (toggle.getReturnCode() != IDialogConstants.YES_ID)
					return;
			} else if (MessageDialogWithToggle.NEVER.equals(store.getString(SWITCH_TO_WEB_DEVELOPMENT_PERSPECTIVE)))
				return;

			try {
				PlatformUI.getWorkbench().showPerspective(MozillaPerspective.ID, window);
			} catch (WorkbenchException e) {
				MozIDEUIPlugin.log(e);
			}
		}
	}

}
