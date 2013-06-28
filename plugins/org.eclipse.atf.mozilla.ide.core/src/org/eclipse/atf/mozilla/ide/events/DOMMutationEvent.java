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
package org.eclipse.atf.mozilla.ide.events;

import org.eclipse.atf.mozilla.ide.common.IWebBrowser;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMMutationEvent;
import org.mozilla.interfaces.nsIDOMNode;

public class DOMMutationEvent implements IApplicationEvent {
	public static final String NODE_INSERTED = "org.eclipse.atf.mozilla.ide.events.IDOMMutationListener.nodeInserted";
	public static final String NODE_REMOVED = "org.eclipse.atf.mozilla.ide.events.IDOMMutationListener.nodeRemoved";
	public static final String ATTRIBUTE_ADDED = "org.eclipse.atf.mozilla.ide.events.IDOMMutationListener.attributeAdded";
	public static final String ATTRIBUTE_REMOVED = "org.eclipse.atf.mozilla.ide.events.IDOMMutationListener.addributeRemoved";
	public static final String ATTRIBUTE_MODIFIED = "org.eclipse.atf.mozilla.ide.events.IDOMMutationListener.attributeModified";

	public nsIDOMNode parentNode;
	public nsIDOMNode insertedNode;
	public nsIDOMNode removedNode;
	public nsIDOMElement ownerElement;
	public String attributeName;
	public String newValue;
	public String previousValue;
	private String type;
	private IWebBrowser browser;

	public DOMMutationEvent(IWebBrowser browser, String type) {
		this.browser = browser;
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static DOMMutationEvent attributeRemoved(IWebBrowser browser, nsIDOMMutationEvent event) {
		DOMMutationEvent newEvent = new DOMMutationEvent(browser, ATTRIBUTE_REMOVED);
		newEvent.ownerElement = (nsIDOMElement) event.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		newEvent.attributeName = event.getAttrName();
		return newEvent;
	}

	public static DOMMutationEvent attributeModified(IWebBrowser browser, nsIDOMMutationEvent event) {
		DOMMutationEvent newEvent = new DOMMutationEvent(browser, ATTRIBUTE_MODIFIED);
		newEvent.ownerElement = (nsIDOMElement) event.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		newEvent.attributeName = event.getAttrName();
		newEvent.newValue = event.getNewValue();
		newEvent.previousValue = event.getPrevValue();
		return newEvent;
	}

	public static DOMMutationEvent attributeAdded(IWebBrowser browser, nsIDOMMutationEvent event) {
		DOMMutationEvent newEvent = new DOMMutationEvent(browser, ATTRIBUTE_ADDED);
		newEvent.ownerElement = (nsIDOMElement) event.getTarget().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		newEvent.attributeName = event.getAttrName();
		return newEvent;
	}

	public static DOMMutationEvent nodeRemoved(IWebBrowser browser, nsIDOMMutationEvent event) {
		DOMMutationEvent newEvent = new DOMMutationEvent(browser, NODE_REMOVED);
		newEvent.parentNode = event.getRelatedNode();
		newEvent.removedNode = (nsIDOMNode) event.getTarget().queryInterface(nsIDOMNode.NS_IDOMNODE_IID);
		return newEvent;
	}

	public static DOMMutationEvent nodeInserted(IWebBrowser browser, nsIDOMMutationEvent event) {
		DOMMutationEvent newEvent = new DOMMutationEvent(browser, NODE_INSERTED);
		newEvent.parentNode = event.getRelatedNode();
		newEvent.insertedNode = (nsIDOMNode) event.getTarget().queryInterface(nsIDOMNode.NS_IDOMNODE_IID);
		return newEvent;
	}

	public IWebBrowser getBrowser() {
		return browser;
	}
}
