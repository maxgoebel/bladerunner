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
package org.eclipse.atf.mozilla.ide.debug.ui.internal.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.ui.sourcelookup.ISourceDisplay;

public class JSDebugStackFrameAdapterFactory implements IAdapterFactory {

	public static final Class[] supportedAdapters = { IElementContentProvider.class };

	private static final IElementContentProvider jsdStackFrameContentProvider = new JSDebugStackFrameContentProvider();

	public Object getAdapter(Object adaptableObject, Class adapterType) {

		if (adapterType == IElementContentProvider.class) {
			return jsdStackFrameContentProvider;

		}
		if (adapterType == ISourceDisplay.class) {
			return null;
		}

		return null;
	}

	public Class[] getAdapterList() {
		return JSDebugStackFrameAdapterFactory.supportedAdapters;
	}

}
