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


/**
 * Represents a DOM event with the time, type,
 * and any event information.
 * 
 * @author Kevin Sawicki (ksawicki@us.ibm.com)
 * @author Gino Bustelo
 */
public class DOMEvent implements IDOMEvent {
	
	protected String timestamp;
	protected String type;
	protected String details;
	
	/*
	 * @GINO: This will not be use currently
	 * 
	 * This member will hold the serialized target
	 */
	protected String version;
	
	public DOMEvent() {
		timestamp = "";
		type = "";
		details = "";
		version = "";
	}
	
	public DOMEvent( String timestamp, String type, String details, String version) {
		this.timestamp = timestamp;
		this.type = type;
		this.details = details;
		this.version = version;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.domwatcher2.IDOMEvent#getDetails()
	 */
	public String getDetails() {
		return details;
	}
	
	public void setDetails(String details) {
		this.details = details;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.domwatcher2.IDOMEvent#getType()
	 */
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.domwatcher2.IDOMEvent#getTimestamp()
	 */
	public String getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.atf.mozilla.ide.ui.domwatcher2.IDOMEvent#getVersion()
	 */
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
