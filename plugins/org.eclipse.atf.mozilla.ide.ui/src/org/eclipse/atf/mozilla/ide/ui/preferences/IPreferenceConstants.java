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
package org.eclipse.atf.mozilla.ide.ui.preferences;

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;

public interface IPreferenceConstants {
	
	/**
	 * This preference is used to determine what to do when a browser popup is detected.
	 * 
	 * Values:
	 * 
	 * IPreferenceConstants.POPUP_HANDLING_AS_EDITOR - Open popup as another Browser editor
	 * IPreferenceConstants.POPUP_HANDLING_AS_DIALOG - Open popup in a Dialog
	 * IPreferenceConstants.POPUP_HANDLING_IGNORE - Ignore the popup
	 * IPreferenceConstants.POPUP_HANDLING_PROMPT - Ask the end-user
	 */
	static final String POPUP_HANDLING = MozIDEUIPlugin.PLUGIN_ID + "popupHandling";
	
	static final String POPUP_HANDLING_AS_EDITOR = "editor";
	static final String POPUP_HANDLING_AS_DIALOG = "dialog";
	static final String POPUP_HANDLING_IGNORE = "ignore";
	static final String POPUP_HANDLING_PROMPT = "prompt";
	
}
