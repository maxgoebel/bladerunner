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

import org.eclipse.atf.mozilla.ide.ui.MozIDEUIPlugin;
import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.IDOMEventDetailProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.mozilla.interfaces.nsIDOMEvent;

/**
 * The shouldWatch boolean is stored in the PreferenceStore. The default value is
 * passed in through the constructor, it not it's set to true.
 * 
 */
public class PreferencePersistedDOMEventSetting implements IDOMEventSetting {

	protected static final String DOMWATCHER_SETTING_PREFIX = "DOMWATCHER_SETTING.";
	
	protected String eventType;
	
	protected IDOMEventDetailProvider detailProvider = new IDOMEventDetailProvider(){

		public String getDetail(nsIDOMEvent event) {
			return "";
		}
		
	};
	
	/*
	 * Defaults shouldWatch to true
	 */
	public PreferencePersistedDOMEventSetting(String eventType) {
		this( eventType, null, true );
	}
	
	public PreferencePersistedDOMEventSetting(String eventType, IDOMEventDetailProvider detailProvider ) {
		this( eventType, detailProvider, true );
	}
	
	/**
	 * Initializes this settings instance. The shouldWatch value is going to be set
	 * as the default for a preference. 
	 * 
	 * 
	 * @param eventType
	 * @param detailProvider
	 * @param shouldWatch - default value for the preference
	 */
	/*
	 * Dev Note: If the preference already exist, if will retain it's current value, which could
	 * be different than the default. 
	 */
	public PreferencePersistedDOMEventSetting(String eventType, IDOMEventDetailProvider detailProvider, boolean shouldWatch ) {
		this.eventType = eventType;
		
		if( detailProvider != null )
			this.detailProvider = detailProvider;
		
		//setup store
		IPreferenceStore store = MozIDEUIPlugin.getDefault().getPreferenceStore();
		
		store.setDefault( DOMWATCHER_SETTING_PREFIX+eventType, shouldWatch );
	}

	

	
	public IDOMEventDetailProvider getDetailProvider() {
		return detailProvider;
	}

	public String getEventType() {
		return eventType;
	}

	public boolean shouldWatch() {
		IPreferenceStore store = MozIDEUIPlugin.getDefault().getPreferenceStore();
		return store.getBoolean( DOMWATCHER_SETTING_PREFIX+eventType );
	}
	
	public void setShouldWatch( boolean shouldWatch ){
		IPreferenceStore store = MozIDEUIPlugin.getDefault().getPreferenceStore();
		store.setValue( DOMWATCHER_SETTING_PREFIX+eventType, shouldWatch );
	}

}
