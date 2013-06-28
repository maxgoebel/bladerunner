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

import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.StorageDocumentProvider;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

/**
 * Provides JS breakpoint annotations for IStorage based resources
 *
 */
public class URLStorageDocumentProvider extends StorageDocumentProvider {

	private IWorkspace fWorkspace;
	private IResource fRoot;

	protected class URLStorageAnnotationModel extends ResourceMarkerAnnotationModel {

		private String fName;

		public URLStorageAnnotationModel(String name) {
			super(fRoot);
			fName = name;
		}

		protected boolean isAcceptable(IMarker marker) {
			String id = null;
			try {
				id = (String) marker.getAttribute(JSLineBreakpoint.STORAGE_ID);
			} catch (CoreException e) {
				MozillaDebugUIPlugin.log(e);
			}
			return marker != null && fRoot.equals(marker.getResource()) && fName.equals(id);
		}
	}

	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		if (fRoot == null) {
			fWorkspace = ResourcesPlugin.getWorkspace();
			fRoot = fWorkspace.getRoot();
		}

		IEditorInput storage = (IEditorInput) element;
		return new URLStorageAnnotationModel(storage.getName());
	}
}
