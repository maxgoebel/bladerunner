/*******************************************************************************
 * Copyright (c) 2006, 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.debug.ui.internal.adapter;

import org.eclipse.atf.mozilla.ide.debug.model.JSFunctionBreakpoint;
import org.eclipse.atf.mozilla.ide.debug.model.JSLineBreakpoint;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.debug.ui.BreakpointTypeCategory;
import org.eclipse.debug.ui.IBreakpointTypeCategory;

public class JSBreakpointTypeAdapterFactory implements IAdapterFactory {

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof JSLineBreakpoint) {
			return new BreakpointTypeCategory("Line Breakpoints");
		} else if (adaptableObject instanceof JSFunctionBreakpoint) {
			return new BreakpointTypeCategory("Function Breakpoints");
		}

		return new BreakpointTypeCategory("Special Breakpoints");
	}

	public Class[] getAdapterList() {
		return new Class[] { IBreakpointTypeCategory.class };
	}

}
