/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies Ltd. -ongoing improvements
 *******************************************************************************/

package org.eclipse.atf.mozilla.ide.events;

import org.eclipse.jface.util.SafeRunnable;

public class DOMDocumentListener implements IApplicationEventListener {

	/*
	 * Signals that the event.targetDocument is fully loaded
	 */
	public void documentLoaded(DOMDocumentEvent event) {
	}

	/*
	 * Signals that the event.targetDocument is unloaded
	 */
	public void documentUnloaded(DOMDocumentEvent event) {
	}

	public void onEvent(final IApplicationEvent event) {
		if (!(event instanceof DOMDocumentEvent))
			return;

		SafeRunnable.run(new SafeRunnable() {
			public void run() {
				doOnEvent((DOMDocumentEvent) event);
			}
		});
	}

	private void doOnEvent(DOMDocumentEvent event) {
		String type = event.getType();
		if (type.equals(DOMDocumentEvent.LOAD_EVENT))
			documentLoaded(event);
		if (type.equals(DOMDocumentEvent.UNLOAD_EVENT))
			documentUnloaded(event);
	}
}
