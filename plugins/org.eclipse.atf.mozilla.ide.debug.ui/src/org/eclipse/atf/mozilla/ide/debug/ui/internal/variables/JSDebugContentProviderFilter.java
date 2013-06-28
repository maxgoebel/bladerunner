/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *     Joseph Fifield - bug 143470
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.debug.ui.internal.variables;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.debug.internal.model.JSDebugVariable;
import org.eclipse.atf.mozilla.ide.debug.ui.MozillaDebugUIPlugin;
import org.eclipse.atf.mozilla.ide.debug.ui.internal.IJSDebugPreferencesConstants;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.preference.IPreferenceStore;

public class JSDebugContentProviderFilter {

	private static boolean includeFunctions(IPresentationContext context) {
		IPreferenceStore store = MozillaDebugUIPlugin.getDefault().getPreferenceStore();
		String statics = IJSDebugPreferencesConstants.PREF_SHOW_FUNCTIONS;
		return store.getBoolean(statics);
	}

	private static boolean includeInternal(IPresentationContext context) {
		IPreferenceStore store = MozillaDebugUIPlugin.getDefault().getPreferenceStore();
		String statics = IJSDebugPreferencesConstants.PREF_SHOW_INTERNALS;
		return store.getBoolean(statics);
	}

	public static Object[] filterVariables(Object[] children, IPresentationContext context) {
		boolean inclFunctions = includeFunctions(context);
		boolean inclInternal = includeInternal(context);

		if (inclFunctions && inclInternal) // nothing is filtered
			return children;

		List result = new ArrayList();

		for (int i = 0; i < children.length; i++) {
			Object element = children[i];

			if (element instanceof JSDebugVariable) {
				JSDebugVariable variable = (JSDebugVariable) element;
				try {
					IValue value = variable.getValue();
					String referenceTypeName = value.getReferenceTypeName();

					if ((!inclFunctions) && "function".equals(referenceTypeName)) {
						continue;
					}

					if ((!inclInternal) && variable.isInternal()) {
						continue;
					}

					result.add(variable);
				} catch (DebugException e) {
					// ignore
				}
			}
		}
		return result.toArray();

	}

}
