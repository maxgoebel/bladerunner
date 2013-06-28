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
package org.eclipse.atf.mozilla.ide.ui.util;

import java.net.URL;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

public class SourceDisplayUtil {

	public SourceDisplayUtil() {
	}

	/*
	 * This only includes the simple types support (IFile and IStorage)
	 */
	public IEditorInput getEditorInput(Object element) {

		IEditorInput editorInput = null;

		if (element instanceof IFile) {
			editorInput = new FileEditorInput((IFile) element);
		} else if (element instanceof IStorage) {
			editorInput = new StorageEditorInput((IStorage) element);
		}

		return editorInput;

	}

	public String getEditorId(IEditorInput input, Object element) {
		try {
			IEditorDescriptor descriptor = IDE.getEditorDescriptor(input.getName());
			if ("org.eclipse.atf.mozilla.ide.ui.MozBrowserEditor".equals(descriptor.getId()) || ("org.eclipse.ui.systemExternalEditor".equals(descriptor.getId()))) {
				return "org.eclipse.ui.DefaultTextEditor";
			}
			return descriptor.getId();
		} catch (PartInitException pie) {
			return null;
		}
	}

	/**
	 * Determined what editor to open and focuses on the lineNumber if > 0
	 * 
	 * @param input IEditorInput instance
	 * @param lineNumber lineNumber to focus or less than 1 to ignore
	 */
	public void openInEditor(IEditorInput input, int lineNumber) throws PartInitException {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		openInEditor(page, input, lineNumber);

	}

	public void openInEditor(IWorkbenchPage page, IEditorInput input, int lineNumber) throws PartInitException {
		IEditorPart editor = IDE.openEditor(page, input, getEditorId(input, null), true);
		//hightlight the line in the sourcefile

		if (lineNumber > 0) { //must have a valid line number

			ITextEditor textEditor = null;
			if (editor instanceof ITextEditor) {
				textEditor = (ITextEditor) editor;
			} else {
				textEditor = (ITextEditor) editor.getAdapter(ITextEditor.class);
			}
			if (textEditor != null) {
				revealLineInEditor(textEditor, lineNumber);
			}

		}
	}

	private void revealLineInEditor(ITextEditor editor, int lineNumber) {

		lineNumber--; // Document line numbers are 0-based. Debug line numbers are 1-based.

		IRegion region = null;

		IDocumentProvider provider = editor.getDocumentProvider();
		IEditorInput input = editor.getEditorInput();
		try {
			provider.connect(input);
		} catch (CoreException e) {
			return;
		}
		try {
			IDocument document = provider.getDocument(input);
			if (document != null)
				region = document.getLineInformation(lineNumber);
		} catch (BadLocationException e) {
		} finally {
			provider.disconnect(input);
		}

		if (region != null) {
			editor.selectAndReveal(region.getOffset(), 0);
		}
	}

	/*
	 * Editor input used to open 
	 */
	protected class StorageEditorInput extends PlatformObject implements IStorageEditorInput {

		private IStorage _storage;

		public StorageEditorInput(IStorage storage) {
			_storage = storage;
		}

		public IStorage getStorage() throws CoreException {
			return _storage;
		}

		public boolean exists() {
			return true;
		}

		public ImageDescriptor getImageDescriptor() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getName() {
			// shows up in editor tab
			return _storage.getName();
		}

		public IPersistableElement getPersistable() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getToolTipText() {
			Object url = _storage.getAdapter(URL.class);
			return url == null ? getName() : ((URL) url).toExternalForm();
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj instanceof StorageEditorInput) {
				try {
					return ((StorageEditorInput) obj).getStorage().equals(_storage);
				} catch (CoreException ce) {
					MozIDEUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, MozIDEUIPlugin.PLUGIN_ID, "Error getting storage", ce));
					// fallthrough
				}
			}

			//	 Phil's patch -- is it needed?
			//	        URL url = (URL) _storage.getAdapter(URL.class);
			//			if (url != null && url.getProtocol().equals("file")
			//					&& (obj instanceof FileEditorInput)) {
			//				FileEditorInput fileEditorInput = (FileEditorInput)obj;
			//				String fileName = fileEditorInput.getFile().getRawLocation().toString();
			//				String urlFile = url.getFile();
			//				urlFile = URLDecoder.decode(urlFile);
			//				if (urlFile.startsWith("/"))
			//					urlFile = urlFile.substring(1);
			//				return fileName.equals(urlFile);
			//			}

			return false;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return _storage.hashCode();
		}

		public Object getAdapter(Class adapter) {
			try {
				if (IStorage.class.equals(adapter))
					return getStorage();
				if (IResource.class.equals(adapter))
					return super.getAdapter(adapter);
			} catch (CoreException ce) {
				//fallthrough
			}

			return super.getAdapter(adapter);
		}
	}
}
