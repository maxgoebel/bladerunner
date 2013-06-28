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
package org.eclipse.atf.mozilla.ide.ui.domwatcher.model;

import org.mozilla.interfaces.nsIDOMEvent;

/**
 * This interface is use to calculate the Detail string for a particular 
 * event type. Each subclass of nsIDOMEvent might have different members that
 * are used to provide more information about the event. 
 * 
 * @author Gino Bustelo
 *
 */
public interface IDOMEventDetailProvider {

	/*
	 * Returns a String for detail information about the event
	 */
	String getDetail( nsIDOMEvent event );
}
