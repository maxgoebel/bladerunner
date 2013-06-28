/*******************************************************************************
 * Copyright (c) 2009 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.network;

public interface IStatusChange {

	/**
	 * One of statuses defined in INetworkCall
     */
	String getStatus();

    /**
     * Timestamp of the status change
     */
	long getTimestamp();

    /**
     * Related network call
     */
	public INetworkCall getCall();
}
