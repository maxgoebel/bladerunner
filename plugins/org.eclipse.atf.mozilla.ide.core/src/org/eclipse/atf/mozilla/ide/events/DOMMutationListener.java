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
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;

public class DOMMutationListener implements IApplicationEventListener {

	public void nodeInserted(nsIDOMNode parentNode, nsIDOMNode insertedNode) {
	}

	public void nodeRemoved(nsIDOMNode parentNode, nsIDOMNode removedNode) {
	}

	public void attributeAdded(nsIDOMElement ownerElement, String attributeName) {
	}

	public void attributeRemoved(nsIDOMElement ownerElement, String attributeName) {
	}

	public void attributeModified(nsIDOMElement ownerElement, String attributeName, String newValue, String previousValue) {
	}

	public void onEvent(final IApplicationEvent aevent) {
		if (!(aevent instanceof DOMMutationEvent))
			return;

		SafeRunnable.run(new SafeRunnable() {
			public void run() {
				doOnEvent(aevent);
			}
		});
	}

	private void doOnEvent(IApplicationEvent aevent) {
		String type = aevent.getType();
		DOMMutationEvent event = (DOMMutationEvent) aevent;

		if (type.equals(DOMMutationEvent.NODE_INSERTED))
			nodeInserted(event.parentNode, event.insertedNode);
		else if (type.equals(DOMMutationEvent.NODE_REMOVED))
			nodeRemoved(event.parentNode, event.removedNode);
		else if (type.equals(DOMMutationEvent.ATTRIBUTE_ADDED))
			attributeAdded(event.ownerElement, event.attributeName);
		else if (type.equals(DOMMutationEvent.ATTRIBUTE_REMOVED))
			attributeRemoved(event.ownerElement, event.attributeName);
		else if (type.equals(DOMMutationEvent.ATTRIBUTE_MODIFIED))
			attributeModified(event.ownerElement, event.attributeName, event.newValue, event.previousValue);

	}
}
