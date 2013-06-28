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

package org.eclipse.atf.mozilla.ide.debug.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.action.SubStatusLineManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class MozillaErrorStatusHandler implements IStatusHandler {

	public Object handleStatus(final IStatus status, Object source)
			throws CoreException {

		DebugUIPlugin.errorDialog(DebugUIPlugin.getShell(), "JavaScript Error encountered", status.getMessage(), status);
		DebugUIPlugin.getStandardDisplay().syncExec(new Runnable() {

			public void run() {
				//TODO: Really should tie status line to the Debug view and not some random editor
				//TODO: also need to clear it?
				IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
				IWorkbenchWindow window = windows[0];
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						IEditorPart editor = page.getActiveEditor();
						IEditorSite site=  editor.getEditorSite();
						SubStatusLineManager statusLineManager = (SubStatusLineManager)site.getActionBars().getStatusLineManager();
						statusLineManager.setVisible(true);
						//TODO: add images?
						switch (status.getSeverity()) {
						case IStatus.ERROR:
							statusLineManager.setErrorMessage(status.getMessage());
							break;
						default:
							statusLineManager.setMessage(status.getMessage());
						}
					}
				}
			}
		});
		return null;
	}

}
