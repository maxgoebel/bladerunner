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
package org.eclipse.atf.mozilla.ide.ui.netmon.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.atf.mozilla.ide.network.IHTTPRequest;
import org.eclipse.atf.mozilla.ide.network.IHTTPResponse;
import org.eclipse.atf.mozilla.ide.network.INetworkCall;
import org.eclipse.atf.mozilla.ide.network.IRequest;
import org.eclipse.atf.mozilla.ide.network.IResponse;
import org.eclipse.atf.mozilla.ide.network.IStatusChange;
import org.mozilla.interfaces.nsISocketTransport;
import org.mozilla.interfaces.nsITransport;

public class BaseNetworkCall implements INetworkCall {

	public static final long STOP = -2;
	public static final long START = 0;

	protected IHTTPRequest request;
	protected IHTTPResponse response;
	private List statusChanges = new ArrayList();

	private class StatusChange implements IStatusChange {
		private long status;
		private long timestamp;

		public StatusChange(long status, long timestamp) {
			this.status = status;
			this.timestamp = timestamp;
		}

		public String getStatus() {
			if (status == START)
				return INetworkCall.ACTIVE_STATE;
			if (status == STOP)
				return INetworkCall.DONE_STATE;
			if (status == nsITransport.STATUS_READING)
				return INetworkCall.RECEIVING_STATE;
			if (status == nsITransport.STATUS_WRITING)
				return INetworkCall.SENDING_STATE;
			if (status == nsISocketTransport.STATUS_RESOLVING)
				return INetworkCall.RESOLVING_STATE;
			if (status == nsISocketTransport.STATUS_CONNECTING_TO)
				return INetworkCall.CONNECTING_STATE;
			if (status == nsISocketTransport.STATUS_CONNECTED_TO)
				return INetworkCall.CONNECTING_STATE;
			if (status == nsISocketTransport.STATUS_SENDING_TO)
				return INetworkCall.SENDING_STATE;
			if (status == nsISocketTransport.STATUS_WAITING_FOR)
				return INetworkCall.WAITING_STATE;
			if (status == nsISocketTransport.STATUS_RECEIVING_FROM)
				return INetworkCall.RECEIVING_STATE;

			throw new IllegalStateException("Invalid status " + status);
		}

		public long getTimestamp() {
			return timestamp;
		}

		public BaseNetworkCall getCall() {
			return BaseNetworkCall.this;
		}
	}

	/*
	 * Cached info, stored also in statusChanges list.
	 */
	private long startTime = -1L;
	private long endTime = -1L;

	/*
	 * Can be ACTIVE, SUCCESS, WARNING, ERROR
	 */
	protected String state;

	public IRequest getRequest() {
		return request;
	}

	public IResponse getResponse() {
		return response;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getTotalTime() {
		if (endTime == -1 || startTime == -1)
			return -1;
		else
			return (int) (endTime - startTime);
	}

	public String getState() {
		return state;
	}

	public String toString() {
		return "BaseNetworkCall [startTime=" + startTime + ", state=" + state + ", endTime=" + endTime + ", request=" + request + ", response=" + response + "]";
	}

	public void addStatusChange(long status, long timestamp) {
		if (startTime == -1)
			startTime = timestamp;
		endTime = timestamp;
		if (statusChanges.size() > 0) {
			StatusChange last = (StatusChange) statusChanges.get(statusChanges.size() - 1);
			if (last.status != status) {
				statusChanges.add(new StatusChange(status, timestamp));
			}
		} else {
			statusChanges.add(new StatusChange(status, timestamp));
		}
	}

	public IStatusChange[] getStatusChanges() {
		return (IStatusChange[]) statusChanges.toArray(new IStatusChange[statusChanges.size()]);
	}

}
