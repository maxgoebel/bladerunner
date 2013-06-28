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
package org.eclipse.atf.mozilla.ide.debug.ui.internal.adapter;

import org.eclipse.atf.mozilla.ide.debug.ui.internal.variables.JSDebugContentProviderFilter;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.ui.model.elements.StackFrameContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;

public class JSDebugStackFrameContentProvider extends StackFrameContentProvider {

	protected Object[] getAllChildren(Object parent, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
		Object[] children = super.getAllChildren(parent, context, monitor);
		children = JSDebugContentProviderFilter.filterVariables(children, context);
		return children;
	}
}
