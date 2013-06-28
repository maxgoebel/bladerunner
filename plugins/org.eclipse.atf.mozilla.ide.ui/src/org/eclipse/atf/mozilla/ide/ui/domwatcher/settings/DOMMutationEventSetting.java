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

import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.DOMMutationEventDetailProvider;

public class DOMMutationEventSetting extends PreferencePersistedDOMEventSetting{
	
	public DOMMutationEventSetting( String eventType ){
		this( eventType, true );
	}
	
	public DOMMutationEventSetting( String eventType, boolean defaultShouldWatch ){
		super( eventType, new DOMMutationEventDetailProvider(), defaultShouldWatch );
	}
}
