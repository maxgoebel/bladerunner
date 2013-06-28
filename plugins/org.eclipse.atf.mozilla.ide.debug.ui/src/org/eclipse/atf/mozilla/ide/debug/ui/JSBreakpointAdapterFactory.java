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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Provides breakpoint adapters for all local html,htm,js files and remote
 * resources represented by IStorage.
 */
public class JSBreakpointAdapterFactory implements IAdapterFactory {

	private static final String[] supported_file_extensions = { "html", "htm", "js" };

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) adaptableObject;
			IEditorInput input = editorPart.getEditorInput();
			IResource resource = (IResource) input.getAdapter(IResource.class);

			if (resource != null) {
				String extension = resource.getFileExtension();
				if (extension == null)
					return null;

				for (int i = 0; i < supported_file_extensions.length; i++) {
					if (extension.equals(supported_file_extensions[i])) {
						return new JSLineBreakpointAdapter();
					}
				}
			} else {
				IStorage storage = (IStorage) input.getAdapter(IStorage.class);
				if (storage != null)
					return new JSLineBreakpointAdapter();
			}
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] { IToggleBreakpointsTarget.class };
	}

}