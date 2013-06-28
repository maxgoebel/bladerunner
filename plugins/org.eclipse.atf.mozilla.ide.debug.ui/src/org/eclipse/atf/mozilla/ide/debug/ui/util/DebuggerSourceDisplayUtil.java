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
package org.eclipse.atf.mozilla.ide.debug.ui.util;

import org.eclipse.atf.mozilla.ide.core.util.SourceLocatorUtil;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.util.SourceDisplayUtil;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.ui.IEditorInput;

public class DebuggerSourceDisplayUtil extends SourceDisplayUtil {

	public IEditorInput getEditorInput(Object element) {

		IEditorInput editorInput = null;

		if (element instanceof JSLineBreakpoint) {
			element = ((IBreakpoint) element).getMarker();
		}

		IMarker marker = null;
		if (element instanceof IMarker) {
			marker = (IMarker) element;
			element = ((IMarker) element).getResource();
		}

		//superclass defines the simple support for IFile and IStorage
		editorInput = super.getEditorInput(element);

		if (editorInput == null && element instanceof IWorkspaceRoot) {
			try {
				String id = (String) marker.getAttribute(JSLineBreakpoint.STORAGE_ID);

				if (id != null) {
					IStorage storage = SourceLocatorUtil.getInstance().getURLStorage(id);
					editorInput = new StorageEditorInput(storage);
				}
			} catch (CoreException ce) {
				MozillaDebugUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, MozillaDebugUIPlugin.PLUGIN_ID, "Could not find markers for IWorkbenchRoot", ce));
				// failed, so return null
			}
		}

		return editorInput;

	}
}
