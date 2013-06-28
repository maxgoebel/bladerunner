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

import org.eclipse.atf.mozilla.ide.ui.domwatcher.model.IDOMEventDetailProvider;

public interface IDOMEventSetting {
	
	/*
	 * String for the type of the event. This is the DOM event type used
	 * to hook listeners
	 */
	String getEventType();
	
	/*
	 * Determine if this event type should be watch
	 */
	boolean shouldWatch();
	
	/*
	 * Set shouldWatch
	 */
	
	void setShouldWatch( boolean shouldWatch );
	
	/*
	 * Object used to calculate the detail String
	 */
	IDOMEventDetailProvider getDetailProvider();
	
}
