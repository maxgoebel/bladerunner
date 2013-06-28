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
package org.eclipse.atf.mozilla.ide.ui.domwatcher.settings;

/**
 * IDOMEventSetting with a IDOMEventDetailProvider that returns an empty string
 * 
 */
public class NoDetailEventSetting extends PreferencePersistedDOMEventSetting {

	public NoDetailEventSetting( String eventType ){
		super( eventType );
	}
	
	public NoDetailEventSetting( String eventType, boolean defaultShouldWatch ){
		super( eventType, null, defaultShouldWatch );
	}
	
}
