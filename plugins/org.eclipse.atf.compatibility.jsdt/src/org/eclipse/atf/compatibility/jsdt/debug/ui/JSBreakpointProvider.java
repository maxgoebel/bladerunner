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
package org.eclipse.atf.compatibility.jsdt.debug.ui;

import org.eclipse.atf.compatibility.jsdt.Activator;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.ISourceEditingTextTools;
import org.eclipse.wst.sse.ui.internal.provisional.extensions.breakpoint.IBreakpointProvider;

public class JSBreakpointProvider implements IBreakpointProvider {

	public JSBreakpointProvider() {
		super();
		// TODO Auto-generated constructor stub
	}

	public IStatus addBreakpoint(IDocument document, IEditorInput input, int lineNumber, int offset) throws CoreException {
		//		Activator.debug("JSBreakpointProvider.addBreakpoint");
		//		int pos = getValidPosition(document, lineNumber);
		//		if (pos != NO_VALID_CONTENT && canAddBreakpoint(document, input, lineNumber, offset)) {
		IStorage res = getEditorInputStorage(input);
		new JSLineBreakpoint(res, lineNumber, -1, -1, true, null);
		return new Status(IStatus.OK, Activator.PLUGIN_ID, IStatus.OK, "", null);
	}

	public IResource getResource(IEditorInput input) {
		IStorage storage = getEditorInputStorage(input);
		if (storage instanceof IFile)
			return (IFile) storage;
		else
			return JSLineBreakpoint.getStorageMarkerResource();
	}

	protected IStorage getEditorInputStorage(IEditorInput input) {
		if (input instanceof IStorageEditorInput) {
			try {
				return ((IStorageEditorInput) input).getStorage();
			} catch (CoreException e) {
				Activator.log(e);
			}
		}

		return null;
	}

	private ISourceEditingTextTools fSourceEditingTextTools;

	public ISourceEditingTextTools getSourceEditingTextTools() {
		return fSourceEditingTextTools;
	}

	public void setSourceEditingTextTools(ISourceEditingTextTools sourceEditingTextTools) {
		fSourceEditingTextTools = sourceEditingTextTools;
	}

	//	private boolean canAddBreakpoint(IDocument document, IEditorInput input, int lineNumber, int offset) {
	//		IResource res = getEditorInputResource(input);
	//		Document doc = null;
	//		return res != null && !isBreakpointExist(res, lineNumber) && isValidPosition(document, lineNumber) && (getPageLanguage(doc) != JAVA);
	//	}
	//
	//	/*
	//	 * @param res @param lineNumber @return boolean
	//	 */
	//	private boolean isBreakpointExist(IResource res, int lineNumber) {
	//		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
	//		IBreakpoint[] breakpoints = manager.getBreakpoints();
	//		for (int i = 0; i < breakpoints.length; i++) {
	//			if (!(breakpoints[i] instanceof JavascriptLineBreakpoint))
	//				continue;
	//			JavascriptLineBreakpoint breakpoint = (JavascriptLineBreakpoint) breakpoints[i];
	//			try {
	//				if (breakpoint.getResource().equals(res) && breakpoint.getLineNumber() == lineNumber) {
	//					return true;
	//				}
	//			}
	//			catch (CoreException e) {
	//				return true;
	//			}
	//		}
	//		return false;
	//	}
	//
	//	/*
	//	 * @param doc @param idoc @param lineNumber @return boolean
	//	 */
	//	private boolean isValidPosition(IDocument idoc, int lineNumber) {
	//		return getValidPosition(idoc, lineNumber) != NO_VALID_CONTENT;
	//	}
	//
	//	protected int getValidPosition(IDocument idoc, int lineNumber) {
	//		if (!(getSourceEditingTextTools() instanceof IDOMSourceEditingTextTools)) {
	//			return NO_VALID_CONTENT;
	//		}
	//		if (idoc == null)
	//			return NO_VALID_CONTENT;
	//
	//		int startOffset, endOffset;
	//		try {
	//			startOffset = idoc.getLineOffset(lineNumber - 1);
	//			endOffset = idoc.getLineOffset(lineNumber) - 1;
	//
	//			if (idoc == null)
	//				return NO_VALID_CONTENT;
	//			String lineText = idoc.get(startOffset, endOffset - startOffset).trim();
	//
	//			// blank lines or lines with only an open or close brace or
	//			// scriptlet tag cannot have a breakpoint
	//			if (lineText.equals("") || lineText.equals("{") || //$NON-NLS-2$//$NON-NLS-1$
	//						lineText.equals("}") || lineText.equals("<%"))//$NON-NLS-2$//$NON-NLS-1$
	//				return NO_VALID_CONTENT;
	//		}
	//		catch (BadLocationException e) {
	//			return NO_VALID_CONTENT;
	//		}
	//
	//		IStructuredDocumentRegion flatNode = ((IStructuredDocument) idoc).getRegionAtCharacterOffset(startOffset);
	//		// go through the node's regions looking for JSP content
	//		// until reaching the end of the line
	//		while (flatNode != null) {
	//			int validPosition = getValidRegionPosition(((IDOMDocument) ((IDOMSourceEditingTextTools) getSourceEditingTextTools()).getDOMDocument()).getModel(), flatNode, startOffset, endOffset);
	//
	//			if (validPosition == END_OF_LINE)
	//				return NO_VALID_CONTENT;
	//
	//			if (validPosition >= 0)
	//				return validPosition;
	//
	//			flatNode = flatNode.getNext();
	//		}
	//		return NO_VALID_CONTENT;
	//	}
}
