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
package org.eclipse.atf.mozilla.ide.ui.netmon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventListener;

/**
 * This instance holds the IApplicationEvents that have been logged so far. It notifies the
 * registered listeners when calls are added and updated.
 * 
 * @author Gino Bustelo
 *
 */
public class EventRecorder implements IApplicationEventListener {

	protected Set<IApplicationEventListener> listeners = new HashSet<IApplicationEventListener>();

	/*
	 * Log of calls, This is what viewers will have access to
	 */
	protected List<IApplicationEvent> calls = new ArrayList<IApplicationEvent>();

	public void clear() {
		calls.clear();
	}

	public IApplicationEvent[] getAll() {
		IApplicationEvent[] callsArray = new IApplicationEvent[calls.size()];
		return calls.toArray(callsArray);
	}

	public void addListener(IApplicationEventListener netMonitor) {
		listeners.add(netMonitor);
	}

	public void removeListener(IApplicationEventListener netMonitor) {
		listeners.remove(netMonitor);
	}

	private void fireCallAddedNotification(IApplicationEvent call) {
		Iterator<IApplicationEventListener> iterator = listeners.iterator();
		while (iterator.hasNext())
			iterator.next().onEvent(call);
	}

	public void onEvent(IApplicationEvent event) {
		calls.add(event);
		fireCallAddedNotification(event);
	}
}
