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
/**
 * 
 */
package org.eclipse.atf.mozilla.ide.ui.domwatcher.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is a singleton class that holds the settings for the DOMWatcher. Every
 * registered event type has a settings object.
 * 
 * @author Gino Bustelo
 *
 */
public class DOMWatcherSettings {

	protected static DOMWatcherSettings instance = null;
	
	public static DOMWatcherSettings getInstance(){
		if( instance == null )
			instance = new DOMWatcherSettings();
		
		return instance;
	}
	
	protected HashMap eventTypeSettingsMap = new HashMap();
	protected List settings = new ArrayList();;
	
	private DOMWatcherSettings(){
		addSetting( new DOMMouseEventSetting("click") );
		addSetting( new DOMMouseEventSetting("dblclick") );
		addSetting( new DOMMouseEventSetting("mousedown") );
		addSetting( new DOMMouseEventSetting("mouseup") );
		addSetting( new DOMMouseEventSetting("mouseover") );
		addSetting( new DOMMouseEventSetting("mousemove", false ) );
		addSetting( new DOMMouseEventSetting("mouseout") );
		
		addSetting( new DOMKeyEventSetting("keypress") );
		addSetting( new DOMKeyEventSetting("keydown") );
		addSetting( new DOMKeyEventSetting("keyup") );
		
		addSetting( new PreferencePersistedDOMEventSetting("load") );
		addSetting( new PreferencePersistedDOMEventSetting("unload") );
		addSetting( new PreferencePersistedDOMEventSetting("abort") );
		addSetting( new PreferencePersistedDOMEventSetting("error") );
		addSetting( new PreferencePersistedDOMEventSetting("resize") );
		addSetting( new PreferencePersistedDOMEventSetting("scroll") );
		
		addSetting( new PreferencePersistedDOMEventSetting("select") );
		addSetting( new PreferencePersistedDOMEventSetting("change") );
		addSetting( new PreferencePersistedDOMEventSetting("submit") );
		addSetting( new PreferencePersistedDOMEventSetting("reset") );
		addSetting( new PreferencePersistedDOMEventSetting("focus") );
		addSetting( new PreferencePersistedDOMEventSetting("blur") );
		
		
		addSetting( new DOMMutationEventSetting("DOMNodeInserted") );
		addSetting( new DOMMutationEventSetting("DOMNodeRemoved") );
		addSetting( new DOMMutationEventSetting("DOMAttrModified") );
		
	}
	
	protected void addSetting( IDOMEventSetting setting ){
		settings.add( setting );
		eventTypeSettingsMap.put( setting.getEventType(), setting );
	}
	
	public IDOMEventSetting getSetting( String eventType ){
		return (IDOMEventSetting)eventTypeSettingsMap.get( eventType );
	}
	
	public IDOMEventSetting [] getSettings(){
		IDOMEventSetting [] settingsArray = new IDOMEventSetting[ settings.size() ];
		return (IDOMEventSetting[])settings.toArray( settingsArray );
	}
	
	public String [] getSupportedEventTypes(){
		String [] supportedEventTypes = new String[ settings.size() ];
		return (String [])eventTypeSettingsMap.keySet().toArray( supportedEventTypes );
	}
}
