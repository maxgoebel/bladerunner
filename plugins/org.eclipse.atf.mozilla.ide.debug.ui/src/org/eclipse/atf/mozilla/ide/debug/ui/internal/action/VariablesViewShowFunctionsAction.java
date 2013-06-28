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
package org.eclipse.atf.mozilla.ide.debug.ui.internal.action;

import org.eclipse.atf.mozilla.ide.debug.ui.internal.IJSDebugPreferencesConstants;

/**
 * Action that shows or hides functions from the debug variables view.
 * 
 * @author jfifield
 */
public class VariablesViewShowFunctionsAction extends AbstractVariablesViewAction {

	protected String getPreferenceKey() {
		return IJSDebugPreferencesConstants.PREF_SHOW_FUNCTIONS;
	}

}
