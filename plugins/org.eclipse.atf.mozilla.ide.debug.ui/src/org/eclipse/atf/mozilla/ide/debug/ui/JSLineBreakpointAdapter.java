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
package org.eclipse.atf.mozilla.ide.debug.ui;

import org.eclipse.atf.mozilla.ide.debug.MozillaDebugPlugin;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTargetExtension;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Supports JS breakpoint creation/deletion in ITextEditor
 */
public class JSLineBreakpointAdapter implements IToggleBreakpointsTargetExtension {

	public boolean canToggleLineBreakpoints(IWorkbenchPart part, ISelection selection) {
		return true;
	}

	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {

		if (!(part instanceof ITextEditor))
			return;

		ITextEditor textEditor = (ITextEditor) part;
		IEditorInput input = textEditor.getEditorInput();

		ITextSelection textSelection = (ITextSelection) selection;
		int lineNumber = textSelection.getStartLine();

		IStorage storage = null;
		if (input instanceof IStorageEditorInput) {
			storage = ((IStorageEditorInput) input).getStorage();
		}

		IBreakpoint breakpoint = findBreakpoint(storage, lineNumber + 1);
		if (breakpoint != null) {
			breakpoint.delete();
			return;
		}

		JSLineBreakpoint lineBreakpoint = new JSLineBreakpoint(storage, lineNumber + 1, -1, -1, true, null);
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(lineBreakpoint);
	}

	private IBreakpoint findBreakpoint(IStorage storage, int lineNumber) throws CoreException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(MozillaDebugPlugin.DEBUG_MODEL_ID);

		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			IMarker marker = breakpoint.getMarker();
			if (storage instanceof IFile) {
				if (!storage.equals(marker.getResource())) {
					continue;
				}
			} else {
				String id = storage.getName();
				String markerId = null;
				try {
					markerId = (String) marker.getAttribute(JSLineBreakpoint.STORAGE_ID);
				} catch (CoreException e) {
					MozillaDebugUIPlugin.log(e);
				}

				if (!id.equals(markerId))
					continue;
			}

			if (((ILineBreakpoint) breakpoint).getLineNumber() != (lineNumber)) {
				continue;
			}

			return breakpoint;
		}

		return null;
	}

	public boolean canToggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	public boolean canToggleWatchpoints(IWorkbenchPart part, ISelection selection) {
		return false;
	}

	public void toggleMethodBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		// empty
	}

	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		// empty
	}

	public boolean canToggleBreakpoints(IWorkbenchPart part, ISelection selection) {
		return canToggleLineBreakpoints(part, selection);
	}

	public void toggleBreakpoints(IWorkbenchPart part, ISelection selection) throws CoreException {
		toggleLineBreakpoints(part, selection);
	}

}
