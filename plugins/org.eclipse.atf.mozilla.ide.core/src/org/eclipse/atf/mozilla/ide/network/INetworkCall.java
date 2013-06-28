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

package org.eclipse.atf.mozilla.ide.network;

public interface INetworkCall {

	static final String ACTIVE_STATE = "ACTIVE";
	static final String RESOLVING_STATE = "RESOLVING";
	static final String CONNECTING_STATE = "CONNECTING";
	static final String WAITING_STATE = "WAITING";
	static final String RECEIVING_STATE = "RECEIVING";
	static final String SENDING_STATE = "SENDING";
	static final String DONE_STATE = "DONE";

	static final String SUCCESS_STATE = "SUCCESS";
	static final String WARNING_STATE = "WARNING";
	static final String ERROR_STATE = "ERROR";

	/**
	 * Request that initiated this network call.
	 * 
	 * @return request that initiated this network call.
	 */
	IRequest getRequest();

	/**
	 * A response to the request.
	 * 
	 * @return response or null if connection was broken.
	 */
	IResponse getResponse();

	/**
	 * One of the four states (ACTIVE | SUCCESS | WARNING | ERROR)
	 * @return
	 */
	String getState();

	IStatusChange[] getStatusChanges();

	/**
	 * The time in ms representing the start time of the call
	 * 
	 * -1 if not set
	 * @return
	 */
	long getStartTime();

	/**
	 * The time in ms representing the end time of the call
	 * 
	 * -1 if not set
	 * @return
	 */
	long getEndTime();

	/**
	 * The time in ms representing the total time of the call
	 * 
	 * -1 if not set
	 * @return
	 */
	int getTotalTime();
}
