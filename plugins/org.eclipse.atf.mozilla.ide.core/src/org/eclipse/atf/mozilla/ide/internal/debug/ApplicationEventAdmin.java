/*******************************************************************************
 * Copyright (c) 2009, 2010 Zend Technologies Ltd. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zend Technologies Ltd. - initial API and implementation
 *******************************************************************************/
package org.eclipse.atf.mozilla.ide.internal.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.eclipse.atf.mozilla.ide.core.MozideCorePlugin;
import org.eclipse.atf.mozilla.ide.events.IApplicationEvent;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventAdmin;
import org.eclipse.atf.mozilla.ide.events.IApplicationEventListener;

public class ApplicationEventAdmin implements IApplicationEventAdmin {

	private Map<IWebBrowser, List<IApplicationEventListener>> listeners = new HashMap<IWebBrowser, List<IApplicationEventListener>>();
	private List<IApplicationEventListener> unboundListeners = new ArrayList<IApplicationEventListener>();

	private List eventsQueue = new ArrayList();

	private boolean isTerminated = false;
	private Thread t;

	public ApplicationEventAdmin() {
		Runnable runnable = new Runnable() {
			public void run() {
				synchronized (eventsQueue) {
					while (!isTerminated) {
						try {
							eventsQueue.wait(1000);
						} catch (InterruptedException e) {
							// empty
						}
						notifyListeners();
					}
				}
			}
		};
		t = new Thread(runnable);
		t.start();
	}

	private void notifyListeners() {
		IApplicationEvent[] events = null;

		synchronized (eventsQueue) {
			if (eventsQueue.size() == 0) {
				return;
			}

			events = (IApplicationEvent[]) eventsQueue.toArray(new IApplicationEvent[eventsQueue.size()]);
			eventsQueue.clear();
		}

		for (int i = 0; i < events.length; i++) {
			IApplicationEvent event = events[i];

			try {
				sendEvent(event);
			} catch (RuntimeException e) {
				MozideCorePlugin.log(e);
			}
		}
	}

	public void postEvent(IApplicationEvent event) {
		synchronized (eventsQueue) {
			eventsQueue.add(event);
			eventsQueue.notify();
		}
	}

	public synchronized void sendEvent(IApplicationEvent event) {
		List<IApplicationEventListener> bListeners = listeners.get(event.getBrowser());
		if (bListeners != null) {
			for (Iterator<IApplicationEventListener> l = bListeners.iterator(); l.hasNext();) {
				IApplicationEventListener listener = l.next();
				try {
					listener.onEvent(event);
				} catch (RuntimeException e) {
					MozideCorePlugin.log(e);
				}
			}
		}

		for (Iterator<IApplicationEventListener> l = unboundListeners.iterator(); l.hasNext();) {
			IApplicationEventListener listener = l.next();
			try {
				listener.onEvent(event);
			} catch (RuntimeException e) {
				MozideCorePlugin.log(e);
			}
		}
	}

	public void addEventListener(IWebBrowser browser, IApplicationEventListener listener) {
		List list = listeners.get(browser);
		if (list == null) {
			list = new ArrayList();
			listeners.put(browser, list);
		}
		list.add(listener);
	}

	public void removeEventListener(IWebBrowser browser, IApplicationEventListener listener) {
		List list = listeners.get(browser);
		if (list != null)
			list.remove(listener);
	}

	public void terminate() {
		isTerminated = true;
		t.interrupt();
	}

	public void dispose(IWebBrowser browser) {
		List list = listeners.remove(browser);
		if (list != null) {
			list.clear();
		}
	}

	public void addEventListener(IApplicationEventListener listener) {
		unboundListeners.add(listener);
	}

	public void removeEventListener(IApplicationEventListener listener) {
		unboundListeners.remove(listener);
	}
}
